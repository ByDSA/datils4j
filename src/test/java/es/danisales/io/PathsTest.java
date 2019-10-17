package es.danisales.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathsTest {
    @Test
    public void stripExtensionFrom() {
        String ret = FileUtils.Paths.stripExtensionFrom("123.txt.zip");
        assertEquals("123.txt", ret);
        ret = FileUtils.Paths.stripExtensionFrom(ret);
        assertEquals("123", ret);
    }
}
