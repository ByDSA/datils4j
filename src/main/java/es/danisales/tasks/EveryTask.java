package es.danisales.tasks;

import java.util.Date;

public abstract class EveryTask extends Action {
	protected Date lastApply = new Date(0);
	protected Long every;
	
	public EveryTask(long e, Mode m) {
		super(m);
		every = e;
	}
	
	@Override
	public boolean check() {
		return super.check() && lastApply.getTime() == 0 || lastApply.getTime() + every - new Date().getTime() < 0;
	}
}
