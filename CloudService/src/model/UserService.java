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
	HttpServletRequest response;
	
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
	public HashMap<String, User> listUsers(){
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		Users users = (Users)ctx.getAttribute("users");
		
		if(curr.getRole().equals(Role.SUPER_ADMIN))
		{
			return users.getUsers();
		}else {
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
			return orgUsers;
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
	public HashMap<String, User> addNewUser(@FormParam("email") String email,@FormParam("password") String password,@FormParam("name") String name,@FormParam("surname") String surname,@FormParam("role") String role,@FormParam("organisation") String organisation){
		String[] args = {email, password, name, organisation, surname, role};
		if(Validator.valEmpty(args)) {
			return null;
		}
		if(!Validator.valEmail(email)) {
			return null;
		}
		
		if(!Validator.valRole(role)) {
			return null;
		}
		Organisations organisations = (Organisations)ctx.getAttribute("organisations");
		Users users = (Users)ctx.getAttribute("users");
		Role r = Role.ADMIN;
		if(role == "USER") {
			r = Role.USER;
		}
		User user = new User(email,password,name,surname,organisation,r);
		
		if(!users.getUsers().containsKey(email)) {
			organisations.getOrganisations().get(organisation).getUsers().add(email);
			users.getUsers().put(email, user);
			
			users.WriteToFile(ctx.getRealPath("."));
			organisations.WriteToFile(ctx.getRealPath("."));
			return listUsers();
		}
		
		return null;
	}
	
	@POST
	@Path("/editUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, User> editUser(@FormParam("email") String email,@FormParam("password") String password,@FormParam("name") String name,@FormParam("surname") String surname,@FormParam("role") String role){
	
		String[] args = {email, password, name, surname, role};
		if(Validator.valEmpty(args)) {
			return null;
		}
		if(!Validator.valEmail(email)) {
			return null;
		}
		
		if(!Validator.valRole(role)) {
			return null;
		}
		Users users = (Users)ctx.getAttribute("users");
		Role r = Role.ADMIN;
		if(role == "User") {
			r = Role.USER;
		}
		
		User user = users.getUsers().get(email);
		user.setName(name);
		user.setSurname(surname);
		user.setPassword(password);
		user.setRole(r);
		
		System.out.println(user);
		
		users.getUsers().put(email, user);
		
		ctx.setAttribute("users", users);
		
		users.WriteToFile(ctx.getRealPath("."));
		return listUsers();
	}
	
	@POST
	@Path("/updateProfile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public User updateProfile(@FormParam("oldEmail") String oldEmail, @FormParam("email") String email,@FormParam("password") String password, @FormParam("name") String name,@FormParam("surname") String surname){		
		
		String[] args = {email, password, name, surname};
		if(Validator.valEmpty(args)) {
			return null;
		}
		if(!Validator.valEmail(email)) {
			return null;
		}
		Users users = (Users)ctx.getAttribute("users");
		
		User user = users.getUsers().get(oldEmail);
		
		
		if(!oldEmail.equals(email)) {
			for(String u : users.getUsers().keySet()) {
				if(u.equals(email)) {
					return null;
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
		return user;
	}
	
	@POST
	@Path("/deleteUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, User> deleteUser(@FormParam("email") String email,@FormParam("organisation") String organisation)
	{
		String[] args = {email, organisation};
		if(Validator.valEmpty(args)) {
			return null;
		}
		if(!Validator.valEmail(email)) {
			return null;
		}
		Users users = (Users)ctx.getAttribute("users");
		users.getUsers().remove(email);
		
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		
		Organisation org = organisations.getOrganisations().get(organisation);
		
		org.getUsers().remove(email);
		
		
		users.WriteToFile(ctx.getRealPath("."));
		organisations.WriteToFile(ctx.getRealPath("."));
		return listUsers();
	}
	
}
