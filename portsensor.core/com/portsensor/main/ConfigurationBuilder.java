package com.portsensor.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.portsensor.sensor.PortProfile;
import com.portsensor.testers.PortTester;

public class ConfigurationBuilder {
	
	public static void autoConfigure() {
		String inConfigFile = "";
		String inUrl = "";
		String inMonitorId = "";
		String inMonitorPass = "";
		
		Element eRoot = new Element("configuration");
		Document doc = new Document(eRoot);
		
		Element eSettings = new Element("settings");
		eRoot.addContent(eSettings);
		
		System.out.print("Config File Name [config.xml]: ");
		inConfigFile = readLine();
		if(0 == inConfigFile.length()) inConfigFile = "config.xml";
		
		System.out.print("PortSensor URL: ");
		inUrl = readLine();
		eSettings.addContent(new Element("portal_url").setText(inUrl));
		
		System.out.print("Monitor ID: ");
		inMonitorId = readLine();
		eSettings.addContent(new Element("username").setText(inMonitorId));
		
		System.out.print("Monitor Password: ");
		inMonitorPass = readLine();
		eSettings.addContent(new Element("secret_key").setText(inMonitorPass));
		
		// [TODO] Detect Linux?  Use load/disk/etc
		
		// Detect common ports
		Boolean bAddAnother = true;

		while(bAddAnother) {
			String inDeviceId = "";
			String inHostname = "";
			System.out.print("Add Device ID: ");
			inDeviceId = readLine();
	
			System.out.print("Device hostname [localhost]: ");
			inHostname = readLine();
			if(0 == inHostname.length()) inHostname = "localhost";
			
			eRoot.addContent(new org.jdom.Comment(inHostname));
			Element eDevice = new Element("device");
			eDevice.setAttribute("id", inDeviceId);
			eRoot.addContent(eDevice);
			
			System.out.println("Detecting ports, please wait...");
			List<PortProfile> detectedPorts = detectPorts(inHostname);
			Iterator<PortProfile> i = detectedPorts.iterator();
	
			while(i.hasNext()) {
				PortProfile port = i.next();
				String inUsePort = "";
				
				System.out.print("Monitor " + port.getName() + " (port " + port.getPort() + ")? [Y]/N: ");
				inUsePort = readLine();
				if(0 == inUsePort.length()) inUsePort = "Y";
				
				if(inUsePort.equalsIgnoreCase("y")) {
					Element eSensor = new Element("sensor");
					eDevice.addContent(eSensor);
					eSensor.addContent(new Element("name").setText(port.getName()));
					eSensor.addContent(new Element("command").setText("#PORT " + inHostname + " " + port.getPort()));
					eSensor.addContent(new Element("type").setText("text"));
					Element eCritical = new Element("critical"); 
					eSensor.addContent(eCritical);
					eCritical.setAttribute("oper", "eq");
					eCritical.setAttribute("value", "DOWN");
					eCritical.setText("Critical");
				}
			}
			
			System.out.println("Add another device? Y/[N]: ");
			String inAddAnother = readLine();
			bAddAnother = (0 == inAddAnother.length() || inAddAnother.equalsIgnoreCase("N"))
				? false : true;
		}
		
		ConfigurationBuilder.saveConfigXml(doc, inConfigFile);
	}
	
	public static void saveConfigXml(Document doc, String configFile) {
	    XMLOutputter outputter = new XMLOutputter();
	    outputter.setFormat(Format.getPrettyFormat());
//    	System.out.println(outputter.outputString(doc));
    	
    	// Write to config file
    	try {
    		FileWriter fw = new FileWriter(configFile);
    		outputter.output(doc, fw);
    		fw.close();
    		
    	} catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	
    	System.out.println("Configuration saved to " + configFile + " !");
	}
	
	private static List<PortProfile> detectPorts(String hostname) {
		List<PortProfile> ports = new ArrayList<PortProfile>();
		
		// FTP
		if(PortTester.testPort(hostname, 21))
			ports.add(new PortProfile("FTP", 21));
		
		// SSH
		if(PortTester.testPort(hostname, 22))
			ports.add(new PortProfile("SSH", 22));
		
		// Telnet
		if(PortTester.testPort(hostname, 23))
			ports.add(new PortProfile("Telnet", 23));
		
		// SMTP
		if(PortTester.testPort(hostname, 25))
			ports.add(new PortProfile("SMTP", 25));

		// DNS
		if(PortTester.testPort(hostname, 53))
			ports.add(new PortProfile("DNS", 53));
		
		// HTTP
		if(PortTester.testPort(hostname, 80))
			ports.add(new PortProfile("HTTP", 80));
		
		// POP3
		if(PortTester.testPort(hostname, 110))
			ports.add(new PortProfile("POP3", 110));

		// HTTP-SSL
		if(PortTester.testPort(hostname, 443))
			ports.add(new PortProfile("HTTP-SSL", 443));
		
		// IMAP-SSL
		if(PortTester.testPort(hostname, 993))
			ports.add(new PortProfile("IMAP-SSL", 993));
		
		// POP3-SSL
		if(PortTester.testPort(hostname, 995))
			ports.add(new PortProfile("POP3-SSL", 995));
		
		// MSSQL
		if(PortTester.testPort(hostname, 1433))
			ports.add(new PortProfile("MSSQL", 1433));
		
		// MySQL
		if(PortTester.testPort(hostname, 3306))
			ports.add(new PortProfile("MySQL", 3306));
		
		// PostgreSQL
		if(PortTester.testPort(hostname, 5432))
			ports.add(new PortProfile("PostgreSQL", 5432));
		
		return ports;
	}
	
	private static String readLine() {
		int ch;
		StringBuilder str = new StringBuilder();
		
		try {
			while (-1 != (ch = System.in.read())) {
				if(ch == '\r' || ch == '\n') {
					System.in.skip(System.in.available()); // flush
					return str.toString();
				}
				
				str.append((char)ch);
			}
			
		} catch(IOException ioe) {}
		
		return str.toString();
	}

}
