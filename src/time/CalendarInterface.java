package time;

import java.util.List;

import rules.Rule;

public interface CalendarInterface extends Rule, List<Rule> {
	void addException(Rule r);
	List<Rule> getExceptions();
}
