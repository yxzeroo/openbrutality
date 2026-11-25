package org.brutality.utils.misc;

public class MiscHelper {
    private static final int count = 1;
    private static final int count2 = 3999;
    private static final String[] stringArray = new String[]{"", "M", "MM", "MMM", "MMMM"};
    private static final String[] stringArray2 = new String[]{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] stringArray3 = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] stringArray4 = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    public static String computeString(int n) {
        if (n >= 1 && n <= 3999) {
            return stringArray[n / 1000] + stringArray2[n % 1000 / 100] + stringArray3[n % 100 / 10] + stringArray4[n % 10];
        }
        throw new IllegalArgumentException(String.format("The number must be in the range [%d, %d]", 1, 3999));
    }
}
