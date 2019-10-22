package es.danisales.random.target;

import es.danisales.datastructures.Picker;

public interface Target<RET> extends Picker<RET> {
	RET pickDart(long dart);
	long getSurface();

    void onRollDice();
}
