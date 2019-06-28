package es.danisales.tasks;

import java.util.Date;

public abstract class EveryIniTask extends EveryTask {		
	public EveryIniTask(long e, Mode m) {
		super( e, m );
	}

	@Override
	public void innerRun() {
		lastApply.setTime( new Date().getTime() );
	}
}
