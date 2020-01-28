package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class VMCategories {
	private HashMap<String, VMCategory> vmCategories;

	public VMCategories() {
		super();
	}
	
	public VMCategories(String realPath) {
		String sep = File.separator;
		Gson gson = new Gson();
		HashMap<String, VMCategory> categories = null;
		
		try {
			categories = gson.fromJson(new FileReader(realPath + sep + "data" + sep + "categories.json"), new TypeToken<HashMap<String, VMCategory>>(){}.getType());
		} catch (Exception e) {
			Logger.log("greska u categories");
		}
		
		this.vmCategories = categories;
		
	}
	public HashMap<String, VMCategory> getVmCategories() {
		return vmCategories;
	}

	public void setVmCategories(HashMap<String, VMCategory> vmCategories) {
		this.vmCategories = vmCategories;
	}
	
}
