package es.danisales.process;

import java.util.List;

@SuppressWarnings("unused")
public interface ProcessActionDisableAddArgs {
    default ProcessActionBuilder addArg(String... arg) {
        throw new RuntimeException("Disabled");
    }

    default ProcessActionBuilder addArg(List<String> arg) {
        throw new RuntimeException("Disabled");
    }
}
