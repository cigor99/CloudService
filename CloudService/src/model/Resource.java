/**
 * 
 */
package model;

/**
 * @author Igor
 *
 */
public abstract class Resource {
	// mandatory
	private String name;
	
	private String organisation;

	public Resource() {
	}
	
	
	public Resource(String name, String organisation) {
		super();
		this.name = name;
		this.organisation = organisation;
	}

	@Override
	public String toString() {
		return "Resource [name=" + name + ", organisation=" + organisation + "]";
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public Resource(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
