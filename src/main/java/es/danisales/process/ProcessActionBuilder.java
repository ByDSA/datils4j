package es.danisales.process;

import es.danisales.utils.building.OnceBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessActionBuilder extends OnceBuilder<ProcessActionBuilder, ProcessAction> {
    private static final Map<String[], ProcessAction> registeredProcessAction = new ConcurrentHashMap<>();

    List<String> args = new ArrayList<>();

    ProcessActionBuilder() {
    } // Sólo se puede llamar desde ProcessAction.builder()

    static void registerInstance(ProcessActionImp p) {
        registeredProcessAction.put(p.paramsWithName, p);
    }

    @SuppressWarnings("WeakerAccess")
    public @NonNull ProcessActionBuilder addArg(@NonNull List<String> newArgs) {
        args.addAll(newArgs);

        return self();
    }

    public @NonNull ProcessActionBuilder addArg(int pos, @NonNull List<String> newArgs) {
        args.addAll(pos, newArgs);

        return self();
    }

    @NonNull
    @Override
    protected ProcessAction buildOnce() {
        return new ProcessActionImp(this);
    }

    @NonNull List<String> getParams() {
        int sizeArgs = args.size();
        if (sizeArgs > 1)
            return args.subList(1, sizeArgs - 1);
        else
            return Collections.emptyList();
    }

    @NonNull
    @Override
    protected ProcessActionBuilder self() {
        return this;
    }
}
