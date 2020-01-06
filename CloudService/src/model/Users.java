/**
 * 
 */
package model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ksenija
 *
 */
public class Users {
	private HashMap<String, User> users;

	public Users() {
		super();
	}

	public Users(String realPath) throws JsonParseException, JsonMappingException, IOException {
		String sep = File.separator;
		ObjectMapper objectMapper = new ObjectMapper();
		HashMap read = objectMapper.readValue(new File(realPath + sep + "data" + sep + "users.json"), HashMap.class);
		HashMap<String, User> putInUsers = new HashMap<String, User>();
		for(Object o : read.keySet())
		{
			LinkedHashMap lhm = (LinkedHashMap)read.get(o);
			String jsonString = objectMapper.writeValueAsString(lhm);
			User u = objectMapper.readValue(jsonString, User.class);
			putInUsers.put(u.getEmail(),u);
		}
		
		this.users = putInUsers;
	}

	public HashMap<String, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<String, User> users) {
		this.users = users;
	}
}
