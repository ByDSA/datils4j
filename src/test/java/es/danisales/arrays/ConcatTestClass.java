package es.danisales.arrays;

class ConcatTestClass {
    static final ConcatTestClass[][] DATA_TEST = new ConcatTestClass[][]{
            {ConcatTestClass.of(1), ConcatTestClass.of(2), ConcatTestClass.of(3)},
            {ConcatTestClass.of(4), ConcatTestClass.of(5), ConcatTestClass.of(6), ConcatTestClass.of(7)},
            {ConcatTestClass.of(8), ConcatTestClass.of(9), ConcatTestClass.of(10), ConcatTestClass.of(11), ConcatTestClass.of(12)},
            {ConcatTestClass.of(1), ConcatTestClass.of(2), ConcatTestClass.of(3)},
            {ConcatTestClass.of(4), ConcatTestClass.of(5), ConcatTestClass.of(6), ConcatTestClass.of(7)},
            {ConcatTestClass.of(8), ConcatTestClass.of(9), ConcatTestClass.of(10), ConcatTestClass.of(11), ConcatTestClass.of(12)},
    };
    private Object i;

    private ConcatTestClass(Object i) {
        this.i = i;
    }

    private static ConcatTestClass of(Object i) {
        return new ConcatTestClass(i);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConcatTestClass)
            return ((ConcatTestClass) o).i.equals(i);
        else
            return o.equals(i);
    }
}
