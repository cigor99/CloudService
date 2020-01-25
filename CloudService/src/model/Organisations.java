/**
 * 
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * @author Ksenija
 *
 */
public class Organisations {

	private HashMap<String, Organisation> organisations;
	
	public Organisations() {
		super();
	}
	
	public Organisations(String realPath){
		String sep = File.separator;
		Gson gson = new Gson();
		HashMap<String, Organisation> organisations = null;
		try {
			organisations = gson.fromJson(new FileReader(realPath + sep+ "data"+ sep + "organisations.json"), new TypeToken<HashMap<String, Organisation>>(){}.getType());
		} catch (Exception e) {
			Logger.log("grska u organizacijama");
		}
			
		this.organisations = organisations;
	}

	public HashMap<String, Organisation> getOrganisations() {
		return organisations;
	}

	public void setOrganisations(HashMap<String, Organisation> organisations) {
		this.organisations = organisations;
	}
	
	
}
