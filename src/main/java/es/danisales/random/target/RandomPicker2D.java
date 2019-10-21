package es.danisales.random.target;

import java.util.Objects;

class RandomPicker2D<PICK_TYPE extends Target<PICK_TYPE>> extends RandomPickerImp<PICK_TYPE> {
    RandomPicker2D(RandomPickerBuilder builder) {
        super(builder);
	}

	@Override
	public final PICK_TYPE pickDart(long dart) {
        checkNotEmpty();

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
    public void onRollDice() {
	}

	private long surfaceWithNext() {
		long size = 0;
        for (Target t : this) {
            t.onRollDice();
            size += Math.max(t.getSurface(), 0);
		}

		return size;
	}

    @Override
	public final PICK_TYPE pick() {
        checkNotEmpty();

        long surface = surfaceWithNext();
        checkSurface(surface);

        if (size() == 1) { // Para evitar el rand y sea más eficiente, especialmente para el SecureRandom
            Target<PICK_TYPE> t = get(0);
            return t.pick();
        }

        long dart = randUntil(surface);

		PICK_TYPE t = pickDart(dart);
		return t;
    }

    private void checkSurface(long surface) {
        if (surface <= 0)
            throw new IllegalStateException("El tamaño de la suma de los target es 0");
	}
}
