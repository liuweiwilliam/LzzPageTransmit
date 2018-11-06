package com.lzz.pagetransmit.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class LzzProperties {
	private static String DomainName = "";
	private static String RawDomainName = "";
	private static String TransmitDomain = "";
	private static String ProjectName = "";
	
	public static String getDomainName(){
		if (DomainName == "") initDDProperties();
		return DomainName;
	}
	
	public static String getRawDomainName(){
		if (RawDomainName == "") initDDProperties();
		return RawDomainName;
	}
	
	public static String getTransmitDomain(){
		if (TransmitDomain == "") initDDProperties();
		return TransmitDomain;
	}
	
	public static String getProjectName(){
		if (ProjectName == "") initDDProperties();
		return ProjectName;
	}
	
	private static void initDDProperties() {
		// TODO Auto-generated method stub
		Properties properties = new Properties();
		String path = Thread.currentThread().getContextClassLoader().getResource("domain.properties").getPath();
		try {
			properties.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RawDomainName = properties.getProperty("RawDomainName");
		DomainName = properties.getProperty("DomainName");
		TransmitDomain = properties.getProperty("TransmitDomain");
		ProjectName = properties.getProperty("ProjectName");
	}

	
}
