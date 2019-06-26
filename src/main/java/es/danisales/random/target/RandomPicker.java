package es.danisales.random.target;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class RandomPicker<PICK_TYPE extends Target> extends ArrayList<Target> implements Target {
	private static Random rand;
	static {
		setNormalRandom();
	}

	public RandomPicker() {
	}

    @SuppressWarnings("unused")
    public static void setSecureRandom() {
        rand = new SecureRandom();
    }

    @SuppressWarnings("WeakerAccess")
    public static void setNormalRandom() {
        rand = new Random();
    }

	@Override
	public final PICK_TYPE pickDart(long dart) {
		if (size() == 0)
			throw new EmptyException();

		long acc = 0;
        Target dartTarget = null;
        for (Target t : this) {
            acc += Math.max(0, t.getSurface());
			if (dart < acc) {
				dartTarget = t;
                acc -= t.getSurface();
				break;
			}
		}

		Objects.requireNonNull(dartTarget);
		PICK_TYPE ret = (PICK_TYPE) dartTarget.pickDart(dart - acc);

		return ret;
	}

	@Override
	public final long getSurface() {
		long size = 0;
        for (Target t : this) {
            size += Math.max(t.getSurface(), 0);
		}

		return size;
	}

	@Override
	public void beforeOnPick() {
	}

	@Override
	public void afterOnPick() {
	}

	@Override
	public void next() {
	}

	private long surfaceWithNext() {
		long size = 0;
        for (Target t : this) {
			t.next();
            size += Math.max(t.getSurface(), 0);
		}

		return size;
	}

	private static long rand(long max) {
		return ( rand.nextLong() & Long.MAX_VALUE ) % max;
	}

    @Override
	public final PICK_TYPE pick() {
        if (size() == 0)
            throw new EmptyException();

		beforeOnPick();
        long surface = surfaceWithNext();

        if (surface <= 0)
            throw new NoSurfaceException();

        if (size() == 1) { // Para evitar el rand y sea más eficiente, especialmente para el SecureRandom
            Target t = get(0);
            return (PICK_TYPE) t.pick();
        }

        long dart = rand(surface);

		PICK_TYPE t = pickDart(dart);
		afterOnPick();
		return t;
    }

	public static class NoSurfaceException extends RuntimeException {
		@SuppressWarnings("WeakerAccess")
		public NoSurfaceException() {
			super("El tamaño de la suma de los target es 0");
		}
	}

	public static class EmptyException extends RuntimeException {
		@SuppressWarnings("WeakerAccess")
		public EmptyException() {
			super("PackTarget vacío");
		}
	}
}
