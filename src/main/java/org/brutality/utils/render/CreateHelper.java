package org.brutality.utils.render;

import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class CreateHelper {
    private static int program;
    private static int count;
    private static int count2;
    private static boolean flag;

    public static boolean matchesFloat(float f, float f2, float f3, float f4, int n) {
        if (OpenGlHelper.shadersSupported && !flag) {
            boolean flag2;
            try {
                if (program == 0 || !GL20.glIsProgram(program)) {
                    create();
                }

                GL20.glUseProgram(program);
                GL20.glUniform2f(count, f3 - f, f4 - f2);
                GL20.glUniform4f(count2, (n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, ((0xFF | ~n) - ~n) / 255.0F, (n >>> 24) / 255.0F);
                float f5 = f3 - f;
                float f6 = f4 - f2;
                float f7 = 8.0F;
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2f(-f7, -f7);
                GL11.glVertex2f(f - f7, f2 - f7);
                GL11.glTexCoord2f(-f7, f6 + f7);
                GL11.glVertex2f(f - f7, f4 + f7);
                GL11.glTexCoord2f(f5 + f7, f6 + f7);
                GL11.glVertex2f(f3 + f7, f4 + f7);
                GL11.glTexCoord2f(f5 + f7, -f7);
                GL11.glVertex2f(f3 + f7, f2 - f7);
                GL11.glEnd();
                return true;
            } catch (RuntimeException e) {
                flag = true;
                LogManager.getLogger(CreateHelper.class).warn("Nametag coverage shader unavailable; using geometry border", (Throwable)e);
                flag2 = false;
            } finally {
                GL20.glUseProgram(0);
            }

            return flag2;
        }
        return false;
    }

    private static int computeInt(int n, String str) {
        int n2 = GL20.glCreateShader(n);
        GL20.glShaderSource(n2, str);
        GL20.glCompileShader(n2);
        if (GL20.glGetShaderi(n2, GL20.GL_COMPILE_STATUS) == 0) {
            String str2 = GL20.glGetShaderInfoLog(n2, 2048);
            GL20.glDeleteShader(n2);
            throw new IllegalStateException(str2);
        }
        return n2;
    }

    private static void create() {
        int n = 0;
        int n2 = 0;
        int n3 = 0;

        try {
            n = computeInt(35633, "#version 120\nvoid main(){gl_Position=gl_ModelViewProjectionMatrix*gl_Vertex;gl_TexCoord[0]=gl_MultiTexCoord0;}");
            n2 = computeInt(
                35632,
                "#version 120\nuniform vec2 size;\nuniform vec4 color;\nvoid main() {\n    vec2 p = gl_TexCoord[0].xy;\n    float d = max(max(-p.x, p.x-size.x), max(-p.y, p.y-size.y));\n    float pixel = max(max(fwidth(p.x), fwidth(p.y)), 0.0001);\n    float outerCoverage = clamp(0.5-d/pixel, 0.0, 1.0);\n    float innerCoverage = clamp(0.5-(d+0.50)/pixel, 0.0, 1.0);\n    float coverage = outerCoverage-innerCoverage;\n    if(coverage<=0.0)discard;\n    gl_FragColor = vec4(color.rgb, color.a * coverage);\n}\n"
            );
            n3 = GL20.glCreateProgram();
            GL20.glAttachShader(n3, n);
            GL20.glAttachShader(n3, n2);
            GL20.glLinkProgram(n3);
            if (GL20.glGetProgrami(n3, GL20.GL_LINK_STATUS) == 0) {
                throw new IllegalStateException(GL20.glGetProgramInfoLog(n3, 2048));
            }

            program = n3;
            n3 = 0;
            count = GL20.glGetUniformLocation(program, "size");
            count2 = GL20.glGetUniformLocation(program, "color");
        } finally {
            if (n != 0) {
                GL20.glDeleteShader(n);
            }

            if (n2 != 0) {
                GL20.glDeleteShader(n2);
            }

            if (n3 != 0) {
                GL20.glDeleteProgram(n3);
            }
        }
    }

    private CreateHelper() {
    }
}
