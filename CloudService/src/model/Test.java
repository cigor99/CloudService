package model;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Test {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		/*
		ObjectMapper mapper = new ObjectMapper();
		
		
		ArrayList<String> users = new ArrayList<String>();
		users.add("email1");
		
		Organisation org = new Organisation("Organisation1", "This is an organisation", "path to logo", users, new HashMap<String, Resource>());
		
		users.add("email2");
		
		Organisation org2 = new Organisation("Organisation2", "This is an organisation", "path to logo", users, new HashMap<String, Resource>());

		HashMap<String, Organisation> mapa = new HashMap<String, Organisation>();
		mapa.put(org.getName(), org);
		mapa.put(org2.getName(), org);
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File("org"), mapa);
		
		*/
		
		Gson gson = new Gson();
		HashMap<String, User> users = gson.fromJson(new FileReader("C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\webapps\\CloudService\\data\\users.json"), new TypeToken<HashMap<String, User>>(){}.getType());
		Logger.log(users.get("email3").toString());
		for(String s : users.keySet()) {
			System.out.println(s);
			System.out.println(users.get(s));
			System.out.println("======================================");
		}
		
	}

}
