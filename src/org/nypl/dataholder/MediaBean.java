package org.nypl.dataholder;

import java.io.Serializable;

public class MediaBean implements Serializable{

	private String mediaId;
	private String medianame;
	
	
	
	public String getMediaID() {
		return mediaId;
	}
	public void setMediaID(String mediaId) {
		this.mediaId = mediaId;
	}
	public String getMediaName() {
		return medianame;
	}
	public void setMediaName(String medianame) {
		this.medianame = medianame;
	}
}
