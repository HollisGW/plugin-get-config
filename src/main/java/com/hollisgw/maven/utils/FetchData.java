package com.hollisgw.maven.utils;

/**
 * Created by hollisgw on 15/8/28.
 */
public class FetchData {

	private String url;
	private String content;

	public FetchData() {
	    this(null, null);
	}
	
	public FetchData(String url) {
	    this(url, null);
	}
	
	public FetchData(String url, String content) {
	    this.url = url;
	    this.content = content;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
