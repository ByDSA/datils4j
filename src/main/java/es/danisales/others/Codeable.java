package es.danisales.others;

import es.danisales.utils.Valuable;

@Deprecated
public interface Codeable extends Valuable<Integer> {
	int getCode();

	default Integer getValue() {
		return getCode();
	}

}
