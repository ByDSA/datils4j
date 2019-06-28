package es.danisales.tasks;

public abstract class LoopTask extends Action {
	@Override
	public boolean check() {
		return true;
	}

	public LoopTask(Mode m) {
		super(m);
	}
}
