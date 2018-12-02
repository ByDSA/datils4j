package ffmpeg;

import java.io.File;
import java.nio.file.Path;

import process.ProcessUtils;

public class FfmpegUtils {
	private static String _ffmpeg_path = "ffmpeg";
	private static String _ffprobe_path = "ffprobe";
	
	public static Path ffmpeg_path() {
		return new File(_ffmpeg_path).toPath();
	}
	
	public static Path ffprobe_path() {
		return new File(_ffprobe_path).toPath();
	}
	
	public static void setFfmpegPath(String p) {
		_ffmpeg_path = p;
	}

	public static int ffmpeg(String[] params) {
		return ProcessUtils.execute(FfmpegUtils.ffmpeg_path().toString(), params);
	}
	
	public static void mp4_fast(File f) {
		String path = f.getAbsolutePath();
		FfmpegUtils.ffmpeg(new String[] {"-i", path, "-c:v", "h264", "-profile:v", "main", "-pixel_format", "yuv420p", "-preset", "fast", "-c:a", "aac", path + ".mp4"});
	}
	
	public static void gif(File f) {
		String path = f.getAbsolutePath();

		ffmpeg( new String[] {"-i", path, "-vf", "scale=-1:270", path + ".gif"} );
	}
	
	public static int ffprobe(String[] params) {
		return ProcessUtils.execute(FfmpegUtils.ffprobe_path().toString(), params);
	}
	
	public static int width(File f) {
		String path = f.getAbsolutePath();
		FfmpegUtils.ffprobe(new String[] {"-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width", "-of", "default=nw=1:nk=1", path});
		return 0;
	}
	
	
}
