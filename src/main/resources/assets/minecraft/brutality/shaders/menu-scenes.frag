#version 120
// Original Brutality menu scenes. No external shader source is incorporated.
// Every scene is framed around a centered launcher: focal subjects sit to the
// sides or behind the glass panel, and a shared filmic grade, vignette and
// dither finish each one so gradients stay smooth on 8-bit framebuffers.
uniform vec2 RESOLUTION;
uniform float TIME;
uniform float MODE;
uniform float INTRO;
uniform vec2 ORIGIN;

const mat2 OCTAVE = mat2(0.80, 0.60, -0.60, 0.80);

float hash(vec2 p) {
    vec3 q = fract(vec3(p.xyx) * 0.1031);
    q += dot(q, q.yzx + 33.33);
    return fract((q.x + q.y) * q.z);
}
float noise(vec2 p) {
    vec2 i = floor(p), f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}
float fbm(vec2 p) {
    float sum = 0.0, weight = 0.5;
    for (int i = 0; i < 5; i++) { sum += weight * noise(p); p = OCTAVE * p * 2.03 + vec2(1.7, 9.2); weight *= 0.5; }
    return sum / 0.96875;
}
float fbm3(vec2 p) {
    float sum = 0.0, weight = 0.5;
    for (int i = 0; i < 3; i++) { sum += weight * noise(p); p = OCTAVE * p * 2.03 + vec2(1.7, 9.2); weight *= 0.5; }
    return sum / 0.875;
}
float clouds(vec2 p) {
    float sum = 0.0, weight = 0.5;
    for (int i = 0; i < 4; i++) { sum += weight * noise(p); p = p * 2.03 + 1.7; weight *= 0.5; }
    return sum;
}
float ridged(vec2 p) {
    float sum = 0.0, weight = 0.55;
    for (int i = 0; i < 4; i++) { float n = 1.0 - abs(noise(p) * 2.0 - 1.0); sum += weight * n * n; p = OCTAVE * p * 2.1 + 3.1; weight *= 0.5; }
    return sum;
}
mat2 rot(float a) { float c = cos(a), s = sin(a); return mat2(c, -s, s, c); }
float px() { return 1.0 / RESOLUTION.y; }
float rectangle(vec2 p, vec2 halfSize) {
    vec2 d = abs(p) - halfSize;
    float aa = 1.2 * px();
    return 1.0 - smoothstep(-aa, aa, max(d.x, d.y));
}
float band(float v, float lo, float hi) { float aa = 1.2 * px(); return smoothstep(lo - aa, lo + aa, v) * (1.0 - smoothstep(hi - aa, hi + aa, v)); }

// Pixel-sized stars with a soft halo; brightness and twinkle vary per star.
vec3 starLayer(vec2 p, float scale, float seed, float density) {
    vec2 q = p * scale, id = floor(q), f = fract(q) - 0.5;
    float h = hash(id + seed);
    if (h < 1.0 - density) return vec3(0.0);
    vec2 o = (vec2(hash(id + seed + 13.1), hash(id + seed + 71.7)) - 0.5) * 0.7;
    float d = length(f - o) * RESOLUTION.y / scale;
    float b = hash(id + seed + 5.3);
    float twinkle = 0.7 + 0.3 * sin(TIME * (0.8 + h * 2.5) + h * 40.0);
    vec3 tint = mix(vec3(0.70, 0.80, 1.00), vec3(1.00, 0.86, 0.72), hash(id + seed + 2.2));
    return tint * (exp(-d * d * 0.55) * 1.1 + exp(-d * 0.55) * 0.10) * b * b * twinkle;
}
vec3 starfield(vec2 p) {
    return starLayer(p, 16.0, 0.0, 0.24) + starLayer(p, 31.0, 7.0, 0.15) * 0.7 + starLayer(p + 3.1, 57.0, 3.0, 0.09) * 0.45;
}
vec3 aces(vec3 x) { return clamp((x * (2.51 * x + 0.03)) / (x * (2.43 * x + 0.59) + 0.14), 0.0, 1.0); }

// Halloween analytic shapes (also used by the bat entrance) 
float triangleEye(vec2 q) {
    float shape = max(abs(q.x) - (.14 - q.y) * .68, max(q.y - .14, -q.y - .12));
    return 1.0 - smoothstep(-.012, .012, shape);
}
vec4 pumpkin(vec2 q, float seed) {
    float aa = .015;
    vec2 bulb = q / vec2(1.0, .78);
    float edge = length(bulb);
    float mask = 1.0 - smoothstep(1.0 - aa, 1.0 + aa, edge);
    vec2 sq = vec2(q.x - .06 * (q.y - .78), q.y - .86);
    vec2 sd = abs(sq) - vec2(.07, .14);
    float stem = 1.0 - smoothstep(-.01, .01, max(sd.x, sd.y));
    float z = sqrt(max(0.0, 1.0 - dot(bulb, bulb)));
    float ribs = .72 + .28 * cos(atan(q.x, max(.02, z)) * 12.0);
    float light = .25 + .75 * clamp(z * .8 - q.x * .25 + q.y * .2, 0.0, 1.0);
    vec3 skin = mix(vec3(.18, .03, .012), vec3(.98, .33, .04), light) * ribs;
    skin += vec3(.30, .10, .02) * pow(1.0 - z, 3.0);
    float eyes = max(triangleEye(q - vec2(-.34, .18)), triangleEye(q - vec2(.34, .18)));
    vec2 mouth = q - vec2(0.0, -.27);
    float smile = 1.0 - smoothstep(.94, 1.02, length(mouth / vec2(.56, .21)));
    vec2 t1 = abs(mouth - vec2(-.27, .17)) - vec2(.06, .11), t2 = abs(mouth - vec2(.24, -.15)) - vec2(.06, .09);
    float teeth = max(1.0 - smoothstep(-.01, .01, max(t1.x, t1.y)), 1.0 - smoothstep(-.01, .01, max(t2.x, t2.y)));
    float face = max(eyes, smile * (1.0 - teeth)) * mask;
    float flame = .90 + .10 * sin(TIME * 3.1 + seed) * sin(TIME * 5.7 + seed);
    skin = mix(skin, vec3(1.25, .78, .22) * flame, face);
    skin = mix(skin, vec3(.14, .17, .06), stem);
    return vec4(skin, max(mask, stem));
}
vec3 jackLantern(vec3 base, vec2 p, vec2 center, float size, float seed) {
    vec2 q = (p - center) / size;
    float flicker = .85 + .15 * sin(TIME * 4.3 + seed * 3.0);
    base += vec3(.45, .14, .02) * exp(-dot(q, q) * 0.9) * flicker;
    vec4 fruit = pumpkin(q, seed);
    return mix(base, fruit.rgb, fruit.a);
}
float bareTree(vec2 q, float seed) {
    float bend = .027 * sin(q.y * 5.0 + seed);
    float taper = max(.005, .017 - .02 * q.y);
    float trunk = (1.0 - smoothstep(taper, taper + .003, abs(q.x + bend))) * step(-.07, q.y) * step(q.y, .59);
    for (int j = 0; j < 3; j++) {
        float f = float(j), level = .20 + f * .11;
        vec2 twig = q - vec2(-.027 * sin(level * 5.0 + seed), level);
        float incline = .62 + hash(vec2(seed, f)) * .45;
        float branch = abs(twig.y - abs(twig.x) * incline);
        float span = .15 - f * .022 + hash(vec2(f, seed + 3.0)) * .035;
        float asymmetry = twig.x < 0.0 ? .80 : 1.0;
        trunk = max(trunk, (1.0 - smoothstep(.003, .007, branch)) * step(abs(twig.x), span * asymmetry));
    }
    return trunk;
}
float batTriangle(vec2 q, vec2 a, vec2 b, vec2 c, float aa) {
    vec2 ab = b - a, bc = c - b, ca = a - c;
    float d = max(max((ab.x * (q.y - a.y) - ab.y * (q.x - a.x)) / length(ab),
                      (bc.x * (q.y - b.y) - bc.y * (q.x - b.x)) / length(bc)),
                      (ca.x * (q.y - c.y) - ca.y * (q.x - c.x)) / length(ca));
    return 1.0 - smoothstep(-aa, aa, d);
}
float flyingBat(vec2 q, float flap, float aa) {
    vec2 w = vec2(abs(q.x), q.y);
    w.y /= mix(.45, 1.0, flap);
    float wing = batTriangle(w, vec2(.06, .12), vec2(1.0, .43), vec2(.35, -.38), aa);
    wing *= smoothstep(.17 - aa, .17 + aa, length(w - vec2(.30, -.36)));
    wing *= smoothstep(.20 - aa, .20 + aa, length(w - vec2(.67, -.18)));
    float body = 1.0 - smoothstep(1.0 - aa * 5.0, 1.0 + aa * 5.0, length(q / vec2(.10, .24)));
    float head = 1.0 - smoothstep(.13 - aa, .13 + aa, length(q - vec2(0.0, .18)));
    float ear = batTriangle(vec2(abs(q.x), q.y), vec2(.025, .23), vec2(.09, .37), vec2(.12, .20), aa);
    return max(max(wing, body), max(head, ear));
}

// Shared planet surface (Earth / Mars) 
// Returns lit colour in rgb and coverage in a. tilt rotates the texture frame so
// a limb seen from close up shows equatorial terrain instead of a polar cap.
vec4 planet(vec2 p, vec2 center, float radius, vec3 light, float tilt, float kind) {
    vec2 q = (p - center) / radius;
    float r = length(q);
    float edge = 1.6 / (RESOLUTION.y * radius);
    float cover = 1.0 - smoothstep(1.0 - edge, 1.0 + edge, r);
    if (cover <= 0.0) return vec4(0.0);
    float z = sqrt(max(0.0, 1.0 - r * r));
    vec3 n = vec3(q, z);
    float ct = cos(tilt), st = sin(tilt);
    vec3 m = vec3(n.x, n.y * ct - n.z * st, n.y * st + n.z * ct);
    float lon = atan(m.x, m.z) + TIME * (kind < 0.5 ? 0.010 : 0.006);
    float lat = asin(clamp(m.y, -1.0, 1.0));
    vec2 tex = vec2(lon * 2.6, lat * 2.6);
    float diffuse = max(dot(n, light), 0.0);
    float wrap = smoothstep(-0.12, 0.35, dot(n, light));
    vec3 col;
    if (kind < 0.5) {
        float land = fbm(tex * 1.25 + 4.0);
        float mask = smoothstep(0.53, 0.57, land);
        vec3 ocean = mix(vec3(0.006, 0.035, 0.11), vec3(0.02, 0.13, 0.30), z);
        vec3 ground = mix(vec3(0.07, 0.17, 0.06), vec3(0.40, 0.33, 0.19), smoothstep(0.56, 0.74, land));
        ground = mix(ground, vec3(0.85, 0.88, 0.92), smoothstep(1.05, 1.25, abs(lat)));
        float cloud = smoothstep(0.50, 0.78, fbm(tex * 2.1 + vec2(TIME * 0.012, 0.0) + 9.0));
        col = mix(ocean, ground, mask) * (diffuse * 1.15 + 0.015);
        vec3 h = normalize(light + vec3(0.0, 0.0, 1.0));
        col += vec3(1.0, 0.85, 0.6) * pow(max(dot(n, h), 0.0), 60.0) * (1.0 - mask) * (1.0 - cloud) * 0.9;
        col = mix(col, vec3(1.0) * (diffuse * 1.1 + 0.02), cloud * 0.85);
        float city = step(0.66, noise(tex * 34.0)) * mask * (1.0 - cloud);
        col += vec3(1.0, 0.62, 0.25) * city * 0.55 * (1.0 - wrap);
        col += vec3(0.25, 0.55, 1.20) * pow(1.0 - z, 2.5) * (wrap * 0.9 + 0.04);
    } else {
        float terrain = fbm(tex * 1.4 + 2.0);
        float dark = smoothstep(0.45, 0.70, fbm(tex * 0.9 - 6.0));
        vec3 dust = mix(vec3(0.55, 0.20, 0.08), vec3(0.86, 0.48, 0.25), terrain);
        dust = mix(dust, vec3(0.30, 0.11, 0.06), dark * 0.65);
        float crater = smoothstep(0.70, 0.76, noise(tex * 9.0)) * 0.25;
        dust *= 1.0 - crater;
        dust = mix(dust, vec3(0.95, 0.92, 0.88), smoothstep(1.18, 1.30, lat));
        col = dust * (diffuse * 1.1 + 0.01);
        col += vec3(0.95, 0.45, 0.25) * pow(1.0 - z, 3.0) * (wrap * 0.6 + 0.02);
    }
    return vec4(col, cover);
}
vec3 atmosphereRim(vec2 p, vec2 center, float radius, vec3 light, vec3 tint, float thickness) {
    vec2 q = (p - center) / radius;
    float r = length(q);
    if (r < 1.0) return vec3(0.0);
    float lit = clamp(dot(normalize(vec3(q, 0.15)), light) * 0.9 + 0.25, 0.0, 1.0);
    return tint * exp(-(r - 1.0) * radius / thickness) * lit;
}

// Scenes 
vec3 sceneFire(vec2 p, vec2 uv) {
    float t = TIME;
    vec3 c = mix(vec3(0.075, 0.020, 0.012), vec3(0.010, 0.006, 0.010), smoothstep(0.0, 0.9, uv.y));
    vec2 q = vec2(p.x * 3.2, uv.y * 1.5 - t * 0.75);
    vec2 warp = vec2(fbm(q * 0.8 + vec2(0.0, t * 0.15)), fbm(q * 0.8 + vec2(5.2, 1.3)));
    float n = fbm(q + warp * 1.5 + vec2(0.0, -t * 0.3));
    float rise = 0.42 + 0.16 * smoothstep(0.15, 0.85, abs(p.x)) + 0.05 * sin(p.x * 3.0 + t * 0.6);
    float heat = clamp((n * 1.2 - uv.y / rise * 0.78) * 1.35 + 0.05, 0.0, 1.0);
    vec3 fire = vec3(1.45, 0.38, 0.05) * heat + vec3(0.85, 0.50, 0.12) * pow(heat, 3.0) + vec3(0.25, 0.25, 0.30) * pow(heat, 8.0);
    c += fire;
    float smoke = fbm(vec2(p.x * 1.6, uv.y * 1.4 - t * 0.12) + warp);
    c = mix(c, vec3(0.06, 0.035, 0.035), smoothstep(0.45, 0.85, smoke) * smoothstep(0.25, 0.8, uv.y) * 0.55);
    c += vec3(0.45, 0.10, 0.02) * exp(-uv.y * 3.5) * 0.35;
    for (int i = 0; i < 3; i++) {
        float f = float(i), scale = 7.0 + f * 5.0;
        vec2 g = vec2(p.x * scale, (uv.y - t * (0.08 + 0.035 * f)) * scale * 0.8);
        g.x += sin(g.y * 0.6 + f * 2.0 + t * 0.7) * 0.35;
        vec2 id = floor(g), lv = fract(g) - 0.5;
        float h = hash(id + f * 13.0);
        if (h > 0.72) {
            vec2 o = (vec2(hash(id + f + 3.1), hash(id + f + 8.7)) - 0.5) * 0.6;
            float d = length(lv - o) * RESOLUTION.y / scale;
            float fade = (1.0 - smoothstep(0.2, 0.95, uv.y)) * (0.5 + 0.5 * sin(t * 3.0 + h * 50.0));
            c += vec3(1.6, 0.55, 0.12) * (exp(-d * d * 0.35) + exp(-d * 0.45) * 0.18) * fade * (1.0 - f * 0.25);
        }
    }
    return c;
}

vec3 sceneNormal(vec2 p, vec2 uv) {
    float t = TIME * 0.05;
    vec2 q = p * 1.35;
    vec2 a = vec2(fbm(q + vec2(t, 0.0)), fbm(q + vec2(3.1, 1.7) - t));
    vec2 b = vec2(fbm(q + a * 1.8 + vec2(1.7, 9.2) + t * 0.6), fbm(q + a * 1.8 + vec2(8.3, 2.8)));
    float n = fbm(q + b * 1.6);
    vec3 c = mix(vec3(0.015, 0.022, 0.060), vec3(0.07, 0.13, 0.32), smoothstep(0.20, 0.75, n));
    c = mix(c, vec3(0.30, 0.22, 0.62), smoothstep(0.45, 0.95, b.y) * 0.55);
    c = mix(c, vec3(0.06, 0.32, 0.46), smoothstep(0.50, 0.90, a.x) * smoothstep(0.35, 0.8, n) * 0.6);
    float sheen = pow(smoothstep(0.58, 0.92, n), 2.0);
    c += vec3(0.50, 0.62, 1.0) * sheen * 0.30;
    c += vec3(0.12, 0.10, 0.30) * exp(-length(p - vec2(0.0, 0.55)) * 1.8) * 0.5;
    return c;
}

vec3 sceneSolar(vec2 p, vec2 uv, float aspect) {
    float systemScale = 0.50 * min(1.0, aspect);
    vec2 sunPos = vec2(0.36 * max(0.0, aspect - 0.45), 0.02);
    vec2 q = (p - sunPos) / systemScale;
    q = vec2(q.x * 0.978 + q.y * 0.208, -q.x * 0.208 + q.y * 0.978);
    float edge = 1.5 / (RESOLUTION.y * systemScale);
    vec3 c = vec3(0.006, 0.008, 0.022);
    vec2 g = rot(0.45) * p;
    float milky = exp(-g.y * g.y * 9.0);
    c += vec3(0.10, 0.07, 0.16) * fbm(p * 2.2 + 4.0) * milky + vec3(0.04, 0.06, 0.10) * milky * 0.6;
    c += starfield(p) * (0.7 + milky);
    float sd = length(q);
    c += vec3(1.4, 0.55, 0.12) * exp(-sd * 9.0) * 0.55 + vec3(1.0, 0.6, 0.3) * exp(-sd * 28.0) * 0.8;
    for (int i = 0; i < 8; i++) {
        float radius = 0.15 + float(i) * 0.093;
        float orbit = abs(length(q / vec2(1.0, 0.55)) - radius);
        c += vec3(0.30, 0.38, 0.55) * (1.0 - smoothstep(0.0, edge * 1.2, orbit)) * 0.22;
    }
    float sunRadius = 0.074;
    float sunMask = 1.0 - smoothstep(sunRadius - edge, sunRadius + edge, sd);
    float granules = fbm(q * 48.0 + vec2(TIME * 0.06, -TIME * 0.03));
    vec3 sun = mix(vec3(1.6, 0.62, 0.12), vec3(1.9, 1.5, 0.85), granules) * (1.0 - 0.35 * pow(sd / sunRadius, 2.0));
    c = mix(c, sun, sunMask);
    for (int i = 0; i < 8; i++) {
        float f = float(i), orbitRadius = 0.15 + f * 0.093;
        float angle = TIME * 0.035 / pow(orbitRadius, 1.15) + f * 2.399;
        vec2 center = vec2(cos(angle), sin(angle) * 0.55) * orbitRadius;
        vec3 tint = vec3(0.55, 0.49, 0.43); float radius = 0.017;
        if (i == 1) { tint = vec3(0.90, 0.68, 0.37); radius = 0.025; }
        if (i == 2) { tint = vec3(0.16, 0.48, 0.88); radius = 0.027; }
        if (i == 3) { tint = vec3(0.80, 0.28, 0.14); radius = 0.021; }
        if (i == 4) { tint = vec3(0.85, 0.64, 0.44); radius = 0.052; }
        if (i == 5) { tint = vec3(0.88, 0.77, 0.48); radius = 0.044; }
        if (i == 6) { tint = vec3(0.38, 0.79, 0.83); radius = 0.032; }
        if (i == 7) { tint = vec3(0.20, 0.35, 0.86); radius = 0.031; }
        vec2 local = q - center;
        if (length(local) > radius * 2.2) continue;
        bool behind = sin(angle) > 0.0;
        if (i == 5 && behind) {
            float rd = length(local / vec2(1.0, 0.32));
            float rings = band(rd, radius * 1.25, radius * 2.0) * (0.75 + 0.25 * sin(rd / radius * 30.0));
            c = mix(c, vec3(0.70, 0.60, 0.40), rings * 0.8);
        }
        float disk = 1.0 - smoothstep(radius - edge, radius + edge, length(local));
        float z = sqrt(max(0.0, radius * radius - dot(local, local)));
        vec3 normal = vec3(local, z) / radius;
        vec3 l = normalize(vec3(-center, 0.12));
        float light = max(0.0, dot(normal, l));
        if (i == 2) tint = mix(tint, vec3(0.24, 0.58, 0.30), smoothstep(0.47, 0.60, clouds(local / radius * 3.0 + vec2(TIME * 0.025, 0.0))));
        if (i == 4 || i == 5) tint *= 0.80 + 0.20 * sin(local.y / radius * 18.0 + f);
        vec3 body = tint * (light * 1.25 + 0.02) + tint * pow(1.0 - normal.z, 3.0) * light * 0.6;
        c = mix(c, body, disk);
        if (i == 5 && !behind) {
            float rd = length(local / vec2(1.0, 0.32));
            float front = local.y < 0.0 ? 1.0 : (1.0 - disk);
            float rings = band(rd, radius * 1.25, radius * 2.0) * (0.75 + 0.25 * sin(rd / radius * 30.0));
            c = mix(c, vec3(0.70, 0.60, 0.40), rings * 0.8 * front);
        }
    }
    return c;
}

float auroraCurtains(vec2 p, float t, out vec3 tint) {
    float total = 0.0;
    tint = vec3(0.0);
    for (int i = 0; i < 3; i++) {
        float f = float(i);
        float base = -0.06 + f * 0.09 + 0.16 * (fbm3(vec2(p.x * 0.8 + t * 0.10 + f * 3.7, f)) - 0.5) + 0.05 * sin(p.x * 1.8 + t * 0.35 + f * 2.0);
        float h = p.y - base;
        float rays = 0.30 + 0.70 * pow(noise(vec2(p.x * 32.0 + fbm3(vec2(p.x * 2.5, t * 0.2 + f)) * 7.0, t * 0.3 + f * 9.0)), 2.0);
        float curtain = smoothstep(-0.05, 0.015, h) * exp(-max(h, 0.0) * (3.5 + f * 1.5)) * rays;
        curtain *= smoothstep(0.30, 0.75, fbm3(vec2(p.x * 1.1 - t * 0.15 + f * 2.0, f * 5.0)));
        vec3 col = mix(vec3(0.20, 1.30, 0.60), vec3(0.65, 0.22, 1.05), smoothstep(0.04, 0.30, h));
        col = mix(col, vec3(0.10, 0.75, 0.95), f * 0.25);
        tint += col * curtain;
        total += curtain;
    }
    return total;
}
vec3 auroraSky(vec2 p, vec2 uv, float t) {
    vec3 c = mix(vec3(0.020, 0.045, 0.070), vec3(0.004, 0.008, 0.025), smoothstep(0.0, 0.9, uv.y));
    c += starfield(p) * 0.8;
    vec3 tint;
    auroraCurtains(p, t, tint);
    c += tint * 0.75;
    return c;
}
vec3 sceneAurora(vec2 p, vec2 uv, float aspect) {
    float t = TIME * 0.6;
    float shore = -0.30;
    float ridge = shore + 0.015 + (0.05 + 0.24 * smoothstep(0.10, 0.85, abs(p.x))) * ridged(vec2(p.x * 1.8 + 2.0, 0.5));
    vec3 tint; auroraCurtains(vec2(p.x, 0.22), t, tint);
    vec3 c;
    if (p.y > shore) {
        c = auroraSky(p, uv, t);
        if (p.y < ridge) {
            float below = ridge - p.y;
            float face = fbm3(vec2(p.x * 14.0, p.y * 22.0));
            float snow = smoothstep(0.55, 0.75, face + exp(-below * 30.0) * 0.4);
            c = vec3(0.005, 0.008, 0.016) + tint * 0.025 + snow * (vec3(0.025, 0.04, 0.06) + tint * 0.06);
            c += tint * 0.25 * exp(-below * RESOLUTION.y * 0.25);
        }
    } else {
        float depth = shore - p.y;
        float ripple = noise(vec2(p.x * 5.0, depth * 90.0 - TIME * 0.7)) - 0.5;
        float mirrorRidge = ridge - shore;
        vec2 r = vec2(p.x + ripple * 0.015 * (1.0 + depth * 6.0), shore + depth * 2.6);
        c = auroraSky(r, vec2(uv.x, r.y + 0.5), t) * 0.50 + vec3(0.004, 0.010, 0.016);
        if (depth < mirrorRidge * 0.9 + ripple * 0.004) c = vec3(0.004, 0.007, 0.013) + tint * 0.02;
        c *= 1.0 - smoothstep(0.0, 0.22, depth) * 0.45;
    }
    return c;
}

vec3 sceneSynth(vec2 p, vec2 uv, float aspect) {
    float horizon = -0.15;
    vec3 c = mix(vec3(0.30, 0.05, 0.28), vec3(0.025, 0.010, 0.07), smoothstep(horizon, 0.5, p.y));
    c += vec3(0.75, 0.20, 0.35) * exp(-abs(p.y - horizon) * 9.0) * 0.45;
    c += starfield(p) * smoothstep(0.0, 0.3, p.y);
    vec2 sunPos = vec2(aspect * 0.30, horizon + 0.12);
    vec2 s = p - sunPos;
    float sr = 0.17;
    float sunDisk = 1.0 - smoothstep(sr - px() * 1.5, sr + px() * 1.5, length(s));
    float stripeY = (s.y + sr) / sr;
    float stripes = s.y < 0.02 ? step(0.30 + 0.25 * (1.0 - stripeY), fract(stripeY * 5.0 - TIME * 0.08)) : 1.0;
    vec3 sunCol = mix(vec3(1.5, 0.25, 0.55), vec3(1.6, 1.1, 0.30), smoothstep(-sr, sr, s.y));
    c += vec3(1.0, 0.25, 0.45) * exp(-max(length(s) - sr, 0.0) * 9.0) * 0.45;
    c = mix(c, sunCol, sunDisk * stripes);
    for (int i = 0; i < 2; i++) {
        float f = float(i);
        float h = horizon + 0.03 + (0.10 - f * 0.04) * ridged(vec2(p.x * (2.2 + f * 1.5) + f * 7.0, f)) * (0.35 + 0.65 * smoothstep(0.0, 0.7, abs(p.x - f * 0.2)));
        float d = h - p.y;
        if (d > 0.0 && p.y > horizon) {
            vec3 m = mix(vec3(0.07, 0.02, 0.12), vec3(0.025, 0.008, 0.05), f);
            m += vec3(0.9, 0.2, 0.8) * exp(-d * RESOLUTION.y * 0.35) * (0.6 - f * 0.25);
            c = m;
        }
    }
    if (p.y < horizon) {
        float depth = horizon - p.y;
        float z = 0.18 / depth;
        vec2 grid = vec2(p.x * z * 2.2, z + TIME * 0.9);
        vec2 gv = abs(fract(grid) - 0.5) / max(fwidth(grid), vec2(1e-4));
        float line = 1.0 - min(min(gv.x, gv.y) / 1.4, 1.0);
        vec2 soft = abs(fract(grid) - 0.5);
        float glow = exp(-min(soft.x, soft.y) * 18.0) * 0.35;
        float fade = smoothstep(0.0, 0.06, depth);
        c = mix(vec3(0.05, 0.01, 0.09), vec3(0.012, 0.004, 0.03), smoothstep(0.0, 0.4, depth));
        vec3 lc = mix(vec3(1.2, 0.20, 0.95), vec3(0.20, 0.85, 1.3), smoothstep(0.0, 0.35, depth));
        c += lc * (line + glow) * fade;
        c += vec3(1.0, 0.3, 0.5) * exp(-depth * 18.0) * 0.35;
        c += vec3(1.0, 0.4, 0.5) * exp(-abs(p.x - sunPos.x) * 10.0) * exp(-depth * 10.0) * 0.25;
    }
    float scan = 0.96 + 0.04 * sin(gl_FragCoord.y * 3.14159);
    return c * scan;
}

vec3 sceneEarth(vec2 p, vec2 uv, float aspect) {
    vec3 c = vec3(0.004, 0.006, 0.016) + starfield(p) * 0.9;
    c += vec3(0.05, 0.03, 0.09) * fbm(p * 1.8 + 11.0) * smoothstep(-0.1, 0.5, p.y);
    vec2 center = vec2(0.0, -2.20);
    float radius = 1.95;
    vec3 light = normalize(vec3(-0.55, 0.25, 0.30));
    c += atmosphereRim(p, center, radius, light, vec3(0.30, 0.60, 1.40), 0.035);
    vec4 world = planet(p, center, radius, light, 1.18, 0.0);
    c = mix(c, world.rgb, world.a);
    float sx = -0.62 * min(1.0, aspect / 1.6);
    vec2 sun = vec2(sx, center.y + sqrt(radius * radius - sx * sx) + 0.006);
    vec2 d = p - sun;
    float flare = exp(-length(d) * 30.0) * 1.6 + exp(-length(d) * 7.0) * 0.35;
    flare += exp(-abs(d.y) * 260.0) * exp(-abs(d.x) * 2.5) * 0.55;
    flare += exp(-abs(d.x) * 220.0) * exp(-abs(d.y) * 9.0) * 0.18;
    c += vec3(1.0, 0.82, 0.60) * flare;
    float limb = abs(length(p - center) - radius);
    c += vec3(1.0, 0.65, 0.40) * exp(-limb * 300.0) * exp(-abs(p.x - sun.x) * 3.5) * 0.6;
    return c;
}

vec3 sceneMars(vec2 p, vec2 uv, float aspect) {
    vec3 c = vec3(0.010, 0.006, 0.010) + starfield(p) * 0.85;
    c += vec3(0.10, 0.03, 0.02) * fbm(p * 1.6 - 3.0) * 0.6;
    vec2 center = vec2(max(0.48, aspect * 0.5 - 0.10), 0.16);
    float radius = 0.40;
    vec3 light = normalize(vec3(-0.75, 0.25, 0.45));
    c += atmosphereRim(p, center, radius, light, vec3(1.1, 0.45, 0.25), 0.018);
    vec4 world = planet(p, center, radius, light, 0.25, 1.0);
    c = mix(c, world.rgb, world.a);
    vec2 moonPos = vec2(center.x - 0.55, center.y - 0.42) + vec2(cos(TIME * 0.03), sin(TIME * 0.03)) * 0.02;
    vec2 mq = (p - moonPos) / vec2(0.030, 0.022);
    float moon = 1.0 - smoothstep(1.0 - 0.08, 1.0 + 0.08, length(mq + 0.12 * vec2(noise(mq * 2.0), 0.0)));
    float mz = sqrt(max(0.0, 1.0 - dot(mq, mq)));
    c = mix(c, vec3(0.45, 0.40, 0.36) * (max(dot(normalize(vec3(mq, mz)), light), 0.0) + 0.03), moon);
    return c;
}

vec3 londonSky(vec2 p, vec2 uv, vec2 sun) {
    vec3 c = mix(vec3(1.05, 0.48, 0.28), vec3(0.06, 0.08, 0.20), smoothstep(-0.18, 0.45, p.y));
    c = mix(c, vec3(0.020, 0.025, 0.07), smoothstep(0.30, 0.55, p.y));
    float sd = length(p - sun);
    c += vec3(1.2, 0.55, 0.20) * exp(-sd * 6.0) * 0.55 + vec3(1.4, 0.95, 0.6) * exp(-sd * 40.0);
    vec2 cq = vec2(p.x * 1.2 + TIME * 0.01, p.y * 4.0);
    float dens = smoothstep(0.45, 0.8, fbm(cq)) * smoothstep(-0.02, 0.12, p.y) * (1.0 - smoothstep(0.28, 0.48, p.y));
    float lit = clamp((fbm(cq) - fbm(cq + normalize(sun - p) * vec2(0.3, 1.0) * 0.15)) * 4.0 + 0.4, 0.0, 1.0);
    vec3 cloud = mix(vec3(0.10, 0.06, 0.12), vec3(1.2, 0.55, 0.35), lit * exp(-sd * 1.6));
    c = mix(c, cloud, dens * 0.85);
    return c;
}
float towerBridge(vec2 b, float span) {
    float s = 0.0;
    s = max(s, rectangle(b - vec2(0.0, 0.055), vec2(span + 0.40, 0.010)));
    for (int i = 0; i < 2; i++) {
        float side = float(i) * 2.0 - 1.0;
        vec2 tw = b - vec2(side * span, 0.0);
        s = max(s, rectangle(tw - vec2(0.0, 0.15), vec2(0.040, 0.15)));
        s = max(s, rectangle(tw - vec2(0.0, 0.31), vec2(0.046, 0.010)));
        for (int k = 0; k < 2; k++) {
            float kx = float(k) * 2.0 - 1.0;
            vec2 tr = tw - vec2(kx * 0.034, 0.32);
            s = max(s, rectangle(tr, vec2(0.009, 0.02)));
            s = max(s, (1.0 - smoothstep(0.0, px() * 1.5, abs(tr.x) - 0.009 * (1.0 - (tr.y - 0.02) / 0.045)))
                       * step(0.02, tr.y) * step(tr.y, 0.065));
        }
        vec2 roof = tw - vec2(0.0, 0.32);
        s = max(s, (1.0 - smoothstep(0.0, px() * 1.5, abs(roof.x) - 0.035 * (1.0 - roof.y / 0.08))) * step(0.0, roof.y) * step(roof.y, 0.08));
        float outer = side * (abs(b.x) - span) ;
        float sx = abs(b.x) - span;
        if (sx > 0.04 && sx < 0.40) {
            float cy = 0.27 - 0.20 * sqrt(clamp((sx - 0.04) / 0.36, 0.0, 1.0));
            s = max(s, 1.0 - smoothstep(0.0012, 0.0045, abs(b.y - cy)));
        }
    }
    s = max(s, rectangle(b - vec2(0.0, 0.255), vec2(span, 0.006)));
    s = max(s, rectangle(b - vec2(0.0, 0.285), vec2(span, 0.006)));
    return s;
}
vec3 sceneLondon(vec2 p, vec2 uv, float aspect) {
    float water = -0.26;
    vec2 sun = vec2(-aspect * 0.08, -0.06);
    float span = min(0.56, aspect * 0.34);
    vec3 c;
    vec2 sp = p;
    bool reflected = p.y < water;
    if (reflected) {
        float depth = water - p.y;
        sp = vec2(p.x + (noise(vec2(p.x * 3.0, depth * 70.0 - TIME * 0.6)) - 0.5) * 0.008 * (0.5 + depth * 3.0), water + depth);
    }
    c = londonSky(sp, uv, sun);
    float far = water + 0.012 + 0.035 * hash(vec2(floor(sp.x * 45.0), 3.0)) * hash(vec2(floor(sp.x * 45.0), 8.0));
    if (sp.y < far) c = mix(c, vec3(0.16, 0.09, 0.13), 0.75);
    vec2 b = vec2(sp.x, sp.y - water);
    float bridge = towerBridge(b, span);
    vec3 stone = vec3(0.045, 0.040, 0.065);
    c = mix(c, stone, bridge);
    float windows = 0.0;
    for (int i = 0; i < 2; i++) {
        float side = float(i) * 2.0 - 1.0;
        vec2 tw = b - vec2(side * span, 0.0);
        vec2 wg = vec2(tw.x / 0.016, (tw.y - 0.09) / 0.045);
        if (abs(tw.x) < 0.028 && tw.y > 0.09 && tw.y < 0.27)
            windows += rectangle(vec2(fract(wg.x) - 0.5, fract(wg.y) - 0.5) * vec2(0.016, 0.045), vec2(0.003, 0.012));
    }
    c += vec3(1.3, 0.75, 0.30) * windows * 0.8;
    float lamps = exp(-abs(b.y - 0.068) * 600.0) * pow(0.5 + 0.5 * cos(b.x * 160.0), 30.0) * step(abs(b.x), span + 0.40);
    c += vec3(1.4, 0.80, 0.35) * lamps;
    if (reflected) {
        float depth = water - p.y;
        c = c * vec3(0.45, 0.50, 0.60) + vec3(0.004, 0.008, 0.016);
        float glint = exp(-abs(p.x - sun.x) * (4.0 + depth * 10.0)) * pow(noise(vec2(p.x * 30.0, depth * 200.0 - TIME)), 3.0);
        c += vec3(1.2, 0.6, 0.25) * glint * 0.8;
        c *= 1.0 - smoothstep(0.0, 0.35, depth) * 0.5;
    }
    return c;
}

float skylineHeight(float x, float width, float minH, float maxH, float seed) {
    float cell = floor(x / width);
    float h = hash(vec2(cell, seed));
    return minH + (maxH - minH) * h * h;
}
vec3 sceneNewYork(vec2 p, vec2 uv, float aspect) {
    vec3 c = mix(vec3(0.70, 0.36, 0.30), vec3(0.04, 0.07, 0.17), smoothstep(-0.30, 0.25, p.y));
    c = mix(c, vec3(0.010, 0.015, 0.045), smoothstep(0.15, 0.5, p.y));
    c += starfield(p) * smoothstep(0.1, 0.4, p.y) * 0.5;
    c += vec3(0.35, 0.22, 0.30) * exp(-abs(p.y + 0.18) * 6.0) * 0.4;
    float ground = -0.50;
    for (int layer = 0; layer < 3; layer++) {
        float f = float(layer);
        float width = 0.050 - f * 0.010;
        float top = ground + skylineHeight(p.x + f * 0.37, width, 0.18 - f * 0.05, 0.42 - f * 0.08, f * 17.0);
        float esX = aspect * 0.33, chX = -aspect * 0.34;
        if (layer == 1) {
            vec2 es = vec2(p.x - esX, p.y - ground);
            float emp = rectangle(es - vec2(0.0, 0.25), vec2(0.040, 0.25));
            emp = max(emp, rectangle(es - vec2(0.0, 0.53), vec2(0.026, 0.03)));
            emp = max(emp, rectangle(es - vec2(0.0, 0.58), vec2(0.015, 0.025)));
            emp = max(emp, rectangle(es - vec2(0.0, 0.64), vec2(0.0025, 0.05)));
            vec2 ch = vec2(p.x - chX, p.y - ground);
            float chr = rectangle(ch - vec2(0.0, 0.22), vec2(0.032, 0.22));
            for (int k = 0; k < 4; k++) {
                float fk = float(k);
                vec2 arc = ch - vec2(0.0, 0.44);
                float w = 0.032 - fk * 0.007;
                chr = max(chr, rectangle(arc - vec2(0.0, fk * 0.022 + 0.011), vec2(w, 0.011)));
            }
            chr = max(chr, rectangle(ch - vec2(0.0, 0.56), vec2(0.002, 0.04)));
            if (emp > 0.5 || chr > 0.5) top = max(top, p.y + 0.001);
            c += vec3(0.85, 0.75, 1.1) * exp(-length(es - vec2(0.0, 0.69)) * 70.0) * 0.6;
            c += vec3(1.1, 0.95, 0.75) * chr * smoothstep(0.43, 0.53, ch.y) * 0.25;
        }
        float inside = 1.0 - smoothstep(top - px(), top + px(), p.y);
        if (inside > 0.0) {
            vec3 haze = mix(vec3(0.16, 0.14, 0.24), vec3(0.012, 0.016, 0.03), f / 2.0);
            vec2 wg = vec2(p.x / (width / 6.0), (p.y - ground) / 0.016);
            vec2 wid = floor(wg);
            float lit = step(0.55 + f * 0.12, hash(wid + f * 31.0)) * step(0.25, fract(wg.x)) * step(0.30, fract(wg.y));
            float flick = 0.85 + 0.15 * sin(TIME * 0.5 + hash(wid) * 30.0);
            vec3 wc = mix(vec3(1.0, 0.72, 0.40), vec3(0.65, 0.80, 1.0), step(0.75, hash(wid + 9.0)));
            vec3 b = haze + wc * lit * flick * (0.25 + f * 0.30);
            c = mix(c, b, inside);
        }
        c += vec3(0.25, 0.18, 0.28) * exp(-abs(p.y - (ground + 0.10 + f * 0.04)) * 18.0) * 0.12 * (2.0 - f);
    }
    return c;
}

vec3 sceneBlackHole(vec2 p, vec2 uv, float aspect) {
    vec2 center = vec2(aspect * 0.32, 0.05);
    float rs = 0.098;
    vec2 q = p - center;
    float R = length(q);
    vec2 lensed = p - q / max(R, 1e-3) * (rs * rs * 1.6 / max(R, rs * 0.5));
    vec3 c = vec3(0.006, 0.004, 0.016) + starfield(lensed) * 0.9;
    c += vec3(0.08, 0.03, 0.12) * fbm(lensed * 1.6 + 5.0) * 0.8;
    float t = TIME * 0.9;
    float tilt = 0.20;
    vec2 dq = vec2(q.x, q.y / tilt) * rot(-0.08);
    float r = length(dq);
    float swirl = fbm(rot(t * 0.35 / pow(max(r, 0.05) / rs, 1.5)) * dq * (6.0 / rs) * 0.18);
    float diskI = smoothstep(rs * 1.45, rs * 1.75, r) * (1.0 - smoothstep(rs * 3.0, rs * 6.2, r)) * pow(rs * 1.7 / max(r, 1e-3), 1.3);
    float doppler = 1.0 - 0.65 * dq.x / max(r, 1e-3);
    vec3 hot = mix(vec3(0.55, 0.12, 0.85), vec3(1.6, 0.75, 1.5), smoothstep(rs * 5.0, rs * 1.6, r));
    vec3 disk = hot * diskI * (0.45 + 0.85 * swirl) * doppler * 1.3;
    float lensAng = atan(q.y, q.x);
    float back = smoothstep(rs * 1.02, rs * 1.12, R) * (1.0 - smoothstep(rs * 1.35, rs * 2.05, R));
    back *= 0.35 + 0.65 * smoothstep(-0.2, 0.9, q.y / max(R, 1e-3));
    float backSwirl = fbm(vec2(lensAng * 3.0 + t * 0.6, R / rs * 4.0));
    c += mix(vec3(0.7, 0.2, 0.95), vec3(1.4, 0.8, 1.4), backSwirl) * back * (0.5 + 0.6 * backSwirl) * (1.0 - 0.4 * q.x / max(R, 1e-3));
    c += vec3(1.5, 0.9, 1.6) * exp(-abs(R - rs * 1.03) * RESOLUTION.y * 0.22) * 0.9;
    float shadow = smoothstep(rs * 0.97, rs * 1.01, R);
    c *= shadow;
    float front = q.y < 0.0 ? 1.0 : shadow;
    c += disk * front;
    c += vec3(0.45, 0.15, 0.6) * exp(-max(R - rs, 0.0) * 6.0) * 0.12;
    return c;
}

vec3 sceneNebula(vec2 p, vec2 uv) {
    float t = TIME * 0.012;
    vec2 q = p * 1.7 + vec2(t, -t * 0.5);
    vec2 w = vec2(fbm(q + 3.0), fbm(q - 2.0));
    float gas = fbm(q + w * 1.9);
    float gas2 = fbm(q * 1.6 + w * 1.2 + 7.0);
    vec3 c = vec3(0.006, 0.008, 0.025);
    c += starfield(p) * 0.9;
    c += vec3(0.60, 0.16, 0.75) * pow(gas, 2.6) * 1.6;
    c += vec3(0.08, 0.40, 0.65) * pow(gas2, 3.0) * 1.3;
    c += vec3(1.0, 0.45, 0.55) * pow(smoothstep(0.55, 0.95, gas * gas2 * 1.8), 2.0) * 0.8;
    float dust = smoothstep(0.50, 0.75, fbm(q * 2.2 + w * 2.5 - 4.0));
    c *= 1.0 - dust * 0.72;
    for (int i = 0; i < 2; i++) {
        float f = float(i);
        vec2 g = p * (4.0 + f * 3.0) + f * 13.0;
        vec2 id = floor(g), lv = fract(g) - 0.5;
        if (hash(id + 41.0) > 0.86) {
            vec2 o = (vec2(hash(id + 2.0), hash(id + 5.0)) - 0.5) * 0.5;
            vec2 d = (lv - o) * RESOLUTION.y / (4.0 + f * 3.0);
            float spikes = exp(-abs(d.x) * 0.9) * exp(-abs(d.y) * 0.04) + exp(-abs(d.y) * 0.9) * exp(-abs(d.x) * 0.04);
            float core = exp(-dot(d, d) * 0.08);
            vec3 tint = mix(vec3(0.75, 0.85, 1.2), vec3(1.2, 0.85, 0.75), hash(id + 9.0));
            c += tint * (core * 1.4 + spikes * 0.35 + exp(-length(d) * 0.12) * 0.12);
        }
    }
    return c;
}

float alpineRidge(float x, float f) {
    return -0.04 - f * 0.105 + (0.30 - f * 0.05) * ridged(vec2(x * (1.0 + f * 0.65) + f * 5.3, f * 2.0));
}
vec3 sceneAlpine(vec2 p, vec2 uv, float aspect) {
    vec3 c = mix(vec3(0.62, 0.42, 0.45), vec3(0.14, 0.20, 0.38), smoothstep(-0.20, 0.12, p.y));
    c = mix(c, vec3(0.015, 0.025, 0.07), smoothstep(0.05, 0.50, p.y));
    c += starfield(p) * smoothstep(0.15, 0.45, p.y) * 0.6;
    vec2 moon = p - vec2(aspect * 0.32, 0.30);
    c += vec3(0.55, 0.60, 0.75) * exp(-length(moon) * 9.0) * 0.35;
    float md = length(moon);
    c = mix(c, vec3(1.25, 1.22, 1.15) * (0.92 + 0.08 * noise(moon * 120.0)), 1.0 - smoothstep(0.038 - px(), 0.038 + px(), md));
    for (int i = 0; i < 4; i++) {
        float f = float(i);
        float h = alpineRidge(p.x, f);
        if (p.y < h) {
            float below = h - p.y;
            vec2 tq = vec2(p.x * (5.0 + f * 2.5), p.y * (6.0 + f * 3.0) + f * 7.0);
            float detail = fbm(tq);
            float detailX = fbm(tq + vec2(0.06, 0.03));
            float shade = clamp(0.5 + (detailX - detail) * 6.0, 0.0, 1.0);
            vec3 tone = mix(vec3(0.20, 0.23, 0.36), vec3(0.03, 0.04, 0.075), f / 3.0);
            vec3 rock = tone * mix(0.55, 1.15, shade);
            float gully = noise(vec2(p.x * (60.0 - f * 8.0), p.y * 5.0 + f));
            float cap = exp(-below * (22.0 - f * 3.0));
            float snow = smoothstep(0.42, 0.52, cap * 0.9 + detail * 0.35 - gully * 0.35) * (1.0 - f * 0.30);
            vec3 snowCol = mix(vec3(0.26, 0.33, 0.55), vec3(0.78, 0.82, 0.95), shade) * (1.0 - f * 0.25);
            vec3 m = mix(rock, snowCol, snow);
            if (i == 3) {
                float teeth = abs(fract(p.x * 85.0) - 0.5) * 2.0;
                float trees = h - 0.05 - 0.02 * noise(vec2(p.x * 9.0, 3.0)) + 0.018 * (1.0 - teeth) * step(0.3, noise(vec2(p.x * 40.0, 5.0)));
                if (p.y < trees) m = vec3(0.010, 0.020, 0.030);
            }
            float haze = smoothstep(0.0, 0.25, below) * (0.38 - f * 0.08);
            c = mix(m, vec3(0.20, 0.22, 0.36), haze);
        }
        float mist = fbm3(vec2(p.x * 2.5 - TIME * 0.015 * (f + 1.0), p.y * 9.0 + f * 4.0));
        c += vec3(0.30, 0.32, 0.42) * smoothstep(0.45, 0.85, mist) * exp(-abs(p.y - (h - 0.12)) * 14.0) * 0.16;
    }
    return c;
}

vec3 sunsetSky(vec2 p, vec2 sun, float horizon) {
    float hy = p.y - horizon;
    vec3 c = mix(vec3(1.25, 0.50, 0.22), vec3(0.55, 0.22, 0.38), smoothstep(0.0, 0.25, hy));
    c = mix(c, vec3(0.09, 0.07, 0.20), smoothstep(0.18, 0.65, hy));
    float sd = length(p - sun);
    c += vec3(1.4, 0.70, 0.25) * exp(-sd * 5.0) * 0.5 + vec3(1.6, 1.1, 0.6) * exp(-sd * 26.0) * 0.8;
    vec2 cq = vec2(p.x * 1.4 + TIME * 0.008, hy * 5.5);
    float d = fbm(cq);
    float dens = smoothstep(0.48, 0.80, d) * smoothstep(0.03, 0.12, hy) * (1.0 - smoothstep(0.30, 0.55, hy));
    float rim = clamp((d - fbm(cq + vec2(sun.x - p.x, -0.6) * 0.12)) * 5.0 + 0.3, 0.0, 1.0);
    vec3 cloud = mix(vec3(0.20, 0.08, 0.16), vec3(1.6, 0.75, 0.38), rim * (0.35 + 0.65 * exp(-sd * 1.6)));
    c = mix(c, cloud, dens * 0.9);
    float sun0 = 1.0 - smoothstep(0.060 - px(), 0.060 + px(), sd);
    c = mix(c, vec3(2.0, 1.45, 0.85), sun0 * step(horizon, p.y));
    return c;
}
vec3 sceneSunset(vec2 p, vec2 uv, float aspect) {
    float horizon = -0.15;
    vec2 sun = vec2(-aspect * 0.34, horizon + 0.075);
    if (p.y >= horizon) {
        vec3 c = sunsetSky(p, sun, horizon);
        float isle = horizon + 0.022 * smoothstep(0.25, 0.0, abs(p.x - aspect * 0.36)) * (0.6 + 0.4 * noise(vec2(p.x * 40.0, 1.0)));
        if (p.y < isle) c = vec3(0.06, 0.03, 0.06);
        return c;
    }
    float depth = horizon - p.y;
    vec2 wq = vec2(p.x * 8.0 / (0.08 + depth), depth * 120.0 / (0.25 + depth * 3.0) - TIME * 0.9);
    float wave = noise(wq) * 0.6 + noise(wq * 2.3) * 0.4;
    vec2 rp = vec2(p.x + (wave - 0.5) * 0.04 * depth * 4.0, horizon + depth * 1.2 + (wave - 0.5) * 0.03);
    vec3 c = sunsetSky(rp, sun, horizon) * 0.55;
    c = mix(c, vec3(0.05, 0.03, 0.09), smoothstep(0.0, 0.45, depth) * 0.6);
    float path = exp(-abs(p.x - sun.x) * (5.0 / (depth * 2.0 + 0.15)));
    float glitter = pow(noise(vec2(p.x * 160.0, depth * 500.0 + TIME * 3.0)), 8.0);
    c += vec3(1.8, 1.0, 0.45) * path * (glitter * 3.0 + 0.12);
    return c;
}

vec3 sceneRain(vec2 p, vec2 uv, float aspect) {
    vec3 c = mix(vec3(0.05, 0.08, 0.14), vec3(0.010, 0.016, 0.035), smoothstep(-0.4, 0.5, p.y));
    c += vec3(0.30, 0.18, 0.25) * exp(-abs(p.y + 0.15) * 4.0) * 0.18;
    float top = -0.5 + skylineHeight(p.x + 0.3, 0.045, 0.16, 0.42, 14.0);
    float building = 1.0 - smoothstep(top - 0.004, top + 0.004, p.y);
    vec2 wg = vec2(p.x / 0.0075, (p.y + 0.5) / 0.014);
    float lit = step(0.66, hash(floor(wg))) * step(0.3, fract(wg.x)) * step(0.3, fract(wg.y));
    c = mix(c, vec3(0.025, 0.035, 0.055) + lit * vec3(0.30, 0.36, 0.38) * 0.6, building * 0.9);
    for (int i = 0; i < 3; i++) {
        float f = float(i), scale = 3.0 + f * 2.5;
        vec2 g = p * scale + vec2(f * 7.3, TIME * 0.015 * (f + 1.0));
        vec2 id = floor(g), lv = fract(g) - 0.5;
        float h = hash(id + f * 11.0);
        if (h > 0.55) {
            vec2 o = (vec2(hash(id + 3.0 + f), hash(id + 7.0 + f)) - 0.5) * 0.5;
            float size = 0.16 + 0.22 * hash(id + 13.0);
            float d = length(lv - o) / size;
            float disk = 1.0 - smoothstep(0.85, 1.0, d);
            float ring = exp(-abs(d - 0.93) * 14.0) * 0.4;
            float pick = hash(id + 21.0);
            vec3 tint = pick < 0.35 ? vec3(1.0, 0.55, 0.20) : pick < 0.6 ? vec3(1.0, 0.25, 0.30) : pick < 0.85 ? vec3(0.30, 0.75, 1.0) : vec3(0.95, 0.90, 0.80);
            float pulse = 0.75 + 0.25 * sin(TIME * (0.4 + h) + h * 30.0);
            c += tint * (disk * 0.10 + ring * 0.12) * pulse * (1.0 - f * 0.22) * smoothstep(0.45, -0.35, p.y);
        }
    }
    c += vec3(0.05, 0.07, 0.10) * fbm(vec2(p.x * 2.0 + TIME * 0.03, p.y * 3.0)) * 0.6;
    for (int i = 0; i < 2; i++) {
        float f = float(i);
        vec2 q = vec2(p.x + p.y * 0.18, p.y) * vec2(70.0 + f * 50.0, 5.0 + f * 3.0);
        q.y += TIME * (3.2 + f * 1.4);
        vec2 id = floor(q), lv = fract(q);
        float drop = exp(-abs(lv.x - 0.5) * (40.0 + f * 30.0)) * smoothstep(0.0, 0.25, lv.y) * (1.0 - smoothstep(0.4, 0.95, lv.y)) * step(0.62, hash(id + f * 9.0));
        c += drop * vec3(0.20, 0.27, 0.35) * (0.8 - f * 0.3);
    }
    vec2 g = p * 9.0;
    vec2 id = floor(g), lv = fract(g) - 0.5;
    if (hash(id + 77.0) > 0.78) {
        vec2 o = (vec2(hash(id + 1.0), hash(id + 2.0)) - 0.5) * 0.5;
        vec2 d = (lv - o) / (0.10 + 0.10 * hash(id + 4.0));
        float r = length(d);
        float bead = 1.0 - smoothstep(0.85, 1.0, r);
        c = mix(c, c * 0.65 + vec3(0.04, 0.05, 0.07), bead * 0.6);
        c += vec3(0.6, 0.7, 0.8) * exp(-length(d - vec2(-0.35, 0.4)) * 9.0) * bead * 0.5;
        c += vec3(0.25, 0.30, 0.38) * smoothstep(0.6, 1.0, r) * bead * step(d.y, 0.0) * 0.4;
    }
    return c;
}

vec3 scenePrism(vec2 p, vec2 uv) {
    float t = TIME * 0.15;
    vec3 c = mix(vec3(0.008, 0.007, 0.024), vec3(0.035, 0.022, 0.075), smoothstep(-0.5, 0.5, p.y));
    c += vec3(0.22, 0.10, 0.42) * exp(-length(p * vec2(0.6, 1.0)) * 2.4) * 0.22;
    // Twisting silk ribbons: width follows the twist, colour follows a thin-film palette.
    for (int i = 0; i < 4; i++) {
        float f = float(i);
        float y0 = -0.24 + f * 0.135 + 0.10 * sin(p.x * 1.5 + t * 1.3 + f * 1.7) + 0.045 * sin(p.x * 3.1 - t + f * 2.3);
        float twist = p.x * 1.15 + t * 0.8 + f * 1.9;
        float w = 0.010 + 0.050 * abs(sin(twist));
        float dy = p.y - y0;
        float s = clamp(dy / w, -1.0, 1.0);
        float cover = 1.0 - smoothstep(w - px() * 1.5, w + px() * 0.5, abs(dy));
        float facing = clamp(cos(twist) * 3.0, -1.0, 1.0);
        float curve = sqrt(max(0.0, 1.0 - s * s));
        vec3 iris = 0.5 + 0.5 * cos(6.2831 * (s * 0.30 + twist * 0.12 + f * 0.21 + vec3(0.0, 0.33, 0.67)));
        iris = mix(vec3(0.62, 0.56, 0.92), iris, 0.70);
        float light = 0.18 + 0.82 * pow(curve, 1.5) * (0.55 + 0.45 * facing * s);
        float sheen = pow(curve, 24.0) * (0.5 + 0.5 * facing);
        vec3 ribbon = iris * light * 0.95 + vec3(1.0, 0.96, 1.05) * sheen * 0.55;
        c = mix(c, ribbon, cover * 0.88);
        c += iris * exp(-abs(dy) * 24.0) * 0.05;
    }
    return c;
}

vec3 sceneSakura(vec2 p, vec2 uv, float aspect) {
    float water = -0.30;
    vec2 moonPos = vec2(aspect * 0.33, 0.12);
    vec3 c = mix(vec3(0.42, 0.18, 0.32), vec3(0.04, 0.035, 0.11), smoothstep(-0.3, 0.45, p.y));
    c += starfield(p) * smoothstep(0.0, 0.4, p.y) * 0.6;
    float md = length(p - moonPos);
    c += vec3(0.95, 0.55, 0.70) * exp(-md * 5.5) * 0.35;
    float moon = 1.0 - smoothstep(0.105 - px() * 1.5, 0.105 + px() * 1.5, md);
    c = mix(c, vec3(1.35, 1.10, 1.10) * (0.94 + 0.06 * fbm((p - moonPos) * 30.0)), moon);
    float wisp = smoothstep(0.55, 0.85, fbm(vec2(p.x * 2.0 + TIME * 0.01, p.y * 9.0))) * smoothstep(-0.05, 0.1, p.y);
    c = mix(c, vec3(0.30, 0.18, 0.30), wisp * 0.45);
    for (int i = 0; i < 2; i++) {
        float f = float(i);
        float h = water + 0.06 + (0.14 - f * 0.06) * fbm3(vec2(p.x * (1.4 + f) + f * 4.0, f));
        if (p.y < h && p.y > water) c = mix(vec3(0.20, 0.11, 0.24), vec3(0.08, 0.04, 0.10), f);
    }
    vec2 gate = p - vec2(moonPos.x, water + 0.02);
    float torii = max(rectangle(gate - vec2(-0.085, 0.13), vec2(0.011, 0.13)), rectangle(gate - vec2(0.085, 0.13), vec2(0.011, 0.13)));
    torii = max(torii, rectangle(gate - vec2(0.0, 0.215), vec2(0.115, 0.009)));
    torii = max(torii, rectangle(gate - vec2(0.0, 0.262 + 0.035 * gate.x * gate.x / 0.02), vec2(0.15, 0.011)));
    torii = max(torii, rectangle(gate - vec2(0.0, 0.238), vec2(0.008, 0.02)));
    c = mix(c, vec3(0.30, 0.03, 0.04), torii);
    if (p.y < water) {
        float depth = water - p.y;
        float ripple = (noise(vec2(p.x * 10.0, depth * 140.0 - TIME * 0.7)) - 0.5) * 0.02;
        vec2 rp = vec2(p.x + ripple, water + depth);
        float rmd = length(rp - moonPos);
        c = mix(vec3(0.10, 0.05, 0.12), vec3(0.03, 0.02, 0.06), smoothstep(0.0, 0.2, depth));
        c += vec3(1.0, 0.70, 0.80) * (exp(-rmd * 5.0) * 0.25 + (1.0 - smoothstep(0.10, 0.12, rmd)) * 0.45) * (0.6 + 0.4 * noise(vec2(p.x * 50.0, depth * 300.0)));
    }
    // A branch reaching in from the top-left corner, dressed with blossom clumps.
    vec2 corner = p - vec2(-aspect * 0.5, 0.5);
    float limb = corner.y + 0.16 + 0.22 * corner.x - 0.06 * sin(corner.x * 7.0);
    float thickness = mix(0.012, 0.0025, clamp(corner.x / 0.55, 0.0, 1.0));
    float branch = (1.0 - smoothstep(0.0, px() * 1.5, abs(limb) - thickness)) * step(corner.x, 0.55) * step(-0.05, corner.x);
    c = mix(c, vec3(0.045, 0.018, 0.035), branch);
    float field = 1.0, shadeY = 0.0;
    for (int i = 0; i < 7; i++) {
        float f = float(i);
        float bx = 0.02 + f * 0.075;
        vec2 clump = vec2(bx, -0.16 - 0.22 * bx + 0.06 * sin(bx * 7.0) + (hash(vec2(f, 2.0)) - 0.40) * 0.12);
        float r = mix(0.085, 0.035, f / 6.0) * (0.8 + 0.4 * hash(vec2(f, 5.0)));
        float d = length(corner - clump) - r;
        if (d < field) { field = d; shadeY = (corner.y - clump.y) / r; }
    }
    float fringe = (fbm(p * 30.0 + 2.0) - 0.5) * 0.060;
    float canopy = 1.0 - smoothstep(-px() * 1.5, px() * 1.5, field + fringe);
    float blossom = fbm(p * 34.0);
    vec3 petals = mix(vec3(0.42, 0.12, 0.28), vec3(1.05, 0.58, 0.76), clamp(0.45 + shadeY * 0.35 + (blossom - 0.5) * 0.9, 0.0, 1.0));
    c += vec3(0.55, 0.22, 0.38) * exp(-max(field, 0.0) * 30.0) * 0.10;
    c = mix(c, petals, canopy);
    for (int i = 0; i < 14; i++) {
        float f = float(i);
        float seed = hash(vec2(f, 4.0));
        float near = step(0.7, seed);
        vec2 center = vec2((seed - 0.5) * aspect * 1.1 + 0.1 * sin(TIME * 0.35 + f),
                           0.5 - fract(hash(vec2(f, 7.0)) + TIME * (0.018 + seed * 0.018)) * 1.1);
        vec2 q = p - center;
        q = rot(TIME * 0.5 + f) * q;
        float size = mix(0.010, 0.020, near);
        float petal = 1.0 - smoothstep(0.55, 1.0, length(q / vec2(size, size * 0.5)));
        c = mix(c, vec3(1.15, 0.65, 0.80), petal * (0.55 + 0.35 * seed));
    }
    return c;
}

vec3 sceneTokyo(vec2 p, vec2 uv, float aspect) {
    float street = -0.36;
    vec3 c = mix(vec3(0.45, 0.10, 0.32), vec3(0.03, 0.02, 0.09), smoothstep(-0.30, 0.35, p.y));
    c += vec3(0.6, 0.15, 0.45) * exp(-abs(p.y - street - 0.05) * 5.0) * 0.25;
    c += starfield(p) * smoothstep(0.15, 0.45, p.y) * 0.4;
    vec2 tw = vec2(p.x - aspect * 0.32, p.y - street);
    float towerH = 0.66;
    if (tw.y > 0.0 && tw.y < towerH) {
        float tt = tw.y / towerH;
        float halfW = 0.085 * pow(1.0 - tt, 1.6) + 0.004;
        float legs = 1.0 - smoothstep(0.0, px() * 1.5, abs(abs(tw.x) - halfW) - 0.0018);
        float lattice = 0.0;
        if (abs(tw.x) < halfW) {
            float k = fract(tw.y * 28.0);
            lattice = 1.0 - smoothstep(0.0, px() * 1.2, abs(abs(tw.x) - halfW * abs(k * 2.0 - 1.0)) - 0.0006);
            lattice = max(lattice, (1.0 - smoothstep(0.0, px() * 1.2, abs(k - 0.5) / 28.0 - 0.0005)) * 0.6);
        }
        float decks = rectangle(tw - vec2(0.0, 0.25), vec2(0.040, 0.010)) + rectangle(tw - vec2(0.0, 0.43), vec2(0.020, 0.008));
        float s = clamp(max(legs, lattice * 0.75) + decks, 0.0, 1.0);
        vec3 col = mix(vec3(1.45, 0.38, 0.08), vec3(1.25, 1.05, 0.95), step(0.82, fract(tt * 5.0)));
        c = mix(c, col, s);
    }
    c += vec3(1.1, 0.35, 0.10) * exp(-length((tw - vec2(0.0, 0.30)) * vec2(1.6, 0.6)) * 9.0) * 0.30;
    for (int layer = 0; layer < 3; layer++) {
        float f = float(layer);
        float width = 0.045 - f * 0.008;
        float top = street + skylineHeight(p.x + f * 0.41, width, 0.10 - f * 0.02, 0.36 - f * 0.08, 62.0 + f * 9.0);
        if (p.y < top && p.y > street) {
            float cell = floor((p.x + f * 0.41) / width);
            vec3 b = mix(vec3(0.12, 0.05, 0.16), vec3(0.015, 0.010, 0.03), f / 2.0);
            vec2 wg = vec2((p.x + f * 0.41) / (width / 5.0), (p.y - street) / 0.012);
            float win = step(0.62, hash(floor(wg) + f)) * step(0.3, fract(wg.x)) * step(0.3, fract(wg.y));
            b += vec3(0.55, 0.50, 0.70) * win * (0.10 + f * 0.10);
            float signX = fract((p.x + f * 0.41) / width);
            float signMask = step(0.70, hash(vec2(cell, 9.0 + f))) * band(signX, 0.35, 0.55) * band(p.y - street, 0.05, top - street - 0.03);
            float sx = hash(vec2(cell, 3.0 + f));
            vec3 neon = sx < 0.33 ? vec3(1.6, 0.20, 0.60) : sx < 0.66 ? vec3(0.20, 1.1, 1.5) : vec3(1.5, 0.9, 0.2);
            float flick = step(0.08, fract(sin(TIME * 2.0 + cell) * 3.7));
            b += neon * signMask * (0.9 + 0.1 * flick) * (0.5 + f * 0.25);
            c = b;
        }
    }
    if (p.y <= street) {
        float depth = street - p.y;
        float shimmer = noise(vec2(p.x * 4.0, depth * 45.0 - TIME * 0.5));
        float x = p.x + (shimmer - 0.5) * 0.004;
        vec3 refl = vec3(0.06, 0.02, 0.07) * (1.0 - smoothstep(0.0, 0.14, depth));
        float width = 0.029;
        float cell = floor((x + 0.82) / width);
        float signRefl = step(0.70, hash(vec2(cell, 11.0))) * smoothstep(0.30, 0.45, fract((x + 0.82) / width)) * (1.0 - smoothstep(0.45, 0.60, fract((x + 0.82) / width)));
        float sx = hash(vec2(cell, 5.0));
        vec3 neon = sx < 0.5 ? vec3(1.2, 0.2, 0.6) : vec3(0.2, 0.9, 1.3);
        refl += neon * signRefl * exp(-depth * 9.0) * (0.35 + 0.35 * shimmer);
        refl += vec3(1.1, 0.35, 0.1) * exp(-abs(x - aspect * 0.32) * 40.0) * exp(-depth * 5.0) * (0.4 + 0.4 * shimmer);
        c = refl + vec3(0.015, 0.010, 0.025);
        float curb = exp(-abs(depth - 0.07) * 300.0);
        float traffic = smoothstep(0.55, 0.95, noise(vec2(p.x * 6.0 - TIME * 0.8, 1.0)));
        c += vec3(1.3, 0.25, 0.15) * curb * traffic * 0.5;
    }
    return c;
}

vec3 sceneHalloween(vec2 p, vec2 uv, float aspect) {
    vec3 c = mix(vec3(.16, .075, .19), vec3(.020, .018, .060), smoothstep(-0.2, 0.5, p.y));
    c += starfield(p) * 0.35 * smoothstep(0.0, 0.35, p.y);
    vec2 moon = p - vec2(aspect * .29, .26);
    c += vec3(.55, .40, .45) * exp(-length(moon) * 6.0) * 0.45;
    float disk = 1.0 - smoothstep(.070 - px(), .070 + px(), length(moon));
    float craters = .86 + .14 * fbm3(moon * 60.0);
    c = mix(c, vec3(1.15, .98, .74) * craters, disk);
    float drift = fbm(vec2(p.x * 1.5 + TIME * .02, p.y * 5.0));
    c = mix(c, vec3(.10, .06, .13), smoothstep(.55, .85, drift) * smoothstep(.05, .2, p.y) * 0.6);
    float haze = clouds(vec2(p.x * 2.0 + TIME * .022, p.y * 4.0));
    c += vec3(.085, .055, .11) * haze;
    for (int layer = 0; layer < 2; layer++) {
        float depth = float(layer);
        for (int i = 0; i < 6; i++) {
            float f = float(i), anchor = (f < 3.0 ? .025 + f * .085 : .80 + (f - 3.0) * .085);
            vec2 tree = p - vec2((anchor - .5) * aspect, -.26 - depth * .12);
            float silhouette = bareTree(tree / (.65 + depth * .3), f);
            c = mix(c, mix(vec3(.10, .07, .14), vec3(.018, .016, .03), depth), silhouette);
        }
    }
    float hill = .13 + .02 * sin(p.x * 3.0) + .013 * sin(p.x * 8.0);
    c = mix(c, vec3(.022, .018, .034) + haze * vec3(.018, .014, .025), 1.0 - smoothstep(hill - .004, hill + .004, uv.y));
    c = jackLantern(c, p, vec2(-aspect * .25, -.29), .055, 2.0);
    c = jackLantern(c, p, vec2(aspect * .25, -.30), .047, 4.0);
    float fog = exp(-abs(uv.y - .19 - haze * .025) * 18.0) * haze;
    float roll = fbm3(vec2(p.x * 3.0 - TIME * .05, p.y * 8.0));
    c = mix(c, vec3(.30, .21, .38), fog * (.40 + .30 * roll));
    c = jackLantern(c, p, vec2(-aspect * .38, -.35), .125, 1.0);
    c = jackLantern(c, p, vec2(aspect * .38, -.36), .115, 5.0);
    for (int i = 0; i < 8; i++) {
        float f = float(i), seed = hash(vec2(f, 71.0));
        float side = f < 4.0 ? -1.0 : 1.0;
        vec2 spark = vec2(side * aspect * .37 + .035 * sin(TIME * .35 + f), -.32 + fract(seed + TIME * .035) * .30);
        float d = length(p - spark) * RESOLUTION.y;
        c += vec3(1.3, .45, .06) * (exp(-d * d * 0.3) + exp(-d * 0.4) * 0.15) * (1.0 - fract(seed + TIME * .035));
    }
    for (int i = 0; i < 3; i++) {
        float f = float(i);
        vec2 bat = p - vec2(aspect * (.16 + f * .06) + .015 * sin(TIME * .35 + f), .20 + f * .04);
        float wing = abs(bat.y - (.012 * sin(TIME * 3.0 + f) - .38 * abs(bat.x)));
        float wings = (1.0 - smoothstep(.002, .005, wing)) * step(abs(bat.x), .033);
        c = mix(c, vec3(.018, .018, .035), wings);
    }
    return c;
}

void main() {
    vec2 uv = (gl_FragCoord.xy - ORIGIN) / RESOLUTION;
    float aspect = RESOLUTION.x / RESOLUTION.y;
    vec2 p = (uv - 0.5) * vec2(aspect, 1.0);
    if (INTRO > 0.0 && MODE > 16.5) {
        float progress = clamp(INTRO - .001, 0.0, 1.0);
        float mask = 0.0;
        // Fixed-size flock: perspective growth sells flight out of the screen.
        // Derive wing beats from INTRO so the entrance always starts consistently.
        for (int i = 0; i < 12; i++) {
            float seed = float(i);
            float delay = hash(vec2(seed, 7.0)) * .19;
            float flight = clamp((progress - delay) / .76, 0.0, 1.0);
            float depth = flight * flight;
            float angle = seed * 2.399 + .35 * sin(seed * 1.7);
            vec2 direction = vec2(cos(angle) * aspect, sin(angle));
            vec2 center = direction * (.045 + depth * 1.30);
            center += vec2(sin(flight * 7.0 + seed), cos(flight * 6.0 + seed)) * .025 * flight;
            float size = .016 + depth * (.23 + hash(vec2(seed, 3.0)) * .14);
            vec2 local = (p - center) / size;
            float bank = sin(angle) * .35 + sin(flight * 5.0 + seed) * .12;
            local = mat2(cos(bank), -sin(bank), sin(bank), cos(bank)) * local;
            float flap = .5 + .5 * sin(flight * 25.0 + seed * 1.9);
            float aa = 1.3 / (RESOLUTION.y * size);
            float silhouette = flyingBat(local, flap, aa);
            float visible = smoothstep(0.0, .055, flight) * (1.0 - smoothstep(.80, 1.0, flight));
            mask = max(mask, silhouette * visible);
        }
        gl_FragColor = vec4(vec3(.018, .009, .029), mask);
        return;
    }
    vec3 c;
    if (MODE < 0.5) c = sceneFire(p, uv);
    else if (MODE < 1.5) c = sceneNormal(p, uv);
    else if (MODE < 2.5) c = sceneSolar(p, uv, aspect);
    else if (MODE < 3.5) c = sceneAurora(p, uv, aspect);
    else if (MODE < 4.5) c = sceneSynth(p, uv, aspect);
    else if (MODE < 5.5) c = sceneEarth(p, uv, aspect);
    else if (MODE < 6.5) c = sceneMars(p, uv, aspect);
    else if (MODE < 7.5) c = sceneLondon(p, uv, aspect);
    else if (MODE < 8.5) c = sceneNewYork(p, uv, aspect);
    else if (MODE < 9.5) c = sceneBlackHole(p, uv, aspect);
    else if (MODE < 10.5) c = sceneNebula(p, uv);
    else if (MODE < 11.5) c = sceneAlpine(p, uv, aspect);
    else if (MODE < 12.5) c = sceneSunset(p, uv, aspect);
    else if (MODE < 13.5) c = sceneRain(p, uv, aspect);
    else if (MODE < 14.5) c = scenePrism(p, uv);
    else if (MODE < 15.5) c = sceneSakura(p, uv, aspect);
    else if (MODE < 16.5) c = sceneTokyo(p, uv, aspect);
    else c = sceneHalloween(p, uv, aspect);
    // Filmic grade: lets glows exceed 1.0 without clipping into flat blobs.
    c = aces(max(c, 0.0) * 1.05);
    float v = length(p * vec2(0.80, 1.0));
    c *= 1.0 - 0.32 * smoothstep(0.35, 1.05, v);
    // Triangular dither removes 8-bit banding from the long, dark gradients.
    float dither = hash(gl_FragCoord.xy + fract(TIME) * 61.0) + hash(gl_FragCoord.yx * 1.37 + 17.0) - 1.0;
    c += dither / 255.0;
    gl_FragColor = vec4(clamp(c, 0.0, 1.0), 1.0);
}
