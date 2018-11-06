package com.lzz.pagetransmit.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.xml.sax.SAXException;

public class LzzFilterUtil {
	
	/**
	 * 判断一个请求是否是基础资源请求,包含“图片”、“css资源”、“字体资源”
	 * @param request_url 请求的链接
	 * @return 是否是基础资源
	 */
	public static boolean isBaseResource(String request_url){
		try {
			boolean rslt = LzzReadtransmitConf.isBaseResource(request_url);
			return rslt;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * 翻译本地请求地址和实际请求地址
	 * @param request_url 本地请求地址
	 * @param request_url2 
	 * @return 实际请求地址
	 */
	public static String replaceRequestUrl(String referer, String request_url) {
		// TODO Auto-generated method stub
		try {
			String rslt = LzzReadtransmitConf.replaceRequestUrl(referer, request_url);
			return rslt;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static JSONObject getRequestParameters(HttpServletRequest request){
		List<String> par_names = new ArrayList<String>();
		List<String> par_values = new ArrayList<String>();
		
		JSONObject rslt = new JSONObject();
		
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
            String name = (String)names.nextElement();//调用nextElement方法获得元素
            String value = request.getParameter(name);
            par_names.add(name);
            par_values.add(value);
		}
		
		rslt.put("par_names", par_names);
		rslt.put("par_values", par_values);
		
		return rslt;
	}
	
	private static List<String> getRequestNameOrValue(String type, HttpServletRequest request){
		JSONObject rslt = getRequestParameters(request);
		List<String> rslt_list = new ArrayList<String>();
		JSONArray array = rslt.getJSONArray(type);
		
		for(int i=0; i<array.size(); i++){
			rslt_list.add(array.get(i).toString());
		}
		
		return rslt_list;
	}
	
	public static List<String> getRequestParameterNames(HttpServletRequest request){
		return getRequestNameOrValue("par_names", request);
	}
	
	public static List<String> getRequestParameterValues(HttpServletRequest request){
		return getRequestNameOrValue("par_values", request);
	}
	
	public static String constructUrl(String url, HttpServletRequest request){
		List<String> par_names = new ArrayList<String>();
		List<String> par_values = new ArrayList<String>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
            String name = (String)names.nextElement();//调用nextElement方法获得元素
            String value = request.getParameter(name);
            par_names.add(name);
            par_values.add(value);
		}
		if (par_names != null) {
			for (int i=0; i<par_names.size(); i++) {
				if (i==0) { url += "?"; } else { url += "&"; }
				url += (par_names.get(i)+"="+par_values.get(i));
			}
		}
		
		return url;
	}

	public static String handleResponseContents(String referer, String real_request_url, String response_contents) {
		// TODO Auto-generated method stub
		return LzzReadtransmitConf.handleResponseContents(referer, real_request_url, response_contents);
	}

	public static boolean isJSRequest(String request_url) {
		// TODO Auto-generated method stub
		if(null==request_url
				|| request_url.equals("")){
			return false;
		}
		
		return request_url.contains("js") && !request_url.contains("jsp");
	}
}
