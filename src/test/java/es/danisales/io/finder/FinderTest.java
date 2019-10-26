package es.danisales.io.finder;

import es.danisales.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private void testCase1() throws IOException {
        temporaryFolder.create();
        File sub1 = temporaryFolder.newFolder("sub1");
        File sub2 = temporaryFolder.newFolder("sub2");

        temporaryFolder.newFile("a.txt");
        temporaryFolder.newFile("b.txt");
        temporaryFolder.newFile("c.txt");
        temporaryFolder.newFile("sub.zip");

        assertTrue(new File(sub1.getPath() + "/d.txt").createNewFile());
        assertTrue(new File(sub1.getPath() + "/e.txt").createNewFile());
        assertTrue(new File(sub1.getPath() + "/1.doc").createNewFile());
        assertTrue(new File(sub1.getPath() + "/2.doc").createNewFile());

        File sub11 = new File(sub1.getPath() + "/sub11");
        assertTrue(sub11.mkdir());
        assertTrue(new File(sub11.getPath() + "/sub.txt").createNewFile());

        assertTrue(new File(sub2.getPath() + "/f.txt").createNewFile());
        assertTrue(new File(sub2.getPath() + "/g.txt").createNewFile());
        assertTrue(new File(sub2.getPath() + "/3.doc").createNewFile());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void testCase2() throws IOException {
        temporaryFolder.create();
        File emptyFolder = temporaryFolder.newFolder("empty");
        for (int i = 0; i < 10; i++) {
            emptyFolder = new File(emptyFolder.getPath() + "/empty");
            assertTrue(emptyFolder.mkdir());
        }

        new File(temporaryFolder.newFolder("nonempty").getPath() + "/a").createNewFile();
    }

    @Test
    public void byExtensionNonRecursively() throws IOException {
        testCase1();

        List<File> foundFiles = new Finder()
                .from(temporaryFolder.getRoot())
                .byExtension("txt")
                .nonRecursively()
                .sort(FileUtils.Comparators.fullpathComparator)
                .find();

        assertEquals(3, foundFiles.size());
        assertEquals("a.txt", foundFiles.get(0).getName());
        assertEquals("b.txt", foundFiles.get(1).getName());
        assertEquals("c.txt", foundFiles.get(2).getName());
    }

    @Test
    public void byExtensionRecursively() throws IOException {
        testCase1();

        List<File> foundFiles = new Finder()
                .from(temporaryFolder.getRoot())
                .byExtension("txt")
                .recursively()
                .sort(FileUtils.Comparators.fullpathComparator)
                .find();

        assertEquals(8, foundFiles.size());
        assertEquals("a.txt", foundFiles.get(0).getName());
        assertEquals("b.txt", foundFiles.get(1).getName());
        assertEquals("c.txt", foundFiles.get(2).getName());
        assertEquals("d.txt", foundFiles.get(3).getName());
        assertEquals("e.txt", foundFiles.get(4).getName());
        assertEquals("sub.txt", foundFiles.get(5).getName());
        assertEquals("f.txt", foundFiles.get(6).getName());
        assertEquals("g.txt", foundFiles.get(7).getName());
    }

    @Test
    public void addRule() throws IOException {
        testCase1();

        List<File> foundFiles = new Finder()
                .from(temporaryFolder.getRoot())
                .byExtension("txt")
                .nonRecursively()
                .addRule((File f) -> !f.getName().startsWith("a"))
                .sort(FileUtils.Comparators.fullpathComparator)
                .find();

        assertEquals(2, foundFiles.size());

        assertEquals("b.txt", foundFiles.get(0).getName());
        assertEquals("c.txt", foundFiles.get(1).getName());
    }

    @Test
    public void onlyFiles() throws IOException {
        testCase1();

        List<File> foundFiles = new Finder()
                .from(temporaryFolder.getRoot())
                .nonRecursively()
                .addRule((File f) -> f.getName().startsWith("sub"))
                .onlyFiles()
                .sort(FileUtils.Comparators.fullpathComparator)
                .find();

        assertEquals(1, foundFiles.size());

        assertEquals("sub.zip", foundFiles.get(0).getName());
    }

    @Test
    public void onlyFolders() throws IOException {
        testCase1();

        List<File> foundFiles = new Finder()
                .from(temporaryFolder.getRoot())
                .recursively()
                .addRule((File f) -> f.getName().startsWith("sub"))
                .onlyFolders()
                .sort(FileUtils.Comparators.fullpathComparator)
                .find();


        assertEquals("sub1", foundFiles.get(0).getName());
        assertEquals("sub11", foundFiles.get(1).getName());
        assertEquals("sub2", foundFiles.get(2).getName());

        assertEquals(3, foundFiles.size());
    }

    @Test
    public void filesAndFolders() throws IOException {
        testCase1();

        List<File> foundFilesAndFolders = new Finder()
                .from(temporaryFolder.getRoot())
                .recursively()
                .addRule((File f) -> f.getName().startsWith("sub"))
                .find();

        assertEquals("sub.zip", foundFilesAndFolders.get(0).getName());
        assertEquals("sub1", foundFilesAndFolders.get(1).getName());
        assertEquals("sub11", foundFilesAndFolders.get(2).getName());
        assertEquals("sub.txt", foundFilesAndFolders.get(3).getName());
        assertEquals("sub2", foundFilesAndFolders.get(4).getName());

        assertEquals(5, foundFilesAndFolders.size());
    }

    @Test
    public void emptyFolders() throws IOException {
        testCase2();
        assertEquals(1, numberOfEmptyFolders());
    }

    @Test
    public void deleteEmptyFolders() throws IOException {
        testCase2();
        assertEquals(12, numberOfFolders());

        assertTrue(new Finder()
                .from(temporaryFolder.getRoot())
                .emptyFolders()
                .recursively()
                .findAndDelete()
        );

        assertEquals(1, numberOfFolders());
        assertEquals(0, numberOfEmptyFolders());
    }

    private int numberOfFolders() {
        List<File> folders = new Finder()
                .from(temporaryFolder.getRoot())
                .onlyFolders()
                .recursively()
                .find();

        return folders.size();
    }

    private int numberOfEmptyFolders() {
        return findEmptyFoldersRecursively(temporaryFolder.getRoot()).size();
    }

    private List<File> findEmptyFoldersRecursively(File root) {
        return new Finder()
                .from(root)
                .recursively()
                .emptyFolders()
                .find();
    }
}