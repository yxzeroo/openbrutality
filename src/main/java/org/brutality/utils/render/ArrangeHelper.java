package org.brutality.utils.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArrangeHelper {
    public static float computeFloat(float f, int n) {
        int n2 = Math.max(1, n);
        return (float)Math.round(f * n2) / n2;
    }

    public static Map<String, Float> arrange(List<ArrangeHelper.Entry> list, String str) {
        ArrayList<ArrangeHelper.Entry> list2 = new ArrayList<>(list);
        list2.sort(Comparator.comparingDouble(entry -> entry.factor2));
        HashMap map = new HashMap();
        ArrayList<ArrangeHelper.Entry> list3 = new ArrayList<>();

        for (ArrangeHelper.Entry entry2 : list2) {
            float f = entry2.factor2;
            boolean flag;
            if (!entry2.text.equals(str)) {
                do {
                    flag = false;

                    for (ArrangeHelper.Entry entry3 : list3) {
                        if (!entry3.text.equals(str)) {
                            float f2 = (Float)map.get(entry3.text);
                            float f3 = f2 + entry3.factor4 + 3.0F;
                            if (entry2.factor < entry3.factor + entry3.factor3
                                && entry2.factor + entry2.factor3 > entry3.factor
                                && f < f3
                                && f + entry2.factor4 > f2) {
                                f = f3;
                                flag = true;
                            }
                        }
                    }
                } while (flag);
            }

            map.put(entry2.text, f);
            list3.add(entry2);
        }

        return map;
    }

    private ArrangeHelper() {
    }

    public static final class Entry {
        public final String text;
        public final float factor;
        public final float factor2;
        public final float factor3;
        public final float factor4;

        public Entry(String text, float factor, float f, float f2, float f3) {
            this.text = text;
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.factor4 = f3;
        }
    }
}
