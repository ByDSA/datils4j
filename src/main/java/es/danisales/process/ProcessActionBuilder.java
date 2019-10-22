package es.danisales.process;

import es.danisales.utils.building.Builder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessActionBuilder extends Builder {
    private static final Map<String[], ProcessAction> registeredProcessAction = new ConcurrentHashMap<>();

    List<String> args = new ArrayList<>();

    ProcessActionBuilder() {
    } // Sólo se puede llamar desde ProcessAction.builder()

    static void registerInstance(ProcessActionImp p) {
        registeredProcessAction.put(p.paramsWithName, p);
    }

    @SuppressWarnings("WeakerAccess")
    public @NonNull ProcessActionBuilder addArg(String... newArgs) {
        if (newArgs != null)
            this.args.addAll(Arrays.asList(newArgs));

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public @NonNull ProcessActionBuilder addArg(List<String> newArgs) {
        if (newArgs != null)
            args.addAll(newArgs);

        return self();
    }

    @Override
    public @NonNull ProcessAction build() {
        ProcessAction ret = registeredProcessAction.get(args);

        if (ret == null)
            ret = new ProcessActionImp(this);

        return ret;
    }

    @Override
    protected @NonNull ProcessActionBuilder self() {
        return this;
    }

    @NonNull List<String> getParams() {
        int sizeArgs = args.size();
        if (sizeArgs > 1)
            return args.subList(1, sizeArgs - 1);
        else
            return Collections.emptyList();
    }
}
