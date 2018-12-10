package tasks;

import java.util.Date;

public abstract class EveryEndTask extends EveryTask {		
	public EveryEndTask(long e) {
		super( e );
	}

	@Override
	public void innerRun() {
		lastApply.setTime( new Date().getTime() );
	}
}
