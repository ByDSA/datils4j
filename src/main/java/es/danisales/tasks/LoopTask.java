package es.danisales.tasks;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoopTask implements Action {
	private Action actionAdapter;

	private LoopTask(Builder builder) {
		checkNotNull(builder.mode);
		checkNotNull(builder.function);

		actionAdapter = Action.of(builder.mode, builder.function);
	}

	@Override
	public long getCheckingTime() {
		return actionAdapter.getCheckingTime();
	}

	@Override
	public void setCheckingTime(long checkingTime) {
		actionAdapter.setCheckingTime(checkingTime);
	}

	@Override
	public void addAfter(Runnable r) {
		actionAdapter.addAfter(r);
	}

	@Override
	public void addInterruptedListener(Runnable a) {
		actionAdapter.addInterruptedListener(a);
	}

	@Override
	public boolean isRunning() {
		return actionAdapter.isRunning();
	}

	@Override
	public boolean isWaitingCheck() {
		return actionAdapter.isWaitingCheck();
	}

	@Override
	public boolean isEnding() {
		return actionAdapter.isEnding();
	}

	@Override
	public boolean isDone() {
		return actionAdapter.isDone();
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
	public void join() throws InterruptedException {
		actionAdapter.join();
	}

	@Override
	public void joinNext() {
		actionAdapter.joinNext();
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
	public void setCheckFunction(Supplier<Boolean> f) {
		actionAdapter.setCheckFunction(f);
	}

	@Override
	public boolean check() {
		return actionAdapter.check();
	}

	@Override
	public void run() {
		actionAdapter.run();
	}

	public static class Builder extends ActionBuilder<Builder, LoopTask> {
		@Override
		public Action build() {
			return new LoopTask(this);
		}

		@Override
		protected Builder self() {
			return this;
		}
	}
}
