package model;

import java.io.IOException;
import java.util.ArrayList;

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
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public User validate(@FormParam("username") String username, @FormParam("password") String password) throws JsonParseException, JsonMappingException, IOException
	{
		Users users = getUsers();
		/*
		for(String email : users.getUsers().keySet())
		{
			if(email.equals(username) && users.getUsers().get(email).getPassword().equals(password))
			{
				request.getSession().setAttribute("currentUser", users.getUsers().get(email));
				return users.getUsers().get(email);
			}
		}
		*/
		for(User u : users.getUsers()) {
			if(u.getEmail().equals(username) && u.getPassword().equals(password)) {
				request.getSession().setAttribute("currentUser", u);
				return u;
			}
		}
		return null;
	}

	private Users getUsers() throws JsonParseException, JsonMappingException, IOException {
		Users users = (Users) ctx.getAttribute("users");
		if(users == null)
		{
			users = new Users(ctx.getRealPath("."));
			ctx.setAttribute("users", users);
		}
		
		return users;
	}
	
	@GET
	@Path("/getUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> listUsers() throws JsonParseException, JsonMappingException, IOException {
		Users users = (Users)ctx.getAttribute("users");
		return users.getUsers();
	}
}
