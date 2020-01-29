/**
 * 
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import com.google.gson.Gson;
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
	
	
}
