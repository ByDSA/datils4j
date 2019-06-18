package es.danisales.time;

import java.util.List;

import es.danisales.rules.Rule;

public interface CalendarInterface extends Rule, List<Rule> {
	void addException(Rule r);
	List<Rule> getExceptions();
}
