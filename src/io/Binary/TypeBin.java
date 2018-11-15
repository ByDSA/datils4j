package io.Binary;

public abstract class TypeBin<T> implements Binary {
	private T var;
	
	public TypeBin(T v) {
		var = v;
	}
	
	public TypeBin() {
		this(null);
	}
	
	public T get() {
		return var;
	}
	
	public void set(T v) {
		var = v;
	}
	
	@Override
	public String toString() {
		return var.toString();
	}
}
