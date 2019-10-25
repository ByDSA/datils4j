package es.danisales.multimedia.m3u;

import es.danisales.io.text.LinearStringFile;

import java.nio.file.Path;

public class Playlist extends LinearStringFile<Element> {
    private boolean isLoading;
    private Element lastReadLine;

    public Playlist(Path path) {
        super(path);
    }

    @Override
    final protected Element stringToLine(long i, String l) {
        if (!isLoading)
            throw new RuntimeException("Can't be called stringToLine if not loading");

        Element ret = null;
        StringBuilder sb = new StringBuilder(l);
        if (l.startsWith("#EXTINF:")) {
            sb.delete(0, "#EXTINF:".length());
            ret = new Element();
            readTitle(ret, sb);
        } else if (l.startsWith("#EXTVLCOPT:")) {
            sb.delete(0, "#EXTVLCOPT:".length());
            String sbStr = sb.toString();
            if (sbStr.startsWith("start-time=")) {
                sb.delete(0, "start-time=".length());
                lastReadLine.startTime = Integer.valueOf(sb.toString());
            } else if (sbStr.startsWith("stop-time=")) {
                sb.delete(0, "stop-time=".length());
                lastReadLine.stopTime = Integer.valueOf(sb.toString());
            }
        } else {
            readPathfile(lastReadLine, sb);
        }

        return ret;
    }

    @Override
    protected String lineToString(long i, Element element) {
        return element.renderText();
    }

    private void readTitle(Element element, StringBuilder line) {
        element.length = Integer.valueOf( popUntil(line, ",") );
        if (line.charAt(0) == ',')
            popUntil(line, ",");
        element.title = line.toString();
    }

    private void readPathfile(Element element, StringBuilder line) {
        element.path = line.toString();
    }

    private String popUntil(StringBuilder stringBuilder, String separator) {
        int index = stringBuilder.indexOf(separator);
        String ret = stringBuilder.substring(0, index);
        stringBuilder.delete(0, index + separator.length());

        return ret;
    }

    @Override
    public boolean load() {
        lastReadLine = null;
        isLoading = true;
        boolean ret = super.load();
        isLoading = false;
        lastReadLine = null;

        return ret;
    }
}
