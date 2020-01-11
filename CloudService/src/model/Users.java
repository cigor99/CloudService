/**
 * 
 */
package model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
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
	
	public Users(String realPath) throws JsonParseException, JsonMappingException, IOException {
		String sep = File.separator;
		Gson gson = new Gson();
		HashMap<String, User> users = gson.fromJson(new FileReader(realPath + sep+ "data"+ sep + "users.json"), new TypeToken<HashMap<String, User>>(){}.getType());
		this.users = users;
	}

	public HashMap<String, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<String, User> users) {
		this.users = users;
	}

}
