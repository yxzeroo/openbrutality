package org.brutality.script;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class InMemoryJavaSource extends SimpleJavaFileObject {
    private final String text;
    public final String text2;
    public int count;

    @Override
    public CharSequence getCharContent(boolean flag) {
        return this.text;
    }

    public InMemoryJavaSource(String str, String text, int count) {
        super(URI.create("string:///" + str + ".java"), Kind.SOURCE);
        this.text = text;
        this.text2 = str;
        this.count = count;
    }
}
