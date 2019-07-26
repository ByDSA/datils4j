package es.danisales.tasks;

import java.util.Date;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class EveryTask implements Action {
	protected Date lastApply = new Date(0);
	private final Action actionAdapter;
	protected long every;

	public EveryTask(Builder builder) {
		checkNotNull(builder.mode);
		checkNotNull(builder.function);
		actionAdapter = Action.of(builder.mode, builder.function);
		every = builder.every;
	}

	@Override
	public void addAfter(Runnable r) {
		actionAdapter.addAfter(r);
	}

	@Override
	public void addOnInterrupt(Runnable a) {
		actionAdapter.addOnInterrupt(a);
	}

	@Override
	public boolean isRunning() {
		return actionAdapter.isRunning();
	}

	@Override
	public boolean isDone() {
		return actionAdapter.isDone();
	}

	@Override
	public boolean isReady() {
		return lastApply.getTime() == 0 || lastApply.getTime() + every - new Date().getTime() < 0;
	}

	@Override
	public boolean isSuccessful() {
		return actionAdapter.isSuccessful();
	}

	@Override
	public void interrupt() {
		actionAdapter.interrupt();
	}

	@Override
	public Mode getMode() {
		return actionAdapter.getMode();
	}

	@Override
	public void addNext(Action a) {
		actionAdapter.addNext(a);
	}

	@Override
	public void addPrevious(Action a) {
		actionAdapter.addPrevious(a);
	}

	@Override
	public int waitFor() {
		return actionAdapter.waitFor();
	}

	@Override
	public int waitForNext() {
		return actionAdapter.waitForNext();
	}

	@Override
	public String getName() {
		return actionAdapter.getName();
	}

	@Override
	public void setName(String s) {
		actionAdapter.setName(s);
	}

	@Override
	public boolean hasPrevious(Action a) {
		return actionAdapter.hasPrevious(a);
	}

	@Override
	public boolean hasNext(Action a) {
		return actionAdapter.hasNext(a);
	}

	@Override
	public Object getContext() {
		return actionAdapter.getContext();
	}

	@Override
	public void run(Object context) {
		actionAdapter.run(context);
	}

	@Override
	public Consumer<? extends Action> getFunc() {
		return actionAdapter.getFunc();
	}

	@Override
	public void run() {
		actionAdapter.run();
	}

	public static class Builder extends ActionBuilder<Builder, EveryTask> {
		long every = 0;

		@Override
		public EveryTask build() {
			return new EveryTask(this);
		}

		public Builder setEvery(long every) {
			this.every = every;

			return self();
		}

		@Override
		protected Builder self() {
			return this;
		}
	}
}
