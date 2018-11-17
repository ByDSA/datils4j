package tasks;

import java.util.Date;

public abstract class EveryIniTask extends EveryTask {		
	public EveryIniTask(long e) {
		super( e );
	}

	@Override
	public boolean apply(int n) {
		applying.set( true );
		lastApply.setTime( new Date().getTime() );
		boolean ret = internalApply(n);
		applying.set( false );
		return ret;
	}
}
