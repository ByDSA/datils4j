package es.danisales.io.text.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.danisales.io.text.TextFile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class JsonLoader {
    public static <T> @Nullable T load(@NonNull Path path, @NonNull Class<T> tClass) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(tClass);

        TextFile file = new TextFile(path);
        if (!file.load()) {
            try {
                throw new IOException("Cannot load " + file.toPath().toString());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String json = file.joinLines();
        final Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
    public static <T> boolean save(@NonNull Path path, @NonNull T obj) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(obj);

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TextFile file = new TextFile(path);
        file.add( gson.toJson(obj) );
        return file.save();
    }
}
