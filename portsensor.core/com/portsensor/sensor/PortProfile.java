package com.portsensor.sensor;

public class PortProfile {
	String name;
	Integer port;
	
	public PortProfile(String name, Integer port) {
		this.name = name;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public Integer getPort() {
		return port;
	}
}
