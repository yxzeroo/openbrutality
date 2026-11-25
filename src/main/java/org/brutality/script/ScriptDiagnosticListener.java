package org.brutality.script;

import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import org.brutality.utils.MessageUtils;

public class ScriptDiagnosticListener implements DiagnosticListener<JavaFileObject> {
    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        String message = diagnostic.getMessage(null);
        if (diagnostic.getSource() != null) {
            InMemoryJavaSource source = (InMemoryJavaSource)diagnostic.getSource();
            MessageUtils.x("§cError loading script §b" + this.computeString(source.text2));
        }

        JavaFileObject source2 = (JavaFileObject)diagnostic.getSource();
        if (source2 != null) {
            int n = message.indexOf("\n");
            String message2 = diagnostic.getMessage(Locale.getDefault());
            MessageUtils.x(" §7err: §c" + (n == -1 ? message2 : message2.substring(0, n)));
            InMemoryJavaSource inMemoryJavaSource = (InMemoryJavaSource)source2;
            MessageUtils.x(" §7line: §c" + (diagnostic.getLineNumber() - inMemoryJavaSource.count));
            String str = inMemoryJavaSource.getCharContent(true).toString();
            int startPosition = (int)diagnostic.getStartPosition();
            int endPosition = (int)diagnostic.getEndPosition();
            if (startPosition >= 0 && endPosition >= 0 && startPosition < str.length()) {
                int n2 = str.indexOf("\n", startPosition);
                String str2 = n2 != -1 ? str.substring(startPosition, n2) : str.substring(startPosition, Math.min(endPosition, str.length()));
                if (!str2.trim().startsWith("import ")) {
                    MessageUtils.x(" §7src: §c" + str2);
                }
            }
        }
    }

    private String computeString(String str) {
        if (str.startsWith("sc_")) {
            String str2 = str.substring(3);
            int n = str2.lastIndexOf(95);
            if (n > 0) {
                return str2.substring(0, n);
            }
        }

        return str;
    }
}
