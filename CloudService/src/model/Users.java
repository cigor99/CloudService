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
		Logger.log(users.get("email3").toString());
		this.users = users;
	}

	public HashMap<String, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<String, User> users) {
		this.users = users;
	}

}
