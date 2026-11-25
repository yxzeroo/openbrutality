package org.brutality.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class StringEncryptionIntegration implements Opcodes {
    private static final String text = "org/brutality/system/runtime/StringDecryptor";
    private static final String text2 = "d";
    private static final int count = -1213784962;
    private static int count2 = 0;
    private static int count3 = 0;
    private static int count4 = 0;
    private static final boolean flag = false;
    private static final List<String> entries = new ArrayList<>();
    private static final Set<String> itemSet = new HashSet<>(Arrays.asList("org/brutality/"));

    public static void handleFile(File inputJar, File outputJar) throws Exception {
        count2 = 0;
        count3 = 0;
        count4 = 0;
        entries.clear();

        try (
            JarFile jarFile = new JarFile(inputJar);
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar));
        ) {
            Enumeration enumeration = jarFile.entries();
            HashSet set = new HashSet();

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry)enumeration.nextElement();
                String name = jarEntry.getName();
                if (!set.contains(name)) {
                    set.add(name);
                    if (name.endsWith(".class") && !name.contains("module-info")) {
                        boolean flag = false;

                        try {
                            boolean flag2 = false;

                            for (String str : itemSet) {
                                if (name.startsWith(str)) {
                                    flag2 = true;
                                    break;
                                }
                            }

                            if (flag2
                                && !name.equals("org/brutality/system/runtime/StringDecryptor.class")
                                && !name.startsWith("org/brutality/system/ObfuscationPipeline")
                                && !name.startsWith("org/brutality/system/StringEncryptionIntegration")
                                && !name.startsWith("org/brutality/system/ControlFlowHardening")
                                && !name.startsWith("org/brutality/system/IntegritySealer")) {
                                ClassReader classReader = new ClassReader(jarFile.getInputStream(jarEntry));
                                ClassNode classNode = new ClassNode();
                                classReader.accept(classNode, 0);
                                boolean flag3 = matchesClassNode(classNode);
                                if (flag3) {
                                    count3++;
                                    ClassWriter classWriter = new ClassWriter(2);
                                    classNode.accept(classWriter);
                                    jarOutputStream.putNextEntry(new JarEntry(name));
                                    jarOutputStream.write(classWriter.toByteArray());
                                    jarOutputStream.closeEntry();
                                    flag = true;
                                } else {
                                    handleJarFile(jarFile, jarOutputStream, jarEntry);
                                    flag = true;
                                }
                            } else {
                                handleJarFile(jarFile, jarOutputStream, jarEntry);
                                flag = true;
                            }
                        } catch (Exception e) {
                            if (!flag) {
                                handleJarFile(jarFile, jarOutputStream, jarEntry);
                            }
                        }
                    } else {
                        handleJarFile(jarFile, jarOutputStream, jarEntry);
                    }
                }
            }

            handleJarOutputStream(jarOutputStream);
        }
    }

    private static void handleJarFile(JarFile jarFile, JarOutputStream jarOutputStream, JarEntry jarEntry) throws Exception {
        jarOutputStream.putNextEntry(new JarEntry(jarEntry.getName()));

        try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
            byte[] bytes = new byte[8192];

            int n;
            while ((n = inputStream.read(bytes)) > 0) {
                jarOutputStream.write(bytes, 0, n);
            }
        }

        jarOutputStream.closeEntry();
    }

    private static boolean matchesClassNode(ClassNode classNode) {
        boolean flag = false;

        for (MethodNode method : classNode.methods) {
            if (matchesString(classNode.name, method)) {
                flag = true;
            }
        }

        return flag;
    }

    private static boolean matchesString(String str, MethodNode methodNode) {
        boolean flag = false;

        for (AbstractInsnNode abstractInsnNode : methodNode.instructions.toArray()) {
            if (abstractInsnNode instanceof LdcInsnNode ldcInsnNode && ldcInsnNode.cst instanceof String str3) {
                if (matchesString(str3)) {
                    count4++;
                } else {
                    int n = computeInt(str, methodNode.name, str3.length());
                    String str2 = computeString(str3, n);
                    InsnList insnList = new InsnList();
                    insnList.add(new LdcInsnNode(str2));
                    insnList.add(new LdcInsnNode(n));
                    insnList.add(new MethodInsnNode(184, "org/brutality/system/runtime/StringDecryptor", "d", "(Ljava/lang/String;I)Ljava/lang/String;", false));
                    methodNode.instructions.insert(ldcInsnNode, insnList);
                    methodNode.instructions.remove(ldcInsnNode);
                    count2++;
                    flag = true;
                }
            }
        }

        return flag;
    }

    public static int computeInt(String str, String str2, int n) {
        int n2 = -1213784962;
        n2 = n2 * 31 + (str == null ? 0 : str.hashCode());
        n2 = n2 * 31 + (str2 == null ? 0 : str2.hashCode());
        n2 = n2 * 31 + n;
        n2 ^= n2 >>> 16;
        n2 *= 2146121005;
        n2 ^= n2 >>> 15;
        return n2 == 0 ? -1213784962 : n2;
    }

    private static boolean matchesString(String str) {
        if (str != null && !str.isEmpty()) {
            if (str.length() <= 1) {
                return true;
            }
            if (str.startsWith("(") && str.contains(")")) {
                return true;
            }
            if (str.startsWith("L") && str.endsWith(";") && !str.contains(" ")) {
                return true;
            }
            if (str.startsWith("@") && !str.contains(" ")) {
                return true;
            }
            if (str.matches("^\\(.*\\)[A-Z]$")) {
                return true;
            }
            if (str.startsWith("[") && str.endsWith(";") && !str.contains(" ")) {
                return true;
            }
            return str.matches("^[ZBCSIJFD]$") ? true : str.trim().isEmpty();
        }
        return true;
    }

    private static String computeString(String str, int n) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int n2 = n;

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= (byte)(n2 >>> (i & 3) * 8);
            n2 = n2 * 73244475 ^ i + -1640531527 ^ n;
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    private static void handleJarOutputStream(JarOutputStream jarOutputStream) throws Exception {
        ClassNode classNode = new ClassNode();
        classNode.visit(52, 33, "org/brutality/system/runtime/StringDecryptor", null, "java/lang/Object", null);
        classNode.fields.add(new FieldNode(26, "c", "Ljava/util/concurrent/ConcurrentHashMap;", null, null));
        MethodNode methodNode = new MethodNode(8, "<clinit>", "()V", null, null);
        InsnList instructions = methodNode.instructions;
        instructions.add(new TypeInsnNode(187, "java/util/concurrent/ConcurrentHashMap"));
        instructions.add(new InsnNode(89));
        instructions.add(new MethodInsnNode(183, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
        instructions.add(new FieldInsnNode(179, "org/brutality/system/runtime/StringDecryptor", "c", "Ljava/util/concurrent/ConcurrentHashMap;"));
        instructions.add(new InsnNode(177));
        classNode.methods.add(methodNode);
        MethodNode methodNode2 = new MethodNode(2, "<init>", "()V", null, null);
        methodNode2.instructions.add(new VarInsnNode(25, 0));
        methodNode2.instructions.add(new MethodInsnNode(183, "java/lang/Object", "<init>", "()V", false));
        methodNode2.instructions.add(new InsnNode(177));
        classNode.methods.add(methodNode2);
        MethodNode methodNode3 = new MethodNode(9, "d", "(Ljava/lang/String;I)Ljava/lang/String;", null, null);
        InsnList instructions2 = methodNode3.instructions;
        instructions2.add(new FieldInsnNode(178, "org/brutality/system/runtime/StringDecryptor", "c", "Ljava/util/concurrent/ConcurrentHashMap;"));
        instructions2.add(new VarInsnNode(25, 0));
        instructions2.add(new MethodInsnNode(182, "java/util/concurrent/ConcurrentHashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false));
        instructions2.add(new VarInsnNode(58, 2));
        LabelNode labelNode = new LabelNode();
        instructions2.add(new VarInsnNode(25, 2));
        instructions2.add(new JumpInsnNode(198, labelNode));
        instructions2.add(new VarInsnNode(25, 2));
        instructions2.add(new TypeInsnNode(192, "java/lang/String"));
        instructions2.add(new InsnNode(176));
        instructions2.add(labelNode);
        instructions2.add(new VarInsnNode(25, 0));
        instructions2.add(new VarInsnNode(21, 1));
        instructions2.add(new MethodInsnNode(184, "org/brutality/system/runtime/StringDecryptor", "x", "(Ljava/lang/String;I)Ljava/lang/String;", false));
        instructions2.add(new VarInsnNode(58, 3));
        instructions2.add(new FieldInsnNode(178, "org/brutality/system/runtime/StringDecryptor", "c", "Ljava/util/concurrent/ConcurrentHashMap;"));
        instructions2.add(new VarInsnNode(25, 0));
        instructions2.add(new VarInsnNode(25, 3));
        instructions2.add(new MethodInsnNode(182, "java/util/concurrent/ConcurrentHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
        instructions2.add(new InsnNode(87));
        instructions2.add(new VarInsnNode(25, 3));
        instructions2.add(new InsnNode(176));
        classNode.methods.add(methodNode3);
        MethodNode methodNode4 = new MethodNode(10, "x", "(Ljava/lang/String;I)Ljava/lang/String;", null, null);
        InsnList instructions3 = methodNode4.instructions;
        instructions3.add(new MethodInsnNode(184, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false));
        instructions3.add(new VarInsnNode(25, 0));
        instructions3.add(new MethodInsnNode(182, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false));
        instructions3.add(new VarInsnNode(58, 2));
        instructions3.add(new VarInsnNode(21, 1));
        instructions3.add(new VarInsnNode(54, 3));
        instructions3.add(new InsnNode(3));
        instructions3.add(new VarInsnNode(54, 4));
        LabelNode labelNode2 = new LabelNode();
        LabelNode labelNode3 = new LabelNode();
        instructions3.add(labelNode2);
        instructions3.add(new VarInsnNode(21, 4));
        instructions3.add(new VarInsnNode(25, 2));
        instructions3.add(new InsnNode(190));
        instructions3.add(new JumpInsnNode(162, labelNode3));
        instructions3.add(new VarInsnNode(25, 2));
        instructions3.add(new VarInsnNode(21, 4));
        instructions3.add(new InsnNode(92));
        instructions3.add(new InsnNode(51));
        instructions3.add(new VarInsnNode(21, 3));
        instructions3.add(new VarInsnNode(21, 4));
        instructions3.add(new InsnNode(6));
        instructions3.add(new InsnNode(126));
        instructions3.add(new IntInsnNode(16, 8));
        instructions3.add(new InsnNode(104));
        instructions3.add(new InsnNode(124));
        instructions3.add(new InsnNode(130));
        instructions3.add(new InsnNode(145));
        instructions3.add(new InsnNode(84));
        instructions3.add(new VarInsnNode(21, 3));
        instructions3.add(new LdcInsnNode(73244475));
        instructions3.add(new InsnNode(104));
        instructions3.add(new VarInsnNode(21, 4));
        instructions3.add(new LdcInsnNode(-1640531527));
        instructions3.add(new InsnNode(96));
        instructions3.add(new InsnNode(130));
        instructions3.add(new VarInsnNode(21, 1));
        instructions3.add(new InsnNode(130));
        instructions3.add(new VarInsnNode(54, 3));
        instructions3.add(new IincInsnNode(4, 1));
        instructions3.add(new JumpInsnNode(167, labelNode2));
        instructions3.add(labelNode3);
        instructions3.add(new TypeInsnNode(187, "java/lang/String"));
        instructions3.add(new InsnNode(89));
        instructions3.add(new VarInsnNode(25, 2));
        instructions3.add(new FieldInsnNode(178, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;"));
        instructions3.add(new MethodInsnNode(183, "java/lang/String", "<init>", "([BLjava/nio/charset/Charset;)V", false));
        instructions3.add(new InsnNode(176));
        classNode.methods.add(methodNode4);
        ClassWriter classWriter = new ClassWriter(2);
        classNode.accept(classWriter);
        jarOutputStream.putNextEntry(new JarEntry("org/brutality/system/runtime/StringDecryptor.class"));
        jarOutputStream.write(classWriter.toByteArray());
        jarOutputStream.closeEntry();
    }
}
