package com.lzz.pagetransmit.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class LzzSendRequest {
	public static String sendByGet(String url, List<String> par_names, List<String> par_values, HttpServletRequest request, HttpServletResponse response)
	{
		String response_str = "";
		
		BufferedReader in = null;
		
		String complete_url = url;
		
		if(null==par_names) par_names = new ArrayList<String>();
		if(null==par_values) par_values = new ArrayList<String>();
		
		for(int i=0; i<par_names.size(); i++)
		{
			if(i==0)
			{
				complete_url += "?";
			}
			else
			{
				complete_url += "&";
			}
			complete_url += par_names.get(i);
			complete_url += "=";
			complete_url += par_values.get(i);
		}
		
		//System.out.println("complete_url:" + complete_url);
		try{
			URL realUrl = new URL(complete_url);
			// 打开和URL之间的链接
			URLConnection connection = realUrl.openConnection();
			
			// 设置通用的请求属性
			setRequestProperties(connection, request);
			
	        // 建立实际的链接
	        connection.connect();
	        
	        // 获取响应头字段
	        Map<String, List<String>> map = connection.getHeaderFields();
	        
	        setResponseCookie(map, response);
	        
	        // 定义 BufferedReader输入流来读取URL的相应
	        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
	
	        String line;
	        while ((line = in.readLine()) != null) 
	        {
	        	response_str += line + "\n";
	        }
	        
	        if(response_str.length()>0) response_str = response_str.substring(0, response_str.length()-1);
		}catch(Exception e){
			System.out.println("发送get请求异常" + e);
			response_str = "ERROR";
            e.printStackTrace();
		}finally{
			try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
		}
		
		return response_str;
	}
	
	private static void setRequestProperties(URLConnection connection,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

	}

	public static String sendByPost(String url, List<String> par_names, List<String> par_values, String contents, HttpServletRequest request, HttpServletResponse response){
		
		String response_str = "";
		
		BufferedReader in = null;
		
		String complete_url = url;
		
		if(null==par_names) par_names = new ArrayList<String>();
		if(null==par_values) par_values = new ArrayList<String>();
		
		for(int i=0; i<par_names.size(); i++)
		{
			if(i==0)
			{
				complete_url += "?";
			}
			else
			{
				complete_url += "&";
			}
			complete_url += par_names.get(i);
			complete_url += "=";
			complete_url += par_values.get(i);
		}
		
		try{
			JSONObject json_obj = JSONObject.fromObject(contents);
            System.out.println(json_obj.toString());
            byte[] requestStringBytes = json_obj.toString().getBytes("utf-8");
            
			URL realUrl = new URL(complete_url);

			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			
			connection.setDoOutput(true); 
            connection.setDoInput(true);
			connection.setRequestMethod("POST"); 
			connection.setUseCaches(false); 
			connection.setRequestProperty("Content-length", "" + requestStringBytes.length);
			
			setRequestProperties(connection, request);
	
	        connection.connect();
	        
	        DataOutputStream out = new DataOutputStream(connection.getOutputStream()); 

            out.write(requestStringBytes);
            out.flush(); 
            out.close(); 
            
	        Map<String, List<String>> map = connection.getHeaderFields();
	        setResponseCookie(map, response);
	        
	        in = new BufferedReader(new InputStreamReader(
	                connection.getInputStream()));
	
	        String line;
	        while ((line = in.readLine()) != null) 
	        {
	            response_str += line + "\n";
	        }
	        if(response_str.length()>0) response_str = response_str.substring(0, response_str.length()-1);
	        System.out.println("post finish, response is : " + response_str);
	        
		}catch(Exception e){
			System.out.println("post请求失败" + e);
            e.printStackTrace();
		}finally{
			try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
		}
		
		return response_str;
	}
	
	private static void setResponseCookie(Map<String, List<String>> map, HttpServletResponse response){
		//set response header 
		response.reset();
		for (String key : map.keySet()){
			 //System.out.println(key + "--->" + map.get(key));
			 
			 if (null==key) continue;
			 
	         if (key.equals("Content-Length")){
	         	continue;
	         }else{
	         	if(key.equals("set-cookie")
	         			|| key.equals("Set-Cookie")){
	         		continue;
	         	}
	         
	         	response.addHeader(key, map.get(key).toString().replace("[", "").replace("]", ""));
	         }
		}
		 
		//set cookie
		List<String> cookieValue = map.get("Set-Cookie");
		if(null==cookieValue) cookieValue = map.get("set-cookie");
	        
		if (null!=cookieValue) {
			for (String coo : cookieValue) {
				String[] coo_arr = coo.split(";");
 				Cookie cookie1 = null;
 				for (int i=0; i<coo_arr.length; i++) {
 					String[] coo_par = coo_arr[i].split("=");
 					if (null==cookie1) {
 						cookie1 = new Cookie(coo_par[0], coo_par[1]);
 						
 						continue;
 					}
 					if (coo_par.length==2) {
 						if (coo_par[0].equals(" Expires")) cookie1.setMaxAge(30*60);
 						if (coo_par[0].equals(" Domain")) cookie1.setDomain(LzzProperties.getRawDomainName());
 						if (coo_par[0].equals(" Path")) cookie1.setPath(coo_par[1]);
 						continue;
 					} else {
 						cookie1.setHttpOnly(true);
 					}
 					
 					response.addCookie(cookie1);
 				}
 			}
         }
	}
}
