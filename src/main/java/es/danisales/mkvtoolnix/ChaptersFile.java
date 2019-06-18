package es.danisales.mkvtoolnix;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import es.danisales.io.Text.TextFile;
import es.danisales.strings.StringUtils;

public class ChaptersFile extends TextFile<String> {
	List<Chapter> chapters;

	public ChaptersFile(String path) {
		super( path );

		encoding = StandardCharsets.ISO_8859_1;
		chapters = new ArrayList();
	}

	@Override
	protected String stringToLine(long n, String l) {
		throw new RuntimeException("No se puede leer archivo de cap�tulos MKV");
	}

	public static String time2str(Duration d) {
		final long NANO_MILLI = 1000000;
		final long NANO_SEC = NANO_MILLI * 1000;
		final long NANO_MINUTE = 60 * NANO_SEC;
		final long NANO_HOUR = NANO_MINUTE * 60;

		long nano = d.toNanos();

		long h = nano / NANO_HOUR;
		nano -= h * NANO_HOUR;
		long m = nano / NANO_MINUTE;
		nano -= m * NANO_MINUTE;
		long s = nano / NANO_SEC;
		nano -= s * NANO_SEC;

		return StringUtils.zerosPad( (int)h, 2 ) + ":" + StringUtils.zerosPad( (int)m, 2 ) + ":" + StringUtils.zerosPad( (int)s, 2 ) + "." + StringUtils.zerosPad( (int)nano/ 1000000, 3 );
	}

	@Override
	public boolean save() {
		lines.clear();
		/*
		lines.add( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" );

		lines.add( "<!DOCTYPE Chapters SYSTEM \"matroskachapters.dtd\">" );
		lines.add( "<Chapters>" );
		lines.add( "  <EditionEntry>" );
		for (Chapter c : chapters) {
			lines.add( "    <ChapterAtom>" );
			lines.add( "      <ChapterTimeStart>" + time2str( c.getIni() ) + "</ChapterTimeStart>" );
			lines.add( "      <ChapterTimeEnd>" + time2str( c.getEnd() ) + "</ChapterTimeEnd>" );
			lines.add( "      <ChapterDisplay>" );
			lines.add( "        <ChapterString>" + c.getTitle() +"</ChapterString>" );
			lines.add( "        <ChapterLanguage>" + c.getLanguage().value() +"</ChapterLanguage>" );
			lines.add( "      </ChapterDisplay>" );
			lines.add( "    </ChapterAtom>" );
		}
		lines.add( "  </EditionEntry>" );
		lines.add( "</Chapters>" );*/


		for(int i = 0; i < chapters.size(); i++) {
			Chapter c = chapters.get( i );
			String n = "CHAPTER" + StringUtils.zerosPad( i, 2 );
			lines.add( n + "=" + time2str( c.getIni() ) );
			lines.add(n + "NAME=" + c.getTitle());
		}

		return super.save();
	}

	public void add(Chapter c) {
		chapters.add( c );
	}

	public void addAll(List<Chapter> l) {
		for(Chapter c : l)
			chapters.add( c );
	}

	public static class Chapter {
		String title;
		Duration ini;

		public Chapter(String t, Duration ini) {
			title = t;
			this.ini = ini;
		}

		public Duration getIni() {
			return ini;
		}

		public String getTitle() {
			return title;
		}
	}

	public int size() {
		return chapters.size();
	}
}
