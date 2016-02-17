package com.clj.jaf.http2;


/**
 * 带http重试机制的handler
 */
public abstract class HttpResponseRetryHandler implements HttpResponseHandler,HttpRetryStrategy{
	
	private int retryNum = RestConstant.DEFAULT_RETRY_NUM;
	
	public HttpResponseRetryHandler(){
	}
	
	public HttpResponseRetryHandler(int retryNum){
		this.retryNum = retryNum;
	}
	
	public int getRetryNum() {
		return retryNum;
	}

	public void setRetryNum(int retryNum) {
		this.retryNum = retryNum;
	}
}
