package es.danisales.tasks;

import java.util.Date;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class EveryTask extends ActionBounding {
	private Date lastApply = new Date(0);
	private long every;

	private EveryTask(Builder builder) {
		super(Action.of(Objects.requireNonNull(builder.mode), Objects.requireNonNull(builder.function)));
		every = builder.every;
	}

	public static EveryTask newInstance(long every) {
		return new Builder().setEvery(every).build();
	}

	@Override
	public boolean isReady() {
		return lastApply.getTime() == 0 || lastApply.getTime() + every - new Date().getTime() < 0;
	}

	public static class Builder extends ActionBuilder<Builder, EveryTask> {
		long every = 0;

		@Override
		public EveryTask build() {
			checkArgument(every > 0);
			return new EveryTask(this);
		}

		@SuppressWarnings("WeakerAccess")
		public Builder setEvery(long every) {
			checkArgument(every > 0);
			this.every = every;

			return self();
		}

		@Override
		protected Builder self() {
			return this;
		}
	}
}
