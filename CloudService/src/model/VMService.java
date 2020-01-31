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
	@Path("/addVM")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public HashMap<String, VM> addVM(VM vm){
		
		System.out.println("Uso");
		
		VMs vms = (VMs) ctx.getAttribute("vms");
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(vms.getVms().containsKey(vm.getName()))
			return null;
		
		vms.getVms().put(vm.getName(), vm);
		
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
		
		System.out.println(getVMs());
		return getVMs();
		
	}
	
}
