package org.nypl.dataholder;

import java.io.Serializable;

public class AudioBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String clipId;
	private String clipFrom;
	private String clipTo;
	private String clipVersionId;
	private String playid;
	private String audioPath;
	
	public String getClipPlayID() {
		return playid;
	}
	public void setClipPlayID(String playid) {
		this.playid = playid;
	}
	
	public String getClipID() {
		return clipId;
	}
	public void setClipID(String clipId) {
		this.clipId = clipId;
	}

	public String getClipFrom() {
		if (clipFrom==null){
			return "0";
		}
		else{
		return clipFrom;
		}
	}
	public void setClipFrom(String clipFrom) {
		this.clipFrom = clipFrom;
	}
	
	public String getClipTo() {
		return clipTo;
	}
	public void setClipTo(String clipTo) {
		this.clipTo = clipTo;
	}
	public String getClipVersionId() {
		return clipVersionId;
	}
	public void setClipVesrsionId(String clipVersionId) {
		this.clipVersionId = clipVersionId;
	}
	public String getAudioPath() {
		return audioPath;
	}
	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}
	
	
}
