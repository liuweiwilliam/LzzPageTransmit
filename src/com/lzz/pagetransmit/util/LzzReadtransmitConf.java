package com.lzz.pagetransmit.util;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.lzz.pagetransmit.clazz.LzzProjectDictionary;

public class LzzReadtransmitConf {
	private static Map<String, String> urlFiltersMapRequest = new Hashtable<String, String>();
	private static Map<String, String> urlFiltersMapResponse = new Hashtable<String, String>();
	private static Vector<String> postFixIgnores = new Vector<String>();
	private static Vector<String> pageItemIgnoreJS = new Vector<String>();
	private static LzzProjectDictionary projectDictionary = new LzzProjectDictionary();
	private static String projectRootDictionary = "";
	private static String transmitIndex = "";
	private static String transmitRoot = "";
	private static String localProjectRoot = "";
	private static String localProjectName = "";
	private static boolean readed = false;
	
	public static void readConf() throws ParserConfigurationException, SAXException, IOException{
		if(readed){
			return;
		}
		
		String path = Thread.currentThread().getContextClassLoader()
				.getResource("pageTransmit-conf.xml").getPath();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		db = dbf.newDocumentBuilder();
		Document doc = db.parse(path);
		NodeList total = doc.getElementsByTagName("pageTransmit");
		
		NodeList pageTransmit = total.item(0).getChildNodes();
		for(int i = 0 ;i < pageTransmit.getLength(); i++){
			String node_name = pageTransmit.item(i).getNodeName();
			
			if(node_name.equals("projectRootDictionary")){
				projectRootDictionary = pageTransmit.item(i).getTextContent();
				projectDictionary.setDictionaryName(projectRootDictionary);
				projectDictionary.setPath(projectRootDictionary);
			}
			
			if(node_name.equals("requestFilterUrl")){
				handleRequestFilterUrl(pageTransmit.item(i).getChildNodes());
			}
			
			if(node_name.equals("responseFilterUrl")){
				handleResponseFilterUrl(pageTransmit.item(i).getChildNodes());
			}
			
			if(node_name.equals("baseResource")){
				handleBaseResource(pageTransmit.item(i).getChildNodes());
			}
			
			if(node_name.equals("pageItemIgnores")){
				handlePageItemIgnoreJS(pageTransmit.item(i).getChildNodes());
			}
			
			if(node_name.equals("transmitIndex")){
				handleTransmitIndex(pageTransmit.item(i));
			}
			
			if(node_name.equals("transmitRoot")){
				handleTransmitRoot(pageTransmit.item(i));
			}
			
			if(node_name.equals("localProjectRoot")){
				handleLocalProjectRoot(pageTransmit.item(i));
			}
			
			if(node_name.equals("localProjectName")){
				handleLocalProjectName(pageTransmit.item(i));
			}
		}
		
		readed = true;
	}

	private static void handleTransmitIndex(Node item) {
		// TODO Auto-generated method stub
		transmitIndex = item.getTextContent();
	}

	private static void handleTransmitRoot(Node item) {
		// TODO Auto-generated method stub
		transmitRoot = item.getTextContent();
	}
	
	private static void handleLocalProjectRoot(Node item) {
		// TODO Auto-generated method stub
		localProjectRoot = item.getTextContent();
	}

	private static void handleLocalProjectName(Node item) {
		// TODO Auto-generated method stub
		localProjectName = item.getTextContent();
	}

	private static void handlePageItemIgnoreJS(NodeList filter_base_resource_nodes) {
		// TODO Auto-generated method stub
		for(int i = 0 ;i < filter_base_resource_nodes.getLength(); i++){
			String node_name = filter_base_resource_nodes.item(i).getNodeName();
			
			if("js".equals(node_name)){
				String js = filter_base_resource_nodes.item(i).getTextContent();
				if(null==js
						|| "".equals(js)){
					continue;
				}
				pageItemIgnoreJS.add(js);
			}
		}
	}

	private static void handleBaseResource(NodeList filter_base_resource_nodes) {
		// TODO Auto-generated method stub
		for(int i = 0 ;i < filter_base_resource_nodes.getLength(); i++){
			String node_name = filter_base_resource_nodes.item(i).getNodeName();
			
			if(!"postFix".equals(node_name)){
				continue;
			}
			
			String post_fix = filter_base_resource_nodes.item(i).getTextContent();
			//System.out.println("filter ignore : " + post_fix);
			
			if(null==post_fix
					|| "".equals(post_fix)){
				continue;
			}
			
			postFixIgnores.add(post_fix);
		}
	}

	private static void handleRequestFilterUrl(NodeList filter_url_nodes) {
		// TODO Auto-generated method stub
		handFilterUrl(filter_url_nodes, true);
	}
	
	private static void handleResponseFilterUrl(NodeList filter_url_nodes) {
		// TODO Auto-generated method stub
		handFilterUrl(filter_url_nodes, false);
	}
	
	private static void handFilterUrl(NodeList filter_url_nodes, boolean is_request) {
		// TODO Auto-generated method stub
		for(int i = 0 ;i < filter_url_nodes.getLength(); i++){
			String node_name = filter_url_nodes.item(i).getNodeName();
			
			if(!"url".equals(node_name)){
				continue;
			}
			
			String url_val = filter_url_nodes.item(i).getTextContent();
			//System.out.println("filter url : " + url_val);

			if(null==url_val
					|| "".equals(url_val)){
				continue;
			}
			
			String localUrl = "";
			String remoteUrl = "";
			
			NodeList urls = filter_url_nodes.item(i).getChildNodes();
			for(int j=0; j<urls.getLength(); j++){
				String url_name = urls.item(j).getNodeName();
				
				if(url_name.equals("localUrl")){
					localUrl = urls.item(j).getTextContent();
				}
				
				if(url_name.equals("remoteUrl")){
					remoteUrl = urls.item(j).getTextContent();
				}
			}
			
			if(is_request){
				urlFiltersMapRequest.put(localUrl, remoteUrl);
			}else{
				urlFiltersMapResponse.put(remoteUrl, localUrl);
			}
		}
	}
	
	public static boolean isBaseResource(String request_url) throws ParserConfigurationException, SAXException, IOException{
		readConf();
		
		for(String postFix : postFixIgnores){
			if(request_url.contains(postFix)){
				return true;
			}
		}
		
		return false;
	}

	public static boolean isIgnorePageItemJS(String request_url){
		for(String url : pageItemIgnoreJS){
			if(request_url.contains(url)){
				return true;
			}
		}
		
		return false;
	}

	public static String replaceRequestUrl(String referer, String request_url) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		readConf();
		
		if(LzzProperties.getProjectName().equals(request_url)){
			//首页地址的请求
			return transmitIndex;
		}
		
		return urlFiltersMapRequest.get(request_url);
	}
	
	public static String getTransmitIndex() throws ParserConfigurationException, SAXException, IOException{
		readConf();
		return transmitIndex;
	}
	public static String getLocalProjectRoot() throws ParserConfigurationException, SAXException, IOException{
		readConf();
		return localProjectRoot;
	}

	/**
	 * 替换response中配置替换的链接
	 * @param response_contents
	 * @return
	 */
	public static String handleResponseContents(String referer, String real_request_url, String response_contents) {
		// TODO Auto-generated method stub
		String root_path = projectDictionary.getPath();
		
		getSubDictionaryFromUrl(root_path, real_request_url);
		
		if(localProjectRoot.equals(referer)){
			referer = transmitIndex;
		}
		
		//处理script标签中的链接
		response_contents = handleScriptSrc(referer, response_contents);
		
		//处理link中的css href
		response_contents = handleLinkHref(referer, response_contents);
		
		//处理css中的url
		if(real_request_url.contains(".css")){
			response_contents = handleCssUrl(referer, response_contents);
		}
		
		//处理js中的url
		if(real_request_url.contains(".js")){
			response_contents = handleJsUrl(referer, response_contents);
		}
		
		response_contents = response_contents.replaceAll(transmitRoot, localProjectRoot);
		
		//处理不过滤的page items
		response_contents = handleIgnorePageItems(response_contents);
		
		return response_contents;
	}
	
	private static String handleIgnorePageItems(String response_contents) {
		// TODO Auto-generated method stub
		for(int i=0; i<pageItemIgnoreJS.size(); i++){
			response_contents = response_contents.replaceAll(
					localProjectRoot+pageItemIgnoreJS.get(i), transmitRoot+pageItemIgnoreJS.get(i));
		}
		
		return response_contents;
	}

	private static String handleJsUrl(String referer, String response_contents) {
		// TODO Auto-generated method stub
		Map<String, String>replaceMap = new Hashtable<String, String>();
		int index = response_contents.indexOf("url:");
		while(index>=0){
			int index2;
			if(-1==response_contents.indexOf("?", index+1)){
				if(-1==response_contents.indexOf("\"", index+1)
						&& -1==response_contents.indexOf("\'", index+1)){
					break;
				}
				
				index2 = response_contents.indexOf("\"", index)==-1?
						response_contents.indexOf("\'", index):
							response_contents.indexOf("\"", index)+ 1;
			}else{
				index2 = response_contents.indexOf("?", index) + 1;
			}
			
			

			if(index2<0) break; 
			
			String url = response_contents.substring(index + "url:".length()+1, index2-"?".length());
			
			if("".equals(url)){
				index = response_contents.indexOf("url:", index2);
				continue;
			}
			
			index = index2+1;
			
			String real_path = getRealPathBySrcOrHref(referer, url);
			replaceMap.put(url, real_path);
			
			//添加进请求url映射map
			String local_request_url = real_path.replace(transmitRoot, "");
			urlFiltersMapRequest.put(localProjectName+local_request_url, real_path);
			
			index = response_contents.indexOf("url:", index2);
		}
		
		for(Entry<String, String> entry:replaceMap.entrySet()){
			response_contents = response_contents.replaceAll(entry.getKey(), entry.getValue());
		}
		
		return response_contents;
	}

	private static String handleCssUrl(String referer, String response_contents) {
		// TODO Auto-generated method stub
		Map<String, String>replaceMap = new Hashtable<String, String>();
		int index = response_contents.indexOf("url(");
		while(index>=0){
			int index2 = response_contents.indexOf(")", index) + ")".length();

			if(index2<0) break; 
			
			String url = response_contents.substring(index + "url(".length(), index2-")".length());
			
			if("".equals(url)){
				index = response_contents.indexOf("url(", index2);
				continue;
			}
			
			index = index2+1;
			
			String real_path = getRealPathBySrcOrHref(referer, url);
			replaceMap.put(url, real_path);
			
			//添加进请求url映射map
			String local_request_url = real_path.replace(transmitRoot, "");
			urlFiltersMapRequest.put(localProjectName+local_request_url, real_path);
			
			index = response_contents.indexOf("url(", index2);
		}
		
		for(Entry<String, String> entry:replaceMap.entrySet()){
			response_contents = response_contents.replaceAll(entry.getKey(), entry.getValue());
		}
		
		return response_contents;
	}

	private static String handleLinkHref(String referer, String response_contents) {
		// TODO Auto-generated method stub
		Map<String, String>replaceMap = new Hashtable<String, String>();
		int index = response_contents.indexOf("<link");
		while(index>=0){
			int index2 = response_contents.indexOf(">", index) + ">".length();

			if(index2<0) break; 
			
			String contents = response_contents.substring(index, index2);
			if(-1==contents.indexOf("href=")){
				index = response_contents.indexOf("<link", index2);
				continue;
			}
			int src_ind1 = contents.indexOf("href=") + "href=".length()+1;
			int src_ind2_1 = contents.indexOf("\"", src_ind1);
			int src_ind2_2 = contents.indexOf("\'", src_ind1);
			
			int src_ind2 = src_ind2_1==-1?src_ind2_2:src_ind2_1;
			String href = contents.substring(src_ind1, src_ind2);
			
			if(href.contains("http")
					|| href.contains("//")){
				index = index2+1;
				continue;
			}
			
			index = index2+1;
			
			String real_path = getRealPathBySrcOrHref(referer, href);
			replaceMap.put(href, real_path);
			
			//添加进请求url映射map
			String local_request_url = real_path.replace(transmitRoot, "");
			urlFiltersMapRequest.put(localProjectName+local_request_url, real_path);
			
			index = response_contents.indexOf("<link", index2);
		}
		
		for(Entry<String, String> entry:replaceMap.entrySet()){
			response_contents = response_contents.replaceAll(entry.getKey(), entry.getValue());
		}
		
		return response_contents;
	}

	private static String handleScriptSrc(String referer, String response_contents) {
		// TODO Auto-generated method stub
		Map<String, String> replaceMap = new Hashtable<String, String>();
		int index = response_contents.indexOf("<script");
		while(index>=0){
			int index2 = response_contents.indexOf(">", index) + ">".length();
			if(response_contents.indexOf("</script>", index)==index2){
				index2 = response_contents.indexOf("</script>", index) + "</script>".length();
			}
			if(index2<0) break; 
			
			String contents = response_contents.substring(index, index2);
			
			if(-1==contents.indexOf("src=")){
				index = response_contents.indexOf("<script", index2);
				continue;
			}
			int src_ind1 = contents.indexOf("src=") + "src=".length()+1;
			
			int src_ind2_1 = contents.indexOf("\"", src_ind1);
			int src_ind2_2 = contents.indexOf("\'", src_ind1);
			
			int src_ind2 = src_ind2_1==-1?src_ind2_2:src_ind2_1;
			
			String src = contents.substring(src_ind1, src_ind2);
			
			if(src.contains("http")
					|| src.contains("//")){
				index = index2+1;
				continue;
			}
			
			index = index2+1;
			
			String real_path = getRealPathBySrcOrHref(referer, src);
			replaceMap.put(src, real_path);
			
			//添加进请求url映射map
			String local_request_url = real_path.replace(transmitRoot, "");
			urlFiltersMapRequest.put(localProjectName+local_request_url, real_path);
			
			index = response_contents.indexOf("<script", index2);
		}
		
		for(Entry<String, String> entry:replaceMap.entrySet()){
			response_contents = response_contents.replaceAll(entry.getKey(), entry.getValue());
		}
		
		return response_contents;
	}

	private static String getRealPathBySrcOrHref(String referer, String src) {
		// TODO Auto-generated method stub
		if(null==referer){
			referer = transmitIndex;
		}
		
		referer = referer.replace(localProjectRoot, transmitRoot);
		if(null!=referer && !"".equals(referer)){
			referer = referer.substring(0, referer.lastIndexOf("/"));
		}
		while(src.contains("../")){
			src = src.substring(src.indexOf("../") + "../".length());
			referer = referer.substring(0, referer.lastIndexOf("/"));
		}
		
		return referer + "/" + src;
	}

	private static void getSubDictionaryFromUrl(String root_path,
			String real_request_url) {
		// TODO Auto-generated method stub
		///4S-VOLVO/page-login/login.html
		int index = real_request_url.indexOf(root_path) + root_path.length();
		while(index>=0){
			int index2 = real_request_url.indexOf("/", index);

			if(index2<0) break; 
			
			String dir_name = real_request_url.substring(index, index2);

			index = index2+1;
			projectDictionary.addSubDictionary(dir_name);
		}
		
		System.out.println(projectDictionary.toString());
	}
}
