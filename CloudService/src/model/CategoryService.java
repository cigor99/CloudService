package model;

import java.util.HashMap;
import java.util.Locale.Category;

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

@Path("catServ")
public class CategoryService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/getCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, VMCategory> getCategories()
	{
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		
		if(categories == null)
		{
			categories = new VMCategories(ctx.getRealPath("."));
			ctx.setAttribute("vmCategories", categories);
		}
		
		return categories.getVmCategories();
	}
	
	@POST
	@Path("/addVMCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, VMCategory> addVMCategory(@FormParam("name") String name, @FormParam("core") String core,@FormParam("ram") String ram,@FormParam("gpu") String gpu)
	{
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		
		if(categories.getVmCategories().containsKey(name))
			return null;
		
		VMCategory vmc = new VMCategory(name, Integer.parseInt(core), Integer.parseInt(ram), Integer.parseInt(gpu));
		
		categories.getVmCategories().put(name, vmc);
		
		return categories.getVmCategories();
		
	}
	
	@POST
	@Path("/editVMCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, VMCategory> editVMCategory(@FormParam("oldName") String oldName, @FormParam("newName") String newName, @FormParam("core") String core,@FormParam("ram") String ram,@FormParam("gpu") String gpu)
	{
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		
		if(!newName.equals(oldName))
		{
			if(categories.getVmCategories().containsKey(newName))
				return null;
		}
		
		VMCategory vmc = categories.getVmCategories().get(oldName);
		
		vmc.setName(newName);
		vmc.setNumCPUCores(Integer.parseInt(core));
		vmc.setNumGPUCores(Integer.parseInt(gpu));
		vmc.setRamCapacity(Integer.parseInt(ram));
		
		
		return categories.getVmCategories();
		
	}
	
	private HashMap<String, VM> getVMs()
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
	@Path("/deleteCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, VMCategory> deleteCategory(@FormParam("oldName") String oldName)
	{
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		
		VMCategory cat = categories.getVmCategories().get(oldName);
		
		HashMap<String, VM> vms = getVMs();
		for(VM vm: vms.values()) {
			if(vm.getCategory().getName().equals(cat.getName()))
				return null;
		}
		categories.getVmCategories().remove(oldName);
		
		return categories.getVmCategories();
		
	}
	
	@POST
	@Path("/getCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public VMCategory getCategory(@FormParam("catName") String catName)
	{
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		
		return categories.getVmCategories().get(catName);
		
	}
}
