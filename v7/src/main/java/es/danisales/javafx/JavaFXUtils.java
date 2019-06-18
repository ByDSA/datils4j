package es.danisales.javafx;

import java.io.File;
import java.util.function.Function;

import es.danisales.log.string.Logging;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

public class JavaFXUtils {
	public static File directoryShowDialogFilter(DirectoryChooser dc, Window ownerWindow, String errorMsg, Function<File, Boolean> func) {
		boolean ok = false;
		File f;
		do {
			f = dc.showDialog( ownerWindow );
			ok = f == null || func.apply( f );
			if (!ok)
				Logging.error( errorMsg );
		} while (!ok);

		return f;
	}
}
