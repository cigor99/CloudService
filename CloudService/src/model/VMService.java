package model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("vmServ")
public class VMService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/getVMs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVMs()
	{
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}	
		VMs vms = (VMs) ctx.getAttribute("vms");
		
		if(vms == null)
		{
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if(curr.getRole().equals(Role.SUPER_ADMIN)) {
			try {
				JSON = mapper.writeValueAsString(vms.getVms());
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
			
		
		Organisations organs = (Organisations) ctx.getAttribute("organisations");
		
		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for(String r : organs.getOrganisations().get(curr.getOrganisation()).getResources()) {
			if(vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}
		
		try {
			JSON = mapper.writeValueAsString(orgVM);
		}catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();
	}
	
	@POST
	@Path("/addVM")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addVM(VM vm){
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(curr.getRole().equals(Role.USER)) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(vm == null) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if(isVMEmpty(vm)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		ArrayList<Integer> positive = new ArrayList<Integer>();
		positive.add(vm.getNumCPUCores());
		positive.add(vm.getRamCapacity());
		if(vm.getNumGPUCores() < 0) 
			return Response.status(400).entity("Error 400 : Number of GPU cores must be equal or greater than 0 !").build();
		if(!Validator.valPositive(positive))
			return Response.status(400).entity("Error 400 : Number of CPU cores/Ram capacity must be greater than 0 !").build();
		
		VMs vms = (VMs) ctx.getAttribute("vms");
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(vms.getVms().containsKey(vm.getName()))
			return Response.status(400).entity("Error 400 : Virtual machine with given name already exists!").build();
		
		Organisations orgs = (Organisations) ctx.getAttribute("organisations");
		orgs.getOrganisations().get(vm.getOrganisation()).getResources().add(vm.getName());
		
		if (vm.getDiscs() != null) {
			for(Disc d : discs.getDiscs().values()) {
				for(String dvm : vm.getDiscs()) {
					if(d.getName().equals(dvm)) {
						d.setVmName(vm.getName());
					}
				}
			}
		}
		vms.getVms().put(vm.getName(), vm);
		ctx.setAttribute("vms", vms);
		ctx.setAttribute("organisations", orgs);
		ctx.setAttribute("discs", discs);
		vms.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if(curr.getRole().equals(Role.SUPER_ADMIN)) {
			try {
				JSON = mapper.writeValueAsString(vms.getVms());
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
		
		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for(String r : orgs.getOrganisations().get(curr.getOrganisation()).getResources()) {
			if(vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}
		
		try {
			JSON = mapper.writeValueAsString(orgVM);
		}catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();
		
	}
	
	private boolean isVMEmpty(Object ob) {
		boolean ind = true;
		if(ob instanceof VM) {
			VM vm = (VM) ob;
			ind = ind && (vm.getName() == null);
			ind = ind && (vm.getOrganisation() == null);
			ind = ind && Validator.valPositive(vm.getNumCPUCores());
			ind = ind && Validator.valPositive(vm.getRamCapacity());
			ind = ind && (vm.getNumGPUCores()<0);
			ind = ind && (vm.getCategory() == null);
			return ind;
		}else if(ob instanceof VMWrapper) {
			VMWrapper vm = (VMWrapper) ob;
			ind = ind && (vm.getName() == null);
			ind = ind && (vm.getOrganisation() == null);
			ind = ind && Validator.valPositive(vm.getNumCPUCores());
			ind = ind && Validator.valPositive(vm.getRamCapacity());
			ind = ind && (vm.getNumGPUCores()<0);
			ind = ind && (vm.getCategory() == null);
			return ind;
		}
		return true;
	}
	
	@POST
	@Path("/editVM")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editVM(VMWrapper vmWrap){
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(curr.getRole().equals(Role.USER)) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		
		if(curr.getRole().equals(Role.ADMIN)) {
			if(curr.getOrganisation().equals(vmWrap.getOrganisation())) {
				return Response.status(400).entity("Error 403 : Access denied !").build();
			}
		}
		if(vmWrap == null) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if(isVMEmpty(vmWrap)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		ArrayList<Integer> positive = new ArrayList<Integer>();
		positive.add(vmWrap.getNumCPUCores());
		positive.add(vmWrap.getRamCapacity());
		if(vmWrap.getNumGPUCores() < 0) 
			return Response.status(400).entity("Error 400 : Number of GPU cores must be equal or greater than 0 !").build();
		if(!Validator.valPositive(positive))
			return Response.status(400).entity("Error 400 : Number of CPU cores/Ram capacity must be greater than 0 !").build();
		
		VMs vms = (VMs) ctx.getAttribute("vms");
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(!vmWrap.getOldName().equals(vmWrap.getName())) {
			if(vms.getVms().containsKey(vmWrap.getName()))
				return Response.status(400).entity("Error 400 : Virtual machine with given name already exists!").build();
		}
		
		VM vm = vms.getVms().get(vmWrap.getName());
		
		if (vm.getDiscs() != null) {
			if(vmWrap.getDiscs() != null) {
				for(String oldDisc : vm.getDiscs()) {
					if(!vmWrap.getDiscs().contains(oldDisc)) {
						discs.getDiscs().get(oldDisc).setVmName(null);
					}
				}
			}else {
				for(String oldDisc : vm.getDiscs()) {
					discs.getDiscs().get(oldDisc).setVmName(null);
				}
			}
		}
		
		vm.setCategory(vmWrap.getCategory());
		vm.setDiscs(vmWrap.getDiscs());
		vm.setName(vmWrap.getName());
		vm.setNumCPUCores(vmWrap.getNumCPUCores());
		vm.setNumGPUCores(vmWrap.getNumGPUCores());
		vm.setRamCapacity(vmWrap.getRamCapacity());
		
		vms.getVms().remove(vmWrap.getOldName());
		vms.getVms().put(vm.getName(),vm);
		Organisations orgs = (Organisations) ctx.getAttribute("organisations");
		
		if(!vmWrap.getOldName().equals(vmWrap.getName())) {
			orgs.getOrganisations().get(vmWrap.getOrganisation()).getResources().remove(vm.getName());
			orgs.getOrganisations().get(vmWrap.getOrganisation()).getResources().add(vm.getName());
		}
		
		ctx.setAttribute("vms", vms);
		ctx.setAttribute("organisations", orgs);
		ctx.setAttribute("discs", discs);
		vms.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if(curr.getRole().equals(Role.SUPER_ADMIN)) {
			try {
				JSON = mapper.writeValueAsString(vms.getVms());
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
		
		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for(String r : orgs.getOrganisations().get(curr.getOrganisation()).getResources()) {
			if(vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}
		
		try {
			JSON = mapper.writeValueAsString(orgVM);
		}catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();
		
	}
	
	/////POTREBNA POMOC=============================================================================================================================
	
	@POST
	@Path("/deleteVM")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteVM(@FormParam("oldName") String oldName, @FormParam("organisation") String organisation){
		
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(curr.getRole().equals(Role.USER)) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		
		String[] args = {oldName, organisation};
		if(Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		
		VMs vms = (VMs) ctx.getAttribute("vms");
		
		VM vm = vms.getVms().get(oldName);		
		vms.getVms().remove(oldName);
		
		Organisations orgs = (Organisations) ctx.getAttribute("organisations");
		
		orgs.getOrganisations().get(organisation).getResources().remove(oldName);
		
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		for(String d : vm.getDiscs()) {
			discs.getDiscs().get(d).setVmName(null);
		}
		
		return getVMs();
		
	}
	
	@POST
	@Path("/getFillterVMs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, VM> getFillterVMs(@FormParam("text") String text)
	{
		HashMap<String,VM> vms = getVMs2();
		
		HashMap<String, VM> fillterVMs = new HashMap<String, VM>();
		
		for (String name : vms.keySet()) {
			if(name.contains(text))
				fillterVMs.put(name,vms.get(name));
		}
		
		return fillterVMs;
	}
	
	private HashMap<String,VM>  getVMs2()
	{
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		VMs vms = (VMs) ctx.getAttribute("vms");
		
		if(vms == null)
		{
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}
		if(curr.getRole().equals(Role.SUPER_ADMIN)) {
			return vms.getVms();
		}
			
		
		Organisations organs = (Organisations) ctx.getAttribute("organisations");
		
		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for(String r : organs.getOrganisations().get(curr.getOrganisation()).getResources()) {
			if(vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}
		
		return orgVM;
	}
	
	@POST
	@Path("/filterVM")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, VM> filterVM(@FormParam("cpufrom") String cpufrom,@FormParam("cputo") String cputo,
			@FormParam("ramfrom") String ramfrom,@FormParam("ramto") String ramto,
			@FormParam("gpufrom") String gpufrom,@FormParam("gputo") String gputo,
			@FormParam("text") String text)
	{
		
		HashMap<String,VM> vms = getFillterVMs(text);
		
		//svi
		Integer cpufromI = from(cpufrom);
		Integer cputoI = to(cputo);
		Integer ramfromI = from(ramfrom);
		Integer ramtoI = to(ramto);
		Integer gpufromI = from(gpufrom);
		Integer gputoI = to(gputo);
		
		//ako je bilo sta od toga svega neko slovo tj nije prazan string ili broj vracam gresku
		if(cpufromI == null || cputoI == null || ramfromI==null || ramtoI==null || gpufromI==null || gputoI==null ) {
			return null;
		}
		
		HashMap<String, VM> fillterVMs = new HashMap<String, VM>();
		
		for (VM vm : vms.values()) {
			if(vm.getNumCPUCores() >= cpufromI && vm.getNumCPUCores() <= cputoI && vm.getRamCapacity() >= ramfromI && vm.getRamCapacity() <= ramtoI && vm.getNumGPUCores() >= gpufromI && vm.getNumGPUCores() <=gputoI  ) {
				fillterVMs.put(vm.getName(),vm);
			}
		}
		
		return fillterVMs;
	}
	
	private static Integer from(String from) {
		if(from.equals("")) {
			return Integer.MIN_VALUE;
		}else {
			if(isNumeric(from)) {
				return Integer.parseInt(from);
			}
		}
		
		return null;
	}
	
	private static Integer to(String to) {
		if(to.equals("")) {
			return Integer.MAX_VALUE;
		}else {
			if(isNumeric(to)) {
				return Integer.parseInt(to);
			}
		}
		
		return null;
	}
	
	private static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        int i = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
}
