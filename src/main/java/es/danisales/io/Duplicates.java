package es.danisales.io;

import es.danisales.crypt.hash.Hash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Duplicates {
    private Duplicates() {
    } // noninstantiable

    public static List<List<File>> duplicatedFiles(List<File> files, boolean secure, Consumer<File> func) throws IOException {
        Map<File, Hash> file2hashMap = new HashMap<>();
        Map<Hash, List<File>> hash2filesMap = new HashMap<>();

        for (File f : files) {
            func.accept(f);
            Hash hash = defaultHashFileFunction(f);

            file2hashMap.put(f, hash);

            List<File> filesList = hash2filesMap.computeIfAbsent(hash, k -> new ArrayList<>());
            filesList.add(f);
        }

        List<List<File>> ret = null;

        if (secure) ;
        else
            ret = duplicatedFilesInsecure(hash2filesMap);

        return ret;
    }

    private static List<List<File>> duplicatedFilesInsecure(Map<Hash, List<File>> hash2filesMap) {
        List<List<File>> ret = new ArrayList<>();

        for (Map.Entry<Hash, List<File>> e : hash2filesMap.entrySet()) {
            Hash hash = e.getKey();
            List<File> list = e.getValue();

            ret.add(list);
        }

        return ret;
    }

    public static Hash defaultHashFileFunction(File f) throws IOException {
        return Hash.sha256fromFile(f);
    }
}
