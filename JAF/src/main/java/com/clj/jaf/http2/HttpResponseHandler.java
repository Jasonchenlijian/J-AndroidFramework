package com.clj.jaf.http2;

import java.util.List;
import java.util.Map;


public interface HttpResponseHandler {
	
	/**
	 * http请求成功后，response转换成content
	 * @param content
	 * @param heads
	 */
	void onSuccess(String content, Map<String, List<String>> heads);
	
	/**
	 * http请求失败后，response转换成jsonString
	 * 
	 * @param e
	 */
	void onFail(RestException e);
	
}
