package tasks;

import java.util.Date;

public abstract class EveryTask extends TaskAdapter {
	protected Date lastApply = new Date(0);
	protected Long every;
	
	public EveryTask(long e) {
		every = e;
	}
	
	@Override
	public boolean check() {
		return lastApply.getTime() == 0 || lastApply.getTime() + every - new Date().getTime() < 0;
	}
	
	abstract protected boolean internalApply(int n);
}
