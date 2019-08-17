package es.danisales.rules;

import es.danisales.datastructures.ListAdapter;
import es.danisales.utils.OnceBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class RuleList extends ListAdapter<Rule> implements Rule, List<Rule> {
    private final boolean whenEmptyValue;

    private RuleList(Builder builder) {
        super(builder.list);

        whenEmptyValue = builder.whenEmptyValue;
    }

    @SuppressWarnings("unused")
    public static RuleList from(@NonNull Rule... r) {
        checkNotNull(r);
        Builder builder = new Builder()
                .setList(new ArrayList<>())
                .setWhenEmptyValue(true);
        RuleList l = builder.build();
        l.addAll(Arrays.asList(r));

        return l;
    }

    @SuppressWarnings("unused")
    public static RuleList fromDefaultValue(boolean whenEmptyValue) {
        Builder builder = new Builder()
                .setList(new ArrayList<>())
                .setWhenEmptyValue(whenEmptyValue);
        return builder.build();
    }

    @Override
    public boolean check() {
        synchronized (this) {
            if (isEmpty())
                return whenEmptyValue;

            for (Rule r : this)
                if (!r.check())
                    return false;
        }
        return true;
    }

    static class Builder extends OnceBuilder<Builder, RuleList> {
        List<Rule> list;
        Boolean whenEmptyValue;

        public Builder setList(List<Rule> list1) {
            checkNotInstantiated();
            checkNotNull(list1);
            list = list1;

            return self();
        }

        public Builder setWhenEmptyValue(boolean whenEmptyValue1) {
            checkNotInstantiated();
            whenEmptyValue = whenEmptyValue1;

            return self();
        }

        @Override
        protected RuleList buildOnce() {
            checkNotNull(list);
            checkNotNull(whenEmptyValue);
            return new RuleList(self());
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
