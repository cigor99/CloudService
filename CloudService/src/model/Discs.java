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
public class Discs {
	
	private HashMap<String, Disc> discs;

	public Discs() {
	}
	
	public Discs(String realPath) {
		String sep = File.separator;
		Gson gson = new Gson();
		HashMap<String, Disc> discs = null;
		
		try {
			discs = gson.fromJson(new FileReader(realPath + sep + "data" + sep + "discs.json"), new TypeToken<HashMap<String, Disc>>(){}.getType());
		} catch (Exception e) {
			Logger.log("greska u discs");
		}
		this.discs = discs;
	}
	
	public void WriteToFile(String realPath) {
		String sep = File.separator;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer1 = null;
		try {
			writer1 = new FileWriter(realPath + sep+ "data"+ sep + "discs.json");
		} catch (IOException e) {
			Logger.log("error");
			e.printStackTrace();
		}
		gson.toJson(this.discs, writer1);
		
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

		System.out.println("Upisao");
	}

	public HashMap<String, Disc> getDiscs() {
		return discs;
	}

	public void setDiscs(HashMap<String, Disc> discs) {
		this.discs = discs;
	}

	@Override
	public String toString() {
		String line = "==============================\n";
		for(Disc d : discs.values()) {
			line += d.toString() + "\n";
		}
		return line+"============================\n";
	}
	
	
	
	
}
