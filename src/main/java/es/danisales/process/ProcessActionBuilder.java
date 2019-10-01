package es.danisales.process;

import es.danisales.utils.Builder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessActionBuilder extends Builder {
    private static final Map<String[], ProcessAction> registeredProcessAction = new ConcurrentHashMap<>();

    List<String> args = new ArrayList<>();

    static void registerInstance(ProcessActionImp p) {
        registeredProcessAction.put(p.paramsWithName, p);
    }

    public ProcessActionBuilder addArg(String... newArgs) {
        if (newArgs != null)
            this.args.addAll(Arrays.asList(newArgs));

        return self();
    }

    public ProcessActionBuilder addArg(List<String> newArgs) {
        if (newArgs != null)
            args.addAll(newArgs);

        return self();
    }

    @Override
    public ProcessAction build() {
        ProcessAction ret = registeredProcessAction.get(args);

        if (ret == null)
            ret = new ProcessActionImp(this);

        return ret;
    }

    @Override
    protected ProcessActionBuilder self() {
        return this;
    }

    List<String> getParams() {
        int sizeArgs = args.size();
        if (sizeArgs > 1)
            return args.subList(1, sizeArgs - 1);
        else
            return Collections.emptyList();
    }
}
