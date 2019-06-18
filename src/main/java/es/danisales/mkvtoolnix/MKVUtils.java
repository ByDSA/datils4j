package es.danisales.mkvtoolnix;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import es.danisales.process.ProcessUtils;

public class MKVUtils {
	private static Path _mkvtoolnix_folder = new File("C:/es.danisales.mkvtoolnix/").toPath();

	public static Path mkvtoolnix_path() {
		return _mkvtoolnix_folder;
	}

	public static void setMkvtoolnixPath(Path p) {
		_mkvtoolnix_folder = p;
	}

	public static Path mkvmerge_path() {
		return new File(_mkvtoolnix_folder + "/mkvmerge").toPath();
	}

	public static Path mkvextract_path() {
		return new File(_mkvtoolnix_folder + "/mkvextract").toPath();
	}

	public static int mkvmerge(List<String> args) {
		return ProcessUtils.execute(MKVUtils.mkvmerge_path().toString(), args);
	}
	
	public static int mkvmerge(File out, List<File> in) {
		List<String> list = new ArrayList();
		list.add( "-o" );
		list.add( out.getAbsolutePath() );
		for (File f : in)
			list.add( f.getAbsolutePath() );
		return mkvmerge( list );
	}
}
