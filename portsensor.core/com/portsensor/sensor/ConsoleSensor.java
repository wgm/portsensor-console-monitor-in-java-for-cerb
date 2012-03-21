package com.portsensor.sensor;

import java.util.ArrayList;
import java.util.List;

public class ConsoleSensor {
	private String id = "";
	private String name = "";
	private String command = "";
	private String type = ConsoleSensor.TYPE_TEXT;
	private List<SensorCheck> checks = new ArrayList<SensorCheck>();
	
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_NUMBER = "number";
	public static final String TYPE_PERCENT = "percent";
	public static final String TYPE_DECIMAL = "decimal";
	public static final String TYPE_UPDOWN = "updown";
	
	public ConsoleSensor(String id, String name, String command, String type) {
		this.setId(id);
		this.setName(name);
		this.setCommand(command);
		this.setType(type);
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<SensorCheck> getChecks() {
		return checks;
	}

	public void setChecks(List<SensorCheck> checks) {
		this.checks = checks;
	}
}
