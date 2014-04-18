package org.nypl.libretto.dataholder;

import java.io.Serializable;

public class ChaptersBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String versionID;
	private String htmlFile;
	private String chapterID;
	private String chapterName;
	private String chapterPlayOrder;
	private String chapterMappingID;
	
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chaptername) {
		this.chapterName = chaptername;
	} 
	public String getChapterID() {
		return chapterID;
	}
	public void setChapterID(String chapterid) {
		this.chapterID = chapterid;
	} 
	public String getChapterMappingID() {
		return chapterMappingID;
	}
	public void setChapterMappingID(String chapterMapping) {
		this.chapterMappingID = chapterMapping;
	} 
	public String getChapterPlayOrder() {
		return chapterPlayOrder;
	}
	public void setChapterPlayOrder(String chapterplayorder) {
		this.chapterPlayOrder = chapterplayorder;
	} 

	

	public String getVersionID() {
		return versionID;
	}
	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}
	public String getHTMLFile() {
		return htmlFile;
	}
	public void setHTMLFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}
}