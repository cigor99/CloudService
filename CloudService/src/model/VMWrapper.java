package model;

public class VMWrapper extends VM{
	
	private String oldName;
	private boolean checked;

	public VMWrapper() {
		super();
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
