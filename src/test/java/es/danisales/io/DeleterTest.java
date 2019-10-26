package es.danisales.io;

import es.danisales.io.finder.Finder;
import es.danisales.io.text.TextFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeleterTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createTestcase1() {
        try {
            temporaryFolder.create();
            temporaryFolder.newFile("a");
            temporaryFolder.newFile("b");
            temporaryFolder.newFile("c");
            File sub1 = temporaryFolder.newFolder("sub1");
            File sub2 = temporaryFolder.newFolder("sub2");
            File sub3 = temporaryFolder.newFolder("sub3");
            File emptyTestFolder = temporaryFolder.newFolder("emptyTest");

            createFile(sub1, "d");
            createFile(sub1, "e");
            createFile(sub2, "f");
            createFile(sub2, "g");
            createFile(sub3, "h");

            File zeroFilesFolder = createFolder(emptyTestFolder, "zeroFiles");
            createFile(zeroFilesFolder, "zero1");
            createFile(zeroFilesFolder, "zero2");
            createFile(zeroFilesFolder, "zero3");
            createFile(zeroFilesFolder, "zero4");
            File nonEmptyFolder = createFolder(emptyTestFolder, "nonEmpty");
            TextFile txtFile = new TextFile(Paths.get(nonEmptyFolder.getPath() + "/txt"));
            txtFile.append("aaa");

            File emptyFolder = emptyTestFolder;
            for (int i = 0; i < 10; i++)
                emptyFolder = createFolder(emptyFolder, "empty");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createFolder(File rootFile, String folderName) {
        File sub1 = new File(rootFile.getPath() + "/" + folderName);
        sub1.mkdirs();

        return sub1;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
    private File createFile(File rootFile, String fileName) throws IOException {
        File sub1 = new File(rootFile.getPath() + "/" + fileName);
        sub1.createNewFile();

        return sub1;
    }

    @Test
    public void fromFilesNonRecursively() {
        createTestcase1();

        List<File> files = new Finder()
                .from(temporaryFolder.getRoot())
                .nonRecursively()
                .onlyFiles()
                .find();

        assertEquals(3, files.size());

        assertTrue(
                new Deleter()
                        .from(files)
                        .delete()
        );

        files = new Finder()
                .from(temporaryFolder.getRoot())
                .nonRecursively()
                .onlyFiles()
                .find();

        assertEquals(0, files.size());
    }

    @Test
    public void fromFoldersNonRecursively() {
        createTestcase1();

        List<File> files = new Finder()
                .from(temporaryFolder.getRoot())
                .nonRecursively()
                .onlyFolders()
                .find();

        assertEquals(4, files.size());

        assertTrue(
                new Deleter()
                        .from(files)
                        .delete()
        );

        files = new Finder()
                .from(temporaryFolder.getRoot())
                .recursively()
                .onlyFolders()
                .find();

        assertEquals(0, files.size());
    }

    @Test
    public void emptyParents() {
        createTestcase1();

        List<File> files = new Finder()
                .from(temporaryFolder.getRoot())
                .recursively()
                .onlyFolders()
                .find();

        assertEquals(16, files.size());

        files.clear();
        files.add(new File(temporaryFolder.getRoot().getPath() + "/emptyTest/empty/empty/empty/empty/empty/empty/empty/empty/empty/empty"));

        assertTrue(new Deleter()
                .from(files)
                .emptyParents()
                .delete()
        );

        files = new Finder()
                .from(temporaryFolder.getRoot())
                .recursively()
                .onlyFolders()
                .find();

        assertEquals(6, files.size());
    }

    @Test
    public void emptyFileParents() {
        createTestcase1();

        File emptyTestFolder = new File(temporaryFolder.getRoot().getPath() + "/emptyTest/");

        List<File> files = new Finder()
                .from(emptyTestFolder)
                .nonRecursively()
                .onlyFolders()
                .find();

        assertEquals(3, files.size());

        files.clear();
        files.add(new File(temporaryFolder.getRoot().getPath() + "/emptyTest/zeroFiles/zero1"));

        assertTrue(new Deleter()
                .from(files)
                .emptyFileParents()
                .delete()
        );

        files = new Finder()
                .from(emptyTestFolder)
                .nonRecursively()
                .onlyFolders()
                .find();

        assertEquals(2, files.size());
    }
}