package com.lzz.pagetransmit.action;

public class XtlUtil {
	public static String getPar(String rslt, String par){
		int index = rslt.indexOf(par + "\":\"") + (par + "\":\"").length();
		int index2 = rslt.indexOf("\"", index);
		
		System.out.println(rslt.substring(index, index2));
		
		return rslt.substring(index, index2);
	}
}	
