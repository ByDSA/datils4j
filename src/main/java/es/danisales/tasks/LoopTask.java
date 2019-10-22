package es.danisales.tasks;

import org.checkerframework.checker.nullness.qual.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public class LoopTask extends ActionAdapter {
	private LoopTask(Builder builder) {
		super(builder);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ActionBuilder<Builder, LoopTask, LoopTask> {
		private Builder() {
		}

		@Override
        public @NonNull LoopTask buildOnce() {
			checkArgument(instance == null, "Just one instantiation");
			instance = new LoopTask(this);
			return instance;
		}

		@Override
        protected @NonNull Builder self() {
			return this;
		}
	}
}
