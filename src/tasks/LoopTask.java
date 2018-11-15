package tasks;

import java.util.Date;

public abstract class LoopTask extends TaskAdapter {
	@Override
	public boolean check() {
		return true;
	}
	
	abstract public boolean innerApply(int n);
}
