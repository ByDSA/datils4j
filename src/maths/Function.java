package maths;

public interface Function {
	default double get(double x) {
		return x;
	}
}
