package org.nypl.dataholder;

import java.io.Serializable;

public class VersionBean implements Serializable{
	private String versionID;
	private String versionPlayID;
	private String versionName;
	private String htmlFile;
	private String bookmark;
	private String note;
	private boolean bookmarked;
	private String playName;
	private String playImage;
	private String author;
	private String playaudioname;
	private String versionUUID;
	
	
	
	public String getVersionAudioName() {
		return playaudioname;
	}
	public void setVersionAudioName(String playaudioname) {
		this.playaudioname = playaudioname;
	} 
	
	public String getVersionPlayImage() {
		return playImage;
	}
	public void setVersionPlayImage(String playImage) {
		this.playImage = playImage;
	}
	
	public String getVersionPlayName() {
		return playName;
	}
	public void setVersionPlayName(String playName) {
		this.playName = playName;
	}
	public String getVersionUUID() {
		return versionUUID;
	}
	public void setVersionUUID(String versionUUID) {
		this.versionUUID = versionUUID;
	}
	public String getVersionID() {
		return versionID;
	}
	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}
	public String getVersionPlayID() {
		return versionPlayID;
	}
	public void setVersionPlayID(String versionPlayID) {
		this.versionPlayID = versionPlayID;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getVersionHTMLFile() {
		return htmlFile;
	}
	public void setVersionHTMLFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}
	public String getBookmark() {
		return bookmark;
	}
	public void setBookmark(String bookmark) {
		this.bookmark = bookmark;
	}
	public String getNotes() {
		return note;
	}
	public void setNotes(String note) {
		this.note = note;
	}
	public boolean isBookmarked() {
		return bookmarked;
	}
	public void setBookmarked(boolean bookmarked) {
		this.bookmarked = bookmarked;
	}
	
	
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}
}
