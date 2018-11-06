package com.lzz.pagetransmit.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class LzzSendByPost {
	public static String sendByPost(String url, List<String> par_name, List<String> par_value, String contents)
	{
		String response = "";
		
		BufferedReader in = null;
//		System.out.println("get from " + url);
		
		String complete_url = url;
		assert par_name.size()==par_value.size();
		
		for(int i=0; i<par_name.size(); i++)
		{
			if(i==0)
			{
				complete_url += "?";
			}
			else
			{
				complete_url += "&";
			}
			complete_url += par_name.get(i);
			complete_url += "=";
			complete_url += par_value.get(i);
		}
		
//		System.out.println("complete_url:" + complete_url);
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
	        //connection.setInstanceFollowRedirects(true); 
			connection.setRequestProperty("Content-length", "" + requestStringBytes.length);
	        connection.setRequestProperty("Content-Type", 
	                   "application/json;encoding=utf-8"); 
			//connection.setRequestProperty("Content-Type", "application/octet-stream");
			//connection.setRequestProperty("Connection", "Keep-Alive");
	        connection.setRequestProperty("Charset", "utf-8");
	
	        connection.connect();
	        
	        DataOutputStream out = new DataOutputStream(connection.getOutputStream()); 

//			JSONObject obj = new JSONObject();
//	        String json_string = "{"
//	        		+ "\"chatid\": \"chat081fc7db20f781fbcad15622d567c82f\","
//	        		+ "\"sender\": \"061266430629261842\","
//	        		+ "\"msgtype\": \"text\","
//	        		+ "\"text\": {"
//	        		+ "\"content\": \"syied by wangxiaoqiang\""
//					+ "}"	
//	        		+ "}";
	        
//	        String json_string = "{"
//	        			+ "\"call_back_tag\": [\"user_add_org\", \"user_modify_org\", \"user_leave_org\"],"
//	        			+ "\"token\": \"123456\","
//	        			+ "\"aes_key\": \"abc1111111111000000000011111111110000000000\","
//	        			+ "\"url\":\"http://ylxlxx.f3322.net:11000\""
//	        			+ "}";
	        
	        
            //out.writeBytes(json_obj.toString());
            out.write(requestStringBytes);
            out.flush(); 
            out.close(); 
            
	        Map<String, List<String>> map = connection.getHeaderFields();
//	        for (String key : map.keySet())
//	        {
//	            System.out.println(key + "--->" + map.get(key));
//	        }
	        
	        in = new BufferedReader(new InputStreamReader(
	                connection.getInputStream()));
	
	        String line;
	        while ((line = in.readLine()) != null) 
	        {
	            response += line;
	        }
	        System.out.println("post finish, response is : " + response);
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
		
		return response;
	}
}
