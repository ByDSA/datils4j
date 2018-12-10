package maths;

public class LinealFunction implements Function {
	double a, b;
	
	public LinealFunction(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	public double get(double x) {
		return a + b*x;
	}
}
