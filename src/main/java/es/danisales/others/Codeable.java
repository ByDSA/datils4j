package es.danisales.others;

import java.util.function.Supplier;

@Deprecated
public interface Codeable extends Supplier<Integer> {
	int getCode();

	@Override
	default Integer get() {
		return getCode();
	}
}
