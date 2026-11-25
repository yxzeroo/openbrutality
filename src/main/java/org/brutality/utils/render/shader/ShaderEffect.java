package org.brutality.utils.render.shader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShaderEffect implements McpApi2 {
    private final int count;
    private float factor;
    private final Map<String, Integer> lookupMap = new HashMap<>();
    private static Framebuffer framebuffer = null;
    private static Framebuffer framebuffer2 = null;
    private static final Map<String, ShaderEffect.Entry> lookupMap2 = new LinkedHashMap<>();
    private static ShaderEffect instance;
    private static float factor2;
    private static long timestamp;
    private static Boolean booleanValue;
    private static final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    private static final IntBuffer intBuffer2 = BufferUtils.createIntBuffer(16);
    private final String text = "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 texelSize, direction;\nuniform float radius;\nuniform float weights[256];\n\n#define offset texelSize * direction\n\nvoid main() {\n    if (direction.y > 0 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n    float blr = texture2D(inTexture, gl_TexCoord[0].st).a * weights[0];\n\n    for (float f = 1.0; f <= radius; f++) {\n        blr += texture2D(inTexture, gl_TexCoord[0].st + f * offset).a * (weights[int(abs(f))]);\n        blr += texture2D(inTexture, gl_TexCoord[0].st - f * offset).a * (weights[int(abs(f))]);\n    }\n\n    gl_FragColor = vec4(0.0, 0.0, 0.0, blr);\n}\n";
    private final String text2 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D textureIn;\nuniform float radius, alpha;\n\nfloat roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) -size, 0.)) - radius;\n}\n\n\nvoid main() {\n    float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;\n    gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);\n}";
    private final String text3 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color, outlineColor;\nuniform float radius, outlineThickness;\n\nfloat roundedSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;\n}\n\nvoid main() {\n    float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);\n\n    float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));\n\n    vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);\n    gl_FragColor = mix(outlineColor, insideColor, blendAmount);\n\n}";
    private final String text4 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color1, color2, color3, color4;\nuniform float radius;\n\n#define NOISE .5/255.0\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b , 0.0)) - r;\n}\n\nvec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n    //Dithering the color\n    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 st = gl_TexCoord[0].st;\n    vec2 halfSize = rectSize * .5;\n    \n   // use the bottom leftColor as the alpha\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius)));\n    vec4 gradient = createGradient(st, color1, color2, color3, color4);    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);\n}";
    private final String text5 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color;\nuniform float radius;\nuniform bool blur;\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b, 0.0)) - r;\n}\n\n\nvoid main() {\n    vec2 rectHalf = rectSize * .5;\n    // Smooth the result (free antialiasing).\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;\n    gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n\n}";
    private final String text6 = "#version 120\nuniform sampler2D inTexture;\nuniform vec2 direction;\nuniform int premultiply;\nvec4 readColor(vec2 uv) { vec4 c=texture2D(inTexture,uv); if(premultiply==1)c.rgb*=c.a; return c; }\nvoid main() {\n    vec2 uv=gl_TexCoord[0].st;\n    vec4 c=readColor(uv)*0.227027;\n    c+=(readColor(uv+direction*1.384615)+readColor(uv-direction*1.384615))*0.316216;\n    c+=(readColor(uv+direction*3.230769)+readColor(uv-direction*3.230769))*0.070270;\n    gl_FragColor=c;\n}\n";
    private final String text7 = "#version 120\nuniform sampler2D inTexture,maskTexture;\nuniform float strength;\nuniform int glow;\nvoid main() {\n    vec2 uv=gl_TexCoord[0].st; vec4 c=texture2D(inTexture,uv);\n    if(glow==1)gl_FragColor=vec4(c.rgb*strength,0.0);\n    else gl_FragColor=vec4(c.rgb,texture2D(maskTexture,uv).a*strength);\n}\n";
    private final String text8 = "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform int check;\n\nvoid main() {\n  //  if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum.rgb *= sum.a;\n    vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);\n    smpl1.rgb *= smpl1.a;\n    sum += smpl1 * 2.0;\n    vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3 * 2.0;\n    vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp5.rgb *= smp5.a;\n    sum += smp5 * 2.0;\n    vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    smp6.rgb *= smp6.a;\n    sum += smp6;\n    vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);\n    smp7.rgb *= smp7.a;\n    sum += smp7 * 2.0;\n    vec4 result = sum / 12.0;\n    gl_FragColor = vec4(result.rgb / max(result.a, 0.00001), mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a),check));\n}";
    private final String text9 = "#version 120\n\nuniform sampler2D inTexture;\nuniform vec2 offset, halfpixel, iResolution;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);\n    sum.rgb *= sum.a;\n    sum *= 4.0;\n    vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);\n    smp1.rgb *= smp1.a;\n    sum += smp1;\n    vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3;\n    vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 result = sum / 8.0;\n    gl_FragColor = vec4(result.rgb / max(result.a, 0.00001), result.a);\n}";
    private final String text10 = "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform int check;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;\n\n    gl_FragColor = vec4(sum.rgb /12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));\n}\n";
    private final String text11 = "#version 120\n\nuniform sampler2D inTexture;\nuniform vec2 offset, halfpixel, iResolution;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;\n    sum += texture2D(inTexture, uv - halfpixel.xy * offset);\n    sum += texture2D(inTexture, uv + halfpixel.xy * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n    gl_FragColor = vec4(sum.rgb * .125, 1.0);\n}\n";
    private final String text12 = "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D tex;\nuniform vec4 color1, color2, color3, color4;\n\n#define NOISE .5/255.0\n\nvec3 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n    //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));\n    return color;\n}\nvoid main() {\n    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n    float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4).rgb, texColorAlpha);\n}";
    private final String text13 = "uniform float TIME;\nuniform vec2 RESOLUTION;\nuniform vec2 ORIGIN;\nconst float PI = 3.141592654;\nconst float TAU = 3.141592654 * 2;\n\nconst float gravity = 1.0;\nconst float waterTension = 0.01;\n\nconst vec3 skyCol1 = vec3(0.6, 0.35, 0.3).zyx*0.5;\nconst vec3 skyCol2 = vec3(1.0, 0.3, 0.3).zyx*0.5 ;\nconst vec3 sunCol1 = vec3(1.0,0.5,0.4).zyx;\nconst vec3 sunCol2 = vec3(1.0,0.8,0.8).zyx;\nconst vec3 seaCol1 = vec3(0.1,0.2,0.2)*0.2;\nconst vec3 seaCol2 = vec3(0.2,0.9,0.6)*0.5;\n\n// License: Unknown, author: Unknown, found: don't remember\nfloat tanh_approx(float x) {\n  //  Found this somewhere on the interwebs\n  //  return tanh(x);\n  float x2 = x*x;\n  return clamp(x*(27.0 + x2)/(27.0+9.0*x2), -1.0, 1.0);\n}\n\nvec2 wave(in float t, in float a, in float w, in float p) {\n  float x = t;\n  float y = a*sin(t*w + p);\n  return vec2(x, y);\n}\n\nvec2 dwave(in float t, in float a, in float w, in float p) {\n  float dx = 1.0;\n  float dy = a*w*cos(t*w + p);\n  return vec2(dx, dy);\n}\n\nvec2 gravityWave(in float t, in float a, in float k, in float h) {\n  float w = sqrt(gravity*k*tanh_approx(k*h));\n  return wave(t, a ,k, w*TIME);\n}\n\nvec2 capillaryWave(in float t, in float a, in float k, in float h) {\n  float w = sqrt((gravity*k + waterTension*k*k*k)*tanh_approx(k*h));\n  return wave(t, a, k, w*TIME);\n}\n\nvec2 gravityWaveD(in float t, in float a, in float k, in float h) {\n  float w = sqrt(gravity*k*tanh_approx(k*h));\n  return dwave(t, a, k, w*TIME);\n}\n\nvec2 capillaryWaveD(in float t, in float a, in float k, in float h) {\n  float w = sqrt((gravity*k + waterTension*k*k*k)*tanh_approx(k*h));\n  return dwave(t, a, k, w*TIME);\n}\n\nvoid mrot(inout vec2 p, in float a) {\n  float c = cos(a);\n  float s = sin(a);\n  p = vec2(c*p.x + s*p.y, -s*p.x + c*p.y);\n}\n\nvec4 sea(in vec2 p, in float ia) {\n  float y = 0.0;\n  vec3 d = vec3(0.0);\n\n  const int maxIter = 8;\n  const int midIter = 4;\n\n  float kk = 1.0/1.3;\n  float aa = 1.0/(kk*kk);\n  float k = 1.0*pow(kk, -float(maxIter) + 1.0);\n  float a = ia*0.25*pow(aa, -float(maxIter) + 1.0);\n\n  float h = 25.0;\n  p *= 0.5;\n\n  vec2 waveDir = vec2(0.0, 1.0);\n\n  for (int i = midIter; i < maxIter; ++i) {\n    float t = dot(-waveDir, p) + float(i);\n    y += capillaryWave(t, a, k, h).y;\n    vec2 dw = capillaryWaveD(-t, a, k, h);\n\n    d += vec3(waveDir.x, dw.y, waveDir.y);\n\n    mrot(waveDir, PI/3.0);\n\n    k *= kk;\n    a *= aa;\n  }\n\n  waveDir = vec2(0.0, 1.0);\n\n  for (int i = 0; i < midIter; ++i) {\n    float t = dot(waveDir, p) + float(i);\n    y += gravityWave(t, a, k, h).y;\n    vec2 dw = gravityWaveD(t, a, k, h);\n\n    vec2 d2 = vec2(0.0, dw.x);\n\n    d += vec3(waveDir.x, dw.y, waveDir.y);\n\n    mrot(waveDir, -step(2.0, float(i)));\n\n    k *= kk;\n    a *= aa;\n  }\n\n  vec3 t = normalize(d);\n  vec3 nxz = normalize(vec3(t.z, 0.0, -t.x));\n  vec3 nor = cross(t, nxz);\n\n  return vec4(y, nor);\n}\n\nvec3 sunDirection() {\n  vec3 dir = normalize(vec3(0.42, 0.07, 1.0));\n  return dir;\n}\n\nvec3 skyColor(in vec3 rd) {\n  vec3 sunDir = sunDirection();\n  float sunDot = max(dot(rd, sunDir), 0.0);\n  vec3 final = vec3(0.0);\n  final += mix(skyCol1, skyCol2, rd.y);\n  final += 0.5*sunCol1*pow(sunDot, 90.0);\n  final += 4.0*sunCol2*pow(sunDot, 900.0);\n  return final;\n}\n\nvec3 render(in vec3 ro, in vec3 rd) {\n  vec3 col = vec3(0.0);\n\n  float dsea = (0.0 - ro.y)/rd.y;\n\n  vec3 sunDir = sunDirection();\n\n  vec3 sky = skyColor(rd);\n\n  if (dsea > 0.0) {\n    vec3 p = ro + dsea*rd;\n    vec4 s = sea(p.xz, 1.0);\n    float h = s.x;\n    vec3 nor = s.yzw;\n    nor = mix(nor, vec3(0.0, 1.0, 0.0), smoothstep(0.0, 200.0, dsea));\n\n    float fre = clamp(1.0 - dot(-nor,rd), 0.0, 1.0);\n    fre = fre*fre*fre;\n    float dif = mix(0.25, 1.0, max(dot(nor,sunDir), 0.0));\n\n    vec3 refl = skyColor(reflect(rd, nor));\n    vec3 refr = seaCol1 + dif*sunCol1*seaCol2*0.1;\n\n    col = mix(refr, 0.9*refl, fre);\n\n    float atten = max(1.0 - dot(dsea,dsea) * 0.001, 0.0);\n    col += seaCol2*(p.y - h) * 2.0 * atten;\n\n    col = mix(col, sky, 1.0 - exp(-0.01*dsea));\n\n  } else {\n    col = sky;\n  }\n\n  return col;\n}\n\nvoid mainImage( out vec4 fragColor, in vec2 fragCoord ) {\n  vec2 q = fragCoord/RESOLUTION.xy;\n  vec2 p = -1.0 + 2.0*q;\n  p.x *= RESOLUTION.x/RESOLUTION.y;\n\n  vec3 ro = vec3(0.0, 10.0, 0.0);\n  vec3 ww = normalize(vec3(0.0, -0.1, 1.0));\n  vec3 uu = normalize(cross( vec3(0.0,1.0,0.0), ww));\n  vec3 vv = normalize(cross(ww,uu));\n  vec3 rd = normalize(p.x*uu + p.y*vv + 2.5*ww);\n\n  vec3 col = render(ro, rd);\n  if (rd.y > 0.0) {\n    vec2 sq = rd.xy / max(rd.z, 0.2) * 90.0;\n    vec2 sid = floor(sq);\n    vec2 sf = fract(sq) - 0.5;\n    float sh = fract(sin(dot(sid, vec2(127.1, 311.7))) * 43758.5453);\n    if (sh > 0.93) {\n      vec2 so = vec2(fract(sh * 17.0), fract(sh * 31.0)) - 0.5;\n      float sd = length(sf - so * 0.7) * RESOLUTION.y / 90.0 * 1.6;\n      col += vec3(0.75, 0.82, 1.0) * exp(-sd * sd * 0.5) * (0.5 + 0.5 * sin(TIME * 1.3 + sh * 60.0)) * smoothstep(0.0, 0.08, rd.y) * 0.8;\n    }\n  }\n  col = clamp((col * (2.51 * col + 0.03)) / (col * (2.43 * col + 0.59) + 0.14), 0.0, 1.0);\n  col *= 1.0 - 0.30 * smoothstep(0.6, 1.9, length(p * vec2(0.75, 1.0)));\n  col += (fract(sin(dot(fragCoord + fract(TIME) * 61.0, vec2(12.9898, 78.233))) * 43758.5453) - 0.5) / 255.0;\n\n  fragColor = vec4(col,1.0);\n}\n\nvoid main(void)\n{\n mainImage(gl_FragColor, gl_FragCoord.xy-ORIGIN);\n}\n";
    private final String text14 = "#version 120\n\nuniform sampler2D textureIn;\nuniform vec2 texelSize, direction;\nuniform float radius;\nuniform float weights[256];\n\n#define offset texelSize * direction\n\nvoid main() {\n    vec3 blr = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];\n\n    for (float f = 1.0; f <= radius; f++) {\n        blr += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);\n        blr += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);\n    }\n\n    gl_FragColor = vec4(blr, 1.0);\n}\n";
    private final String text15 = "#extension GL_OES_standard_derivatives : enable\n\n#ifdef GL_ES\nprecision highp float;\n#endif\n\nuniform float time;\nuniform vec2  resolution;\nuniform float zoom;\n\n#define PI 3.1415926535\n\nmat2 rotate3d(float angle)\n{\n    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));\n}\n\nvoid main()\n{\n    vec2 p = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);\n    p = rotate3d((time * 2.0) * PI) * p;\n    float t;\n    if (sin(time) == 10.0)\n        t = 0.075 / abs(1.0 - length(p));\n    else\n        t = 0.075 / abs(0.4/*sin(time)*/ - length(p));\n    gl_FragColor = vec4(     ( 1. -exp( -vec3(t)  * vec3(0.13*(sin(time)+12.0), p.y*0.7, 3.0) )) , 1.0);\n}";
    private final String text16 = "#version 120\n\nuniform sampler2D textureIn;\nuniform vec4 color;\n\nvoid main() {\n    float alpha = texture2D(textureIn, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(color.rgb, color.a * mix(0.0, alpha, step(0.0, alpha)));\n}\n";
    private final String text17 = "#version 120\n\nuniform sampler2D textureIn, textureToCheck;\nuniform vec2 texelSize, direction;\nuniform vec3 color;\nuniform bool avoidTexture;\nuniform float exposure, radius;\nuniform float weights[256];\n\n#define offset direction * texelSize\n\nvoid main() {\n    if (direction.y == 1 && avoidTexture) {\n        if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n    }\n\n    float innerAlpha = texture2D(textureIn, gl_TexCoord[0].st).a * weights[0];\n\n    for (float r = 1.0; r <= radius; r ++) {\n        innerAlpha += texture2D(textureIn, gl_TexCoord[0].st + offset * r).a * weights[int(r)];\n        innerAlpha += texture2D(textureIn, gl_TexCoord[0].st - offset * r).a * weights[int(r)];\n    }\n\n    gl_FragColor = vec4(color, mix(innerAlpha, 1.0 - exp(-innerAlpha * exposure), step(0.0, direction.y)));\n}\n";
    private final String text18 = "#version 120\n\nuniform vec2 texelSize, direction;\nuniform sampler2D texture;\nuniform float radius;\nuniform vec3 color;\n\n#define offset direction * texelSize\n\nvoid main() {\n    float centerAlpha = texture2D(texture, gl_TexCoord[0].xy).a;\n    float innerAlpha = centerAlpha;\n    for (float r = 1.0; r <= radius; r++) {\n        float alphaCurrent1 = texture2D(texture, gl_TexCoord[0].xy + offset * r).a;\n        float alphaCurrent2 = texture2D(texture, gl_TexCoord[0].xy - offset * r).a;\n\n        innerAlpha += alphaCurrent1 + alphaCurrent2;\n    }\n\n    gl_FragColor = vec4(color, innerAlpha) * step(0.0, -centerAlpha);\n}\n\n\n";
    private final String text19 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nfloat fbm(vec2 p) {\n    float value = 0.0;\n    float amplitude = 0.5;\n    float frequency = 1.0;\n    for (int i = 0; i < 4; i++) {\n        value += amplitude * noise(p * frequency);\n        amplitude *= 0.5;\n        frequency *= 2.0;\n    }\n    return value;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n    vec2 p = uv * 2.0;\n\n    // Animated fire rising from bottom\n    float fireHeight = fbm(vec2(p.x * 1.5, p.y - TIME * 0.3));\n    fireHeight = smoothstep(0.0, 1.0, fireHeight);\n\n    // Color gradient for fire\n    vec3 darkRed = vec3(0.1, 0.0, 0.0);\n    vec3 deepRed = vec3(0.3, 0.05, 0.05);\n    vec3 brightOrange = vec3(1.0, 0.4, 0.1);\n    vec3 brightYellow = vec3(1.0, 0.9, 0.3);\n\n    vec3 fireColor;\n    if (fireHeight < 0.4) {\n        fireColor = mix(darkRed, deepRed, fireHeight / 0.4);\n    } else if (fireHeight < 0.7) {\n        fireColor = mix(deepRed, brightOrange, (fireHeight - 0.4) / 0.3);\n    } else {\n        fireColor = mix(brightOrange, brightYellow, (fireHeight - 0.7) / 0.3);\n    }\n\n    gl_FragColor = vec4(fireColor, 1.0);\n}\n";
    private final String text20 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Dark sky background\n    vec3 skyColor = vec3(0.02, 0.03, 0.08);\n\n    // Stars\n    float starValue = 0.0;\n    for (float i = 0.0; i < 3.0; i++) {\n        vec2 starUV = uv * (100.0 + i * 50.0);\n        float star = noise(starUV);\n        star = pow(star, 20.0) * (0.3 + i * 0.2);\n        // Twinkling\n        vec2 starFloor = floor(starUV * 10.0);\n        float twinkle = sin(TIME * 3.0 + hash(starFloor) * 6.28) * 0.5 + 0.5;\n        starValue += star * twinkle;\n    }\n    skyColor += vec3(starValue);\n\n    // Aurora bands\n    float auroraValue = 0.0;\n    for (int i = 0; i < 4; i++) {\n        float bandY = 0.6 + float(i) * 0.08;\n        float wave = sin(uv.x * 3.0 + TIME * 0.3 + float(i) * 1.1) * 0.1;\n        float band = smoothstep(bandY - 0.1, bandY + 0.1, uv.y + wave);\n        float shimmer = sin(uv.x * 5.0 + TIME * 0.2 + float(i) * 0.7) * 0.5 + 0.5;\n        auroraValue += band * shimmer * 0.6;\n    }\n\n    vec3 auroraColor = vec3(0.0);\n    vec3 auroraC0 = vec3(0.0, 0.8, 0.6);\n    vec3 auroraC1 = vec3(0.2, 0.6, 1.0);\n    vec3 auroraC2 = vec3(0.6, 0.2, 0.8);\n    vec3 auroraC3 = vec3(0.3, 1.0, 0.4);\n    auroraColor += auroraC0 * auroraValue * 0.1;\n    auroraColor += auroraC1 * auroraValue * 0.2;\n    auroraColor += auroraC2 * auroraValue * 0.3;\n    auroraColor += auroraC3 * auroraValue * 0.4;\n\n    skyColor += auroraColor * 0.8;\n\n    // God rays / light shaft effect from top\n    float rayAngle = TIME * 0.1;\n    for (int i = 0; i < 3; i++) {\n        float rayX = 0.5 + sin(rayAngle + float(i) * 2.09) * 0.3;\n        float rayDist = abs(uv.x - rayX);\n        float rayIntensity = smoothstep(0.3, 0.0, rayDist);\n        float rayFade = smoothstep(0.0, 0.3, uv.y);\n        vec3 rayColor = vec3(1.0, 1.0, 0.9) * rayIntensity * rayFade * 0.15;\n        skyColor += rayColor;\n    }\n\n    // Fog at bottom\n    float fogAmount = smoothstep(0.0, 0.3, uv.y);\n    vec3 fogColor = vec3(0.4, 0.5, 0.6);\n    skyColor = mix(skyColor, fogColor * 0.3, 1.0 - fogAmount);\n\n    gl_FragColor = vec4(skyColor, 1.0);\n}\n";
    private final String text21 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nfloat fbm(vec2 p) {\n    float value = 0.0;\n    float amplitude = 0.5;\n    for (int i = 0; i < 6; i++) {\n        value += amplitude * noise(p);\n        p *= 2.0;\n        amplitude *= 0.5;\n    }\n    return value;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Slow moving lava\n    vec2 lavaUV = uv * 2.0;\n    float lavaNoise = fbm(lavaUV + TIME * 0.1);\n\n    // Dark rock\n    vec3 rockColor = vec3(0.1, 0.08, 0.08);\n    vec3 lavaColor = vec3(1.0, 0.3, 0.0);\n    vec3 glowColor = vec3(1.0, 0.6, 0.1);\n\n    // Create lava pattern\n    float lavaPattern = smoothstep(-0.2, 0.2, lavaNoise);\n\n    // Cracks - bright lines where lava breaks through\n    float crackNoise = fbm(lavaUV * 3.0 + TIME * 0.05);\n    float cracks = smoothstep(0.4, 0.6, crackNoise);\n\n    // Bubbling effect\n    float bubbleNoise = noise(uv * 10.0 + TIME * 0.5);\n    float bubbles = smoothstep(0.7, 0.9, bubbleNoise) * 0.3;\n\n    // Combine all effects\n    vec3 color = mix(rockColor, lavaColor, lavaPattern * 0.6);\n    color += glowColor * cracks * 0.8;\n    color += glowColor * bubbles;\n\n    // Add subtle red glow\n    color += vec3(0.2, 0.05, 0.0) * (1.0 - uv.y);\n\n    gl_FragColor = vec4(color, 1.0);\n}\n";
    private final String text22 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Dark starfield background\n    vec3 bgColor = vec3(0.01, 0.01, 0.02);\n    float stars = hash(uv * 200.0);\n    bgColor += vec3(stars * 0.5);\n\n    // Aurora curtains - multiple color bands\n    vec3 aurora = vec3(0.0);\n    for (int band = 0; band < 4; band++) {\n        float bandY = 0.5 + float(band) * 0.1;\n        float yOffset = sin(TIME * 0.2 + float(band)) * 0.05;\n\n        // Wave pattern\n        float wave = sin(uv.x * 8.0 + TIME * 0.3 + float(band) * 1.5) * 0.03;\n        wave += sin(uv.x * 12.0 - TIME * 0.2 + float(band) * 2.0) * 0.02;\n\n        float curtainY = bandY + yOffset + wave;\n        float dist = abs(uv.y - curtainY);\n\n        // Fade at edges\n        float alpha = smoothstep(0.15, 0.0, dist) * smoothstep(0.0, 0.15, uv.y);\n\n        // Color based on band\n        vec3 bandColor;\n        if (band == 0) bandColor = vec3(0.2, 0.9, 0.5);\n        else if (band == 1) bandColor = vec3(0.1, 0.7, 1.0);\n        else if (band == 2) bandColor = vec3(0.6, 0.3, 0.9);\n        else bandColor = vec3(0.9, 0.4, 0.7);\n\n        aurora += bandColor * alpha * 0.7;\n    }\n\n    vec3 color = bgColor + aurora;\n\n    // Subtle vignette\n    float vignette = 1.0 - length(uv - 0.5) * 0.5;\n    color *= vignette;\n\n    gl_FragColor = vec4(color, 1.0);\n}\n";
    private final String text23 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nfloat fbm(vec2 p) {\n    float value = 0.0;\n    float amplitude = 0.5;\n    for (int i = 0; i < 5; i++) {\n        value += amplitude * noise(p);\n        p *= 2.0;\n        amplitude *= 0.5;\n    }\n    return value;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Deep black space\n    vec3 color = vec3(0.0, 0.0, 0.01);\n\n    // Rotating star field\n    float angle = TIME * 0.02;\n    float cosA = cos(angle);\n    float sinA = sin(angle);\n    mat2 rotation = mat2(cosA, -sinA, sinA, cosA);\n    vec2 rotatedUV = (uv - 0.5) * rotation + 0.5;\n\n    float stars = pow(noise(rotatedUV * 500.0), 30.0);\n    color += vec3(stars * 0.8);\n\n    // Twinkling stars\n    vec2 rotatedFloor = floor(rotatedUV * 200.0);\n    float twinkle = sin(TIME * 2.0 + hash(rotatedFloor) * 6.28) * 0.5 + 0.5;\n    color += vec3(twinkle * stars * 0.3);\n\n    // Nebula color washes\n    vec3 nebulaC0 = vec3(0.3, 0.1, 0.5);\n    vec3 nebulaC1 = vec3(0.1, 0.2, 0.6);\n    vec3 nebulaC2 = vec3(0.5, 0.1, 0.4);\n\n    for (int i = 0; i < 3; i++) {\n        float scale = 1.5 + float(i) * 0.5;\n        vec2 timeOffset = TIME * vec2(0.02, 0.01) * float(i + 1);\n        vec2 nebulaUV = uv * scale + timeOffset;\n        float nebula = fbm(nebulaUV) * 0.4;\n        if (i == 0) color += nebulaC0 * nebula * 0.3;\n        else if (i == 1) color += nebulaC1 * nebula * 0.3;\n        else color += nebulaC2 * nebula * 0.3;\n    }\n\n    // Planet 1 - larger, top right\n    vec2 planet1Pos = vec2(0.8, 0.3);\n    float planet1Dist = length(uv - planet1Pos);\n    float planet1 = smoothstep(0.08, 0.075, planet1Dist);\n    vec3 planet1Color = vec3(0.4, 0.3, 0.7);\n    float rim1 = smoothstep(0.075, 0.08, planet1Dist);\n    planet1Color += vec3(rim1 * 0.5);\n    color = mix(color, planet1Color, planet1);\n\n    // Planet 2 - smaller, bottom left\n    vec2 planet2Pos = vec2(0.2, 0.7);\n    float planet2Dist = length(uv - planet2Pos);\n    float planet2 = smoothstep(0.05, 0.045, planet2Dist);\n    vec3 planet2Color = vec3(0.7, 0.3, 0.2);\n    float rim2 = smoothstep(0.045, 0.05, planet2Dist);\n    planet2Color += vec3(rim2 * 0.5);\n    color = mix(color, planet2Color, planet2);\n\n    // Subtle vignette\n    float vignette = 1.0 - length(uv - 0.5) * 0.8;\n    color *= clamp(vignette, 0.0, 1.0);\n\n    gl_FragColor = vec4(color, 1.0);\n}\n";
    private final String text24 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Fade to black at bottom\n    float fade = smoothstep(0.0, 0.3, uv.y);\n\n    // Multiple columns of falling code\n    vec3 color = vec3(0.0);\n\n    for (int i = 0; i < 15; i++) {\n        float columnX = fract(float(i) * 73.17 / 15.0);\n        float speed = 0.5 + hash(vec2(float(i), 0.0)) * 0.5;\n        float speedY = 0.3 + hash(vec2(0.0, float(i))) * 0.2;\n\n        float dropY = fract(TIME * speedY + float(i) * 31.1);\n        dropY = 1.0 - dropY;\n\n        float dist = abs(uv.x - columnX);\n        float columnWidth = 0.02 + hash(vec2(float(i), 1.0)) * 0.02;\n        float column = smoothstep(columnWidth, 0.0, dist);\n\n        // Bright green characters\n        float charRow = floor(dropY * 20.0);\n        float brightness = hash(vec2(float(i), charRow));\n        brightness = pow(brightness, 3.0);\n\n        // Some characters are brighter (whiter)\n        float isBright = step(0.95, brightness);\n        vec3 charColor = mix(vec3(0.0, 0.5, 0.1), vec3(0.4, 1.0, 0.4), isBright);\n\n        // Head of the column is brighter\n        float head = smoothstep(0.95, 1.0, dropY);\n        charColor += vec3(head * 0.5);\n\n        // Trail effect\n        float trail = smoothstep(0.0, 0.3, dropY);\n        trail *= smoothstep(0.3, 0.0, dropY - 0.05);\n\n        color += charColor * column * trail * brightness;\n    }\n\n    // Background fade\n    color *= fade;\n\n    // Green tint\n    color += vec3(0.0, 0.05, 0.0);\n\n    gl_FragColor = vec4(color, 1.0);\n}\n";
    private final String text25 = "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n    float y = uv.y;\n\n    // Warm gradient\n    vec3 darkBlue = vec3(0.1, 0.1, 0.3);\n    vec3 purple = vec3(0.4, 0.2, 0.5);\n    vec3 pink = vec3(0.8, 0.4, 0.4);\n    vec3 orange = vec3(1.0, 0.5, 0.2);\n\n    vec3 skyColor;\n    if (y < 0.25) {\n        skyColor = mix(darkBlue, purple, y / 0.25);\n    } else if (y < 0.5) {\n        skyColor = mix(purple, pink, (y - 0.25) / 0.25);\n    } else if (y < 0.75) {\n        skyColor = mix(pink, orange, (y - 0.5) / 0.25);\n    } else {\n        skyColor = mix(orange, vec3(0.3, 0.2, 0.2), (y - 0.75) / 0.25);\n    }\n\n    // Animated sun just above horizon\n    vec2 sunPos = vec2(0.5, 0.72);\n    float sunDist = length(uv - sunPos);\n    float sunRadius = 0.08;\n    float sunGlow = 0.15;\n    float sun = smoothstep(sunRadius, sunRadius - 0.01, sunDist);\n    float glow = smoothstep(sunGlow, sunRadius - 0.01, sunDist) - sun;\n    glow = clamp(glow, 0.0, 1.0);\n\n    // Sun colors with bloom\n    vec3 sunCore = vec3(1.0, 0.9, 0.7);\n    vec3 sunOuter = vec3(1.0, 0.6, 0.3);\n    vec3 sunGlowColor = vec3(1.0, 0.4, 0.2);\n\n    skyColor += sunCore * sun;\n    skyColor += sunOuter * glow;\n    skyColor += sunGlowColor * glow * 0.5;\n\n    // Subtle cloud silhouettes\n    for (int i = 0; i < 3; i++) {\n        float cloudY = 0.6 + float(i) * 0.05;\n        float cloudX = fract(float(i) * 31.1 / 3.0);\n        float cloudSpeed = 0.01 + float(i) * 0.005;\n        cloudX += TIME * cloudSpeed;\n\n        float cloudDist = abs(uv.x - cloudX);\n        float cloudWidth = 0.2 + float(i) * 0.05;\n        float cloud = smoothstep(cloudWidth, 0.0, cloudDist);\n        float cloudHeight = smoothstep(0.02, 0.0, abs(uv.y - cloudY));\n\n        vec3 cloudColor = vec3(0.2, 0.15, 0.25);\n        skyColor = mix(skyColor, cloudColor, cloud * cloudHeight * 0.7);\n    }\n\n    // Add subtle warm atmosphere\n    skyColor += vec3(0.05, 0.03, 0.0) * (1.0 - y);\n\n    gl_FragColor = vec4(skyColor, 1.0);\n}\n";

    private static boolean checkState() {
        if (booleanValue == null) {
            booleanValue = IntelShader.matchesString(GL11.glGetString(7937));
        }

        return booleanValue;
    }

    public static String readInputStream(InputStream in) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void handleString(String str, float... floats) {
        int n = this.computeInt(str);
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

    public static void handleShaderEffect(ShaderEffect shaderEffect, int n, int n2, int n3, int n4) {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        if (!OpenGlHelper.isFramebufferEnabled()) {
            intBuffer2.clear();
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer2);
            int n5 = n * scaleFactor;
            int n6 = mc.displayHeight - (n2 + n4) * scaleFactor;
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();

            try {
                GlStateManager.viewport(n5, n6, Math.max(1, n3 * scaleFactor), Math.max(1, n4 * scaleFactor));
                shaderEffect.handleString("ORIGIN", (float)n5, (float)n6);
                shaderEffect.handleString("RESOLUTION", (float)Math.max(1, n3 * scaleFactor), (float)Math.max(1, n4 * scaleFactor));
                process();
            } finally {
                shaderEffect.handleString("ORIGIN", 0.0F, 0.0F);
                shaderEffect.unload();
                GlStateManager.viewport(intBuffer2.get(0), intBuffer2.get(1), intBuffer2.get(2), intBuffer2.get(3));
            }
        } else {
            intBuffer2.clear();
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer2);
            int n7 = IntelShader.computeInt(checkState());
            int n8 = Math.max(1, Math.min(n7, n3 * scaleFactor));
            int n9 = Math.max(1, Math.min(n7, n4 * scaleFactor));
            int n10 = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            String str = shaderEffect.count + ":" + shaderEffect.factor;
            ShaderEffect.Entry entry = lookupMap2.get(str);
            if (entry == null || entry.shaderEffect != shaderEffect || entry.framebuffer.framebufferWidth != n8 || entry.framebuffer.framebufferHeight != n9) {
                if (entry != null) {
                    entry.framebuffer.deleteFramebuffer();
                }

                if (lookupMap2.size() >= 24 && !lookupMap2.containsKey(str)) {
                    Iterator it = lookupMap2.values().iterator();
                    ShaderEffect.Entry entry2 = (ShaderEffect.Entry)it.next();
                    entry2.framebuffer.deleteFramebuffer();
                    it.remove();
                }

                entry = new ShaderEffect.Entry(shaderEffect, n8, n9);
                lookupMap2.put(str, entry);
            }

            framebuffer2 = entry.framebuffer;
            long now = System.nanoTime();
            boolean flag = entry.timestamp == 0L || now - entry.timestamp >= IntelShader.computeLong(checkState());
            boolean flag2 = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST);
            if (flag2) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            GlStateManager.colorMask(true, true, true, true);
            if (flag) {
                framebuffer2.framebufferClear();
                framebuffer2.bindFramebuffer(true);
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                shaderEffect.handleString("ORIGIN", 0.0F, 0.0F);
                shaderEffect.handleString("RESOLUTION", (float)n8, (float)n9);
                process();
                entry.timestamp = now;
            }

            shaderEffect.unload();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, n10);
            GlStateManager.viewport(intBuffer2.get(0), intBuffer2.get(1), intBuffer2.get(2), intBuffer2.get(3));
            if (flag2) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(framebuffer2.framebufferTexture);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(n, n2);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(n, n2 + n4);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(n + n3, n2 + n4);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(n + n3, n2);
            GL11.glEnd();
            GlStateManager.bindTexture(0);
        }
    }

    public void unload() {
        GL20.glUseProgram(0);
    }

    public void handleString(String str, float f, float f2, float f3, float f4) {
        GL20.glUniform4f(this.computeInt(str), f, f2, f3, f4);
    }

    public ShaderEffect(String str, String str2) {
        int n = GL20.glCreateProgram();
        int n2 = 0;
        int n3 = 0;

        try {
            n2 = switch (str) {
                case "hudBlur" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform sampler2D inTexture;\nuniform vec2 direction;\nuniform int premultiply;\nvec4 readColor(vec2 uv) { vec4 c=texture2D(inTexture,uv); if(premultiply==1)c.rgb*=c.a; return c; }\nvoid main() {\n    vec2 uv=gl_TexCoord[0].st;\n    vec4 c=readColor(uv)*0.227027;\n    c+=(readColor(uv+direction*1.384615)+readColor(uv-direction*1.384615))*0.316216;\n    c+=(readColor(uv+direction*3.230769)+readColor(uv-direction*3.230769))*0.070270;\n    gl_FragColor=c;\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "hudComposite" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform sampler2D inTexture,maskTexture;\nuniform float strength;\nuniform int glow;\nvoid main() {\n    vec2 uv=gl_TexCoord[0].st; vec4 c=texture2D(inTexture,uv);\n    if(glow==1)gl_FragColor=vec4(c.rgb*strength,0.0);\n    else gl_FragColor=vec4(c.rgb,texture2D(maskTexture,uv).a*strength);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "shadow" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 texelSize, direction;\nuniform float radius;\nuniform float weights[256];\n\n#define offset texelSize * direction\n\nvoid main() {\n    if (direction.y > 0 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n    float blr = texture2D(inTexture, gl_TexCoord[0].st).a * weights[0];\n\n    for (float f = 1.0; f <= radius; f++) {\n        blr += texture2D(inTexture, gl_TexCoord[0].st + f * offset).a * (weights[int(abs(f))]);\n        blr += texture2D(inTexture, gl_TexCoord[0].st - f * offset).a * (weights[int(abs(f))]);\n    }\n\n    gl_FragColor = vec4(0.0, 0.0, 0.0, blr);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "roundRectTexture" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D textureIn;\nuniform float radius, alpha;\n\nfloat roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) -size, 0.)) - radius;\n}\n\n\nvoid main() {\n    float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;\n    gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "roundRectOutline" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color, outlineColor;\nuniform float radius, outlineThickness;\n\nfloat roundedSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;\n}\n\nvoid main() {\n    float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);\n\n    float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));\n\n    vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);\n    gl_FragColor = mix(outlineColor, insideColor, blendAmount);\n\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "roundedRect" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color;\nuniform float radius;\nuniform bool blur;\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b, 0.0)) - r;\n}\n\n\nvoid main() {\n    vec2 rectHalf = rectSize * .5;\n    // Smooth the result (free antialiasing).\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;\n    gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "roundedRectGradient" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color1, color2, color3, color4;\nuniform float radius;\n\n#define NOISE .5/255.0\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b , 0.0)) - r;\n}\n\nvec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n    //Dithering the color\n    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 st = gl_TexCoord[0].st;\n    vec2 halfSize = rectSize * .5;\n    \n   // use the bottom leftColor as the alpha\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius)));\n    vec4 gradient = createGradient(st, color1, color2, color3, color4);    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "gradient" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D tex;\nuniform vec4 color1, color2, color3, color4;\n\n#define NOISE .5/255.0\n\nvec3 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n    //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));\n    return color;\n}\nvoid main() {\n    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n    float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4).rgb, texColorAlpha);\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "mainmenu" -> this.computeInt(
                    new ByteArrayInputStream(
                        "uniform float TIME;\nuniform vec2 RESOLUTION;\nuniform vec2 ORIGIN;\nconst float PI = 3.141592654;\nconst float TAU = 3.141592654 * 2;\n\nconst float gravity = 1.0;\nconst float waterTension = 0.01;\n\nconst vec3 skyCol1 = vec3(0.6, 0.35, 0.3).zyx*0.5;\nconst vec3 skyCol2 = vec3(1.0, 0.3, 0.3).zyx*0.5 ;\nconst vec3 sunCol1 = vec3(1.0,0.5,0.4).zyx;\nconst vec3 sunCol2 = vec3(1.0,0.8,0.8).zyx;\nconst vec3 seaCol1 = vec3(0.1,0.2,0.2)*0.2;\nconst vec3 seaCol2 = vec3(0.2,0.9,0.6)*0.5;\n\n// License: Unknown, author: Unknown, found: don't remember\nfloat tanh_approx(float x) {\n  //  Found this somewhere on the interwebs\n  //  return tanh(x);\n  float x2 = x*x;\n  return clamp(x*(27.0 + x2)/(27.0+9.0*x2), -1.0, 1.0);\n}\n\nvec2 wave(in float t, in float a, in float w, in float p) {\n  float x = t;\n  float y = a*sin(t*w + p);\n  return vec2(x, y);\n}\n\nvec2 dwave(in float t, in float a, in float w, in float p) {\n  float dx = 1.0;\n  float dy = a*w*cos(t*w + p);\n  return vec2(dx, dy);\n}\n\nvec2 gravityWave(in float t, in float a, in float k, in float h) {\n  float w = sqrt(gravity*k*tanh_approx(k*h));\n  return wave(t, a ,k, w*TIME);\n}\n\nvec2 capillaryWave(in float t, in float a, in float k, in float h) {\n  float w = sqrt((gravity*k + waterTension*k*k*k)*tanh_approx(k*h));\n  return wave(t, a, k, w*TIME);\n}\n\nvec2 gravityWaveD(in float t, in float a, in float k, in float h) {\n  float w = sqrt(gravity*k*tanh_approx(k*h));\n  return dwave(t, a, k, w*TIME);\n}\n\nvec2 capillaryWaveD(in float t, in float a, in float k, in float h) {\n  float w = sqrt((gravity*k + waterTension*k*k*k)*tanh_approx(k*h));\n  return dwave(t, a, k, w*TIME);\n}\n\nvoid mrot(inout vec2 p, in float a) {\n  float c = cos(a);\n  float s = sin(a);\n  p = vec2(c*p.x + s*p.y, -s*p.x + c*p.y);\n}\n\nvec4 sea(in vec2 p, in float ia) {\n  float y = 0.0;\n  vec3 d = vec3(0.0);\n\n  const int maxIter = 8;\n  const int midIter = 4;\n\n  float kk = 1.0/1.3;\n  float aa = 1.0/(kk*kk);\n  float k = 1.0*pow(kk, -float(maxIter) + 1.0);\n  float a = ia*0.25*pow(aa, -float(maxIter) + 1.0);\n\n  float h = 25.0;\n  p *= 0.5;\n\n  vec2 waveDir = vec2(0.0, 1.0);\n\n  for (int i = midIter; i < maxIter; ++i) {\n    float t = dot(-waveDir, p) + float(i);\n    y += capillaryWave(t, a, k, h).y;\n    vec2 dw = capillaryWaveD(-t, a, k, h);\n\n    d += vec3(waveDir.x, dw.y, waveDir.y);\n\n    mrot(waveDir, PI/3.0);\n\n    k *= kk;\n    a *= aa;\n  }\n\n  waveDir = vec2(0.0, 1.0);\n\n  for (int i = 0; i < midIter; ++i) {\n    float t = dot(waveDir, p) + float(i);\n    y += gravityWave(t, a, k, h).y;\n    vec2 dw = gravityWaveD(t, a, k, h);\n\n    vec2 d2 = vec2(0.0, dw.x);\n\n    d += vec3(waveDir.x, dw.y, waveDir.y);\n\n    mrot(waveDir, -step(2.0, float(i)));\n\n    k *= kk;\n    a *= aa;\n  }\n\n  vec3 t = normalize(d);\n  vec3 nxz = normalize(vec3(t.z, 0.0, -t.x));\n  vec3 nor = cross(t, nxz);\n\n  return vec4(y, nor);\n}\n\nvec3 sunDirection() {\n  vec3 dir = normalize(vec3(0.42, 0.07, 1.0));\n  return dir;\n}\n\nvec3 skyColor(in vec3 rd) {\n  vec3 sunDir = sunDirection();\n  float sunDot = max(dot(rd, sunDir), 0.0);\n  vec3 final = vec3(0.0);\n  final += mix(skyCol1, skyCol2, rd.y);\n  final += 0.5*sunCol1*pow(sunDot, 90.0);\n  final += 4.0*sunCol2*pow(sunDot, 900.0);\n  return final;\n}\n\nvec3 render(in vec3 ro, in vec3 rd) {\n  vec3 col = vec3(0.0);\n\n  float dsea = (0.0 - ro.y)/rd.y;\n\n  vec3 sunDir = sunDirection();\n\n  vec3 sky = skyColor(rd);\n\n  if (dsea > 0.0) {\n    vec3 p = ro + dsea*rd;\n    vec4 s = sea(p.xz, 1.0);\n    float h = s.x;\n    vec3 nor = s.yzw;\n    nor = mix(nor, vec3(0.0, 1.0, 0.0), smoothstep(0.0, 200.0, dsea));\n\n    float fre = clamp(1.0 - dot(-nor,rd), 0.0, 1.0);\n    fre = fre*fre*fre;\n    float dif = mix(0.25, 1.0, max(dot(nor,sunDir), 0.0));\n\n    vec3 refl = skyColor(reflect(rd, nor));\n    vec3 refr = seaCol1 + dif*sunCol1*seaCol2*0.1;\n\n    col = mix(refr, 0.9*refl, fre);\n\n    float atten = max(1.0 - dot(dsea,dsea) * 0.001, 0.0);\n    col += seaCol2*(p.y - h) * 2.0 * atten;\n\n    col = mix(col, sky, 1.0 - exp(-0.01*dsea));\n\n  } else {\n    col = sky;\n  }\n\n  return col;\n}\n\nvoid mainImage( out vec4 fragColor, in vec2 fragCoord ) {\n  vec2 q = fragCoord/RESOLUTION.xy;\n  vec2 p = -1.0 + 2.0*q;\n  p.x *= RESOLUTION.x/RESOLUTION.y;\n\n  vec3 ro = vec3(0.0, 10.0, 0.0);\n  vec3 ww = normalize(vec3(0.0, -0.1, 1.0));\n  vec3 uu = normalize(cross( vec3(0.0,1.0,0.0), ww));\n  vec3 vv = normalize(cross(ww,uu));\n  vec3 rd = normalize(p.x*uu + p.y*vv + 2.5*ww);\n\n  vec3 col = render(ro, rd);\n  if (rd.y > 0.0) {\n    vec2 sq = rd.xy / max(rd.z, 0.2) * 90.0;\n    vec2 sid = floor(sq);\n    vec2 sf = fract(sq) - 0.5;\n    float sh = fract(sin(dot(sid, vec2(127.1, 311.7))) * 43758.5453);\n    if (sh > 0.93) {\n      vec2 so = vec2(fract(sh * 17.0), fract(sh * 31.0)) - 0.5;\n      float sd = length(sf - so * 0.7) * RESOLUTION.y / 90.0 * 1.6;\n      col += vec3(0.75, 0.82, 1.0) * exp(-sd * sd * 0.5) * (0.5 + 0.5 * sin(TIME * 1.3 + sh * 60.0)) * smoothstep(0.0, 0.08, rd.y) * 0.8;\n    }\n  }\n  col = clamp((col * (2.51 * col + 0.03)) / (col * (2.43 * col + 0.59) + 0.14), 0.0, 1.0);\n  col *= 1.0 - 0.30 * smoothstep(0.6, 1.9, length(p * vec2(0.75, 1.0)));\n  col += (fract(sin(dot(fragCoord + fract(TIME) * 61.0, vec2(12.9898, 78.233))) * 43758.5453) - 0.5) / 255.0;\n\n  fragColor = vec4(col,1.0);\n}\n\nvoid main(void)\n{\n mainImage(gl_FragColor, gl_FragCoord.xy-ORIGIN);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "kawaseUp" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform int check;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;\n    sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;\n\n    gl_FragColor = vec4(sum.rgb /12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "kawaseDown" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D inTexture;\nuniform vec2 offset, halfpixel, iResolution;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;\n    sum += texture2D(inTexture, uv - halfpixel.xy * offset);\n    sum += texture2D(inTexture, uv + halfpixel.xy * offset);\n    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n    gl_FragColor = vec4(sum.rgb * .125, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "kawaseUpBloom" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D inTexture, textureToCheck;\nuniform vec2 halfpixel, offset, iResolution;\nuniform int check;\n\nvoid main() {\n  //  if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n\n    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n    sum.rgb *= sum.a;\n    vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);\n    smpl1.rgb *= smpl1.a;\n    sum += smpl1 * 2.0;\n    vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3 * 2.0;\n    vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp5.rgb *= smp5.a;\n    sum += smp5 * 2.0;\n    vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n    smp6.rgb *= smp6.a;\n    sum += smp6;\n    vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);\n    smp7.rgb *= smp7.a;\n    sum += smp7 * 2.0;\n    vec4 result = sum / 12.0;\n    gl_FragColor = vec4(result.rgb / max(result.a, 0.00001), mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a),check));\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "kawaseDownBloom" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D inTexture;\nuniform vec2 offset, halfpixel, iResolution;\n\nvoid main() {\n    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);\n    sum.rgb *= sum.a;\n    sum *= 4.0;\n    vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);\n    smp1.rgb *= smp1.a;\n    sum += smp1;\n    vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);\n    smp2.rgb *= smp2.a;\n    sum += smp2;\n    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp3.rgb *= smp3.a;\n    sum += smp3;\n    vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n    smp4.rgb *= smp4.a;\n    sum += smp4;\n    vec4 result = sum / 8.0;\n    gl_FragColor = vec4(result.rgb / max(result.a, 0.00001), result.a);\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "gaussianBlur" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D textureIn;\nuniform vec2 texelSize, direction;\nuniform float radius;\nuniform float weights[256];\n\n#define offset texelSize * direction\n\nvoid main() {\n    vec3 blr = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];\n\n    for (float f = 1.0; f <= radius; f++) {\n        blr += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);\n        blr += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);\n    }\n\n    gl_FragColor = vec4(blr, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "cape" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#extension GL_OES_standard_derivatives : enable\n\n#ifdef GL_ES\nprecision highp float;\n#endif\n\nuniform float time;\nuniform vec2  resolution;\nuniform float zoom;\n\n#define PI 3.1415926535\n\nmat2 rotate3d(float angle)\n{\n    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));\n}\n\nvoid main()\n{\n    vec2 p = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);\n    p = rotate3d((time * 2.0) * PI) * p;\n    float t;\n    if (sin(time) == 10.0)\n        t = 0.075 / abs(1.0 - length(p));\n    else\n        t = 0.075 / abs(0.4/*sin(time)*/ - length(p));\n    gl_FragColor = vec4(     ( 1. -exp( -vec3(t)  * vec3(0.13*(sin(time)+12.0), p.y*0.7, 3.0) )) , 1.0);\n}"
                            .getBytes()
                    ),
                    35632
                );
                case "outline" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform vec2 texelSize, direction;\nuniform sampler2D texture;\nuniform float radius;\nuniform vec3 color;\n\n#define offset direction * texelSize\n\nvoid main() {\n    float centerAlpha = texture2D(texture, gl_TexCoord[0].xy).a;\n    float innerAlpha = centerAlpha;\n    for (float r = 1.0; r <= radius; r++) {\n        float alphaCurrent1 = texture2D(texture, gl_TexCoord[0].xy + offset * r).a;\n        float alphaCurrent2 = texture2D(texture, gl_TexCoord[0].xy - offset * r).a;\n\n        innerAlpha += alphaCurrent1 + alphaCurrent2;\n    }\n\n    gl_FragColor = vec4(color, innerAlpha) * step(0.0, -centerAlpha);\n}\n\n\n"
                            .getBytes()
                    ),
                    35632
                );
                case "glow" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D textureIn, textureToCheck;\nuniform vec2 texelSize, direction;\nuniform vec3 color;\nuniform bool avoidTexture;\nuniform float exposure, radius;\nuniform float weights[256];\n\n#define offset direction * texelSize\n\nvoid main() {\n    if (direction.y == 1 && avoidTexture) {\n        if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n    }\n\n    float innerAlpha = texture2D(textureIn, gl_TexCoord[0].st).a * weights[0];\n\n    for (float r = 1.0; r <= radius; r ++) {\n        innerAlpha += texture2D(textureIn, gl_TexCoord[0].st + offset * r).a * weights[int(r)];\n        innerAlpha += texture2D(textureIn, gl_TexCoord[0].st - offset * r).a * weights[int(r)];\n    }\n\n    gl_FragColor = vec4(color, mix(innerAlpha, 1.0 - exp(-innerAlpha * exposure), step(0.0, direction.y)));\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "chams" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\n\nuniform sampler2D textureIn;\nuniform vec4 color;\n\nvoid main() {\n    float alpha = texture2D(textureIn, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(color.rgb, color.a * mix(0.0, alpha, step(0.0, alpha)));\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "fire" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nfloat fbm(vec2 p) {\n    float value = 0.0;\n    float amplitude = 0.5;\n    float frequency = 1.0;\n    for (int i = 0; i < 4; i++) {\n        value += amplitude * noise(p * frequency);\n        amplitude *= 0.5;\n        frequency *= 2.0;\n    }\n    return value;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n    vec2 p = uv * 2.0;\n\n    // Animated fire rising from bottom\n    float fireHeight = fbm(vec2(p.x * 1.5, p.y - TIME * 0.3));\n    fireHeight = smoothstep(0.0, 1.0, fireHeight);\n\n    // Color gradient for fire\n    vec3 darkRed = vec3(0.1, 0.0, 0.0);\n    vec3 deepRed = vec3(0.3, 0.05, 0.05);\n    vec3 brightOrange = vec3(1.0, 0.4, 0.1);\n    vec3 brightYellow = vec3(1.0, 0.9, 0.3);\n\n    vec3 fireColor;\n    if (fireHeight < 0.4) {\n        fireColor = mix(darkRed, deepRed, fireHeight / 0.4);\n    } else if (fireHeight < 0.7) {\n        fireColor = mix(deepRed, brightOrange, (fireHeight - 0.4) / 0.3);\n    } else {\n        fireColor = mix(brightOrange, brightYellow, (fireHeight - 0.7) / 0.3);\n    }\n\n    gl_FragColor = vec4(fireColor, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "light" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Dark sky background\n    vec3 skyColor = vec3(0.02, 0.03, 0.08);\n\n    // Stars\n    float starValue = 0.0;\n    for (float i = 0.0; i < 3.0; i++) {\n        vec2 starUV = uv * (100.0 + i * 50.0);\n        float star = noise(starUV);\n        star = pow(star, 20.0) * (0.3 + i * 0.2);\n        // Twinkling\n        vec2 starFloor = floor(starUV * 10.0);\n        float twinkle = sin(TIME * 3.0 + hash(starFloor) * 6.28) * 0.5 + 0.5;\n        starValue += star * twinkle;\n    }\n    skyColor += vec3(starValue);\n\n    // Aurora bands\n    float auroraValue = 0.0;\n    for (int i = 0; i < 4; i++) {\n        float bandY = 0.6 + float(i) * 0.08;\n        float wave = sin(uv.x * 3.0 + TIME * 0.3 + float(i) * 1.1) * 0.1;\n        float band = smoothstep(bandY - 0.1, bandY + 0.1, uv.y + wave);\n        float shimmer = sin(uv.x * 5.0 + TIME * 0.2 + float(i) * 0.7) * 0.5 + 0.5;\n        auroraValue += band * shimmer * 0.6;\n    }\n\n    vec3 auroraColor = vec3(0.0);\n    vec3 auroraC0 = vec3(0.0, 0.8, 0.6);\n    vec3 auroraC1 = vec3(0.2, 0.6, 1.0);\n    vec3 auroraC2 = vec3(0.6, 0.2, 0.8);\n    vec3 auroraC3 = vec3(0.3, 1.0, 0.4);\n    auroraColor += auroraC0 * auroraValue * 0.1;\n    auroraColor += auroraC1 * auroraValue * 0.2;\n    auroraColor += auroraC2 * auroraValue * 0.3;\n    auroraColor += auroraC3 * auroraValue * 0.4;\n\n    skyColor += auroraColor * 0.8;\n\n    // God rays / light shaft effect from top\n    float rayAngle = TIME * 0.1;\n    for (int i = 0; i < 3; i++) {\n        float rayX = 0.5 + sin(rayAngle + float(i) * 2.09) * 0.3;\n        float rayDist = abs(uv.x - rayX);\n        float rayIntensity = smoothstep(0.3, 0.0, rayDist);\n        float rayFade = smoothstep(0.0, 0.3, uv.y);\n        vec3 rayColor = vec3(1.0, 1.0, 0.9) * rayIntensity * rayFade * 0.15;\n        skyColor += rayColor;\n    }\n\n    // Fog at bottom\n    float fogAmount = smoothstep(0.0, 0.3, uv.y);\n    vec3 fogColor = vec3(0.4, 0.5, 0.6);\n    skyColor = mix(skyColor, fogColor * 0.3, 1.0 - fogAmount);\n\n    gl_FragColor = vec4(skyColor, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "lava" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nfloat fbm(vec2 p) {\n    float value = 0.0;\n    float amplitude = 0.5;\n    for (int i = 0; i < 6; i++) {\n        value += amplitude * noise(p);\n        p *= 2.0;\n        amplitude *= 0.5;\n    }\n    return value;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Slow moving lava\n    vec2 lavaUV = uv * 2.0;\n    float lavaNoise = fbm(lavaUV + TIME * 0.1);\n\n    // Dark rock\n    vec3 rockColor = vec3(0.1, 0.08, 0.08);\n    vec3 lavaColor = vec3(1.0, 0.3, 0.0);\n    vec3 glowColor = vec3(1.0, 0.6, 0.1);\n\n    // Create lava pattern\n    float lavaPattern = smoothstep(-0.2, 0.2, lavaNoise);\n\n    // Cracks - bright lines where lava breaks through\n    float crackNoise = fbm(lavaUV * 3.0 + TIME * 0.05);\n    float cracks = smoothstep(0.4, 0.6, crackNoise);\n\n    // Bubbling effect\n    float bubbleNoise = noise(uv * 10.0 + TIME * 0.5);\n    float bubbles = smoothstep(0.7, 0.9, bubbleNoise) * 0.3;\n\n    // Combine all effects\n    vec3 color = mix(rockColor, lavaColor, lavaPattern * 0.6);\n    color += glowColor * cracks * 0.8;\n    color += glowColor * bubbles;\n\n    // Add subtle red glow\n    color += vec3(0.2, 0.05, 0.0) * (1.0 - uv.y);\n\n    gl_FragColor = vec4(color, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "aurora" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Dark starfield background\n    vec3 bgColor = vec3(0.01, 0.01, 0.02);\n    float stars = hash(uv * 200.0);\n    bgColor += vec3(stars * 0.5);\n\n    // Aurora curtains - multiple color bands\n    vec3 aurora = vec3(0.0);\n    for (int band = 0; band < 4; band++) {\n        float bandY = 0.5 + float(band) * 0.1;\n        float yOffset = sin(TIME * 0.2 + float(band)) * 0.05;\n\n        // Wave pattern\n        float wave = sin(uv.x * 8.0 + TIME * 0.3 + float(band) * 1.5) * 0.03;\n        wave += sin(uv.x * 12.0 - TIME * 0.2 + float(band) * 2.0) * 0.02;\n\n        float curtainY = bandY + yOffset + wave;\n        float dist = abs(uv.y - curtainY);\n\n        // Fade at edges\n        float alpha = smoothstep(0.15, 0.0, dist) * smoothstep(0.0, 0.15, uv.y);\n\n        // Color based on band\n        vec3 bandColor;\n        if (band == 0) bandColor = vec3(0.2, 0.9, 0.5);\n        else if (band == 1) bandColor = vec3(0.1, 0.7, 1.0);\n        else if (band == 2) bandColor = vec3(0.6, 0.3, 0.9);\n        else bandColor = vec3(0.9, 0.4, 0.7);\n\n        aurora += bandColor * alpha * 0.7;\n    }\n\n    vec3 color = bgColor + aurora;\n\n    // Subtle vignette\n    float vignette = 1.0 - length(uv - 0.5) * 0.5;\n    color *= vignette;\n\n    gl_FragColor = vec4(color, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "space" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nfloat fbm(vec2 p) {\n    float value = 0.0;\n    float amplitude = 0.5;\n    for (int i = 0; i < 5; i++) {\n        value += amplitude * noise(p);\n        p *= 2.0;\n        amplitude *= 0.5;\n    }\n    return value;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Deep black space\n    vec3 color = vec3(0.0, 0.0, 0.01);\n\n    // Rotating star field\n    float angle = TIME * 0.02;\n    float cosA = cos(angle);\n    float sinA = sin(angle);\n    mat2 rotation = mat2(cosA, -sinA, sinA, cosA);\n    vec2 rotatedUV = (uv - 0.5) * rotation + 0.5;\n\n    float stars = pow(noise(rotatedUV * 500.0), 30.0);\n    color += vec3(stars * 0.8);\n\n    // Twinkling stars\n    vec2 rotatedFloor = floor(rotatedUV * 200.0);\n    float twinkle = sin(TIME * 2.0 + hash(rotatedFloor) * 6.28) * 0.5 + 0.5;\n    color += vec3(twinkle * stars * 0.3);\n\n    // Nebula color washes\n    vec3 nebulaC0 = vec3(0.3, 0.1, 0.5);\n    vec3 nebulaC1 = vec3(0.1, 0.2, 0.6);\n    vec3 nebulaC2 = vec3(0.5, 0.1, 0.4);\n\n    for (int i = 0; i < 3; i++) {\n        float scale = 1.5 + float(i) * 0.5;\n        vec2 timeOffset = TIME * vec2(0.02, 0.01) * float(i + 1);\n        vec2 nebulaUV = uv * scale + timeOffset;\n        float nebula = fbm(nebulaUV) * 0.4;\n        if (i == 0) color += nebulaC0 * nebula * 0.3;\n        else if (i == 1) color += nebulaC1 * nebula * 0.3;\n        else color += nebulaC2 * nebula * 0.3;\n    }\n\n    // Planet 1 - larger, top right\n    vec2 planet1Pos = vec2(0.8, 0.3);\n    float planet1Dist = length(uv - planet1Pos);\n    float planet1 = smoothstep(0.08, 0.075, planet1Dist);\n    vec3 planet1Color = vec3(0.4, 0.3, 0.7);\n    float rim1 = smoothstep(0.075, 0.08, planet1Dist);\n    planet1Color += vec3(rim1 * 0.5);\n    color = mix(color, planet1Color, planet1);\n\n    // Planet 2 - smaller, bottom left\n    vec2 planet2Pos = vec2(0.2, 0.7);\n    float planet2Dist = length(uv - planet2Pos);\n    float planet2 = smoothstep(0.05, 0.045, planet2Dist);\n    vec3 planet2Color = vec3(0.7, 0.3, 0.2);\n    float rim2 = smoothstep(0.045, 0.05, planet2Dist);\n    planet2Color += vec3(rim2 * 0.5);\n    color = mix(color, planet2Color, planet2);\n\n    // Subtle vignette\n    float vignette = 1.0 - length(uv - 0.5) * 0.8;\n    color *= clamp(vignette, 0.0, 1.0);\n\n    gl_FragColor = vec4(color, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "matrix" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nfloat hash(vec2 p) {\n    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n}\n\nfloat noise(vec2 p) {\n    vec2 i = floor(p);\n    vec2 f = fract(p);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),\n                  mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);\n    return n;\n}\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n\n    // Fade to black at bottom\n    float fade = smoothstep(0.0, 0.3, uv.y);\n\n    // Multiple columns of falling code\n    vec3 color = vec3(0.0);\n\n    for (int i = 0; i < 15; i++) {\n        float columnX = fract(float(i) * 73.17 / 15.0);\n        float speed = 0.5 + hash(vec2(float(i), 0.0)) * 0.5;\n        float speedY = 0.3 + hash(vec2(0.0, float(i))) * 0.2;\n\n        float dropY = fract(TIME * speedY + float(i) * 31.1);\n        dropY = 1.0 - dropY;\n\n        float dist = abs(uv.x - columnX);\n        float columnWidth = 0.02 + hash(vec2(float(i), 1.0)) * 0.02;\n        float column = smoothstep(columnWidth, 0.0, dist);\n\n        // Bright green characters\n        float charRow = floor(dropY * 20.0);\n        float brightness = hash(vec2(float(i), charRow));\n        brightness = pow(brightness, 3.0);\n\n        // Some characters are brighter (whiter)\n        float isBright = step(0.95, brightness);\n        vec3 charColor = mix(vec3(0.0, 0.5, 0.1), vec3(0.4, 1.0, 0.4), isBright);\n\n        // Head of the column is brighter\n        float head = smoothstep(0.95, 1.0, dropY);\n        charColor += vec3(head * 0.5);\n\n        // Trail effect\n        float trail = smoothstep(0.0, 0.3, dropY);\n        trail *= smoothstep(0.3, 0.0, dropY - 0.05);\n\n        color += charColor * column * trail * brightness;\n    }\n\n    // Background fade\n    color *= fade;\n\n    // Green tint\n    color += vec3(0.0, 0.05, 0.0);\n\n    gl_FragColor = vec4(color, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                case "sunset" -> this.computeInt(
                    new ByteArrayInputStream(
                        "#version 120\nuniform float TIME;\nuniform vec2 RESOLUTION;\n\nvoid main() {\n    vec2 uv = gl_FragCoord.xy / RESOLUTION.xy;\n    float y = uv.y;\n\n    // Warm gradient\n    vec3 darkBlue = vec3(0.1, 0.1, 0.3);\n    vec3 purple = vec3(0.4, 0.2, 0.5);\n    vec3 pink = vec3(0.8, 0.4, 0.4);\n    vec3 orange = vec3(1.0, 0.5, 0.2);\n\n    vec3 skyColor;\n    if (y < 0.25) {\n        skyColor = mix(darkBlue, purple, y / 0.25);\n    } else if (y < 0.5) {\n        skyColor = mix(purple, pink, (y - 0.25) / 0.25);\n    } else if (y < 0.75) {\n        skyColor = mix(pink, orange, (y - 0.5) / 0.25);\n    } else {\n        skyColor = mix(orange, vec3(0.3, 0.2, 0.2), (y - 0.75) / 0.25);\n    }\n\n    // Animated sun just above horizon\n    vec2 sunPos = vec2(0.5, 0.72);\n    float sunDist = length(uv - sunPos);\n    float sunRadius = 0.08;\n    float sunGlow = 0.15;\n    float sun = smoothstep(sunRadius, sunRadius - 0.01, sunDist);\n    float glow = smoothstep(sunGlow, sunRadius - 0.01, sunDist) - sun;\n    glow = clamp(glow, 0.0, 1.0);\n\n    // Sun colors with bloom\n    vec3 sunCore = vec3(1.0, 0.9, 0.7);\n    vec3 sunOuter = vec3(1.0, 0.6, 0.3);\n    vec3 sunGlowColor = vec3(1.0, 0.4, 0.2);\n\n    skyColor += sunCore * sun;\n    skyColor += sunOuter * glow;\n    skyColor += sunGlowColor * glow * 0.5;\n\n    // Subtle cloud silhouettes\n    for (int i = 0; i < 3; i++) {\n        float cloudY = 0.6 + float(i) * 0.05;\n        float cloudX = fract(float(i) * 31.1 / 3.0);\n        float cloudSpeed = 0.01 + float(i) * 0.005;\n        cloudX += TIME * cloudSpeed;\n\n        float cloudDist = abs(uv.x - cloudX);\n        float cloudWidth = 0.2 + float(i) * 0.05;\n        float cloud = smoothstep(cloudWidth, 0.0, cloudDist);\n        float cloudHeight = smoothstep(0.02, 0.0, abs(uv.y - cloudY));\n\n        vec3 cloudColor = vec3(0.2, 0.15, 0.25);\n        skyColor = mix(skyColor, cloudColor, cloud * cloudHeight * 0.7);\n    }\n\n    // Add subtle warm atmosphere\n    skyColor += vec3(0.05, 0.03, 0.0) * (1.0 - y);\n\n    gl_FragColor = vec4(skyColor, 1.0);\n}\n"
                            .getBytes()
                    ),
                    35632
                );
                default -> this.computeInt(mc.getResourceManager().getResource(new ResourceLocation(str)).getInputStream(), 35632);
            };
            GL20.glAttachShader(n, n2);
            n3 = this.computeInt(mc.getResourceManager().getResource(new ResourceLocation(str2)).getInputStream(), 35633);
            GL20.glAttachShader(n, n3);
        } catch (IOException e) {
            GL20.glDeleteProgram(n);
            throw new IllegalStateException("Unable to load shader resources", e);
        } catch (RuntimeException e2) {
            GL20.glDeleteProgram(n);
            throw e2;
        } finally {
            if (n2 != 0) {
                GL20.glDeleteShader(n2);
            }

            if (n3 != 0) {
                GL20.glDeleteShader(n3);
            }
        }

        GL20.glLinkProgram(n);
        GL20.glDetachShader(n, n2);
        GL20.glDetachShader(n, n3);
        int n4 = GL20.glGetProgrami(n, GL20.GL_LINK_STATUS);
        if (n4 == 0) {
            GL20.glDeleteProgram(n);
            throw new IllegalStateException("Shader failed to link!");
        }
        this.count = n;
    }

    public boolean isValid() {
        return GL20.glIsProgram(this.count);
    }

    public static void process2() {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        handleFloat(mc.displayWidth / sr.getScaleFactor(), (float)mc.displayHeight / sr.getScaleFactor());
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

    public void handleString(String str, float factor) {
        if ("MODE".equals(str)) {
            this.factor = factor;
        }

        GL20.glUniform1f(this.computeInt(str), factor);
    }

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
        handleFloat(0.0F, 0.0F, f, f2);
    }

    public void handleString(String str, float f, float f2) {
        GL20.glUniform2f(this.computeInt(str), f, f2);
    }

    public int computeInt(String str) {
        Integer integer = this.lookupMap.get(str);
        if (integer != null) {
            return integer;
        }
        int n = GL20.glGetUniformLocation(this.count, str);
        this.lookupMap.put(str, n);
        return n;
    }

    private int computeInt(InputStream in, int n) {
        int n2 = GL20.glCreateShader(n);
        GL20.glShaderSource(n2, readInputStream(in));
        GL20.glCompileShader(n2);
        if (GL20.glGetShaderi(n2, GL20.GL_COMPILE_STATUS) == 0) {
            GL20.glDeleteShader(n2);
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", n));
        }
        return n2;
    }

    public void handleString(String str, int n, int n2) {
        GL20.glUniform2i(this.computeInt(str), n, n2);
    }

    public void handleString(String str, int n) {
        GL20.glUniform1i(this.computeInt(str), n);
    }

    public void handleString(String str, int... ints) {
        int n = this.computeInt(str);
        if (ints.length > 1) {
            GL20.glUniform2i(n, ints[0], ints[1]);
        } else {
            GL20.glUniform1i(n, ints[0]);
        }
    }

    public ShaderEffect(String str) {
        this(str, "brutality/shaders/vertex.vsh");
    }

    public static void handleShaderEffect(ShaderEffect instance2, float f, float f2) {
        if (!OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            instance2.handleString("ORIGIN", 0.0F, 0.0F);
            instance2.handleString("RESOLUTION", f, f2);
            process();
            instance2.unload();
        } else {
            intBuffer.clear();
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
            int[] ints = IntelShader.computeIntArray(f, f2, checkState());
            int n = ints[0];
            int n2 = ints[1];
            int n3 = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            if (framebuffer == null || framebuffer.framebufferWidth != n || framebuffer.framebufferHeight != n2) {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }

                framebuffer = new Framebuffer(n, n2, false);
                framebuffer.setFramebufferFilter(9729);
                timestamp = 0L;
            }

            boolean flag = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST);
            if (flag) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            GlStateManager.colorMask(true, true, true, true);
            long now = System.nanoTime();
            if (timestamp == 0L || instance != instance2 || factor2 != instance2.factor || now - timestamp >= IntelShader.computeLong2(checkState())) {
                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(true);
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                instance2.handleString("ORIGIN", 0.0F, 0.0F);
                instance2.handleString("RESOLUTION", (float)n, (float)n2);
                process();
                instance = instance2;
                factor2 = instance2.factor;
                timestamp = now;
            }

            instance2.unload();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, n3);
            GlStateManager.viewport(intBuffer.get(0), intBuffer.get(1), intBuffer.get(2), intBuffer.get(3));
            if (flag) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(framebuffer.framebufferTexture);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            process();
            GlStateManager.bindTexture(0);
        }
    }

    public void delete() {
        GL20.glDeleteProgram(this.count);
        Iterator it = lookupMap2.values().iterator();

        while (it.hasNext()) {
            ShaderEffect.Entry entry = (ShaderEffect.Entry)it.next();
            if (entry.shaderEffect == this) {
                entry.framebuffer.deleteFramebuffer();
                it.remove();
            }
        }

        if (instance == this) {
            instance = null;
        }
    }

    public void handleString(String str, float f, float f2, float f3) {
        GL20.glUniform3f(this.computeInt(str), f, f2, f3);
    }

    public void init() {
        GL20.glUseProgram(this.count);
    }

    public static final class Entry {
        public final ShaderEffect shaderEffect;
        public final Framebuffer framebuffer;
        public long timestamp;

        public Entry(ShaderEffect shaderEffect, int n, int n2) {
            this.shaderEffect = shaderEffect;
            this.framebuffer = new Framebuffer(n, n2, false);
            this.framebuffer.setFramebufferFilter(9729);
        }
    }
}
