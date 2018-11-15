package io;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface File {
	String filename();
	void setFilename(String fn);
	public String extension();
	void setExtension(String fn);
	public String folder();
	void setFolder(String fn);
	
	default Path path() {
		return Paths.get( Files.DATA_FOLDER + folder() + "/" + filename() + "." + extension() );
	}
}
