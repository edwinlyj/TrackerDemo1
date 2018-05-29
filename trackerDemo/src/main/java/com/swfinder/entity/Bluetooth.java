package com.swfinder.entity;

import com.sw.sdk.Essential;

public class Bluetooth extends Essential{

	String name; //Bluetooth Name
	String address; //Bluetooth Mac Address
	String toux; //Bluetooth path

	public String getToux() {
		return toux;
	}

	public void setToux(String toux) {
		this.toux = toux;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
