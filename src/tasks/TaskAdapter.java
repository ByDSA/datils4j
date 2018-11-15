package tasks;

import java.util.concurrent.atomic.AtomicBoolean;

public class TaskAdapter implements Task {
	protected AtomicBoolean applying = new AtomicBoolean(false);
	protected Object _lock = new Object();

	public boolean check() {
		return true;
	}
	
	public boolean isApplying() {
		return applying.get();
	}
	
	public Object lock() {
		return _lock;
	}

	@Override
	public void setApplying(boolean b) {
		applying.set( b );
	}

	@Override
	public boolean innerApply(int n) {
		return false;
	}

}
