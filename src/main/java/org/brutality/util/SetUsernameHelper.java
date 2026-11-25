package org.brutality.util;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SetUsernameHelper {
    private String text;
    private String username2;
    private final List<SetUsernameHelper.Entry2> entries = new ArrayList<>();
    private String content;
    private boolean flag;
    private final String text2;

    public SetUsernameHelper(String str) {
        this.text2 = str;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void process() throws IOException {
        if (this.content == null && this.entries.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
    }

    public void setUsername(String username2) {
        this.username2 = username2;
    }

    public void r(String text) {
        this.text = text;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void handleEntry2(SetUsernameHelper.Entry2 entry2) {
        this.entries.add(entry2);
    }

    private static SetUsernameHelper.Entry6 computeEntry6(SetUsernameHelper.Entry2 entry2) {
        SetUsernameHelper.Entry6 entry6 = new SetUsernameHelper.Entry6();
        entry6.handleString("title", entry2.title);
        entry6.handleString("description", entry2.description);
        entry6.handleString("url", entry2.url);
        if (entry2.date != null) {
            entry6.handleString("timestamp", entry2.date.toInstant().toString());
        }

        if (entry2.color != null) {
            Color color = entry2.color;
            int n = ((color.getRed() << 8) + color.getGreen() << 8) + color.getBlue();
            entry6.handleString("color", n);
        }

        if (entry2.entry4 != null) {
            SetUsernameHelper.Entry6 entry62 = new SetUsernameHelper.Entry6();
            entry62.handleString("text", entry2.entry4.text);
            entry62.handleString("icon_url", entry2.entry4.text2);
            entry6.handleString("footer", entry62);
        }

        if (entry2.entry5 != null) {
            SetUsernameHelper.Entry6 entry63 = new SetUsernameHelper.Entry6();
            entry63.handleString("url", entry2.entry5.text);
            entry6.handleString("image", entry63);
        }

        if (entry2.entry52 != null) {
            SetUsernameHelper.Entry6 entry64 = new SetUsernameHelper.Entry6();
            entry64.handleString("url", entry2.entry52.text);
            entry6.handleString("thumbnail", entry64);
        }

        if (entry2.entry != null) {
            SetUsernameHelper.Entry6 entry65 = new SetUsernameHelper.Entry6();
            entry65.handleString("name", entry2.entry.text);
            entry65.handleString("url", entry2.entry.text2);
            entry65.handleString("icon_url", entry2.entry.gl);
            entry6.handleString("author", entry65);
        }

        if (!entry2.entries.isEmpty()) {
            ArrayList list = new ArrayList();

            for (SetUsernameHelper.Entry3 entry3 : entry2.entries) {
                SetUsernameHelper.Entry6 entry66 = new SetUsernameHelper.Entry6();
                entry66.handleString("name", entry3.text);
                entry66.handleString("value", entry3.text2);
                entry66.handleString("inline", entry3.flag);
                list.add(entry66);
            }

            entry6.handleString("fields", list);
        }

        return entry6;
    }

    public static final class Entry {
        public final String text;
        public final String text2;
        public final String gl;

        public Entry(String text, String str, String gl) {
            this.text = text;
            this.text2 = str;
            this.gl = gl;
        }
    }

    public static class Entry2 {
        public final List<SetUsernameHelper.Entry3> entries = new ArrayList<>();
        public Color color;
        public SetUsernameHelper.Entry entry;
        public SetUsernameHelper.Entry5 entry5;
        public SetUsernameHelper.Entry4 entry4;
        public String url;
        public SetUsernameHelper.Entry5 entry52;
        public String title;
        public Date date;
        public String description;

        public SetUsernameHelper.Entry2 setColor(Color color) {
            this.color = color;
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry2() {
            return this.computeEntry22();
        }

        public SetUsernameHelper.Entry2 computeEntry25(String str) {
            return this.computeEntry23(str);
        }

        public SetUsernameHelper.Entry2 computeEntry24(String str) {
            return this.setTitle(str);
        }

        public SetUsernameHelper.Entry2 computeEntry2(String str) {
            return this.setDescription(str);
        }

        public SetUsernameHelper.Entry2 computeEntry2(String str, String str2) {
            return this.computeEntry22(str, str2);
        }

        public SetUsernameHelper.Entry2 computeEntry27(String str) {
            this.entry52 = new SetUsernameHelper.Entry5(str);
            return this;
        }

        public SetUsernameHelper.Entry2 setUrl(String url) {
            this.url = url;
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry2(String str, String str2, String str3) {
            this.entry = new SetUsernameHelper.Entry(str, str2, str3);
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry22(String str, String str2) {
            this.entry4 = new SetUsernameHelper.Entry4(str, str2);
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry22(String str) {
            return this.computeEntry27(str);
        }

        public SetUsernameHelper.Entry2 computeEntry26(String str) {
            return this.setUrl(str);
        }

        public SetUsernameHelper.Entry2 setDescription(String description) {
            this.description = description;
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry2(String str, String str2, boolean flag) {
            return this.computeEntry22(str, str2, flag);
        }

        public SetUsernameHelper.Entry2 setTitle(String title) {
            this.title = title;
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry2(Color color) {
            return this.setColor(color);
        }

        public SetUsernameHelper.Entry2 computeEntry23(String str) {
            this.entry5 = new SetUsernameHelper.Entry5(str);
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry22(String str, String str2, boolean flag) {
            this.entries.add(new SetUsernameHelper.Entry3(str, str2, flag));
            return this;
        }

        public SetUsernameHelper.Entry2 computeEntry22(String str, String str2, String str3) {
            return this.computeEntry2(str, str2, str3);
        }

        public SetUsernameHelper.Entry2 computeEntry22() {
            this.date = new Date();
            return this;
        }
    }

    public static final class Entry3 {
        public final String text;
        public final String text2;
        public final boolean flag;

        public Entry3(String text, String str, boolean flag) {
            this.text = text;
            this.text2 = str;
            this.flag = flag;
        }
    }

    public static final class Entry4 {
        public final String text;
        public final String text2;

        public Entry4(String text, String str) {
            this.text = text;
            this.text2 = str;
        }
    }

    public static final class Entry5 {
        public final String text;

        public Entry5(String text) {
            this.text = text;
        }
    }

    public static final class Entry6 {
        private final Map<String, Object> lookupMap = new LinkedHashMap<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            int n = 0;

            for (Map.Entry entry : this.lookupMap.entrySet()) {
                if (n++ > 0) {
                    sb.append(',');
                }

                sb.append(computeString((String)entry.getKey())).append(':').append(toJson(entry.getValue()));
            }

            return sb.append('}').toString();
        }

        private static String computeString(String str) {
            StringBuilder sb = new StringBuilder(str.length() + 2).append('"');

            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                switch (ch) {
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '\\':
                        sb.append("\\\\");
                        break;
                    default:
                        if (ch < ' ') {
                            sb.append(String.format("\\u%04x", Integer.valueOf(ch)));
                        } else {
                            sb.append(ch);
                        }
                }
            }

            return sb.append('"').toString();
        }

        public void handleString(String str, Object obj) {
            if (obj != null) {
                this.lookupMap.put(str, obj);
            }
        }

        private static String toJson(Object obj) {
            if (obj == null) {
                return "null";
            }
            if (obj instanceof String || obj instanceof Character) {
                return computeString(String.valueOf(obj));
            }
            if (!(obj instanceof Number) && !(obj instanceof Boolean) && !(obj instanceof SetUsernameHelper.Entry6)) {
                if (obj instanceof Collection) {
                    StringBuilder sb = new StringBuilder("[");
                    int n = 0;

                    for (Object obj2 : (Collection)obj) {
                        if (n++ > 0) {
                            sb.append(',');
                        }

                        sb.append(toJson(obj2));
                    }

                    return sb.append(']').toString();
                }
                if (obj.getClass().isArray()) {
                    StringBuilder sb2 = new StringBuilder("[");

                    for (int i = 0; i < Array.getLength(obj); i++) {
                        if (i > 0) {
                            sb2.append(',');
                        }

                        sb2.append(toJson(Array.get(obj, i)));
                    }

                    return sb2.append(']').toString();
                }
                return computeString(String.valueOf(obj));
            }
            return String.valueOf(obj);
        }
    }
}
