package es.danisales.tasks;

import static com.google.common.base.Preconditions.checkArgument;

public class LoopTask extends ActionBounding {
	private LoopTask(Builder builder) {
		super(builder);
	}

	public static class Builder extends ActionBuilder<Builder, LoopTask, LoopTask> {
		@Override
        public LoopTask build() {
			checkArgument(instance == null, "Just one instantiation");
			instance = new LoopTask(this);
			return instance;
		}

		@Override
		protected Builder self() {
			return this;
		}
	}
}
