package org.nypl.dataholder;

import java.io.Serializable;

public class SheetMusicBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String SheetMusicID;
	private String ID;
	private String SheetMusicName;
	private String SheetMusicHTML;
	private String PlayOrder;

	public String getID() {
		return ID;
	}
	public void setUUID(String uuid) {
		this.ID = uuid;
	}
	public void setSheetMusicID(String sheetMusicID) {
		this.SheetMusicID = sheetMusicID;
	}  
	public String getSheetMusicID() {
		return SheetMusicID;
	}

	public String getSheetMusicName() {
		return SheetMusicName;
	}
	public void setSheetMusicName(String sheetMusicName) {
		this.SheetMusicName = sheetMusicName;
	} 
	public String getSheetMusicHTML() {
		return SheetMusicHTML;
	}
	public void setSheetMusicHTML(String sheetMusicHTML) {
		this.SheetMusicHTML = sheetMusicHTML;
	} 
	public String getPlayOrder() {
		return PlayOrder;
	}
	public void setPlayOrder(String playOrder) {
		this.PlayOrder = playOrder;
	} 

	
}