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


@Path("userServ")
public class UserService {
	
	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/loadAll")
	public void loadAll() {
		//load users and organisations
		Users users = (Users) ctx.getAttribute("users");
		
		System.out.println(users);
		if(users == null)
		{
			
			users = new Users(ctx.getRealPath("."));
			System.out.println(users);
			Organisations organisations = new Organisations(ctx.getRealPath("."));
			System.out.println(organisations);
			for(Organisation o : organisations.getOrganisations().values()) {
				System.out.println(o.getUsers());
				for(String u : o.getUsers()) {
					System.out.println("U: " + u);
					users.getUsers().get(u).setOrganisation(o);
				}
			}
			ctx.setAttribute("users", users);
			ctx.setAttribute("organisations", organisations);
			//System.out.println("========================================");
			//System.out.println(organisations);
			//System.out.println("========================================");
			//System.out.println(users);
		}
		
		//System.out.println("========================================");
		//load discs
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		if(discs == null)
		{
			discs = new Discs(ctx.getRealPath("."));
			ctx.setAttribute("discs", discs);
			//System.out.println(discs);
		}
		//load vms
		VMs vms = (VMs) ctx.getAttribute("vms");
		///System.out.println("========================================");
		if(vms == null)
		{
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
			System.out.println(vms);
		}
		//load cats
		VMCategories categories = (VMCategories) ctx.getAttribute("vmCategories");
		//System.out.println("========================================");
		if(categories == null)
		{
			categories = new VMCategories(ctx.getRealPath("."));
			ctx.setAttribute("vmCategories", categories);
			System.out.println(categories);
		}
		
	}
	
	@POST
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public User validate(@FormParam("username") String username, @FormParam("password") String password)
	{
		
		String[] args = {username, password};
		if(Validator.valEmpty(args)) {
			return null;
		}
		if(!username.equals("admin")){
			if(!Validator.valEmail(username)) {
				return null;
			}
		}
		HashMap<String, User> users = getUsers().getUsers();
				
		for(String email : users.keySet())
		{
			if(email.equals(username) && users.get(email).getPassword().equals(password))
			{
				request.getSession().setAttribute("currentUser", users.get(email));
				return users.get(email);
			}
		}
		return null;
	}

	private Users getUsers() {
		Users users = (Users) ctx.getAttribute("users");
		if(users == null)
		{
			users = new Users(ctx.getRealPath("."));
			Organisations organisations = new Organisations(ctx.getRealPath("."));
			for(Organisation o : organisations.getOrganisations().values()) {
				for(String u : o.getUsers()) {
					users.getUsers().get(u).setOrganisation(o);
				}
			}
			ctx.setAttribute("users", users);
			ctx.setAttribute("organisations", organisations);
		}
		
		return users;
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
				if(curr.getOrganisation().getName().equals(users.getUsers().get(email).getOrganisation().getName()))
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
		User user = new User(email,password,name,surname,organisations.getOrganisations().get(organisation),r);
		
		if(!users.getUsers().containsKey(email)) {
			organisations.getOrganisations().get(organisation).getUsers().add(email);
			users.getUsers().put(email, user);
			ctx.setAttribute("users", users);
			ctx.setAttribute("organisatons", organisations);
			
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
