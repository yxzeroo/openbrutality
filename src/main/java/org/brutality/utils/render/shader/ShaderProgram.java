package org.brutality.utils.render.shader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final int count;
    private final HashMap<String, Integer> lookupMap = new HashMap<>();
    private String text = "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform bool check;\nuniform float lastPass;\nuniform float exposure;\n\nvoid main() {\n    if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum.rgb *= sum.a;\n    vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);\n    smpl1.rgb *= smpl1.a;\n    sum += smpl1 * 2.0;\n    vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3 * 2.0;\n    vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp5.rgb *= smp5.a;\n    sum += smp5 * 2.0;\n    vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    smp6.rgb *= smp6.a;\n    sum += smp6;\n    vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);\n    smp7.rgb *= smp7.a;\n    sum += smp7 * 2.0;\n    vec4 result = sum / 12.0;\n    gl_FragColor = vec4(result.rgb / result.a, mix(result.a, 1.0 - exp(-result.a * exposure), step(0.0, lastPass)));\n}";
    private String text2 = "#version 120\n\nuniform sampler2D textureIn, textureToCheck;\nuniform vec2 texelSize, direction;\nuniform vec3 color;\nuniform bool avoidTexture;\nuniform float exposure, radius;\nuniform float weights[256];\n\n#define offset direction * texelSize\n\nvoid main() {\n    if (direction.y == 1 && avoidTexture) {\n        if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n    }\n    vec4 innerColor = texture2D(textureIn, gl_TexCoord[0].st);\n    innerColor.rgb *= innerColor.a;\n    innerColor *= weights[0];\n    for (float r = 1.0; r <= radius; r++) {\n        vec4 colorCurrent1 = texture2D(textureIn, gl_TexCoord[0].st + offset * r);\n        vec4 colorCurrent2 = texture2D(textureIn, gl_TexCoord[0].st - offset * r);\n\n        colorCurrent1.rgb *= colorCurrent1.a;\n        colorCurrent2.rgb *= colorCurrent2.a;\n\n        innerColor += (colorCurrent1 + colorCurrent2) * weights[int(r)];\n    }\n\n    gl_FragColor = vec4(innerColor.rgb / innerColor.a, mix(innerColor.a, 1.0 - exp(-innerColor.a * exposure), step(0.0, direction.y)));\n}\n";
    private String text3 = "#version 120\n\nuniform sampler2D textureIn;\nuniform vec4 color;\nvoid main() {\n    float alpha = texture2D(textureIn, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(color.rgb, color.a * mix(0.0, alpha, step(0.0, alpha)));\n}\n";
    private String text4 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D textureIn;\nuniform float radius, alpha;\n\nfloat roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) -size, 0.)) - radius;\n}\n\n\nvoid main() {\n    float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;\n    gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);\n}";
    private String text5 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color, outlineColor;\nuniform float radius, outlineThickness;\n\nfloat roundedSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;\n}\n\nvoid main() {\n    float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);\n\n    float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));\n\n    vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);\n    gl_FragColor = mix(outlineColor, insideColor, blendAmount);\n\n}";
    private String text6 = "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform int check;\n\nvoid main() {\n  //  if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum.rgb *= sum.a;\n    vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);\n    smpl1.rgb *= smpl1.a;\n    sum += smpl1 * 2.0;\n    vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3 * 2.0;\n    vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp5.rgb *= smp5.a;\n    sum += smp5 * 2.0;\n    vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    smp6.rgb *= smp6.a;\n    sum += smp6;\n    vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);\n    smp7.rgb *= smp7.a;\n    sum += smp7 * 2.0;\n    vec4 result = sum / 12.0;\n    gl_FragColor = vec4(result.rgb / result.a, mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a),check));\n}";
    private String text7 = "#version 120\n\nuniform sampler2D inTexture;\nuniform vec2 offset, halfpixel, iResolution;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);\n    sum.rgb *= sum.a;\n    sum *= 4.0;\n    vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);\n    smp1.rgb *= smp1.a;\n    sum += smp1;\n    vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3;\n    vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 result = sum / 8.0;\n    gl_FragColor = vec4(result.rgb / result.a, result.a);\n}";
    private String text8 = "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform int check;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;\n\n    gl_FragColor = vec4(sum.rgb /12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));\n}\n";
    private String text9 = "#version 120\n\nuniform sampler2D inTexture;\nuniform vec2 offset, halfpixel, iResolution;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;\n    sum += texture2D(inTexture, uv - halfpixel.xy * offset);\n    sum += texture2D(inTexture, uv + halfpixel.xy * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n    gl_FragColor = vec4(sum.rgb * .125, 1.0);\n}\n";
    private String text10 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D tex;\nuniform vec3 color1, color2, color3, color4;\nuniform float alpha;\n\n#define NOISE .5/255.0\n\nvec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){\n    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n    //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n    float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4), texColorAlpha * alpha);\n}";
    private String text11 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D u_texture, u_texture2;\nvoid main() {\n    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n    float texColorAlpha = texture2D(u_texture, gl_TexCoord[0].st).a;\n    vec3 tex2Color = texture2D(u_texture2, gl_TexCoord[0].st).rgb;\n    gl_FragColor = vec4(tex2Color, texColorAlpha);\n}";
    private String text12 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D tex;\nuniform vec4 color1, color2, color3, color4;\n#define NOISE .5/255.0\n\nvec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n    //Dithering the color\n    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n    gl_FragColor = createGradient(coords, color1, color2, color3, color4);\n}";
    private String text13 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color1, color2, color3, color4;\nuniform float radius;\n\n#define NOISE .5/255.0\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b , 0.0)) - r;\n}\n\nvec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n    //Dithering the color\n    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 st = gl_TexCoord[0].st;\n    vec2 halfSize = rectSize * .5;\n    \n   // use the bottom leftColor as the alpha\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius)));\n    vec4 gradient = createGradient(st, color1, color2, color3, color4);    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);\n}";
    private String text14 = "#version 120\n\nuniform sampler2D Sampler0;\nuniform vec2 location, rectSize, iResolution;\nuniform vec4 color;\nuniform vec3 fresnelColor;\nuniform float radius, baseAlpha, fresnelAlpha, fresnelPower, distortStrength, globalAlpha, blurScale;\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b, 0.0)) - r;\n}\n\nvoid main() {\n    vec2 hs = rectSize * 0.5;\n    vec2 p = (gl_FragCoord.xy - location) - hs;\n    float dist = roundSDF(p, hs - 1.0, radius);\n    float mask = 1.0 - smoothstep(0.0, 1.5, dist);\n    if (mask < 0.001) discard;\n\n    float maxd = min(hs.x, hs.y);\n    float edge = 1.0 - clamp(abs(dist) / maxd, 0.0, 1.0);\n    float fresnel = clamp(pow(1.0 - edge, fresnelPower), 0.0, 1.0);\n\n    vec2 dir = (length(p) > 0.0) ? normalize(p) : vec2(0.0);\n    vec2 uv = (gl_FragCoord.xy / iResolution) + dir * fresnel * distortStrength;\n\n    // frosted backdrop: 5x5 gaussian over the screen behind the panel\n    vec2 px = blurScale / iResolution;\n    vec4 bg = vec4(0.0);\n    float total = 0.0;\n    for (float ox = -2.0; ox <= 2.0; ox += 1.0) {\n        for (float oy = -2.0; oy <= 2.0; oy += 1.0) {\n            float w = exp(-(ox * ox + oy * oy) / 4.0);\n            bg += texture2D(Sampler0, uv + vec2(ox, oy) * px) * w;\n            total += w;\n        }\n    }\n    bg /= total;\n\n    vec3 tinted = mix(bg.rgb, color.rgb, color.a);\n    vec3 finalColor = mix(tinted, fresnelColor, fresnel);\n    float finalAlpha = mix(baseAlpha, fresnelAlpha, fresnel) * mask * globalAlpha;\n    if (finalAlpha < 0.001) discard;\n    gl_FragColor = vec4(finalColor, finalAlpha);\n}";
    private String text15 = "#version 120\n\nuniform sampler2D blurTex;\nuniform sampler2D depthTex;\nuniform vec2 iResolution;\nuniform vec4 FogParams;\nuniform float BlurStrength;\nuniform float BlurFloor;\nuniform float SkipSky;\n\nfloat linearizeDepth(float depth, float near, float far) {\n    float z = depth * 2.0 - 1.0;\n    return (2.0 * near * far) / (far + near - z * (far - near));\n}\n\nvoid main() {\n    float depth = texture2D(depthTex, gl_FragCoord.xy / iResolution).r;\n    if (SkipSky > 0.5 && depth >= 0.99999) discard;\n\n    float linZ = linearizeDepth(depth, FogParams.z, FogParams.w);\n    float t = smoothstep(FogParams.x, FogParams.y, linZ) * BlurStrength;\n    t = max(t, BlurFloor);\n    t = clamp(t, 0.0, 1.0);\n    if (t <= 0.0) discard;\n\n    vec3 blurred = texture2D(blurTex, gl_TexCoord[0].st).rgb;\n    gl_FragColor = vec4(blurred, t);\n}";
    private String text16 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color;\nuniform float radius;\nuniform bool blur;\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b, 0.0)) - r;\n}\n\n\nvoid main() {\n    vec2 rectHalf = rectSize * .5;\n    // Smooth the result (free antialiasing).\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;\n    gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n\n}";

    public static void handleFloat(float f, float f2, float f3, float f4) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(f, f2);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(f, f2 + f4);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(f + f3, f2 + f4);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(f + f3, f2);
        GL11.glEnd();
    }

    public static void handleFloat(float f, float f2) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(0.0F, f2);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(f, f2);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(f, 0.0F);
        GL11.glEnd();
    }

    private int computeInt2(String str) {
        Integer integer = this.lookupMap.get(str);
        if (integer != null) {
            return integer;
        }
        int n = GL20.glGetUniformLocation(this.count, str);
        this.lookupMap.put(str, n);
        return n;
    }

    public void handleString(String str, int n) {
        GL20.glUniform1i(this.computeInt2(str), n);
    }

    private static String readInputStream(InputStream in) {
        StringBuilder sb = new StringBuilder();

        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while ((str = reader.readLine()) != null) {
                sb.append(str).append('\n');
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read shader source", e);
        }

        return sb.toString();
    }

    public void handleString(String str, float f) {
        GL20.glUniform1f(this.computeInt2(str), f);
    }

    public ShaderProgram(String str) {
        this(str, "brutality/shaders/classic_vertex.vsh");
    }

    private int computeInt(InputStream in, int n) {
        int n2 = GL20.glCreateShader(n);
        GL20.glShaderSource(n2, readInputStream(in));
        GL20.glCompileShader(n2);
        if (GL20.glGetShaderi(n2, GL20.GL_COMPILE_STATUS) == 0) {
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", n));
        }
        return n2;
    }

    public void handleString(String str, int n, int n2) {
        GL20.glUniform2i(this.computeInt2(str), n, n2);
    }

    public void unload() {
        GL20.glUseProgram(0);
    }

    public int computeInt(String str) {
        return this.computeInt2(str);
    }

    public ShaderProgram(String str, String str2) {
        int n = GL20.glCreateProgram();

        try {
            GL20.glAttachShader(n, switch (str) {
                case "kawaseUpGlow" -> this.computeInt(new ByteArrayInputStream(this.text.getBytes()), 35632);
                case "glow" -> this.computeInt(new ByteArrayInputStream(this.text2.getBytes()), 35632);
                case "chams" -> this.computeInt(new ByteArrayInputStream(this.text3.getBytes()), 35632);
                case "roundRectTexture" -> this.computeInt(new ByteArrayInputStream(this.text4.getBytes()), 35632);
                case "roundRectOutline" -> this.computeInt(new ByteArrayInputStream(this.text5.getBytes()), 35632);
                case "kawaseUpBloom" -> this.computeInt(new ByteArrayInputStream(this.text6.getBytes()), 35632);
                case "kawaseDownBloom" -> this.computeInt(new ByteArrayInputStream(this.text7.getBytes()), 35632);
                case "kawaseUp" -> this.computeInt(new ByteArrayInputStream(this.text8.getBytes()), 35632);
                case "kawaseDown" -> this.computeInt(new ByteArrayInputStream(this.text9.getBytes()), 35632);
                case "gradientMask" -> this.computeInt(new ByteArrayInputStream(this.text10.getBytes()), 35632);
                case "mask" -> this.computeInt(new ByteArrayInputStream(this.text11.getBytes()), 35632);
                case "gradient" -> this.computeInt(new ByteArrayInputStream(this.text12.getBytes()), 35632);
                case "roundedRect" -> this.computeInt(new ByteArrayInputStream(this.text16.getBytes()), 35632);
                case "liquidGlass" -> this.computeInt(new ByteArrayInputStream(this.text14.getBytes()), 35632);
                case "fogDepthComposite" -> this.computeInt(new ByteArrayInputStream(this.text15.getBytes()), 35632);
                case "roundedRectGradient" -> this.computeInt(new ByteArrayInputStream(this.text13.getBytes()), 35632);
                default -> this.computeInt(mc.getResourceManager().getResource(new ResourceLocation(str)).getInputStream(), 35632);
            });
            InputStream resourceAsStream = ShaderProgram.class.getResourceAsStream("/assets/minecraft/brutality/shaders/classic_vertex.vsh");
            if (resourceAsStream == null) {
                throw new IOException("vertex.vsh not found on classpath");
            }

            int n2 = this.computeInt(resourceAsStream, 35633);
            GL20.glAttachShader(n, n2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GL20.glLinkProgram(n);
        int n3 = GL20.glGetProgrami(n, GL20.GL_LINK_STATUS);
        if (n3 == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.count = n;
    }

    public void handleString(String str, float f, float f2, float f3, float f4) {
        GL20.glUniform4f(this.computeInt2(str), f, f2, f3, f4);
    }

    public void handleString(String str, float... floats) {
        int n = this.computeInt2(str);
        switch (floats.length) {
            case 1:
                GL20.glUniform1f(n, floats[0]);
                break;
            case 2:
                GL20.glUniform2f(n, floats[0], floats[1]);
                break;
            case 3:
                GL20.glUniform3f(n, floats[0], floats[1], floats[2]);
                break;
            case 4:
                GL20.glUniform4f(n, floats[0], floats[1], floats[2], floats[3]);
        }
    }

    public ShaderProgram(String str, boolean flag) {
        int n = GL20.glCreateProgram();
        int n2 = this.computeInt(new ByteArrayInputStream(str.getBytes()), 35632);
        int n3 = 0;

        try {
            InputStream resourceAsStream = ShaderProgram.class.getResourceAsStream("/assets/minecraft/brutality/shaders/classic_vertex.vsh");
            if (resourceAsStream == null) {
                throw new IOException("vertex.vsh not found on classpath");
            }

            n3 = this.computeInt(resourceAsStream, 35633);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GL20.glAttachShader(n, n2);
        GL20.glAttachShader(n, n3);
        GL20.glLinkProgram(n);
        int n4 = GL20.glGetProgrami(n, GL20.GL_LINK_STATUS);
        if (n4 == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.count = n;
    }

    public void init() {
        GL20.glUseProgram(this.count);
    }

    public static void process() {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        float scaledWidth_double = (float)sr.getScaledWidth_double();
        float scaledHeight_double = (float)sr.getScaledHeight_double();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(0.0F, scaledHeight_double);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(scaledWidth_double, scaledHeight_double);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(scaledWidth_double, 0.0F);
        GL11.glEnd();
    }

    public void handleString(String str, float f, float f2, float f3) {
        GL20.glUniform3f(this.computeInt2(str), f, f2, f3);
    }

    public void handleString(String str, float f, float f2) {
        GL20.glUniform2f(this.computeInt2(str), f, f2);
    }

    public void handleString(String str, int... ints) {
        int n = this.computeInt2(str);
        if (ints.length > 1) {
            GL20.glUniform2i(n, ints[0], ints[1]);
        } else {
            GL20.glUniform1i(n, ints[0]);
        }
    }
}
