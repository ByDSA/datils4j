package tasks;

import java.util.Date;

public abstract class EveryEndTask extends EveryTask {		
	public EveryEndTask(long e) {
		super( e );
	}

	@Override
	public boolean apply(int n) {
		applying.set( true );
		boolean ret = internalApply(n);
		lastApply.setTime( new Date().getTime() );
		applying.set( false );
		return ret;
	}
}
