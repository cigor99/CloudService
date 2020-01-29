/**
 * 
 */
package model;

import java.io.File;
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
	@GET
	@Path("/listOrganisations")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Organisation> listOrganisations(){
		return getOrganisations().getOrganisations();
	}
	
	// Returns organisation for current user
	@GET
	@Path("/getMyOrg")
	@Produces(MediaType.APPLICATION_JSON)
	public Organisation getMyOrganisation() {
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		User current = (User) request.getSession().getAttribute("currentUser");
		for(Organisation org : organisations.getOrganisations().values()) {
			if(current.getOrganisation().equals(org)) {
				return org;
			}
		}
		return null;
	}
	
	private String makeLogoPath(String fakePath) {
		String sep = File.separator;
		String[] token = fakePath.split("fakepath");
		String path = "data" + "/" + "logos";
		if(token.length==1)
			return path + "/" + "default.jpg";
		else
			return path + token[1];
	}
	
	
	// Returns HashMap of all organisations
	// Creates and adds new organisation to the map
	@POST
	@Path("addNewOrg")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Organisation> addNewOrg(@FormParam("name") String name, @FormParam("description") String description, @FormParam("logo") String logo){
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		Organisation org = new Organisation(name, description, makeLogoPath(logo), new ArrayList<String>(),new  ArrayList<String>());
		if(!organisations.getOrganisations().containsKey(name)) {
			organisations.getOrganisations().put(name, org);
			ctx.setAttribute("organisations", organisations);

			organisations.WriteToFile(ctx.getRealPath("."));
			return organisations.getOrganisations();
		}
		return null;
		
	}
	
	// Returns HashMap of all organisations
	// Sets new parameters for selected organisation
	@POST
	@Path("/editOrg")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public HashMap<String, Organisation> editOrganisation(@FormParam("name") String name, @FormParam("description") String description, @FormParam("logo") String logo){
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		if(!organisations.getOrganisations().containsKey(name)) {
			return null;
		}
		Organisation org = organisations.getOrganisations().get(name);
		org.setName(name);
		org.setDescription(description);
		org.setLogo( makeLogoPath(logo));

		organisations.getOrganisations().put(name, org);
		ctx.setAttribute("organisations", organisations);

		organisations.WriteToFile(ctx.getRealPath("."));
		return organisations.getOrganisations();
	}

}

