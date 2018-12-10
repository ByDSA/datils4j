package tasks;

import java.util.Date;

public abstract class EveryIniTask extends EveryTask {		
	public EveryIniTask(long e) {
		super( e );
	}

	@Override
	public void innerRun() {
		lastApply.setTime( new Date().getTime() );
	}
}
