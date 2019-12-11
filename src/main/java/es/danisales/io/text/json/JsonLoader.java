package es.danisales.io.text.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.danisales.io.text.TextFile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class JsonLoader {
    public static <T> @Nullable T load(@NonNull Path path, @NonNull Class<T> tClass) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(tClass);

        AtomicBoolean loadingError = new AtomicBoolean(false);
        TextFile file = new TextFile(path);
        file.addOnIOException((IOException e) -> {
            loadingError.set(true);
        });
        file.load();
        if (loadingError.get()) {
            try {
                throw new IOException("Cannot load " + file.toPath().toString());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String json = file.renderText();
        final Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }

    @SuppressWarnings({"UnusedReturnValue"})
    public static <T> boolean save(@NonNull Path path, @NonNull T obj) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(obj);
        AtomicBoolean ret = new AtomicBoolean(true);

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TextFile file = new TextFile(path);
        Consumer<IOException> c = (IOException e) -> ret.set(false);
        file.addOnIOException(c);
        file.getLines().add(gson.toJson(obj));
        file.save();

        return ret.get();
    }
}
