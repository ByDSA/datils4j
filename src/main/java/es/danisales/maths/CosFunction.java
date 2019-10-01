package es.danisales.maths;

import java.util.function.Function;

@SuppressWarnings({"unused","WeakerAccess"})
public class CosFunction implements Function<Double, Double> {
	double period;
	long length;

	private Object lengthLock = new Object();
	private Object periodLock = new Object();

	public CosFunction(double T, long l) {
		period = T;
		length = l;
	}

	@Override
	public Double apply(Double x) {
		synchronized (lengthLock) {
			synchronized (periodLock) {
				return (Math.cos(x * length / (period) * Math.PI) + 1) / 2 * 127;
			}
		}
	}

	public long getLength() {
		synchronized (lengthLock) {
			return length;
		}
	}

	public void setLength(long length) {
		synchronized (lengthLock) {
			this.length = length;
		}
	}

	public double getPeriod() {
		synchronized (periodLock) {
			return period;
		}
	}

	public void setPeriod(double period) {
		synchronized (periodLock) {
			this.period = period;
		}
	}
}

