/**
 * 
 */
package model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	
	public void WriteToFile(String realPath) {
		String sep = File.separator;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer = null;
		try {
			writer = new FileWriter(realPath + sep + "data" + sep + "organisations.json");
		} catch (IOException e) {
			Logger.log("Error");
			e.printStackTrace();
		}
		gson.toJson(this.organisations, writer);
		
		try {
			writer.flush();
		} catch (IOException e) {
			Logger.log("Error");
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			Logger.log("Error");
			e.printStackTrace();
		}
		System.out.println("Upisao");
	}

	@Override
	public String toString() {
		String line = "==============================\n";
		for(Organisation o : organisations.values()) {
			line += o.toString() + "\n";
		}
		return line+"============================\n";
	}
	
	
	
	
}
