package es.danisales.maths;

import java.util.function.Function;

public class CosFunction implements Function<Double, Double> {
	double period;
	long length;
	
	public CosFunction(double T, long l) {
		period = T;
		length = l;
	}

	@Override
	public Double apply(Double x) {
		return (Math.cos(x*length/(period)* Math.PI)+1)/2 * 127;
	}
}
