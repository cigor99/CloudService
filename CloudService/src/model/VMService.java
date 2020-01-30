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

@Path("vmServ")
public class VMService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
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
	@Path("/as")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Disc> getDiscsOfOrganisation(){
		Discs discs = (Discs) ctx.getAttribute("discs");
		User user = (User) request.getSession().getAttribute("currentUser");
		String name = user.getOrganisation().getName();
		if(discs == null)
		{
			discs = new Discs(ctx.getRealPath("."));
			ctx.setAttribute("discs", discs);
		}
		Logger logger = new Logger();
		logger.append(discs.toString());
		logger.append(user.toString());
		
		HashMap<String, Disc> ds = new HashMap<String, Disc>();
		for(Disc d : discs.getDiscs().values()) {
			if(d.getOrganisation().getName().equals(name)) {
				ds.put(name, d);
			}
		}
		logger.append(ds.toString());
		logger.logAll();
		return ds;
	}
	
}
