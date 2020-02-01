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


@Path("userServ")
public class UserService {
	
	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/loadAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadAll() {
		//load user
		Users users = (Users) ctx.getAttribute("users");
		if(users == null)
		{
			users = new Users(ctx.getRealPath("."));
			User superAdmin = new User();
			superAdmin.setEmail("super_admin");
			superAdmin.setName("Super Admin");
			superAdmin.setOrganisation(null);
			superAdmin.setPassword("super_admin");
			superAdmin.setRole(Role.SUPER_ADMIN);
			superAdmin.setSurname("Super Admin");
			users.getUsers().put(superAdmin.getEmail(), superAdmin);
			ctx.setAttribute("users", users);
		}
		
		//load organisations
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		if(organisations == null) {
			organisations = new Organisations(ctx.getRealPath("."));
			ctx.setAttribute("organisations", organisations);
		}
		
		
		//load discs
		Discs discs = (Discs) ctx.getAttribute("discs");
		if(discs == null)
		{
			discs = new Discs(ctx.getRealPath("."));
			ctx.setAttribute("discs", discs);

		}
		
		
		//load vms
		VMs vms = (VMs) ctx.getAttribute("vms");
		if(vms == null)
		{
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}
		
		
		//load cats
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		if(categories == null)
		{
			categories = new VMCategories(ctx.getRealPath("."));
			ctx.setAttribute("vmCategories", categories);
		}
		
		return Response.ok().build();
		
	}
	
	@POST
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validate(@FormParam("email") String email, @FormParam("password") String password)
	{
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		
		String[] args = {email, password};
		if(Validator.valEmpty(args)) {
			Response.status(400).entity("Error 400 : Login parameter/s empty").build();
		}
		
		Users users = (Users) ctx.getAttribute("users");
		if(users == null) {
			loadAll();
		}
		users = (Users) ctx.getAttribute("users");
		for(String e : users.getUsers().keySet())
		{
			if(e.equals(email) && users.getUsers().get(e).getPassword().equals(password))
			{
				request.getSession().setAttribute("currentUser", users.getUsers().get(e));
				try {
					json = mapper.writeValueAsString(users.getUsers().get(e));
				} catch (JsonProcessingException e1) {
					e1.printStackTrace();
				}
				return Response.ok(json).build();
			}
		}
		
		return Response.status(400).entity("Error 400 : User with given email and password not found!").build();
	}

	@GET
	@Path("/getUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listUsers(){
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		
		if(curr.getRole().equals(Role.USER)) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		
		Users users = (Users)ctx.getAttribute("users");
		if(users == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		System.out.println("GETUSERS: ROLE:" + curr.getRole());
		if(curr.getRole().equals(Role.SUPER_ADMIN))
		{
			try {
				System.out.println(users);
				JSON = mapper.writeValueAsString(users.getUsers());
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}else {
			System.out.println("GETUSERS ADMIN");
			HashMap<String, User> orgUsers = new HashMap<String, User>();
			for(String email : users.getUsers().keySet()) {
				if(users.getUsers().get(email).getRole().equals(Role.SUPER_ADMIN))
				{
					continue;
				}
				if(curr.getOrganisation().equals(users.getUsers().get(email).getOrganisation()))
				{
					orgUsers.put(email,users.getUsers().get(email));
				}
			}
			try {
				JSON = mapper.writeValueAsString(orgUsers);
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
		
	}
	
	@GET
	@Path("/getCurrentUser")
	@Produces(MediaType.APPLICATION_JSON)
	public User getCurrentUser() {
		User current = (User) request.getSession().getAttribute("currentUser");
		return current;
	}
	
	@GET
	@Path("/logOut")
	public void logOut()
	{
		request.getSession().setAttribute("currentUser", null);
		request.getSession().invalidate();
	}
	
	@POST
	@Path("/addNewUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addNewUser(@FormParam("email") String email,@FormParam("password") String password,@FormParam("name") String name,@FormParam("surname") String surname,@FormParam("role") String role,@FormParam("organisation") String organisation){
		
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(curr.getRole()==Role.USER) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		String[] args = {email, password, name, organisation, surname, role};
		if(Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if(!Validator.valEmail(email)) {
			return Response.status(400).entity("Error 400 : Invalid email entry !").build();
		}
		if(!Validator.valRole(role)) {
			return Response.status(400).entity("Error 400 : Invalid role entry !").build();
		}
		Organisations organisations = (Organisations)ctx.getAttribute("organisations");
		if(organisation == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		String usersOrg = curr.getOrganisation();
		if(!(usersOrg == null)) {
			if(!usersOrg.equals(organisation)) {
				return Response.status(400).entity("Error 400 : You can only add users to your organisation !").build();
			}
		}
		Users users = (Users)ctx.getAttribute("users");
		if(users == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		Role r = Role.ADMIN;
		if(role.equals("User")) {
			r = Role.USER;
		}
		User user = new User(email,password,name,surname,organisation,r);
		if(!users.getUsers().containsKey(email)) {
			organisations.getOrganisations().get(organisation).getUsers().add(email);
			users.getUsers().put(email, user);
			
			ctx.setAttribute("users", users);
			ctx.setAttribute("organisations", organisations);
			users.WriteToFile(ctx.getRealPath("."));
			organisations.WriteToFile(ctx.getRealPath("."));
			
			ObjectMapper mapper = new ObjectMapper();
			String JSON = "";
			if(curr.getRole().equals(Role.SUPER_ADMIN))
			{
				try {
					JSON = mapper.writeValueAsString(users.getUsers());
				}catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return Response.ok(JSON).build();
			}else {
				HashMap<String, User> orgUsers = new HashMap<String, User>();
				for(String s : users.getUsers().keySet()) {
					if(users.getUsers().get(s).getRole().equals(Role.SUPER_ADMIN))
					{
						continue;
					}
					if(curr.getOrganisation().equals(users.getUsers().get(s).getOrganisation()))
					{
						orgUsers.put(s,users.getUsers().get(s));
					}
				}
				try {
					JSON = mapper.writeValueAsString(orgUsers);
				}catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return Response.ok(JSON).build();
			}

		}
		
		return Response.status(400).entity("Error 400 : User with given email already exists!").build();
	}
	
	@POST
	@Path("/editUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response editUser(@FormParam("email") String email,@FormParam("password") String password,@FormParam("name") String name,@FormParam("surname") String surname,@FormParam("role") String role){
	
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(curr.getRole()==Role.USER) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		
		String[] args = {email, password, name, surname, role};
		if(Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if(!Validator.valEmail(email)) {
			return Response.status(400).entity("Error 400 : Invalid email entry !").build();
		}
		
		if(!Validator.valRole(role)) {
			return Response.status(400).entity("Error 400 : Invalid role entry !").build();
		}
		Users users = (Users)ctx.getAttribute("users");
		if(users == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		Role r = Role.ADMIN;
		if(role.equals("User")) {
			r = Role.USER;
		}
		User user = users.getUsers().get(email);
		if(user == null) {
			return Response.status(400).entity("Error 400 : User with given email not found!").build();
		}
		String usersOrg = curr.getOrganisation();
		
		if(!(usersOrg == null)) {
			if(!usersOrg.equals(user.getOrganisation())) {
				return Response.status(400).entity("Error 400 : You can only edit users of your organisation !").build();
			}
		}
		user.setName(name);
		user.setSurname(surname);
		user.setPassword(password);
		user.setRole(r);
		
		System.out.println(user);
		
		users.getUsers().put(email, user);
		
		ctx.setAttribute("users", users);
		
		users.WriteToFile(ctx.getRealPath("."));
		
		
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if(curr.getRole().equals(Role.SUPER_ADMIN))
		{
			try {
				JSON = mapper.writeValueAsString(users.getUsers());
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}else {
			HashMap<String, User> orgUsers = new HashMap<String, User>();
			for(String s : users.getUsers().keySet()) {
				if(users.getUsers().get(s).getRole().equals(Role.SUPER_ADMIN))
				{
					continue;
				}
				if(curr.getOrganisation().equals(users.getUsers().get(s).getOrganisation()))
				{
					orgUsers.put(s,users.getUsers().get(s));
				}
			}
			try {
				JSON = mapper.writeValueAsString(orgUsers);
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
	}
	
	@POST
	@Path("/updateProfile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateProfile(@FormParam("oldEmail") String oldEmail, @FormParam("email") String email,@FormParam("password") String password, @FormParam("name") String name,@FormParam("surname") String surname){		
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		String[] args = {email, password, name, surname};
		if(Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if(!Validator.valEmail(email)) {
			return Response.status(400).entity("Error 400 : Invalid email entry !").build();
		}
		Users users = (Users)ctx.getAttribute("users");
		if(users==null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		
		User user = users.getUsers().get(oldEmail);
		if(user == null) {
			return Response.status(400).entity("Error 400 : User with given email not found!").build();
		}
		
		if(!oldEmail.equals(email)) {
			for(String u : users.getUsers().keySet()) {
				if(u.equals(email)) {
					return Response.status(400).entity("Error 400 : User with given email already exists !").build();
				}
			}
		}
		user.setName(name);
		user.setSurname(surname);
		user.setPassword(password);
		user.setEmail(email);
		
		users.getUsers().remove(oldEmail);
		users.getUsers().put(email, user);
		
		ctx.setAttribute("users", users);
		request.getSession().setAttribute("currentUser", user);
		users.WriteToFile(ctx.getRealPath("."));
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(user);
		}catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		return Response.ok(JSON).build();
	}
	
	@POST
	@Path("/deleteUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteUser(@FormParam("email") String email,@FormParam("organisation") String organisation)
	{
		User curr = (User) request.getSession().getAttribute("currentUser");
		if(curr == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(curr.getEmail().equals(email)) {
			return Response.status(400).entity("Error 400 : You can't delete yourself !").build();
		}
		
		String[] args = {email, organisation};
		if(Validator.valEmpty(args)) {
			return Response.status(400).entity("Error 400 : One or more parameters empty !").build();
		}
		if(!Validator.valEmail(email)) {
			return Response.status(400).entity("Error 400 : Invalid email entry !").build();
		}
		Users users = (Users)ctx.getAttribute("users");
		if(users == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		if(organisations == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		Organisation org = organisations.getOrganisations().get(organisation);
		if(org == null) {
			return Response.status(400).entity("Error 400 : Organisation with given name not found !").build();
		}
		users.getUsers().remove(email);
		org.getUsers().remove(email);
		ctx.setAttribute("users", users);
		ctx.setAttribute("organisations", organisations);
		
		users.WriteToFile(ctx.getRealPath("."));
		organisations.WriteToFile(ctx.getRealPath("."));


		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		if(curr.getRole().equals(Role.SUPER_ADMIN))
		{
			try {
				JSON = mapper.writeValueAsString(users.getUsers());
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}else {
			HashMap<String, User> orgUsers = new HashMap<String, User>();
			for(String s : users.getUsers().keySet()) {
				if(users.getUsers().get(s).getRole().equals(Role.SUPER_ADMIN))
				{
					continue;
				}
				if(curr.getOrganisation().equals(users.getUsers().get(s).getOrganisation()))
				{
					orgUsers.put(s,users.getUsers().get(s));
				}
			}
			try {
				JSON = mapper.writeValueAsString(orgUsers);
			}catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}
	}
	
}
