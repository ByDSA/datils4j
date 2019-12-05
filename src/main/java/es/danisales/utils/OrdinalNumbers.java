package es.danisales.utils;

public class OrdinalNumbers {
    private OrdinalNumbers() {
    }

    // Source: https://stackoverflow.com/a/6810409
    public static String getStringFrom(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }
}
