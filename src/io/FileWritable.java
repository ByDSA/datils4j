package io;

import tasks.TaskManager;

public interface FileWritable extends File {
	final TaskManager threads = new TaskManager();
	
	boolean save();
	
	public void autosave();
	boolean isAutosaving();
	public void setAutosaving(boolean b);
	public boolean getAndSetAutosaving(boolean b);
	
	Boolean isDirty();
	void setDirty(boolean b);
}
