/**
 * 
 */
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

/**
 * @author Igor
 *
 */

@Path("orgServ")
public class OrganisationService {

	@Context
	HttpServletRequest request;
	
	@Context
	ServletContext ctx;

	
	
	// Returns instance of class organisations which has a map of all organisations
	// Organisations will be loaded from file if previously weren't
	private Organisations getOrganisations(){
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		if(organisations == null) {
			organisations = new Organisations(ctx.getRealPath("."));
			ctx.setAttribute("organisations", organisations);
		}
		
		return organisations;
	}
	
	// Returns HashMap of all organisations
	// Organisations will be loaded form file if previously weren't
	
	// MOZDA DA IZBACIM OVO I DA KORISTIM SAMO GORNJU FUNKCIJU ZA SVE
	@GET
	@Path("/listOrganisations")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Organisation> listOrganisations(){
		return getOrganisations().getOrganisations();
	}
	
	// Returns HashMap of organisations
	// Not working================================================
	@POST
	@Path("addNewOrg")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Organisation> addNewOrg(@FormParam("name") String name, @FormParam("description") String description, @FormParam("logo") String logo){
		Users users = (Users) ctx.getAttribute("users");
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		
		
		return null;
	}
}

