package model;

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

@Path("discServ")
public class DiscService {

	@Context
	HttpServletRequest request;

	@Context
	ServletContext ctx;

	@GET
	@Path("/getDiscs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDiscs() {
		User curr = (User) request.getSession().getAttribute("currentUser");

		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		Discs discs = (Discs) ctx.getAttribute("discs");

		if (discs == null) {
			discs = new Discs(ctx.getRealPath("."));
			ctx.setAttribute("discs", discs);
		}
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if (curr.getRole().equals(Role.SUPER_ADMIN)) {
			try {
				JSON = mapper.writeValueAsString(discs.getDiscs());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		} else {
			HashMap<String, Disc> available = new HashMap<String, Disc>();
			for (Disc d : discs.getDiscs().values()) {
				if(!(d.getOrganisation()==null)) {
					if (d.getOrganisation().equals(curr.getOrganisation())) {
						available.put(d.getName(), d);
					}
				}

			}
			try {
				JSON = mapper.writeValueAsString(available);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
	}

	@GET
	@Path("/getVMs")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, VM> getVMs() {

		User curr = (User) request.getSession().getAttribute("currentUser");



		VMs vms = (VMs) ctx.getAttribute("vms");

		if (vms == null) {
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}
		if (curr.getRole().equals(Role.SUPER_ADMIN)) {
			return vms.getVms();
		}
	
		Organisations organs = (Organisations) ctx.getAttribute("organisations");

		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for (String r : organs.getOrganisations().get(curr.getOrganisation()).getResources()) {
		
			
			if (vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}

		return orgVM;

	}

	@POST
	@Path("/addDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addDisc(@FormParam("name") String name, @FormParam("discType") String discType,
			@FormParam("capacity") String capacity, @FormParam("vmName") String vmName, @FormParam("orgName") String organisation) {
		User curr = (User) request.getSession().getAttribute("currentUser");
		Logger l = new Logger("addDISC.txt");
		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (curr.getRole().equals(Role.USER)) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		String[] args = { capacity, discType, name };
		if (Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if (!Validator.valType(discType)) {
			return Response.status(400).entity("Error 400 : Invalid type entry !").build();
		}
		if (!Validator.valPositive(capacity)) {
			return Response.status(400).entity("Error 400 : Disc capacity must be greater than 0 !").build();
		}

		Discs discs = (Discs) ctx.getAttribute("discs");

		if (discs.getDiscs().containsKey(name))
			return Response.status(400).entity("Error 400 : Disc with given name already exists!").build();

		DiscType dt = DiscType.HDD;
		if (discType.equals("SSD"))
			dt = DiscType.SSD;

		Organisations organs = (Organisations) ctx.getAttribute("organisations");

		Disc disc = null;
		if (curr.getRole().equals(Role.SUPER_ADMIN))
			if(vmName.equals("")) {
				disc = new Disc(name, organisation, dt, Integer.parseInt(capacity), null);
			}else {
				disc = new Disc(name, organisation, dt, Integer.parseInt(capacity), vmName);
			}
		else {
			if(vmName.equals("")) {
				disc = new Disc(name, curr.getOrganisation(), dt, Integer.parseInt(capacity), null);
				organs.getOrganisations().get(curr.getOrganisation()).getResources().add(name);
			}else {
				disc = new Disc(name, curr.getOrganisation(), dt, Integer.parseInt(capacity), vmName);
				organs.getOrganisations().get(curr.getOrganisation()).getResources().add(name);
			}
		}
		l.append(disc.toString());

		discs.getDiscs().put(name, disc);
		// ctx.setAttribute("discs", discs);
		discs.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if (curr.getRole().equals(Role.SUPER_ADMIN)) {
			try {
				JSON = mapper.writeValueAsString(discs.getDiscs());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
		
		HashMap<String, VM> vms = getVMs();
		l.append(vms.toString());
		vms.get(vmName).getDiscs().add(name);
		l.append(vms.toString());
		l.preciseLog(l.getLine());
		HashMap<String, Disc> available = new HashMap<String, Disc>();
		for (Disc d : discs.getDiscs().values()) {
			if (d.getOrganisation().equals(curr.getOrganisation())) {
				available.put(d.getName(), d);
			}

		}
		try {
			JSON = mapper.writeValueAsString(available);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();

	}

	@POST
	@Path("/editDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response editVMCategory(@FormParam("oldName") String oldName, @FormParam("newName") String newName,
			@FormParam("discType") String discType, @FormParam("capacity") String capacity,
			@FormParam("oldVMname") String oldVMname, @FormParam("newVMname") String newVMname, @FormParam("orgName") String organisation) {
		User curr = (User) request.getSession().getAttribute("currentUser");
		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (curr.getRole().equals(Role.USER)) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		String[] args = { capacity, discType, newName, oldName};
		if (Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if (!Validator.valType(discType)) {
			return Response.status(400).entity("Error 400 : Invalid type entry !").build();
		}
		if (!Validator.valPositive(capacity)) {
			return Response.status(400).entity("Error 400 : Disc capacity must be greater than 0 !").build();
		}

		Discs discs = (Discs) ctx.getAttribute("discs");

		Organisations orgs = (Organisations) ctx.getAttribute("organisations");

		if (!newName.equals(oldName)) {
			if (discs.getDiscs().containsKey(newName))
				return Response.status(400).entity("Error 400 : Disc with given name already exists!").build();
		}

		Disc d = discs.getDiscs().get(oldName);

		d.setName(newName);
		if (discType.equals("SSD"))
			d.setType(DiscType.SSD);
		else
			d.setType(DiscType.HDD);

		d.setCapacity(Integer.parseInt(capacity));

		d.setVmName(newVMname);

		// izmenjen u discs
		discs.getDiscs().remove(oldName);
		discs.getDiscs().put(newName, d);

		// izmenjen u org resources
		if(d.getOrganisation() != null) {
			Organisation o = orgs.getOrganisations().get(organisation);
			o.getResources().remove(oldName);
			o.getResources().add(newName);
			orgs.WriteToFile(ctx.getRealPath("."));
		}

		Logger l = new Logger("DiscService_editDisc.txt");
		l.append(d.toString());
		
		// izmenjen u vm
		HashMap<String, VM> vms = getVMs();
		
		if(vms.get(oldName) != null) {
			vms.get(oldVMname).getDiscs().remove(oldName);
		}
		if(newVMname != null) {
			if(!(newVMname.equals(""))) {
				vms.get(newVMname).getDiscs().add(newName);
				try {
					VMs sav = (VMs) ctx.getAttribute("vms");
					sav.WriteToFile(ctx.getRealPath("."));
				}catch (Exception e) {
					System.out.println("do nothing");
				}
			}
		}
		

		discs.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if (curr.getRole().equals(Role.SUPER_ADMIN)) {
			try {
				JSON = mapper.writeValueAsString(discs.getDiscs());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
		
		HashMap<String, Disc> available = new HashMap<String, Disc>();
		for (Disc d2 : discs.getDiscs().values()) {
			if (d2.getOrganisation().equals(curr.getOrganisation())) {
				available.put(d2.getName(), d2);
			}

		}
		try {
			JSON = mapper.writeValueAsString(available);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();
	}


	@POST
	@Path("/deleteDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteCategory(@FormParam("oldName") String oldName) {
		User curr = (User) request.getSession().getAttribute("currentUser");

		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (!(curr.getRole().equals(Role.SUPER_ADMIN))) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (oldName.equals("")) {
			return null;
		}

		Discs discs = (Discs) ctx.getAttribute("discs");

		Organisations orgs = (Organisations) ctx.getAttribute("organisations");

		Disc d = discs.getDiscs().get(oldName);

		// obrisan iz diskova
		discs.getDiscs().remove(oldName);
		discs.WriteToFile(ctx.getRealPath("."));

		// obrisan iz resources
		Organisation o = orgs.getOrganisations().get(d.getOrganisation());
		if(o != null){
			o.getResources().remove(oldName);
			orgs.WriteToFile(ctx.getRealPath("."));
		}

		// obrisan iz vm
		HashMap<String, VM> vms = getVMs();
		if(d.getVmName() != null) {
			if(!(d.getVmName().equals(""))) {
				if(vms.get(d.getVmName()) != null) {
					vms.get(d.getVmName()).getDiscs().remove(oldName);
					try {
						VMs sav = (VMs) ctx.getAttribute("vms");
						sav.WriteToFile(ctx.getRealPath("."));
					}catch (Exception e) {
						System.out.println("do nothing");
					}
				}
			}
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(discs.getDiscs());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();

	}

	@POST
	@Path("/getOrgFreeDiscs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> getOrgFreeDiscs(@FormParam("orgName") String orgName) {

		if (orgName.equals("")) {
			return null; // Response.status(400).entity("Error 400 : Organisation name can't empty
							// !").build();
		}
		Discs discs = (Discs) ctx.getAttribute("discs");

		HashMap<String, Disc> freeDiscs = new HashMap<String, Disc>();

		for (Disc d : discs.getDiscs().values()) {
			if (d.getOrganisation().equals(orgName) && d.getVmName() == null) {
				freeDiscs.put(d.getName(), d);
			}
		}

		return freeDiscs;

	}

	@POST
	@Path("/getAllVMDiscs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> getAllVMDiscs(@FormParam("vmName") String vmName) {
		if (vmName.equals("")) {
			return null;
		}
		Discs discs = (Discs) ctx.getAttribute("discs");

		HashMap<String, Disc> allDiscs = new HashMap<String, Disc>();

		for (Disc d : discs.getDiscs().values()) {
			if (d.getVmName() != null) {
				if (d.getVmName().equals(vmName))
					allDiscs.put(d.getName(), d);
			}
		}

		return allDiscs;

	}

}
