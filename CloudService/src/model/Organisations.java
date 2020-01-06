/**
 * 
 */
package model;

import java.util.HashMap;

/**
 * @author Ksenija
 *
 */
public class Organisations {

	private HashMap<String, Organisation> organisations;
	
	public Organisations() {
		super();
	}

	public HashMap<String, Organisation> getOrganisations() {
		return organisations;
	}

	public void setOrganisations(HashMap<String, Organisation> organisations) {
		this.organisations = organisations;
	}
	
	
}
