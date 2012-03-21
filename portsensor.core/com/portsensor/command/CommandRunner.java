package com.portsensor.command;

import java.io.IOException;

public class CommandRunner implements Runnable {
	Process process = null;
	Command command = null;
	
	public CommandRunner(Process process) {
		this.process = process;
	}
	
	public void run() {
		try {
			while(0 != process.waitFor()) {
				if(process.getErrorStream().available() > 0
						|| process.getInputStream().available() > 0
						) {
					return;
				}
			}
			this.command = new Command(process);
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
			return;
			
		} catch(InterruptedException ie) {
//			System.out.println("Timed out on command: " + this.cmd);
			return;
		}
	}

	public Command getCommand() {
		return command;
	}

}
