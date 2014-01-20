package org.nypl.dataholder;

import java.io.Serializable;

public class PlaysBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String playID;
	private String playName;
	private String playImage;
	private String playCaption;
	private String playAuthors;
	private String playAuthorsInfo;
	private String versionName;
	private String versionBookmark;
	private String versionID;
	private String playUrl;
	private String scrollPosition;
	
	
	public String getPlayVersionID() {
		return versionID;
	}
	public void setPlayVersionID(String versionID) {
		this.versionID = versionID;
	}
	
	
	public String getPlayVersionBookmark() {
		return versionBookmark;
	}
	public void setPlayVersionBookmark(String versionBookmark) {
		this.versionBookmark = versionBookmark;
	}
	
	public String getPlayVersion() {
		return versionName;
	}
	public void setPlayVersion(String versionName) {
		this.versionName = versionName;
	}
	
	public String getPlayID() {
		return playID;
	}
	public void setPlayID(String playID) {
		this.playID = playID;
	}

	public String getPlayUrl(){
		return playUrl;
	}
	public void setPlayUrl(String playUrl){
		this.playUrl= playUrl;
	}
	public String getPlayName() {
		return playName;
	}
	public void setPlayName(String playName) {
		this.playName = playName;
	}
	public String getPlayImage() {
		return playImage;
	}
	public void setPlayImage(String playImage) {
		this.playImage = playImage;
	}

	public String getPlayAuthors() {
		return playAuthors;
	}
	public void setPlayAuthors(String playAuthors) {
		this.playAuthors = playAuthors;
	}
	
	public String getScrollPosition() {
		return scrollPosition;
	}
	public void setScrollPosition(String scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

}
