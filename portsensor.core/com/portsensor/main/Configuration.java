package com.portsensor.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.portsensor.sensor.ConsoleSensor;
import com.portsensor.sensor.SensorCheck;

public class Configuration {
	private ArrayList<ConsoleSensor> sensors = new ArrayList<ConsoleSensor>();
	private HashMap<String,String> settings = new HashMap<String,String>();
	static private Configuration instance = null;
	
	static public final String SETTING_POST_URL = "post_url"; 
	static public final String SETTING_USERNAME = "username"; 
	static public final String SETTING_SECRET_KEY = "secret_key"; 
	
	private Configuration() {}
	
	static public Configuration getInstance() {
		if(null == Configuration.instance) {
			Configuration.instance = new Configuration();
		}
		return Configuration.instance;
	}
	
	@SuppressWarnings("unchecked")
	public boolean load(String cfgFile) {
		Document doc = this.getConfigFileAsXml(cfgFile);
		Element eRoot = doc.getRootElement();
	    Iterator<Element> i;
	    
	    // Settings
	    List<Element> eSettings = eRoot.getChild("settings").getChildren();
	    i = eSettings.iterator();
	    while(i.hasNext()) {
	    	Element eSetting = (Element) i.next();
	    	settings.put(eSetting.getName(), eSetting.getTextTrim());
	    }
	    
    	// Sensors
	    List<Element> eSensors = eRoot.getChildren("sensor");
	    Iterator<Element> j = eSensors.iterator();
	    while(j.hasNext()) {
	    	Element eSensor = (Element) j.next();
	    	
	    	// Validate and normalize datatype
	    	String eType = eSensor.getChildText("type");
	    	String sType = ConsoleSensor.TYPE_TEXT; // default
	    	
	    	if(null != eType) {
		    	if(eType.equalsIgnoreCase("number")) {
		    		sType = ConsoleSensor.TYPE_NUMBER;
		    	} else if(eType.equalsIgnoreCase("percent")) {
	    			sType = ConsoleSensor.TYPE_PERCENT;
		    	} else if(eType.equalsIgnoreCase("decimal")) {
		    		sType = ConsoleSensor.TYPE_DECIMAL;
		    	} else if(eType.equalsIgnoreCase("updown")) {
		    		sType = ConsoleSensor.TYPE_UPDOWN;
		    	}
	    	}
	    	
	    	ConsoleSensor sensor = new ConsoleSensor(
    			eSensor.getAttributeValue("id"),
    			eSensor.getChildText("name"),
    			eSensor.getChildText("command"),
    			sType
	    	);
	    	
	    	List<SensorCheck> checks = new ArrayList<SensorCheck>();
	    	
	    	// Warnings
	    	List<Element> eWarnings = eSensor.getChildren("warning");
	    	Iterator<Element> k = eWarnings.iterator();
	    	
	    	while(k.hasNext()) {
	    		Element eWarning = (Element) k.next();
	    		checks.add(new SensorCheck(
	    			SensorCheck.STATUS_WARNING,
	    			eWarning.getAttributeValue("oper"),
	    			eWarning.getAttributeValue("value"),
	    			eWarning.getText()
	    		));
	    	}
	    	
	    	// Criticals
	    	List<Element> eCriticals = eSensor.getChildren("critical");
	    	k = eCriticals.iterator();
	    	
	    	while(k.hasNext()) {
	    		Element eCritical = (Element) k.next();
	    		checks.add(new SensorCheck(
		    			SensorCheck.STATUS_CRITICAL,
		    			eCritical.getAttributeValue("oper"),
		    			eCritical.getAttributeValue("value"),
		    			eCritical.getText()
		    		));
	    	}
	    	
	    	sensor.setChecks(checks);
	    	this.sensors.add(sensor);
	    }
	    
	    return true;
	}

	public String getSetting(String key, String defaultValue) {
		if(this.settings.containsKey(key)) {
			return this.settings.get(key);
		}
		
		return (null != defaultValue) ? defaultValue : null;
	}
	
	public ArrayList<ConsoleSensor> getSensors() {
		return sensors;
	}

	private Document getConfigFileAsXml(String cfgFile) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		
		try {
			doc = builder.build(cfgFile);
		} catch (JDOMException e) {
	        System.err.println(cfgFile + " is not well-formed.");
	        System.err.println(e.getMessage());
	        return null;
	        
	    } catch (IOException e) {
	        System.err.println("Could not check " + cfgFile);
	        System.err.println(" because " + e.getMessage());
	        return null;
		}
	    
	    return doc; // Configuration
	}
}
