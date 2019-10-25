package es.danisales.multimedia.m3u;

import es.danisales.io.text.TextRender;

import java.util.Objects;

public class Element implements TextRender {
    public String title = "TITLE";
    public String path = "";
    public float startTime = -1, stopTime = -1;
    public int length = -1;

    @Override
    public String renderText() {
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\r\n");
        sb.append("#EXTINF:");
        sb.append(length);
        sb.append(",");
        if (title.contains(","))
            sb.append(",");
        sb.append(title);
        sb.append("\r\n");

        if (startTime >= 0) {
            sb.append("#EXTVLCOPT:start-time=");
            sb.append(startTime);
            sb.append("\r\n");
        }

        if (stopTime >= 0) {
            sb.append("#EXTVLCOPT:stop-time=");
            sb.append(stopTime);
            sb.append("\r\n");
        }

        sb.append(path);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof Element) )
            return false;

        Element casted = (Element) o;

        return Objects.equals(title, casted.title)
                && Objects.equals(path, casted.path)
                && startTime == casted.startTime
                && stopTime == casted.stopTime
                && length == casted.length;
    }
}
