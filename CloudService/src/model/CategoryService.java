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
		
		System.out.println(categories.getVmCategories());
		
		if(categories.getVmCategories().containsKey(name))
			return null;
		
		VMCategory vmc = new VMCategory(name, Integer.parseInt(core), Integer.parseInt(ram), Integer.parseInt(gpu));
		
		categories.getVmCategories().put(name, vmc);
		
		System.out.println(categories.getVmCategories());
		
		return categories.getVmCategories();
		
	}
	
}