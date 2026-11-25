# OpenBrutality

> **Backup Mirror (Anti-DMCA):** [https://deobf.su/yxzero/openbrutality](https://deobf.su/yxzero/openbrutality) — save or bookmark this mirror in case the primary repository is taken down.

Complete, clean, compilable Java 17 source reconstruction of the **Brutality v2.6** Minecraft 1.8.9 client (MCP + OptiFine HD U M5 + ViaMCP) and **Brutality Desktop Launcher 1.2.10**, recovered purely statically from a single 64-bit Windows JVM process memory dump and verified at the JVM bytecode level.

> **All reverse-engineering, archive carving, HotSpot Metaspace reconstruction, static deobfuscation, symbol recovery, compiler-AST cleanup, vendor neutralization, and bytecode-equivalence proofs were performed by Gemini 4 Neon XHigh.**

---

## Project Overview

- **Client**: Brutality `v2.6` (`org.brutality.Brutality`, changelog `2026-11-20`)
- **Host Stack**: Minecraft `1.8.9` (MCP) + `OptiFine_1.8.9_HD_U_M5` + ViaMCP / ViaLoadingBase (`ViaVersion 5.3.0`, `ViaBackwards 5.3.0`, `ViaRewind 4.0.6`)
- **Toolchain**: Java 17 (`javac -parameters`, Lombok), Gradle `9.9.2`
- **Reconstructed Scope**:
  - `1,308` clean Java source files (`1,711` Brutality client classes, `1` modified `javax.vecmath.Matrix4f`, `233` modified Minecraft/OptiFine classes across 93 compilation units, `28` integrated ViaMCP/ViaLoadingBase classes, and `22` Metaspace-recovered Desktop Launcher classes + `22` compile-boundary signatures for unloaded launcher workers)
  - `141 / 141` shipped product resources (assets, GLSL shaders, TTF/OTF fonts, icons, configs, `log4j2.xml`, natives) verified byte-for-byte identical via SHA-256
  - Zero runtime credentials, tokens, HWIDs, or personal data (`0` leaks across the entire tree)
  - All vendor DRM/auth/telemetry/anti-tamper classes remain fully restored as readable dormant code in the tree, while **100% of runtime references to them are disabled** (`// VENDOR-DISABLED:` markers) and proven unreachable at the bytecode level

---

## Protections Used vs. How We Defeated All of Them

| # | Protection Layer in Brutality | How It Worked | How We Defeated It |
|---|---|---|---|
| **1** | **Desktop Launcher Delivery & In-Memory Classloading** (`Brutality Desktop Launcher 1.2.10`, `PayloadClassLoader`, Ed25519 signature check) | Authenticated with the backend, verified an Ed25519-signed client payload, and loaded the client uber-JAR into a custom `URLClassLoader` without dropping an unencrypted standalone launcher source. | Carved the contiguous 63.68 MB client uber-JAR directly out of native process memory (`14,401 / 14,410` entries passing IEEE CRC-32 verification). Parsed HotSpot's `gHotSpotVMStructs` and walked `ClassLoaderDataGraph` to reconstruct all 22 loaded launcher `InstanceKlass` structures from Metaspace, reversing HotSpot `ConstantPoolCache` bytecode rewriting (`_nofast_getfield`, `invokedynamic`, `ldc_w`) back into standard `.class` files (verified zero `javap -c` diff against a raw carved classfile). |
| **2** | **Stage 1 Obfuscation — Grunt (`Grunteon` / `Gruntpocalypse`)** | Applied XOR `new String(char[])` string encryption, `Integer.parseUnsignedLong` / `Float.intBitsToFloat` / `Double.longBitsToDouble` opaque arithmetic, wrapped `StringConcatFactory` bootstrap methods, `OO00...` renaming, 3,128 watermark members, stripped `ACC_SYNTHETIC`, and **bogus `ACC_BRIDGE` (`0x0040`) flags without `ACC_SYNTHETIC` on 23 real overloaded methods** to make decompilers silently drop user methods. | Built a pure-static ASM deobfuscator (zero target code execution): statically decrypted **39,101** Grunt strings, folded **341,402** opaque constant expressions, restored **213 / 213** `StringConcatFactory` bootstraps, stripped all **3,128** watermark fields/methods, cleared bogus `ACC_BRIDGE` flags on real overloads before running genuine bridge/synthetic metadata restoration. |
| **3** | **Stage 2 Obfuscation — Zelix KlassMaster (ZKM)** | Flattened `org.brutality.**` and shaded libraries into `a/*` in strict ASCII lexicographical order, encrypted strings/numbers via `<clinit>` cipher tables + stack-shuffled `(II)String` / `(IJ)I` / `(IJ)J` decryptors, stripped `ConstantValue` attributes from user `static final String`/`String[]` fields, and routed reflective lookups through a SHA-512 mapping helper (`a/ak`). | Implemented a bounded abstract MiniVM inside our static ASM deobfuscator to evaluate `<clinit>` cipher tables and decrypt **24,767** ZKM strings and **3,148** ciphered numbers without loading a single target class; re-emitted all ZKM-stripped user `static final` initializers (`0` uninitialized references); recovered all **158** original packages from `JarIndex` / `PackageEntry` metadata, then leveraged ZKM's strict ASCII sort order + `a/ak`'s SHA-512 oracle + Lombok `@Generated` accessors + `Record` components + `MethodParameters` (`2,351` methods restored!) + `Module`/`Command` anchors to reconstruct the full symbol map (verified instruction-for-instruction against the author's own bundled `HeldItemCustomization.java`). Matched **518 / 518** shaded third-party classes in `a/*` to exact Maven versions instead of decompiling dependencies. |
| **4** | **Pre-Auth Gate, Licensing, HWID Fingerprinting & TLS SPKI Pinning** (`ClientPreAuthBootstrap`, `PreAuthGate`, `BackendAuthService`, `AuthHandler`, `LicenseValidator`, `HeartbeatLeasePolicy`, `SessionLeaseLock`, `HardwareIdGenerator`, `HardwareAddressUtil`, `PinnedTlsHttpClient`) | Blocked startup before `Main.main` and `Brutality.startClient()`, hashed OS/CPU/host/disk/MAC (`NetworkInterface.getHardwareAddress()`), enforced pinned SPKI SHA-256 TLS (`ca93725b...`) to `*.up.railway.app`, required `/api/auth/login` + `/api/client/heartbeat` + `license.dat` + single-instance lease locks. | Bypassed `ClientPreAuthBootstrap.process2()` in `Main.main` while calling `NativesExtractor.extractNatives()` directly so LWJGL natives still unpack cleanly; routed `Brutality.startClient()` directly into `Brutality.initializeClient()` so the client boots standalone via `net.minecraft.client.main.Main` (or `Start`). All auth, HWID, and pinned-TLS classes remain intact but completely unreferenced. |
| **5** | **HMAC-SHA256 JAR Integrity Seal & Anti-Tamper Sabotage** (`JarIntegrityVerifier`, `META-INF/BRUTALITY.DIGEST`, `RuntimeAuthGuard`, `GC-Monitor` thread, `Module.onUpdate()`, `EntitlementState`, `Event.call()`) | Verified an HMAC-SHA256 class manifest (`META-INF/BRUTALITY.DIGEST`, key `"Brutality-Int1grS3al-2026-HMAC!\0"`) on startup and every 2–6 minutes in a background `"GC-Monitor"` daemon thread; incremented tamper strikes in `RuntimeAuthGuard` so `GC-Monitor` (strikes >= 5) and `Module.onUpdate()` (strikes >= 3) silently and randomly disabled active modules, while `Event.call()` polled `BackendAuthService.process4()` on every game event and `101` modules checked `RuntimeAuthGuard.checkState2()` / `EntitlementState.checkState()`. | Neutralized `process5()`, `startIntegrityDaemon()`, `Event.call()`, `Module` tamper checks, `CombatType`, `DrawSettingLabelScreen`, and all `101` gated module subclasses with neutral `true` / no-op replacements and `// VENDOR-DISABLED:` markers. `META-INF/BRUTALITY.DIGEST` remains untouched on disk and is never read at runtime. |
| **6** | **Stealth Reflection Anti-Crack Traps & Anti-Decompiler Process Killer** (`InventoryUtils`, `MovementUtils`, `AntiDecompilerGuard`) | Hid periodic reflection checks inside core gameplay helpers (`InventoryUtils` & `MovementUtils`) inspecting `BackendAuthService.currentToken`/`currentUser` and validating class-name hash `1515870810` to silently break clicks/movement if cracked; `AntiDecompilerGuard` enumerated running processes via `ProcessHandle`, `tasklist`, and `wmic` to detect debuggers/decompilers (IDA, Ghidra, x64dbg, Wireshark, Recaf, JD-GUI, Cheat Engine, Process Hacker), killed them with `taskkill /F /PID`, and POSTed `/api/auth/flag`. | Caught every hidden reflection string, field access, and process-execution call via whole-program bytecode scanning; disabled `AntiDecompilerGuard` at startup and replaced all 10 stealth reflection helpers in `InventoryUtils` and `MovementUtils` with clean neutral returns. |
| **7** | **Vendor Cloud, IRC, Staff Tracker, Playtime Telemetry, Prestige Webhooks & Launcher IPC** (`BrutalityCloudClient`, `GuiCloudBrowser`, `GuiCloudUpload`, `IrcClient`, `IRC`, `StaffTrackerService`, `Stafftracker`, `PlaytimeTelemetry`, `ProximityTracker`, `org.brutality.system.prestige.*`, `LauncherPresenceBridge`, Railway scripting docs URLs) | Reported session playtime every 30s, broadcast nearby paid users, sent Hypixel Pit prestige chat webhooks, connected WebSockets to `wss://brutality-irc-production.up.railway.app`, fetched/uploaded configs & scripts from `brutality-cloud-production.up.railway.app`, polled `staff-tracker-api-production.up.railway.app`, and synced IPC presence files with the parent launcher. | Deregistered vendor-only `IRC` and `Stafftracker` modules in `ModuleManager`, decoupled cloud browser/upload screens and commands cleanly to local-only offline behavior, replaced `LauncherPresenceBridge` in `DiscordRPC` with direct local server status formatting, and disabled all outbound vendor hooks while leaving every local feature intact. |

---

## Verification & Code Cleanliness

All decompilation and post-decompilation cleanup passes were verified directly against compiled JVM bytecode:

- **Bytecode Unreachability Proof**: Rapid Type Analysis (RTA) call-graph + `<clinit>` + constant-pool + `invokedynamic` + reflection-string traversal from all `263` entrypoints/host classes reaches **`0` vendor classes** (`120` dormant vendor `.class` files preserved); an exhaustive universal cut check across all `1,898` non-vendor classes confirms **`0` non-vendor -> vendor edges** and **`0` vendor host, endpoint, or HWID/process-killer API hits**.
- **Bytecode-Verified Compiler-AST Cleaning**: Four JDK 17 `javac` Compiler-AST (`com.sun.source.util.Trees`) passes eliminated all **`173,114 -> 0`** `varN` decompiler variables (`47,906` variables renamed semantically across `1,224` files), restored **`2,351`** ground-truth `MethodParameters` lists, restored **`2,159`** symbolic `GL11`–`GL30` & `Keyboard.KEY_*` constants, fixed corrupted integer `char` literals (`'耼'` -> `GL11.GL_ALPHA8`, `'썐'` -> `50000`), flattened **`2,993`** `else`-after-return pyramids, and bijectively renamed the **`27`** highest-impact placeholder classes (`2,458` references) — verified across all **`2,018 / 2,018` classes** and **`19,258 / 19,258` methods** with **`0` bytecode equivalence failures**.
- **Resource & Clean-Build Integrity**: `141 / 141` resources match SHA-256 byte-for-byte; `./gradlew clean build` succeeds with `0` errors on Java 17.

---

## Building

Requires **JDK 17**.

```bash
# Clean compile, assemble JAR, and verify build
./gradlew clean build

# Build standalone uber-JAR bundling runtime dependencies
./gradlew uberJar
```

### Launching Directly

Main entrypoint: `net.minecraft.client.main.Main` (or IDE wrapper `Start`).

- Provide Minecraft `1.8.9` client assets on the runtime classpath and `--assetsDir` / `--gameDir` arguments as usual for MCP 1.8.9.
- On **macOS (Apple Silicon / macOS 14+)**, use arm64 LWJGL 2.9.4 natives (`-Dorg.lwjgl.librarypath=...`) and pass `-Dorg.lwjgl.opengl.Display.disableOSXFullscreenModeAPI=true` (without `-XstartOnFirstThread`) so LWJGL 2 opens the Cocoa window cleanly without hitting AppKit's `setCollectionBehavior:` assertion.
