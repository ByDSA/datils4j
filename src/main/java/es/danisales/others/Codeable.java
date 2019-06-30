package es.danisales.others;

import es.danisales.enums.EnumValue;

@Deprecated
public interface Codeable extends EnumValue<Integer> {
	int getCode();

	default Integer value() {
		return getCode();
	}

}
