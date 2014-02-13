package org.nypl.libretto.dataholder;

import java.io.Serializable;

public class LibraryBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String libraryID;
	private String libraryName;
	private String libraryURI;

	
	
	public String getLibraryID() {
		return libraryID;
	}
	public void setLibraryID(String libraryID) {
		this.libraryID = libraryID;
	}
	public String getLibraryName() {
		return libraryName;
	}
	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}
	public String getLibraryURI() {
		return libraryURI;
	}
	public void setLibraryURI(String libraryURI) {
		this.libraryURI = libraryURI;
	}

}
