package rules;

public abstract class IntervalRule<T> implements Rule {
	protected T ini, end;
	
	public IntervalRule(T a, T b) {
		ini = a;
		end = b;
	}
}
