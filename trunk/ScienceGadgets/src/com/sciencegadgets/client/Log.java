package com.sciencegadgets.client;

import java.util.logging.Logger;

public class Log {

	private static Logger log = Logger.getLogger("logger");
	
	public static void info(String msg){
		log.info(msg);
	}
	public static void severe(String msg){
		log.severe(msg);
	}
	
}
