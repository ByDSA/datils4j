package es.danisales.tasks;

public class LoopTask extends ActionBounding {
	private LoopTask(Builder builder) {
		super(Action.of(builder.mode, builder.function));
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
