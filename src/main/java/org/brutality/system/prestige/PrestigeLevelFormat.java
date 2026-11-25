package org.brutality.system.prestige;

public final class PrestigeLevelFormat {
    private static final String[] stringArray = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    private static final String[] stringArray2 = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] stringArray3 = new String[]{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};

    private PrestigeLevelFormat() {
    }

    public static int computeInt(String raw) {
        if (raw != null && !raw.isEmpty()) {
            String str = raw.trim().toUpperCase();
            if (str.matches("\\d+")) {
                return Integer.parseInt(str);
            }
            int n = 0;
            int n2 = 0;

            for (int i = str.length() - 1; i >= 0; i--) {
                int n3 = computeInt(str.charAt(i));
                if (n3 == 0) {
                    return -1;
                }

                if (n3 < n2) {
                    n -= n3;
                } else {
                    n += n3;
                }

                n2 = n3;
            }

            return n > 0 ? n : -1;
        }
        return -1;
    }

    public static String format(int level) {
        if (level < 0) {
            return "?";
        }
        return level == 0 ? "0" : computeString(level);
    }

    private static int computeInt(char ch) {
        switch (ch) {
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'W':
            default:
                return 0;
            case 'I':
                return 1;
            case 'L':
                return 50;
            case 'M':
                return 1000;
            case 'V':
                return 5;
            case 'X':
                return 10;
        }
    }

    private static String computeString(int n) {
        if (n <= 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(stringArray3[n / 100]);
        n %= 100;
        sb.append(stringArray2[n / 10]);
        n %= 10;
        sb.append(stringArray[n]);
        return sb.toString();
    }
}
