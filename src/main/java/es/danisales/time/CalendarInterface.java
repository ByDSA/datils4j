package es.danisales.time;

import es.danisales.rules.Rule;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public interface CalendarInterface extends Rule, List<Rule> {
	void addException(@NonNull Rule r);

    void addAllException(@NonNull List<? extends Rule> r);
	@NonNull List<Rule> getExceptions();
}
