package com.lzz.pagetransmit.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.lzz.pagetransmit.util.LzzFilterUtil;
import com.lzz.pagetransmit.util.LzzReadtransmitConf;
import com.lzz.pagetransmit.util.LzzSendRequest;

public class LzzCookieGetFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException{
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		
		String request_url = request.getRequestURI();
		System.out.println("request url : " + request_url);
		
		if(LzzFilterUtil.isBaseResource(request_url)){
			handleBaseResourceRequest(arg0, arg1, chain);
			return;
		}
		
		handlePageItemRequest(arg0, arg1, chain);
		return;
	}

	private void handlePageItemRequest(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		
		//获取实际请求地址
		String request_url = request.getRequestURI();
		String referer = request.getHeader("Referer");
		String real_request_url = LzzFilterUtil.replaceRequestUrl(referer, request_url);
		
		List<String> par_names = LzzFilterUtil.getRequestParameterNames(request);
		List<String> par_values = LzzFilterUtil.getRequestParameterValues(request);
		
		String response_contents = LzzSendRequest.sendByGet(real_request_url, par_names, par_values, request, response);
	
		handlePageItemResponse(referer, real_request_url, response_contents, response);
	}
	
	
	private void handlePageItemResponse(String referer, String real_request_url, String response_contents, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response_contents = handleResponseContents(referer, real_request_url, response_contents);
		
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(response_contents);
	}

	private String handleResponseContents(String referer, String real_request_url, String response_contents) {
		// TODO Auto-generated method stub
		return LzzFilterUtil.handleResponseContents(referer, real_request_url, response_contents);
	}

	private void handleBaseResourceRequest(ServletRequest arg0,
			ServletResponse arg1, FilterChain chain) throws IOException{
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		
		//获取实际请求地址
		String request_url = request.getRequestURI();
		String referer = request.getHeader("Referer");
		String real_request_url = LzzFilterUtil.replaceRequestUrl(referer, request_url);
		
		handleBaseResourceResponse(real_request_url, response);
	}

	private void handleBaseResourceResponse(String real_request_url, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		if (real_request_url.contains(".jpg")) {
			response.setHeader("content-type", "application/x-jpg");
		}
		if (real_request_url.contains(".png")) {
			response.setHeader("content-type", "image/png");
		}
		else if (real_request_url.contains(".gif")) {
			response.setHeader("content-type", "image/gif");
		}
		else if (real_request_url.contains(".jpeg")) {
			response.setHeader("content-type", "image/jpeg");
		}
		response.setCharacterEncoding("utf-8");
		DataInputStream dataInputStream = new DataInputStream((new URL(real_request_url)).openStream());
		byte[] buff = new byte[1024];
		BufferedImage img = new BufferedImage(1, 1, 1);
		int len = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((len = dataInputStream.read(buff)) > 0) {
			out.write(buff, 0, len);
			buff = new byte[1024];
		}
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		img = ImageIO.read(in);
		dataInputStream.close();
		if (real_request_url.contains(".jpg")) {
			ImageIO.write(img, "jpg", response.getOutputStream());
		}
		if (real_request_url.contains(".png")) {
			ImageIO.write(img, "png", response.getOutputStream());
		}
		else if (real_request_url.contains(".gif")) {
			ImageIO.write(img, "gif", response.getOutputStream());
		}
		else if (real_request_url.contains(".jpeg")) {
			ImageIO.write(img, "jpeg", response.getOutputStream());
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
