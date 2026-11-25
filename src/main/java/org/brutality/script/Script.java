package org.brutality.script;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.tools.StandardJavaFileManager;
import org.brutality.Brutality;
import org.brutality.utils.MessageUtils;

public class Script {
    public String name;
    public Class<?> clazz;
    public Object object;
    public String text;
    public String text2;
    public boolean flag = false;
    public int count;
    public File file;

    private static String computeString(int n) {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }

        String str2 = sb.toString();
        if (str2.startsWith("java")) {
            str2 = "sc" + str2.substring(4);
        }

        return str2;
    }

    public Float[] invokeFloatArray(String str, Object... objs) {
        if (this.clazz != null && this.object != null) {
            Method method = null;

            for (Method declaredMethod : this.clazz.getDeclaredMethods()) {
                if (declaredMethod.getName().equalsIgnoreCase(str) && declaredMethod.getParameterCount() == objs.length && declaredMethod.getReturnType().isArray()) {
                    Class componentType = declaredMethod.getReturnType().getComponentType();
                    if (componentType == float.class || componentType == float.class) {
                        method = declaredMethod;
                        break;
                    }
                }
            }

            if (method != null) {
                try {
                    method.setAccessible(true);
                    Object obj = method.invoke(this.object, objs);
                    if (obj instanceof float[] floats) {
                        Float[] vals = new Float[floats.length];

                        for (int i = 0; i < floats.length; i++) {
                            vals[i] = floats[i];
                        }

                        return vals;
                    }

                    if (obj instanceof Float[]) {
                        return (Float[])obj;
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    this.handleReflectiveOperationException(e);
                }
            }

            return null;
        }
        return null;
    }

    private static String computeString(String str) {
        int n = str.lastIndexOf(46);
        return n > 0 ? str.substring(0, n) : str;
    }

    public void handleString(String str) {
        this.count = 0;
        StringBuilder sb = new StringBuilder();

        for (String str2 : Brutality.scriptManager.entries) {
            this.count++;
            sb.append("import ").append(str2).append(";\n");
        }

        this.count += 2;
        String text = this.text;
        String name = ScriptDefaults.class.getName();
        String str3 = computeString(this.name);
        String str4 = name;
        String str5 = text;
        this.text2 = sb + "public class " + str5 + " extends " + str4 + " {\npublic static final String scriptName = \"" + str3 + "\";\n" + str + "\n}";
    }

    public boolean invokeVoid(String str, Object... objs) {
        if (this.clazz != null && this.object != null) {
            Method method = null;

            for (Method declaredMethod : this.clazz.getDeclaredMethods()) {
                if (declaredMethod.getName().equalsIgnoreCase(str)
                    && declaredMethod.getParameterCount() == objs.length
                    && declaredMethod.getReturnType().equals(void.class)
                    && matchesClassTypeArray(declaredMethod.getParameterTypes(), objs)) {
                    method = declaredMethod;
                    break;
                }
            }

            if (method != null) {
                try {
                    method.setAccessible(true);
                    method.invoke(this.object, objs);
                    return true;
                } catch (InvocationTargetException | IllegalAccessException e) {
                    this.handleReflectiveOperationException(e);
                } catch (IllegalArgumentException ignored) {
                }
            }

            return false;
        }
        return false;
    }

    public void delete() {
        this.clazz = null;
        this.object = null;
        File file = new File(Brutality.scriptManager.text2 + File.separator + this.text + ".class");
        if (file.exists()) {
            file.delete();
        }
    }

    public Script(String name) {
        this.name = name;
        String str = computeString(name).replace(" ", "").replace(".", "_").replace("-", "_").replace(")", "_").replace("(", "_");
        this.text = "sc_" + str + "_" + computeString(5);
    }

    private static boolean matchesClassType(Class<?> clazz, Object obj) {
        if (obj == null) {
            return !clazz.isPrimitive();
        }
        Class clazz2 = obj.getClass();
        if (clazz.isAssignableFrom(clazz2)) {
            return true;
        }
        return !clazz.isPrimitive()
            ? false
            : clazz == float.class && clazz2 == Float.class
                || clazz == double.class && clazz2 == Double.class
                || clazz == int.class && clazz2 == Integer.class
                || clazz == long.class && clazz2 == Long.class
                || clazz == boolean.class && clazz2 == Boolean.class
                || clazz == byte.class && clazz2 == Byte.class
                || clazz == short.class && clazz2 == Short.class
                || clazz == char.class && clazz2 == Character.class;
    }

    public boolean checkState() {
        try {
            if (this.text != null && this.text2 != null) {
                File file = new File(Brutality.scriptManager.text2);
                if (!file.exists() || !file.isDirectory()) {
                    file.mkdir();
                }

                if (Brutality.scriptManager.javaCompiler == null) {
                    return false;
                }
                ScriptDiagnosticListener scriptDiagnosticListener = new ScriptDiagnosticListener();
                StandardJavaFileManager standardFileManager = Brutality.scriptManager.javaCompiler.getStandardFileManager(scriptDiagnosticListener, null, null);
                ArrayList list = new ArrayList();
                list.add("-d");
                list.add(Brutality.scriptManager.text2);
                list.add("-XDuseUnsharedTable");
                String property = System.getProperty("java.class.path");
                if (property != null && !property.isEmpty()) {
                    list.add("-classpath");
                    list.add(property);
                }

                boolean flag = Brutality.scriptManager
                    .javaCompiler
                    .getTask(null, standardFileManager, scriptDiagnosticListener, list, null, Arrays.asList(new InMemoryJavaSource(this.text, this.text2, this.count)))
                    .call();
                if (!flag) {
                    this.flag = true;
                    return false;
                }
                try {
                    ScriptClassLoader scriptClassLoader = new ScriptClassLoader(new URL[]{file.toURI().toURL()}, Script.class.getClassLoader());
                    this.clazz = scriptClassLoader.loadClass(this.text);
                    this.object = this.clazz.newInstance();
                    scriptClassLoader.close();
                    standardFileManager.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                    MessageUtils.x("§7Script §b" + computeString(this.name) + " §7blocked, §cunsafe code§7 detected!");
                    this.flag = true;
                    return false;
                }

                return true;
            }
            return false;
        } catch (Exception e2) {
            this.flag = true;
            return false;
        }
    }

    private void handleReflectiveOperationException(ReflectiveOperationException e) {
        MessageUtils.x("§7Runtime error in script §b" + this.name);
        if (e.getCause() == null) {
            MessageUtils.x(" §7err: §cThrowable");
        } else {
            MessageUtils.x(" §7err: §c" + e.getCause().getClass().getSimpleName());
            StackTraceElement[] stackTrace = e.getCause().getStackTrace();
            if (stackTrace.length > 0) {
                StackTraceElement stackTraceElement = stackTrace[0];

                for (StackTraceElement stackTraceElement2 : stackTrace) {
                    if (stackTraceElement2.getClassName().equalsIgnoreCase(this.text)) {
                        stackTraceElement = stackTraceElement2;
                        break;
                    }
                }

                MessageUtils.x(" §7line: §c" + (stackTraceElement.getLineNumber() - this.count));
                MessageUtils.x(" §7src: §c" + stackTraceElement.getMethodName());
            }
        }
    }

    private static boolean matchesClassTypeArray(Class<?>[] clazzs, Object[] objs) {
        for (int i = 0; i < clazzs.length; i++) {
            if (!matchesClassType(clazzs[i], objs[i])) {
                return false;
            }
        }

        return true;
    }

    public int invokeBoolean(String str, Object... objs) {
        if (this.clazz != null && this.object != null) {
            Method method = null;

            for (Method declaredMethod : this.clazz.getDeclaredMethods()) {
                if (declaredMethod.getName().equalsIgnoreCase(str)
                    && declaredMethod.getParameterCount() == objs.length
                    && declaredMethod.getReturnType().equals(boolean.class)
                    && matchesClassTypeArray(declaredMethod.getParameterTypes(), objs)) {
                    method = declaredMethod;
                    break;
                }
            }

            if (method != null) {
                try {
                    method.setAccessible(true);
                    Object obj = method.invoke(this.object, objs);
                    if (obj instanceof Boolean) {
                        return (Boolean)obj ? 1 : 0;
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    this.handleReflectiveOperationException(e);
                } catch (IllegalArgumentException ignored) {
                }
            }

            return -1;
        }
        return -1;
    }
}
