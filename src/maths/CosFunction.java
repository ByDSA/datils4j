package maths;

public class CosFunction implements Function {
	double period;
	long length;
	
	public CosFunction(double T, long l) {
		period = T;
		length = l;
	}
	
	public double get(double x) {
		return (Math.cos(x*length/(period)*Math.PI)+1)/2 * 127;
	}
}
