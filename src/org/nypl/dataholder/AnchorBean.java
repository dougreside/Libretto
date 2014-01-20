package org.nypl.dataholder;

import java.io.Serializable;

public class AnchorBean implements Serializable{

	
	private String anchorID;
	private String playId;
	private String playVersionId;
	private String htmlId;
	
	
	public String getAnchorID() {
		return anchorID;
	}
	public void setAnchorID(String anchorID) {
		this.anchorID = anchorID;
	}
	public String getAnchorPlayId() {
		return playId;
	}
	public void setAnchorPlayId(String playId) {
		this.playId = playId;
	}
	public String getAnchorPlay_Version_Id() {
		return playVersionId;
	}
	public void setAnchorPlay_Version_Id(String playVersionId) {
		this.playVersionId = playVersionId;
	}
	public String getAnchorHTMLId() {
		return htmlId;
	}
	public void setAnchorHTML_Id(String htmlId) {
		this.htmlId = htmlId;
	}
}
