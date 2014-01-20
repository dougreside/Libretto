package org.nypl.dataholder;

import java.io.Serializable;

public class PlayNoteBean implements Serializable {

	private String playNoteID;
	private String noteID;
	private String noteText;
	private String playID;
	private String versionID;
	private String versionName;
	private String notes;
	private String playImage;
	private String playName;
	private String author;
	
	public String getPlayNoteID() {
		return playNoteID;
	}
	public void setPlayNoteID(String playNoteID) {
		this.playNoteID = playNoteID;
	}
	public String getNoteID() {
		return noteID;
	}
	public void setNoteID(String noteID) {
		this.noteID = noteID;
	}
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	
	public String getPlayID() {
		return playID;
	}
	public void setPlayID(String playID) {
		this.playID = playID;
	}
	
	public String getVersionID() {
		return versionID;
	}
	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}
	
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	public String getNotePlayImage() {
		return playImage;
	}
	public void setNotePlayImage(String playImage) {
		this.playImage = playImage;
	}
	
	public String getNotePlayName() {
		return playName;
	}
	public void setNotePlayName(String playName) {
		this.playName = playName;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}
}
