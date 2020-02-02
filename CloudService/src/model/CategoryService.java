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

@Path("catServ")
public class CategoryService {

	@Context
	HttpServletRequest request;

	@Context
	ServletContext ctx;

	@GET
	@Path("/getCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategories() {
		User curr = (User) request.getSession().getAttribute("currentUser");

		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (!(curr.getRole().equals(Role.SUPER_ADMIN))) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}

		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");

		if (categories == null) {
			categories = new VMCategories(ctx.getRealPath("."));
			ctx.setAttribute("vmCategories", categories);
		}

		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(categories.getVmCategories());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();
	}

	@POST
	@Path("/addVMCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addVMCategory(@FormParam("name") String name, @FormParam("core") String core,
			@FormParam("ram") String ram, @FormParam("gpu") String gpu) {
		User curr = (User) request.getSession().getAttribute("currentUser");

		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (!(curr.getRole().equals(Role.SUPER_ADMIN))) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		String[] args = { name, core, ram, gpu };
		if (Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		String[] nums = { core, ram, gpu };
		if (!Validator.valNumber(nums)) {
			return Response.status(400)
					.entity("Error 400 : You must enter a number for cpu/gpu cores and ram capacity !").build();
		}
		String[] pos = { core, ram };
		if (!Validator.valPositive(pos)) {
			return Response.status(400).entity("Error 400 : Number of CPU cores/Ram capacity must be greater than 0 !")
					.build();
		}

		if (Integer.parseInt(gpu) < 0) {
			return Response.status(400).entity("Error 400 : Number of GPU cores must be equal or greater than 0 !")
					.build();
		}

		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");

		if (categories.getVmCategories().containsKey(name))
			return Response.status(400).entity("Error 400 : Category with given name already exists!").build();

		VMCategory vmc = new VMCategory(name, Integer.parseInt(core), Integer.parseInt(ram), Integer.parseInt(gpu));

		categories.getVmCategories().put(name, vmc);
		ctx.setAttribute("vmCategories", categories);
		categories.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(categories.getVmCategories());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();

	}

	@POST
	@Path("/editVMCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response editVMCategory(@FormParam("oldName") String oldName, @FormParam("newName") String newName,
			@FormParam("core") String core, @FormParam("ram") String ram, @FormParam("gpu") String gpu) {
		//Logger l = new Logger("CategoryService_editVMCategory.txt");
		User curr = (User) request.getSession().getAttribute("currentUser");
		//l.append("NEWNAME: " +newName);
		if (curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (!(curr.getRole().equals(Role.SUPER_ADMIN))) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		String[] args = { oldName, newName, core, gpu, ram };
		if (Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		String[] nums = { core, ram, gpu };
		if (!Validator.valNumber(nums)) {
			return Response.status(400)
					.entity("Error 400 : You must enter a number for cpu/gpu cores and ram capacity !").build();
		}
		String[] pos = { core, ram };
		if (!Validator.valPositive(pos)) {
			return Response.status(400).entity("Error 400 : Number of CPU cores/Ram capacity must be greater than 0 !")
					.build();
		}
		if (Integer.parseInt(gpu) < 0) {
			return Response.status(400).entity("Error 400 : Number of GPU cores must be equal or greater than 0 !")
					.build();
		}
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");

		if (!newName.equals(oldName)) {
			if (categories.getVmCategories().containsKey(newName))
				return Response.status(400).entity("Error 400 : Category with given name already exists!").build();
		}
		
		VMCategory vmc = categories.getVmCategories().get(oldName);
		//l.append("PRE SETOVANJA, GET IZ KATEGORIJE: " + vmc.toString());
		//l.preciseLog(l.getLine());
		vmc.setName(newName); ///OVDE NULL POINTER
		vmc.setNumCPUCores(Integer.parseInt(core));
		vmc.setNumGPUCores(Integer.parseInt(gpu));
		vmc.setRamCapacity(Integer.parseInt(ram));
		categories.getVmCategories().remove(oldName);
		categories.getVmCategories().put(newName, vmc);
		//l.append("POSLE SETOVANJA: " + vmc.toString());
		//l.preciseLog(l.getLine());
		//ctx.setAttribute("vmCategories", categories);
		
		// podesavanje nove kategorije svim virtuelnim masinama koje je imaju
		VMs vms = (VMs)ctx.getAttribute("vms");
		for (VM vm : vms.getVms().values()) {
			if (vm.getCategory().equals(oldName)) {
				vm.setNumCPUCores(vmc.getNumCPUCores());
				vm.setRamCapacity(vmc.getRamCapacity());
				vm.setNumGPUCores(vmc.getNumGPUCores());
			}
		}
		
		categories.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(categories.getVmCategories());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();

	}

	private HashMap<String, VM> getVMs() {
		VMs vms = (VMs) ctx.getAttribute("vms");

		if (vms == null) {
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}

		User curr = (User) request.getSession().getAttribute("currentUser");

		if (curr.getRole().equals(Role.SUPER_ADMIN))
			return vms.getVms();

		Organisations organs = (Organisations) ctx.getAttribute("organisations");

		HashMap<String, VM> orgVM = new HashMap<String, VM>();
		for (String r : organs.getOrganisations().get(curr.getOrganisation()).getResources()) {
			if (vms.getVms().containsKey(r))
				orgVM.put(r, vms.getVms().get(r));
		}

		return orgVM;
	}

	@POST
	@Path("/deleteCategory")
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
			return Response.status(400).entity("Error 400 : Category name can't be empty !").build();
		}
		//Logger l = new Logger("CategoryService_deletecategory.txt");
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		//l.append("Categories iz kontexta\n" + categories);
		VMCategory cat = categories.getVmCategories().get(oldName);
		//l.append("Kategorija: \n" + cat);
		HashMap<String, VM> vms = getVMs();
		//l.append("VMS IZ GETVMS: " + vms);
		
		//l.preciseLog(l.getLine());
		
		for (VM vm : vms.values()) { 
			//l.append("VM IZ FORA" + vm);
			//l.preciseLog(l.getLine());
			if (vm.getCategory().equals(cat.getName())) ///OVDE NULL POINTER
				return Response.status(400).entity("Error 400 : Category is bound to VM, can't be deleted!").build();
		}
		categories.getVmCategories().remove(oldName);
		ctx.setAttribute("vmCategories", categories);
		categories.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(categories.getVmCategories());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();

	}

	@POST
	@Path("/getCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public VMCategory getCategory(@FormParam("catName") String catName) {
		if (catName.equals("")) {
			return null;
		}
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");

		return categories.getVmCategories().get(catName);

	}
	

	@GET
	@Path("/getCategoriesUnsafe")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, VMCategory> getCategoriesUnsafe() {
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");

		if (categories == null) {
			categories = new VMCategories(ctx.getRealPath("."));
			ctx.setAttribute("vmCategories", categories);
		}
		return categories.getVmCategories();
	}
}
