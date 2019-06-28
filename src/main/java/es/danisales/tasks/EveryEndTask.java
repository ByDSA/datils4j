package es.danisales.tasks;

import java.util.Date;

public abstract class EveryEndTask extends EveryTask {		
	public EveryEndTask(long e, Mode m) {
		super( e, m );
	}

	@Override
	public void innerRun() {
		lastApply.setTime( new Date().getTime() );
	}
}
