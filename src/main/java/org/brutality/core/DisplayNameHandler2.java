package org.brutality.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayNameHandler2 {
    public static boolean flag;
    public static Map<String, String> lookupMap = new DisplayNameHandler2.Entry();
    public static Map<String, String> lookupMap2 = new DisplayNameHandler2.Entry2();
    public static Map<String, String> lookupMap3 = new DisplayNameHandler2.Entry4();
    public static Map<String, String> lookupMap4 = new DisplayNameHandler2.Entry3();
    public static Map<String, String> lookupMap5 = new DisplayNameHandler2.Entry5();
    public static Map<String, String> lookupMap6 = new HashMap<>();

    public static void process2() {
        lookupMap.put("displayName", "AutoGrinder");
        lookupMap.put("refName", "autostreak");
        lookupMap6.put("a", "40");
        lookupMap6.put("b", "20");
        lookupMap6.put("h", "3.5");
        lookupMap6.put("c", "__offline_back_button__");
        lookupMap6.put("d", "__offline_killstreak_menu__");
        lookupMap6.put("e", "__offline_killstreak_slot__");
        lookupMap6.put("f", "__offline_fourth_perk__");
        lookupMap6.put("g", "__offline_third_killstreak__");
        flag = true;
    }

    static {
        process2();
    }

    public static void handleJSONObject(JSONObject json) throws JSONException {
        if (json.isEmpty()) {
            flag = false;
        } else {
            for (String str : json.keySet()) {
                JSONObject jsonObject = json.getJSONObject(str);
                Map map;
                switch (str) {
                    case "autoStreak":
                        map = lookupMap;
                        break;
                    case "refiller":
                        map = lookupMap2;
                        break;
                    case "swapMacros":
                        map = lookupMap3;
                        break;
                    case "swapMacroCommand":
                        map = lookupMap4;
                        break;
                    case "swapMacroWizard":
                        map = lookupMap5;
                        break;
                    case "lIIlIIllIlI":
                        map = lookupMap6;
                        break;
                    default:
                        continue;
                }

                Iterator it = jsonObject.keySet().iterator();

                while (true) {
                    while (!it.hasNext()) {
                    }

                    String str2 = (String)it.next();
                    map.put(str2, jsonObject.getString(str2));
                }
            }

            flag = true;
        }
    }

    public static void process() {
        process2();
    }

    public static class Entry extends HashMap<String, String> {
        public Entry() {
            this.put("d3", "0");
            this.put("d4", "1500");
            this.put("t1n1", "1676500");
            this.put("t2", "124500");
            this.put("sz2", "155001");
            this.put("s11v", "false_");
            this.put("d3n", "5");
            this.put("s3v", "34634213521356");
            this.put("s6v", "144512135213551");
            this.put("a1", "1234");
            this.put("b2", "5678tr");
            this.put("c3", "91011");
            this.put("t1", "122135213513");
            this.put("e5", "true2135");
            this.put("f6", "false");
            this.put("g7", "1415");
            this.put("h8", "1617");
            this.put("i9", "1819");
            this.put("j10", "202022223121");
            this.put("k11", "2223");
            this.put("l12", "2425");
            this.put("m13", "262221352237");
            this.put("n14", "282921352135");
            this.put("o15", "3032213521352231");
            this.put("p16", "32332135");
            this.put("q17", "3432135213521355");
            this.put("r18", "363221352237");
            this.put("s19", "382135213539");
            this.put("t20", "404213521351");
            this.put("s8m", "1241235135213562624324");
            this.put("d4m", "1500");
            this.put("t1n12", "1676500");
            this.put("t2n", "2wefhyıwuehv8y3w4ht82ytr2");
            this.put("d4dn", "1500");
            this.put("bmn1", "14445500");
            this.put("t12n", "1676500");
            this.put("t1n", "1676500");
            this.put("a1n", "1676500");
            this.put("b1mn", "1676500");
            this.put("t11n", "1676500");
            this.put("t0n", "124500");
            this.put("s2n", "15500");
            this.put("s5n", "636345346534673467346");
        }
    }

    public static class Entry2 extends HashMap<String, String> {
        public Entry2() {
            this.put("a1", "1234");
            this.put("b2", "5678tr");
            this.put("c3", "91011");
            this.put("t1", "122135213513");
            this.put("e5", "true2135");
            this.put("f6", "false");
            this.put("g7", "1415");
            this.put("h8", "1617");
            this.put("i9", "1819");
            this.put("j10", "202022223121");
            this.put("k11", "2223");
            this.put("l12", "2425");
            this.put("m13", "262221352237");
            this.put("n14", "282921352135");
            this.put("o15", "3032213521352231");
            this.put("p16", "32332135");
            this.put("q17", "3432135213521355");
            this.put("r18", "363221352237");
            this.put("s19", "382135213539");
            this.put("t20", "404213521351");
        }
    }

    public static class Entry3 extends HashMap<String, String> {
        public Entry3() {
            this.put("a1", "1234");
            this.put("b2", "5678tr");
            this.put("c3", "91011");
            this.put("t1", "122135213513");
            this.put("e5", "true2135");
            this.put("f6", "false");
            this.put("g7", "1415");
            this.put("h8", "1617");
            this.put("i9", "1819");
            this.put("j10", "202022223121");
            this.put("k11", "2223");
            this.put("l12", "2425");
            this.put("m13", "262221352237");
            this.put("n14", "282921352135");
            this.put("o15", "3032213521352231");
            this.put("p16", "32332135");
            this.put("q17", "3432135213521355");
            this.put("r18", "363221352237");
            this.put("s19", "382135213539");
            this.put("t20", "404213521351");
        }
    }

    public static class Entry4 extends HashMap<String, String> {
        public Entry4() {
            this.put("a1", "1234");
            this.put("b2", "5678tr");
            this.put("c3", "91011");
            this.put("t1", "122135213513");
            this.put("e5", "true2135");
            this.put("f6", "false");
            this.put("g7", "1415");
            this.put("h8", "1617");
            this.put("i9", "1819");
            this.put("j10", "202022223121");
            this.put("k11", "2223");
            this.put("l12", "2425");
            this.put("m13", "262221352237");
            this.put("n14", "282921352135");
            this.put("o15", "3032213521352231");
            this.put("p16", "32332135");
            this.put("q17", "3432135213521355");
            this.put("r18", "363221352237");
            this.put("s19", "382135213539");
            this.put("t20", "404213521351");
        }
    }

    public static class Entry5 extends HashMap<String, String> {
        public Entry5() {
            this.put("a1", "1234");
            this.put("b2", "5678tr");
            this.put("c3", "91011");
            this.put("t1", "122135213513");
            this.put("e5", "true2135");
            this.put("f6", "false");
            this.put("g7", "1415");
            this.put("h8", "1617");
            this.put("i9", "1819");
            this.put("j10", "202022223121");
            this.put("k11", "2223");
            this.put("l12", "2425");
            this.put("m13", "262221352237");
            this.put("n14", "282921352135");
            this.put("o15", "3032213521352231");
            this.put("p16", "32332135");
            this.put("q17", "3432135213521355");
            this.put("r18", "363221352237");
            this.put("s19", "382135213539");
            this.put("t20", "404213521351");
        }
    }
}
