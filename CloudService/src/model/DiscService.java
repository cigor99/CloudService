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
			for(Resource r : o.getResources().values()) {
				if(discs.getDiscs().containsKey(r.getName())) {
					available.put(r.getName(), (Disc) r);
				}
			}
			return available;
		}
		
	}
	
	
	@POST
	@Path("/addDisc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Disc> addVMCategory(@FormParam("name") String name, @FormParam("discType") String discType, @FormParam("capacity") String capacity, @FormParam("vmName") String vmName)
	{
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(discs.getDiscs().containsKey(name))
			return null;
		/* OVO TI GARANT NE TREBA SAMO SI GLUP
		boolean exists = true;
		if(!vmName.equals("")) {
			exists = false;
			VMs vms = (VMs) ctx.getAttribute("vms");
			for(VM vm : vms.getVms().values()) {
				if(vm.getName().equals(vmName)) {
					exists = true;
				}
			}
		}
		if(!exists)
			return null;
		
		*/
		
		DiscType dt = DiscType.HDD;
		if(discType.equals("SSD"))
			dt = DiscType.SSD;
		
		Disc disc = new Disc(name, dt, Integer.parseInt(capacity), vmName);
		discs.getDiscs().put(name, disc);
		discs.WriteToFile(ctx.getRealPath("."));
		
		return discs.getDiscs();
	}
	
	
}
