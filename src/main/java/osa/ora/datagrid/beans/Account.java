package osa.ora.datagrid.beans;

import java.io.Serializable;

public class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	int id;
	String name;
	String phone;
	int active;

	public Account(int id, String name, String phone, int active) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.active = active;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

}
