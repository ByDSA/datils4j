package es.danisales.mkvtoolnix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.danisales.enums.EnumValue;
import es.danisales.io.FileUtils;

public class MKV extends File {
	String title;
	ChaptersFile chaptersFile;

	String _filename, _folder;

	List<File> input;

	Language defaultLanguage;

	public MKV(String folder, String fname) {
		super(folder + "/" + fname + ".mkv");
		_folder = folder;
		_filename =  fname;
	}

	public MKV(File f) {
		super(f.getAbsolutePath());
		_folder = f.getParent();
		_filename = FileUtils.stripExtension( f.getName() );
	}

	public void setTile(String t) {
		title = t;
	}

	public void add(File in) {
		if (input == null)
			input = new ArrayList();
		input.add( in );
	}

	public ChaptersFile chapters() {
		if (chaptersFile == null) {
			chaptersFile = new ChaptersFile( this.getAbsolutePath() + "-chapters.txt" );
		}
		return chaptersFile;
	}

	public int render() {
		if (input == null)
			throw new RuntimeException("No hay inputs");

		List<String> list = new ArrayList();
		list.add( "-o" );
		list.add( getAbsolutePath() );
		for (File f : input)
			list.add( f.getAbsolutePath() );
		if (title != null) {
			list.add( "--title" );
			list.add( title );
		}
		if (defaultLanguage != null) {
			list.add( "--default-language" );
			list.add( defaultLanguage.value() );
		}

		if (chaptersFile != null && chaptersFile.size() > 0) {
			chaptersFile.save();
			list.add( "--chapters" );
			list.add( chaptersFile.toPath().toAbsolutePath().toString() );
		}
		
		int r = MKVUtils.mkvmerge( list );
		
		chaptersFile.delete();
		
		return r;
	}

	public enum Language implements EnumValue<String> {
		SPA("spa"), ENG("eng");

		String val;

		Language(String v) {
			val = v;
		}

		public String value() {
			return val;
		}
	}
}
