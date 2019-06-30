package es.danisales.random.target;

import java.util.Objects;

public class RandomPicker2D<PICK_TYPE extends Target<PICK_TYPE>> extends RandomPicker<Target<PICK_TYPE>, PICK_TYPE> {
	public RandomPicker2D() {
	}

	@Override
	public final PICK_TYPE pickDart(long dart) {
		if (size() == 0)
			throw new RandomPicker1D.EmptyException();

		long acc = 0;
        Target<PICK_TYPE> dartTarget = null;
        for (Target<PICK_TYPE> t : this) {
            acc += Math.max(0, t.getSurface());
			if (dart < acc) {
				dartTarget = t;
                acc -= t.getSurface();
				break;
			}
		}

		Objects.requireNonNull(dartTarget);

		return dartTarget.pickDart(dart - acc);
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
	public void afterOnPick(PICK_TYPE picked) {
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

    @Override
	public final PICK_TYPE pick() {
        if (size() == 0)
            throw new RandomPicker1D.EmptyException();

		beforeOnPick();
        long surface = surfaceWithNext();

        if (surface <= 0)
            throw new NoSurfaceException();

        if (size() == 1) { // Para evitar el rand y sea más eficiente, especialmente para el SecureRandom
            Target<PICK_TYPE> t = get(0);
            return t.pick();
        }

        long dart = rand(surface);

		PICK_TYPE t = pickDart(dart);
		afterOnPick(t);
		return t;
    }

	public static class NoSurfaceException extends RuntimeException {
		@SuppressWarnings("WeakerAccess")
		public NoSurfaceException() {
			super("El tamaño de la suma de los target es 0");
		}
	}
}
