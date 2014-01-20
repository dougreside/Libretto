package org.nypl.dataholder;

import java.io.Serializable;

public class ChaptersBean implements Serializable{

	/**
	 * 
	 */

	private String chapterid;
	private String chaptertext;
	private static final long serialVersionUID = 1L;
	
	public String getChapterID() {
		return chapterid;
	}
	public void setChapterID(String chapterid) {
		this.chapterid = chapterid;
	}
	
	public String getChapterText() {
		return chaptertext;
	}
	public void setChapterText(String chaptertext) {
		this.chaptertext = chaptertext;
	}

	
	
}
