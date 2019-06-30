package es.danisales.random.target;

public interface Target<RET> {
	RET pickDart(long dart);
	RET pick();
	long getSurface();
	void next();
	void beforeOnPick();
	void afterOnPick(RET picked);
}
