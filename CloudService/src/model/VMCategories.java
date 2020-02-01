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
	
	public void WriteToFile(String realPath) {
		String sep = File.separator;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer1 = null;
		try {
			writer1 = new FileWriter(realPath + sep+ "data"+ sep + "categories.json");
		} catch (IOException e) {
			Logger.log("error");
			e.printStackTrace();
		}
		gson.toJson(this.vmCategories, writer1);
		
		try {
			writer1.flush();
		} catch (IOException e) {
			Logger.log("error");
			e.printStackTrace();
		}
		try {
			writer1.close();
		} catch (IOException e) {
			Logger.log("error");
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		String line = "==============================\n";
		for(VMCategory d : vmCategories.values()) {
			line += d.toString() + "\n";
		}
		return line+"============================\n";
	}
}
