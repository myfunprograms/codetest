/** Represent the state */
package codetest.domain;

public enum StateCode {
	ACTIVE("Active"),
	INACTIVE("Inactive");
	
	private String code;
	
	private StateCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static StateCode getByCode(String code) {
		for (StateCode v : values()) {
			if (v.getCode().equals(code))
				return v;
		}
	
		return null;
	}
}
