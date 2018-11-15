package io;

import java.util.List;

import tasks.TaskManager;

public interface FileSavable extends File {
	final TaskManager threads = new TaskManager(100);
	
	boolean save();
	
	public void autosave();
	boolean isAutosaving();
	public void setAutosaving(boolean b);
	public boolean getAndSetAutosaving(boolean b);
	
	Boolean isDirty();
	void setDirty(boolean b);
}
