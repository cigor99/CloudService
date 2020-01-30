/**
 * 
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * @author Ksenija
 *
 */
public class VMs {

	private HashMap<String, VM> vms;

	public VMs() {
		super();
	}
	
	public VMs(String realPath)
	{
		String sep = File.separator;
		Gson gson = new Gson();
		HashMap<String, VM> vms = null;
		
		try {
			vms = gson.fromJson(new FileReader(realPath + sep + "data" + sep + "vms.json"), new TypeToken<HashMap<String, VM>>(){}.getType());
		} catch (Exception e) {
			Logger.log("Greska u vms");
		}
				
		this.vms = vms;
	}

	public HashMap<String, VM> getVms() {
		return vms;
	}

	public void setVms(HashMap<String, VM> vms) {
		this.vms = vms;
	}
	
	
	public void WriteToFile(String realPath) {
		String sep = File.separator;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer = null;
		try {
			writer = new FileWriter(realPath + sep + "data" + sep + "vms.json");
		} catch (IOException e) {
			Logger.log("Error");
			e.printStackTrace();
		}
		gson.toJson(this.vms, writer);
		
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
	}
	
	@Override
	public String toString() {
		String line = "==============================\n";
		for(VM v : vms.values()) {
			line += v.toString() + "\n";
		}
		return line+"============================\n";
	}
	
}
