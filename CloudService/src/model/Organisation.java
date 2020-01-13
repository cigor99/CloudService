/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Igor
 *
 */
public class Organisation {
	// unique, mandatory
	private String name;
	private String description;
	// use default if not given
	private String logo;

	private ArrayList<String> users;
	private HashMap<String, Resource> resources;

	public Organisation(String name, String description, String logo, ArrayList<String> users,
			HashMap<String, Resource> resources) {
		super();
		this.name = name;
		this.description = description;
		this.logo = logo;
		this.users = users;
		this.resources = resources;
	}

	public Organisation() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Organisation other = (Organisation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public ArrayList<String> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<String> users) {
		this.users = users;
	}

	public HashMap<String, Resource> getResources() {
		return resources;
	}

	public void setResources(HashMap<String, Resource> resources) {
		this.resources = resources;
	}

}
