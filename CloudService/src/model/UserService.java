package model;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Path("userServ")
public class UserService {
	
	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;
	
	@POST
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public User validate(@FormParam("username") String username, @FormParam("password") String password)
	{
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
		Users users = (Users)ctx.getAttribute("users");
		return users.getUsers();
	}
	
	@GET
	@Path("/redirect")
	@Produces(MediaType.APPLICATION_JSON)
	public User getCurrentUser() {
		User usr = (User) request.getSession().getAttribute("currentUser");
		return usr;
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
		
		Organisations organisations = (Organisations)ctx.getAttribute("organisations");
		Users users = (Users)ctx.getAttribute("users");
		Role r = Role.ADMIN;
		if(role == "User") {
			r = Role.USER;
		}
		User user = new User(email,password,name,surname,organisations.getOrganisations().get(organisation),r);
		
		if(!users.getUsers().containsKey(email)) {
			organisations.getOrganisations().get(organisation).getUsers().add(email);
			users.getUsers().put(email, user);
			ctx.setAttribute("users", users);
			ctx.setAttribute("organisatons", organisations);
			return users.getUsers();
		}
		
		return null;
	}
}
