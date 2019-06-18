package es.danisales.ffmpeg;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public class FrameUnit implements TemporalUnit {
	double fps;
	
	public static final FrameUnit NTSC = new FrameUnit(30/1.001d);
	
	public FrameUnit(double fps) {
		this.fps = fps;
	}
	
	@Override
	public <R extends Temporal> R addTo(R arg0, long t) {
		return (R) arg0.plus( getDuration()  );
	}

	@Override
	public long between(Temporal a, Temporal b) {
		return 0;
	}
	
	public Duration of(long n) {
		return Duration.of( n, this );
	}
	
	public long of(Duration d) {
		return Math.round( d.toNanos() * 1d / getDuration().toNanos() );
	}

	@Override
	public Duration getDuration() {
		return Duration.ofNanos(  Math.round( 1000000000d / fps ) );
	}

	@Override
	public boolean isDateBased() {
		return false;
	}

	@Override
	public boolean isDurationEstimated() {
		return false;
	}

	@Override
	public boolean isTimeBased() {
		return true;
	}

}
