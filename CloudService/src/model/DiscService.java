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

@Path("discServ")
public class DiscService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/getDiscs")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Disc> getDiscs()
	{
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(discs == null)
		{
			discs = new Discs(ctx.getRealPath("."));
			ctx.setAttribute("discs", discs);
		}
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr.getRole().equals(Role.SUPER_ADMIN))
			return discs.getDiscs();
		else {
			HashMap<String, Disc> available = new HashMap<String, Disc>();
			Organisation o = curr.getOrganisation();
			for(Disc d : discs.getDiscs().values()) {
				if(d.getOrganisation().equals(o.getName())) {
					available.put(d.getName(), d);
				}
					
			}
			return available;
		}
	}

	
	@GET
	@Path("/getVMs")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, VM> getVMs()
	{
		VMs vms = (VMs) ctx.getAttribute("vms");
		
		if(vms == null)
		{
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}
		
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		if(curr.getRole().equals(Role.SUPER_ADMIN))
			return vms.getVms();
		
		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for(String r : curr.getOrganisation().getResources()) {
			if(vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}
		
		return orgVM;
	}
	
	@POST
	@Path("/addDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> addDisc(@FormParam("name") String name, @FormParam("discType") String discType, @FormParam("capacity") String capacity, @FormParam("vmName") String vmName)
	{
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(discs.getDiscs().containsKey(name))
			return null;
		
		DiscType dt = DiscType.HDD;
		if(discType.equals("SSD"))
			dt = DiscType.SSD;
		
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		Disc disc = null;
		if(curr.getRole().equals(Role.SUPER_ADMIN))
			disc = new Disc(name, null, dt, Integer.parseInt(capacity), vmName);
		else {
			disc = new Disc(name, curr.getOrganisation().getName(), dt, Integer.parseInt(capacity), vmName);
			curr.getOrganisation().getResources().add(name);
		}
		
		discs.getDiscs().put(name, disc);
		
		if(curr.getRole().equals(Role.SUPER_ADMIN))
			return discs.getDiscs();
		
		HashMap<String, VM> vms = getVMs();
		
		vms.get(vmName).getDiscs().add(name);
		
		return getDiscs();
	}
	
	@POST
	@Path("/editDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> editVMCategory(@FormParam("oldName") String oldName, @FormParam("newName") String newName, @FormParam("discType") String discType,@FormParam("capacity") String capacity,@FormParam("oldVMname") String oldVMname,@FormParam("newVMname") String newVMname)
	{
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		Organisations orgs = (Organisations) ctx.getAttribute("organisations");
		
		if(!newName.equals(oldName))
		{
			if(discs.getDiscs().containsKey(newName))
				return null;
		}
		
		Disc d = discs.getDiscs().get(oldName);
		
		d.setName(newName);
		if(discType.equals("SSD"))
			d.setType(DiscType.SSD);
		else
			d.setType(DiscType.HDD);
		
		d.setCapacity(Integer.parseInt(capacity));
		
		d.setVmName(newName);
		
		//izmenjen u discs
		discs.getDiscs().remove(oldName);
		discs.getDiscs().put(newName, d);
		
		//izmenjen u org resources
		Organisation o = orgs.getOrganisations().get(d.getOrganisation());
		o.getResources().remove(oldName);
		o.getResources().add(newName);
		
		//izmenjen u vm 
		HashMap<String, VM> vms = getVMs();
		vms.get(oldVMname).getDiscs().remove(oldName);
		vms.get(newVMname).getDiscs().add(newName);
		
		return discs.getDiscs();
		
	}
	
	@POST
	@Path("/deleteDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> deleteCategory(@FormParam("oldName") String oldName)
	{
		
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		Organisations orgs = (Organisations) ctx.getAttribute("organisations");
		
		Disc d = discs.getDiscs().get(oldName);
		
		//obrisan iz diskova
		discs.getDiscs().remove(oldName);
		
		//obrisan iz resources
		Organisation o = orgs.getOrganisations().get(d.getOrganisation());
		o.getResources().remove(oldName);
		
		//obrisan iz vm
		HashMap<String, VM> vms = getVMs();
		vms.get(d.getVmName()).getDiscs().remove(oldName);
		
		
		return discs.getDiscs();
		
	}
	
	@POST
	@Path("/getOrgFreeDiscs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> getOrgFreeDiscs(@FormParam("orgName") String orgName)
	{
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		HashMap<String, Disc> freeDiscs = new HashMap<String, Disc>();
		
		for(Disc d : discs.getDiscs().values()) {
			if(d.getOrganisation().equals(orgName) && d.getVmName()==null) {
				freeDiscs.put(d.getName(), d);
			}
		}
		
		
		return freeDiscs;
		
	}
	
	@POST
	@Path("/getAllVMDiscs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> getAllVMDiscs(@FormParam("vmName") String vmName)
	{
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		HashMap<String, Disc> allDiscs = new HashMap<String, Disc>();
		
		for(Disc d : discs.getDiscs().values()) {
			if(d.getVmName() != null) {
				if(d.getVmName().equals(vmName))
					allDiscs.put(d.getName(), d);
			}
		}
		
		
		return allDiscs;
		
	}
	
	
}
