/**
 * 
 */
package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	private Organisations getOrganisations() {
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		if (organisations == null) {
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
	public Response listOrganisations() {

		User user = (User) request.getSession().getAttribute("currentUser");

		if (user == null)
			return Response.status(400).entity("Error 403 : Access denied !").build();

		if (!(user.getRole().equals(Role.SUPER_ADMIN)))
			return Response.status(400).entity("Error 403 : Access denied !").build();

		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(getOrganisations().getOrganisations());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return Response.ok(JSON).build();
	}

	// Returns organisation for current user
	@GET
	@Path("/getMyOrg")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMyOrganisation() {
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		if (organisations == null) {
			return Response.status(500).entity("Error 500 : Internal server error !").build();
		}
		User current = (User) request.getSession().getAttribute("currentUser");
		if (current == null) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if(!(current.getRole().equals(Role.ADMIN))) {
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		for (Organisation org : organisations.getOrganisations().values()) {
			if (current.getOrganisation().equals(org.getName())) {
				ObjectMapper mapper = new ObjectMapper();
				String JSON = "";
				try {
					JSON = mapper.writeValueAsString(org);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return Response.ok(JSON).build();
			}
		}
		return Response.status(400).entity("Error 400 : Organisation not found!").build();
	}

	private String makeLogoPath(String fakePath) {
		String[] token = fakePath.split("fakepath");
		String path = "data" + "/" + "logos";
		if (token.length == 1)
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
	public Response addNewOrg(@FormParam("name") String name, @FormParam("description") String description,
			@FormParam("logo") String logo) {

		User user = (User) request.getSession().getAttribute("currentUser");

		if (user == null)
			return Response.status(400).entity("Error 403 : Access denied !").build();

		if (!(user.getRole().equals(Role.SUPER_ADMIN)))
			return Response.status(400).entity("Error 403 : Access denied !").build();

		String[] args = { name};

		if (Validator.valEmpty(args)) {
			Response.status(400).entity("Error 400 : Login parameter/s empty").build();
		}

		String JSON = "";
		ObjectMapper mapper = new ObjectMapper();

		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		Organisation org = new Organisation(name, description, makeLogoPath(logo), new ArrayList<String>(),
				new ArrayList<String>());
		if (!organisations.getOrganisations().containsKey(name)) {
			organisations.getOrganisations().put(name, org);
			organisations.WriteToFile(ctx.getRealPath("."));
			//ctx.setAttribute("organisations", organisations);

			try {
				JSON = mapper.writeValueAsString(organisations.getOrganisations());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return Response.ok(JSON).build();
		}

		return Response.status(400).entity("Error 400 : Organisation with given name already exists!").build();

	}

	// Returns HashMap of all organisations
	// Sets new parameters for selected organisation
	@POST
	@Path("/editOrg")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response editOrganisation(@FormParam("oldName") String oldName, @FormParam("name") String name,
			@FormParam("description") String description, @FormParam("logo") String logo) {
		Organisations organisations = (Organisations) ctx.getAttribute("organisations");
		//Logger l = new Logger();
		User user = (User) request.getSession().getAttribute("currentUser");
		//l.append(user.toString());
		if(user.getRole().equals(Role.USER)){
			return Response.status(400).entity("Error 403 : Access denied !").build();
		}
		if (name.equals("")) {
			return Response.status(400).entity("Error 400 : Name not entered !").build();
		}

		if (!oldName.equals(name)) {
			if (organisations.getOrganisations().containsKey(name)) {
				return Response.status(400).entity("Error 400 : Organisation with given name already exists!").build();
			}
		}

		Organisation org = organisations.getOrganisations().get(oldName);
		//l.append(org.toString());
		
		organisations.getOrganisations().remove(oldName);

		org.setName(name);
		org.setDescription(description);
		org.setLogo(makeLogoPath(logo));
		//l.append(org.toString());
		VMs vms = getVMs();
		//l.append(vms.toString());
		
		for (VM value : vms.getVms().values()) {
			if (value.getOrganisation().equals(oldName)) {
				value.setOrganisation(name);
			}
		}

		Users users = getUsers();
		//l.append(users.toString());
		
		for (User value : users.getUsers().values()) {
			try {
				if (value.getOrganisation().equals(oldName)) {
					value.setOrganisation(name);
				}
			}catch(NullPointerException e) {
				continue;
			}
		}

		Discs discs = getDiscs();
		for (Disc value : discs.getDiscs().values()) {
			if (value.getOrganisation().equals(oldName)) {
				value.setOrganisation(name);
			}
		}

		organisations.getOrganisations().put(name, org);
		//ctx.setAttribute("organisations", organisations);
		//ctx.setAttribute("users", users);
		//ctx.setAttribute("discs", discs);
		//ctx.setAttribute("vms", vms);

		organisations.WriteToFile(ctx.getRealPath("."));
		//l.append(organisations.toString());
		ObjectMapper mapper = new ObjectMapper();
		String JSON = "";
		try {
			JSON = mapper.writeValueAsString(organisations.getOrganisations());
			//l.append(JSON);
			//l.logAll();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Response.ok(JSON).build();
	}
	
	@POST
	@Path("/getMonthlyBill")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMonthlyBill(@FormParam("startDate") String start, @FormParam("endDate") String end)
	{
		DateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate;
		Date endDate;
		
		try {
			startDate = form.parse(start);
			endDate = form.parse(end);
		} catch (ParseException e1) {
			return Response.status(400).entity("Error 400 : Dates not formatted correctly !").build();
		}
		
		User curr = (User) request.getSession().getAttribute("currentUser");
		
		if(curr == null) {
			return Response.status(403).entity("Error 403 : Access denied!").build();
		}
		
		
		if(!curr.getRole().equals(Role.ADMIN)) {
			return Response.status(403).entity("Error 403 : Access denied !").build();
		}
		
		Organisations orgs = (Organisations) ctx.getAttribute("organisations");
		
		Organisation myOrganisation = orgs.getOrganisations().get(curr.getOrganisation());
		
		VMs vms = (VMs) ctx.getAttribute("vms");
		Discs discs = (Discs) ctx.getAttribute("discs");
		
		BillWrap billWrap = new BillWrap();
		
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		
		double totalBill = 0.0;
		
		for(String r : myOrganisation.getResources()) {
			if(vms.getVms().containsKey(r)) {
				//racunanje
				VM vm = vms.getVms().get(r);
				//cena po satu
				Double hourPrice = vm.getNumCPUCores()*0.03472 + vm.getRamCapacity()*0.021 + vm.getNumGPUCores()*0.00139;
				
				//koliko sati je bila aktivna 
				long hoursActive = 0;
				for(Activity a : vm.getActivityList()) {
					//zavrsena je aktivnost
					if(!a.getOffTime().equals("")) {
						Date onDate = null;
						try {
							onDate = formatter.parse(a.getOnTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						Date offDate = null;
						try {
							offDate = formatter.parse(a.getOffTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						//dati Evi
						if(onDate.after(startDate) && offDate.before(endDate)) {
							double diff = Math.ceil((offDate.getTime()-onDate.getTime()) / (60*60*1000));
							hoursActive += diff;
						}
						
					}
				}
				double vmTotal = Math.round((hourPrice*hoursActive)*100.0)/100.0;
				totalBill += vmTotal;
				billWrap.getVmBills().put(r,vmTotal);
			}else {
				double discDays = (endDate.getTime() - startDate.getTime())/(1000*60*60*24);
				double discPrice = 0;
				Disc d = discs.getDiscs().get(r);
				if(d.getType().equals(DiscType.HDD)) {
					discPrice = discDays * 0.003 * d.getCapacity();
				}else
				{
					discPrice = discDays * 0.01 * d.getCapacity();
				}
				totalBill += discPrice;
				billWrap.getDiscBills().put(r,discPrice);
			}
		}
		
		totalBill = Math.round(totalBill*100.0)/100.0;
		
		billWrap.setTotalBill(totalBill);
		
		String JSON = "";
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JSON = mapper.writeValueAsString(billWrap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return Response.ok(JSON).build();
	}

	@GET
	@Path("/listOrganisationsUnsafe")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Organisation> listOrganisationsUnsafe() {
		return getOrganisations().getOrganisations();
	}
	
	private VMs getVMs() {
		VMs vms = (VMs) ctx.getAttribute("vms");
		if (vms == null) {
			vms = new VMs(ctx.getRealPath("."));
			ctx.setAttribute("vms", vms);
		}
		return vms;
	}

	private Discs getDiscs() {
		Discs discs = (Discs) ctx.getAttribute("dics");
		if (discs == null) {
			discs = new Discs(ctx.getRealPath("."));
			ctx.setAttribute("discs", discs);
		}
		return discs;
	}

	private Users getUsers() {
		Users users = (Users) ctx.getAttribute("users");
		if (users == null) {
			users = new Users(ctx.getRealPath("."));
			ctx.setAttribute("users", users);
		}
		return users;
	}
}
