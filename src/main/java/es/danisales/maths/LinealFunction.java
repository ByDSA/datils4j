package es.danisales.maths;

import java.util.function.Function;

public class LinealFunction implements Function<Double, Double> {
	double a, b;
	
	public LinealFunction(double a, double b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Double apply(Double x) {
		return a + b*x;
	}
}
