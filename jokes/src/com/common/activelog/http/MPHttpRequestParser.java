package com.common.activelog.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

public class MPHttpRequestParser {
	private URL mUrl = null;
	private final List<BasicNameValuePair> mParams = 
		new ArrayList<BasicNameValuePair>();
	
	public MPHttpRequestParser(URL url){
		mUrl = url;
		
		parseParams();
	}
	
	public MPHttpRequestParser(){
		mUrl = null;
	}
	
	public MPHttpRequestParser(String url){
		url = getUrlFromContent(url);
		
		try {
			mUrl = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parseParams(){
		String query = mUrl.getQuery();
		String name, value;
		
		mParams.clear();
		
		int offset = 0;
		
		while (offset < query.length()){
			int n = query.indexOf("=", offset);
			if (n < 0) break;
			
			// Get Name.
			name = query.substring(offset, n).trim();
			offset = n + 1;
			
			// Get Value.
			n = query.indexOf("&", offset);
			if (n < 0) {
				n = query.length();
				if (n < 0) break;
			}
			
			value = query.substring(offset, n).trim();
			offset = n + 1;
			
			if (name.length() > 0) name = URLDecoder.decode(name);
			if (value.length() > 0) value = URLDecoder.decode(value);
			this.mParams.add(new BasicNameValuePair(name, value));
		}
	}
	
	public List<BasicNameValuePair> getParams(){
		return this.mParams;
	}
	
	public String findParamValueByName(String name){
		for (BasicNameValuePair pair: mParams){
			if (pair.getName().equals(name)){
				return pair.getValue();
			}
		}
		return null;
	}
	
	public URL getUrl(){
		return this.mUrl;
	}
	
	
	public static String getUrlFromContent(String content){
		String url = null;
		int offset = content.indexOf("http://");
		
		if (offset >= 0){
			url = content.substring(offset);
			int n = url.indexOf("\n");
			int r = url.indexOf("\r");
			
			offset = n < r ? n : r;
			if (offset >= 0){
				url = url.substring(0, offset);
			}
		}
		return url;
	}
}
