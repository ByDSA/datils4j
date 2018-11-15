package settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;

import Log.String.Logging;
import functions.BiForEachThrows;
import io.Files;

public class Settings extends ConcurrentHashMap<String, Object> implements BiForEachThrows<String, Object, Exception> {
	String path;

	public Settings(String p) {
		path = p;
	}

	public boolean load() {
		boolean ret = Files.readObject( path, (in) -> {
			final int S = in.readInt();

			for(int i = 0; i < S; i++) {
				int kLength = in.readInt();
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j < kLength; j++)
					sb.append( in.readChar() );

				String key = sb.toString();
				Object value = in.readObject();

				put(key, value);
			}
		});
		
		if (!ret)
			Logging.error("No se pudo cargar la configuración: " + path);
		
		return ret;
	}

	public void save() {
		Files.writeObject( path, (o) -> {
			o.writeInt( size() );

			forEachThrowing((String key, Object value ) -> {
				o.writeInt( key.length() );
				o.writeChars( key );
				o.writeObject( value );
			});
		});
	}

	public void show() {
		forEach( (String key, Object value ) -> {
			Logging.info( key + ": " + value );
		});
	}

	/** Static **/

	private static Settings _settings;

	private static void loadIfNotExists() {
		if (_settings == null) {
			_settings = new Settings("data/settings");
			_settings.load();
		}
	}

	public static Settings main() {
		loadIfNotExists();
		return _settings;
	}
}
