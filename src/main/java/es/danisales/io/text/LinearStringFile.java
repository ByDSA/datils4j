package es.danisales.io.text;

import es.danisales.io.FileAppendable;
import es.danisales.io.FileAutosavable;
import es.danisales.io.FileReadable;
import es.danisales.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public abstract class LinearStringFile<L>
        extends FileAutosavable
        implements FileAppendable<L>, FileReadable, Iterable<L>, TextRender {
    Charset encoding = StandardCharsets.UTF_8;
    @SuppressWarnings("WeakerAccess")
    protected List<L> lines = new ArrayList<>();
    private String lineSeparator = "\n";

    public LinearStringFile(Path path) {
        super(path);
    }

    @Override
    public void appendAll(List<L> f) throws IOException {
        Path path = toPath();
        lines.addAll(f);
        StringBuilder sb = joinLinesFrom(f);

        Files.write(
                path,
                sb.toString().getBytes(),
                StandardOpenOption.APPEND);
    }

    public Lines getLines() {
        return new Lines();
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    private StringBuilder joinLinesFrom(List<L> data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (L l : data) {
            sb.append(lineToString(i++, l));
            if (lineSeparator != null)
                sb.append(lineSeparator);
        }

        return sb;
    }

    private StringBuilder joinLinesBuffer() {
        return joinLinesFrom(lines);
    }

    @Override
    public String renderText() {
        return joinLinesBuffer().toString();
    }

    @Override
    public void save() {
        Path path = toPath();
        List<String> linesStr = new ArrayList<>();
        int i = 0;
        for (L l : lines) {
            linesStr.add(lineToString(i++, l));
        }

        try {
            Files.write(path, linesStr, encoding);
        } catch (IOException e) {
            callOnIOExceptionListeners(e);
        }
    }

    private void readLargeTextFile(Function<String, Boolean> fReadLine) {
        Path path = toPath();
        try (Scanner scanner = new Scanner(path, encoding.name())) {
            while (scanner.hasNextLine()) {
                if (!fReadLine.apply(scanner.nextLine()))
                    return;
            }
        } catch (IOException e) {
            callOnIOExceptionListeners(e);
        }
    }

    @Override
    public void load() {
        final AtomicLong i = new AtomicLong(0);
        readLargeTextFile(
                lineStr -> {
                    L l = stringToLine(i.getAndIncrement(), lineStr);
                    if (l != null)
                        lines.add(l);
                    return true;
                }

        );
    }

    abstract protected L stringToLine(long i, String l);

    abstract protected String lineToString(long i, L l);

    @Override
    public void append(L f) throws IOException {
        createParents();

        lines.add(f);

        byte[] bytes = (f.toString() + lineSeparator).getBytes();
        Files.write(
                toPath(),
                bytes,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void saveIfAutosave() {
        createParents();
        save();
    }

    private void createParents() {
        FileUtils.mkdirsParent(this);
    }

    @Override
    public @NonNull Iterator<L> iterator() {
        return getLines().iterator();
    }

    public class Lines implements List<L> {
        @Override
        public int size() {
            return lines.size();
        }

        @Override
        public boolean isEmpty() {
            return lines.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return lines.contains(o);
        }

        @Override
        public @NonNull Iterator<L> iterator() {
            return lines.iterator();
        }

        @Override
        public @NonNull Object[] toArray() {
            return lines.toArray();
        }

        @SuppressWarnings("SuspiciousToArrayCall")
        @Override
        @NonNull
        public <T> T[] toArray(@NonNull T[] a) {
            return lines.toArray(a);
        }

        private boolean autosaveIfTrue(boolean ret) {
            if (ret)
                saveIfAutosave();

            return ret;
        }

        @Override
        public boolean add(L l) {
            return autosaveIfTrue(lines.add(l));
        }

        @Override
        public boolean remove(Object o) {
            return autosaveIfTrue(lines.remove(o));
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return lines.containsAll(c);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends L> c) {
            return autosaveIfTrue(lines.addAll(c));
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends L> c) {
            return autosaveIfTrue(lines.addAll(index, c));
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return autosaveIfTrue(lines.removeAll(c));
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return autosaveIfTrue(lines.retainAll(c));
        }

        @Override
        public void clear() {
            boolean canDoAutosaving = !isEmpty();
            lines.clear();

            if (canDoAutosaving)
                saveIfAutosave();
        }

        @Override
        public L get(int index) {
            return lines.get(index);
        }

        @Override
        public L set(int index, L element) {
            L old = lines.set(index, element);
            if (old != element)
                saveIfAutosave();

            return old;
        }

        @Override
        public void add(int index, L element) {
            lines.add(index, element);
            saveIfAutosave();
        }

        @Override
        public L remove(int index) {
            L old = lines.remove(index);
            if (old != null)
                saveIfAutosave();

            return old;
        }

        @Override
        public int indexOf(Object o) {
            return lines.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return lines.lastIndexOf(o);
        }

        @Override
        public @NonNull ListIterator<L> listIterator() {
            return lines.listIterator();
        }

        @Override
        public @NonNull ListIterator<L> listIterator(int index) {
            return lines.listIterator(index);
        }

        @Override
        public @NonNull List<L> subList(int fromIndex, int toIndex) {
            return lines.subList(fromIndex, toIndex);
        }
    }
}
