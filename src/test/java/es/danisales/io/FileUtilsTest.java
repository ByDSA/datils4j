package es.danisales.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class FileUtilsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void mkdirsParent() throws IOException {
        temporaryFolder.create();
        File f = new File(temporaryFolder.getRoot() + "/1/2/3/a.txt");

        FileUtils.mkdirsParent(f);
        assertTrue(f.createNewFile());
    }

    @Test
    public void fileExists() {
    }
}