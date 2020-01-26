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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * @author Ksenija
 *
 */
public class Users {
	private HashMap<String, User> users;
	
	public Users() {
		super();
		this.users = new HashMap<String, User>();
	}
	
	public Users(String realPath){
		String sep = File.separator;
		Gson gson = new Gson();
		HashMap<String, User> users = null;
		try {
			users = gson.fromJson(new FileReader(realPath + sep+ "data"+ sep + "users.json"), new TypeToken<HashMap<String, User>>(){}.getType());
		} catch (Exception e) {
			Logger.log("greska u usrima");
		}
		this.users = users;
	}

	public HashMap<String, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<String, User> users) {
		this.users = users;
	}
	
	public void WriteToFile(String realPath) {
		String sep = File.separator;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer1 = null;
		try {
			writer1 = new FileWriter(realPath + sep+ "data"+ sep + "users.json");
		} catch (IOException e) {
			Logger.log("error");
			e.printStackTrace();
		}
		gson.toJson(this.users, writer1);
		
		try {
			writer1.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Upisao");
	}

}
