package com.portsensor.command;

import java.io.IOException;


/**
 * This factory will return a Command instance using the proper 
 * command interpreter for the local OS.
 * 
 * @author Jeff Standen <jeff@webgroupmedia.com>
 *
 */
public class CommandFactory {
	private CommandFactory() {
	}
	
	/**
	 * Returns a Command instance for more control over input/output.
	 * 
	 * @param String command
	 * @return Command
	 */
	static public Command run(String command) {
		String[] cmd = CommandFactory.getPlatformCommand(command);
		
		Process process = null;
		CommandRunner runner = null;
		
		if(null == cmd)
			return null;

		try {
			process = Runtime.getRuntime().exec(cmd);
			
			runner = new CommandRunner(process);
			Thread t = new Thread(runner);
			t.start();
			
			Long startTime = System.currentTimeMillis();
			while(t.isAlive()) {
				t.join(50); // wait
				Long dur = System.currentTimeMillis() - startTime;
				if(dur >= 10000) { // [JAS]: [TODO] Make configurable
					t.interrupt();
					t.join();
				}
				
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(InterruptedException ie) {
		}
		
		return runner.getCommand();
	}
	
	/**
	 * Run 'command' and just return output.  This automatically handles 
	 * the input/output process. 
	 * 
	 * @param String command
	 * @return String
	 */
	static public String quickRun(String command) {
		Command cmd = CommandFactory.run(command);
		
		if(null != cmd) {
			String output = cmd.getOutput();
			cmd.stop();
			return output;
		}
		
		return new String();
	}
	
	/**
	 * Builds an executable string by determining the proper command 
	 * interpreter for the local OS.
	 * 
	 * @param String command
	 * @return String[]
	 */
	static private String[] getPlatformCommand(String command) {
		String os = System.getProperty("os.name");

		if(os.equalsIgnoreCase("Linux")
				|| os.equalsIgnoreCase("Solaris")
				|| os.equalsIgnoreCase("SunOS")
				|| os.equalsIgnoreCase("FreeBSD")
				) {
			return new String[] {"/bin/bash", "-c", command};	
			
		} else if(os.equalsIgnoreCase("Mac OS X")) {
			return new String[] {"/bin/bash", "-c", command};
			
		} else if(os.equalsIgnoreCase("Windows 98")) {
			return new String[] {"command.com", "/C", command};
			
		} else if(os.equalsIgnoreCase("Windows NT")
				|| os.equalsIgnoreCase("Windows 2000")
				|| os.equalsIgnoreCase("Windows XP")
				|| os.equalsIgnoreCase("Windows 2003")
				|| os.equalsIgnoreCase("Windows Vista")
				) {
			return new String[] {"cmd.exe", "/C", command};
			
		} else {
			System.out.println("OS not currently supported: " + os);
			System.exit(0);
			
		}
		
		return null;
	}
};
