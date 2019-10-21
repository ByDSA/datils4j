package es.danisales.random.target;

import es.danisales.utils.datastructures.Picker;

public interface Target<RET> extends Picker<RET> {
	RET pickDart(long dart);
	long getSurface();

    void onRollDice();
}
