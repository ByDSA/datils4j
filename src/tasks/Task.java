package tasks;

import concurrency.Lockable;
import rules.Rule;

public interface Task extends Rule, Lockable {
	boolean isApplying();
	void setApplying(boolean b);

	default void interrupt() {
		synchronized(lock()) {
			Thread.currentThread().interrupt();
		}
	}
	
	default boolean apply(int n) {
		setApplying( true );
		boolean ret = innerApply(n);
		setApplying( false );
		return ret;
	}
	
	boolean innerApply(int n);
}