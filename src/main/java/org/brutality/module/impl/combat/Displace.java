package org.brutality.module.impl.combat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.Stasis;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.misc.ColorMathUtil;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.opengl.GL11;

public class Displace extends Module {
    public static Displace instance;
    public static NumberSetting numberSetting;
    public static NumberSetting numberSetting2;
    public static BooleanSetting booleanSetting;
    public static BooleanSetting booleanSetting2;
    public static BooleanSetting booleanSetting3;
    public static BooleanSetting booleanSetting4;
    public static BooleanSetting booleanSetting5;
    public static BooleanSetting booleanSetting6;
    public static NumberSetting numberSetting3;
    public static BooleanSetting booleanSetting7;
    public static BooleanSetting booleanSetting8;
    public static BooleanSetting booleanSetting9;
    public static BooleanSetting booleanSetting10;
    public static BooleanSetting booleanSetting11;
    public static BooleanSetting booleanSetting12;
    public static BooleanSetting booleanSetting13;
    public static BooleanSetting booleanSetting14;
    public static Displace.LeftMode leftMode = Displace.LeftMode.Left;
    public static long timestamp;
    public static boolean flag;
    private static int count;
    private static int count2;
    public static int count3 = -1;
    private static int count4 = -1;
    private static EntityLivingBase entityLivingBase;
    private static boolean flag2;
    private static long timestamp2;
    private static double amount;
    private static double amount2;
    private static double amount3;
    private static double amount4;
    private static double amount5;
    private static final Random random = new Random();
    public static NumberSetting numberSetting4;
    private final ModeSetting voidValueSetting = new ModeSetting("Mode", this, "Left", Arrays.asList("Left", "Right", "Random", "Void"));
    private boolean flag3;
    private EntityLivingBase entityLivingBase2;
    private Float floatValue;
    private float factor;
    private boolean flag4;
    private boolean flag5;
    private Float floatValue2;
    private Float floatValue3;
    private boolean flag6;
    private ColorMathUtil.Entry7 entry7;
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final Map<String, Long> lookupMap = new HashMap<>();
    private static Entity entity;
    private static World world;
    private static int count5 = -1;
    private static int count6;
    private static double amount6;
    private static double amount7;
    private static double amount8;
    private static double amount9;
    private static double amount10;
    private static double amount11;

    public String computeString() {
        return leftMode.name();
    }

    private boolean checkState5() {
        return !ColorMathUtil.Entry9.checkState6() && !Autoblockin.active && (Stasis.instance == null || !Stasis.instance.checkState44() || !Stasis.flag);
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living, float f, float f2, double d) {
        if (living != null && computeMinecraft().thePlayer != null) {
            float collisionBorderSize = living.getCollisionBorderSize();
            return OnJumpHelper.computeMovingObjectPosition(living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize), f, f2, d) != null;
        }
        return false;
    }

    public static boolean matchesEntity(Entity entity) {
        if (entity == null || !checkState4()) {
            return false;
        }
        if (!AutoHunt.matchesEntity(entity)) {
            return false;
        }
        if (booleanSetting != null && booleanSetting.isEnabled()) {
            VenomedTimer instance = VenomedTimer.getInstance();
            if (!(entity instanceof EntityPlayer) || instance == null || !instance.checkState44() || !instance.matchesString4(entity.getName())) {
                return false;
            }
        }

        double d = computeMinecraft().thePlayer.posX - entity.posX;
        double d2 = computeMinecraft().thePlayer.posZ - entity.posZ;
        float f = (float)Math.toDegrees(Math.atan2(d2, d)) - 90.0F;
        float f2 = Math.abs(MathHelper.wrapAngleTo180_float(entity.rotationYaw - f));
        if (booleanSetting2 != null && booleanSetting2.isEnabled() && f2 <= numberSetting3.computeDouble() / 2.0) {
            return false;
        }
        if (booleanSetting3 != null && booleanSetting3.isEnabled()) {
            double d3 = (entity.posX - entity.prevPosX) * -d + (entity.posZ - entity.prevPosZ) * -d2;
            if (f2 < 90.0F || d3 <= 0.01 * Math.max(0.01, Math.sqrt(d * d + d2 * d2))) {
                return false;
            }
        }

        return true;
    }

    public void process7() {
        if (this.checkState44()) {
            this.flag6 = false;
            if (!checkState4()) {
                this.process8();
            } else {
                if (count2 > 0 && --count2 == 0) {
                    this.entry7.close();
                }

                if (count > 0) {
                    flag = --count > 0;
                }
            }
        }
    }

    public void handleEntityLivingBase(EntityLivingBase entityLivingBase2, float[] floats) {
        if (this.checkState44()
            && booleanSetting14.isEnabled()
            && !this.flag3
            && this.entityLivingBase2 == null
            && entityLivingBase2 != null
            && floats != null
            && this.checkState5()) {
            if (KillAura.instance != null && matchesEntityLivingBase(entityLivingBase2, KillAura.instance.computeDouble2())) {
                this.process5();
                if (matchesEntity(entityLivingBase2, true, true)) {
                    timestamp = System.currentTimeMillis();
                    double d = KillAura.instance.computeDouble2();
                    if (!matchesEntityLivingBase(entityLivingBase2, floats[0], floats[1], d)) {
                        this.handleString("skip", entityLivingBase2, "aim/range check failed before the displaced look", d);
                        clear();
                    } else {
                        this.entityLivingBase2 = entityLivingBase2;
                        this.flag4 = true;
                        this.floatValue = OnJumpHelper.computeFloat4(floats[0] + computeFloat(), computeMinecraft().thePlayer.getLastReportedRotations()[0]);
                        this.factor = floats[1];
                        gl();
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2() && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
        if (this.checkState44() && true && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
            if (!checkState4()) {
                this.process8();
            } else {
                this.process5();
                if (event instanceof BasicEvent2 && !this.flag3 && !event.cancel2()) {
                    if (computeMinecraft().objectMouseOver != null
                        && computeMinecraft().objectMouseOver.typeOfHit == MovingObjectType.ENTITY
                        && booleanSetting14.isEnabled()
                        && KillAura.instance != null
                        && KillAura.instance.checkState4()) {
                        event.cancel();
                        return;
                    }

                    if (computeMinecraft().objectMouseOver != null
                        && computeMinecraft().objectMouseOver.typeOfHit == MovingObjectType.ENTITY
                        && this.matchesEntity(computeMinecraft().objectMouseOver.entityHit, false)) {
                        event.cancel();
                    }
                } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    if (this.floatValue != null) {
                        return;
                    }

                    if (this.flag5) {
                        if (this.flag4) {
                            this.handleString(
                                "skip",
                                this.entityLivingBase2,
                                "Aura did not service the follow-up (paused, target lost or blocked)",
                                KillAura.instance == null ? 3.0 : KillAura.instance.computeDouble2()
                            );
                            this.process6();
                        } else {
                            this.checkState();
                        }

                        return;
                    }

                    if (this.entityLivingBase2 != null) {
                        float[] floats = this.flag4 && KillAura.instance.floatValueArray5 != null
                            ? KillAura.instance.floatValueArray5
                            : new float[]{computeMinecraft().thePlayer.rotationYaw, computeMinecraft().thePlayer.rotationPitch};
                        double d = this.flag4 ? KillAura.instance.computeDouble2() : 3.0;
                        if (!this.checkState5()
                            || this.flag4 && KillAura.entityLivingBase2 != this.entityLivingBase2
                            || this.entityLivingBase2.isDead
                            || !matchesEntity(this.entityLivingBase2, true, true)
                            || !matchesEntityLivingBase(this.entityLivingBase2, floats[0], floats[1], d)) {
                            this.handleString("skip", this.entityLivingBase2, "eligibility/aim changed before the displaced look", d);
                            this.process6();
                            return;
                        }

                        this.floatValue = OnJumpHelper.computeFloat4(floats[0] + computeFloat(), computeMinecraft().thePlayer.getLastReportedRotations()[0]);
                        this.factor = floats[1];
                        gl();
                        timestamp = System.currentTimeMillis();
                    }
                } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && this.floatValue != null) {
                    ((EventPreMotion)event).setYaw(this.floatValue);
                    ((EventPreMotion)event).setPitch(this.factor);
                } else if (event instanceof EventPostMotion) {
                    if (this.floatValue != null && this.entityLivingBase2 != null) {
                        EventPostMotion event2 = (EventPostMotion)event;
                        if (Math.abs(MathHelper.wrapAngleTo180_float(event2.getYaw() - this.floatValue)) < 0.001F
                            && Math.abs(event2.getPitch() - this.factor) < 0.001F
                            && this.checkState5()) {
                            this.floatValue2 = event2.getYaw();
                            this.flag5 = true;
                        } else {
                            this.handleString("skip", this.entityLivingBase2, "another rotation replaced the displaced look", 3.0);
                            this.process6();
                        }
                    }

                    this.floatValue = null;
                } else if (event instanceof EventRender3D) {
                    this.onEventRender3D((EventRender3D)event);
                }
            }
        }
    }

    public boolean matchesEntityLivingBase2(EntityLivingBase living) {
        if (this.flag3) {
            return true;
        }
        return living == KillAura.entityLivingBase2
            ? !this.matchesEntity(living, true)
            : !booleanSetting14.isEnabled() || !booleanSetting9.isEnabled() || !matchesEntity(living, false, false);
    }

    public boolean checkState2() {
        return !this.flag3 && (flag || this.entityLivingBase2 != null || this.flag6);
    }

    public boolean checkState8() {
        return this.flag5 && this.flag4 && this.checkState();
    }

    public boolean matchesEntityLivingBase(EntityLivingBase living) {
        return this.checkState44() && this.flag4 && this.flag5 && this.entityLivingBase2 == living;
    }

    private boolean matchesEntity(Entity entity, boolean flag2) {
        this.process5();
        if (flag2 ? booleanSetting14.isEnabled() : booleanSetting12.isEnabled() && (!ColorMathUtil.Entry9.checkState4() || booleanSetting13.isEnabled())) {
            double d = flag2 && KillAura.instance != null ? KillAura.instance.computeDouble2() : 3.0;
            if (!(entity instanceof EntityLivingBase) || !matchesEntityLivingBase((EntityLivingBase)entity, d)) {
                return false;
            }
            if (this.entityLivingBase2 == null && (!flag2 || !flag)) {
                if (matchesEntity(entity, true, true)) {
                    this.entityLivingBase2 = (EntityLivingBase)entity;
                    this.flag4 = flag2;
                    return true;
                }
                return booleanSetting9.isEnabled() && matchesEntity(entity, false, false);
            }
            return true;
        }
        return false;
    }

    public void link() {
        numberSetting = new NumberSetting("Angle", this, 100.0, 10.0, 180.0, 5.0);
        numberSetting2 = new NumberSetting("Cooldown", this, 0.5, 0.2, 8.0, 0.1);
        booleanSetting7 = new BooleanSetting("Hide rotations", this, false);
        booleanSetting8 = new BooleanSetting("Only knockback items", this, true);
        booleanSetting9 = new BooleanSetting("Override attacks", this, true);
        booleanSetting10 = new BooleanSetting("Players only", this, false);
        booleanSetting11 = new BooleanSetting("Render arrow", this, true);
        booleanSetting12 = new BooleanSetting("Manual", this, true);
        booleanSetting13 = new BooleanSetting("AutoClicker", this, true);
        booleanSetting14 = new BooleanSetting("KillAura", this, true);
        booleanSetting4 = new BooleanSetting("Stop On Hurt", this, false);
        booleanSetting5 = new BooleanSetting("Only Darks", this, false);
        booleanSetting6 = new BooleanSetting("Disable In PIT", this, false);
        booleanSetting = new BooleanSetting("Only When Venomed", this, false);
        booleanSetting2 = new BooleanSetting("Only Displace If Not Looking", this, false);
        numberSetting3 = new NumberSetting("Looking Angle", this, 90.0, 1.0, 360.0, 5.0).computeNumberSetting(() -> booleanSetting2.isEnabled());
        booleanSetting3 = new BooleanSetting("Only If Turned Around", this, false);
    }

    @Override
    public void onDisable() {
        process3();
        if (count2 > 0) {
            count2 = 0;
            instance.entry7.close();
        }

        this.process6();
        this.lookupMap.clear();
        timestamp = 0L;
        this.flag6 = false;
        flag = false;
        flag2 = false;
        count = 0;
        count3 = -1;
        count4 = -1;
    }

    public void onEventRender3D(EventRender3D event) {
        Minecraft mc = computeMinecraft();
        if (flag2 && booleanSetting11.isEnabled()) {
            if (ColorMathUtil.Entry2.checkState()) {
                long l = System.currentTimeMillis() - timestamp2;
                if (l > 600L) {
                    flag2 = false;
                } else {
                    double d = 1.0;
                    double d2 = l / 100.0;
                    d2 = d2 < 0.0 ? 0.0 : Math.min(d2, 1.0);
                    double d3 = d2 * d2 * (3.0 - 2.0 * d2);
                    double d4 = d * (0.08 + 0.92 * d3);
                    if (l > 500L) {
                        d2 = (l - 500L) / 100.0;
                        d2 = d2 < 0.0 ? 0.0 : Math.min(d2, 1.0);
                        d3 = d2 * d2 * (3.0 - 2.0 * d2);
                        d4 *= 1.0 - 0.92 * d3;
                    }

                    d2 = l / 450.0;
                    d2 = d2 < 0.0 ? 0.0 : Math.min(d2, 1.0);
                    d3 = 1.0 - d2;
                    d2 = 1.0 - d3 * d3 * d3;
                    d3 = Math.sin(l * 0.015) * 0.02 * (0.4 + 0.6 * d4);
                    double d5 = 0.8 * d2;
                    double viewerPosX = mc.getRenderManager().viewerPosX;
                    double viewerPosY = mc.getRenderManager().viewerPosY;
                    double viewerPosZ = mc.getRenderManager().viewerPosZ;
                    double d6 = amount - viewerPosX + amount4 * d5;
                    double d7 = amount2 - viewerPosY + d3;
                    double d8 = amount3 - viewerPosZ + amount5 * d5;
                    double deg = Math.toDegrees(Math.atan2(-amount5, amount4));
                    int n = 16777215 + ((int)(225.0 * d4) << 24 & ~16777215);
                    int n2 = 16185078 + ((int)(255.0 * d4) << 24 & ~16185078);
                    int n3 = (int)(140.0 * d4) << 24 | 329754;
                    GlStateManager.pushMatrix();
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableCull();
                    GlStateManager.depthMask(false);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GlStateManager.translate(d6, d7, d8);
                    GlStateManager.rotate((float)deg, 0.0F, 1.0F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0384, -0.0192, -0.02);
                    handleInt(n3);
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glVertex3d(-0.281184, -0.082416, 0.0);
                    GL11.glVertex3d(0.0, -0.029088, 0.0);
                    GL11.glVertex3d(0.0, 0.029088, 0.0);
                    GL11.glVertex3d(-0.281184, 0.082416, 0.0);
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_TRIANGLES);
                    GL11.glVertex3d(0.0, -0.14543999999999999, 0.0);
                    GL11.glVertex3d(0.29087999999999997, 0.0, 0.0);
                    GL11.glVertex3d(0.0, 0.14543999999999999, 0.0);
                    GL11.glEnd();
                    GL11.glPopMatrix();
                    handleInt(n);
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glVertex3d(-0.2784, -0.0816, 0.0);
                    GL11.glVertex3d(0.0, -0.0288, 0.0);
                    GL11.glVertex3d(0.0, 0.0288, 0.0);
                    GL11.glVertex3d(-0.2784, 0.0816, 0.0);
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_TRIANGLES);
                    GL11.glVertex3d(0.0, -0.144, 0.0);
                    GL11.glVertex3d(0.288, 0.0, 0.0);
                    GL11.glVertex3d(0.0, 0.144, 0.0);
                    GL11.glEnd();
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glLineWidth(2.0F);
                    handleInt(n2);
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    GL11.glVertex3d(-0.2784, -0.0816, 0.0);
                    GL11.glVertex3d(0.0, -0.0288, 0.0);
                    GL11.glVertex3d(0.0, 0.0288, 0.0);
                    GL11.glVertex3d(-0.2784, 0.0816, 0.0);
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    GL11.glVertex3d(0.0, -0.144, 0.0);
                    GL11.glVertex3d(0.288, 0.0, 0.0);
                    GL11.glVertex3d(0.0, 0.144, 0.0);
                    GL11.glEnd();
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.enableTexture2D();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public Float computeFloatType() {
        return this.checkState6() ? this.floatValue : null;
    }

    private static void handleInt(int n) {
        GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, (n >> 24 & 0xFF) / 255.0F);
    }

    public Displace() {
        super("Displace", 0, Category.COMBAT);
        instance = this;
        this.link();
        numberSetting4 = new NumberSetting("Hittable hurt time", this, 10.0, 0.0, 10.0, 1.0);
        this.entry7 = new ColorMathUtil.Entry7(this);
        this.handleSettingArray(
            new Setting[]{
                numberSetting4,
                this.voidValueSetting,
                numberSetting,
                numberSetting2,
                booleanSetting7,
                booleanSetting8,
                booleanSetting9,
                booleanSetting10,
                booleanSetting11,
                booleanSetting12,
                booleanSetting13,
                booleanSetting14,
                booleanSetting4,
                booleanSetting5,
                booleanSetting,
                booleanSetting2,
                numberSetting3,
                booleanSetting3,
                booleanSetting6,
                this.debugSetting
            }
        );
    }

    private void handleString(String str, EntityLivingBase living, String str2, double d, Displace.Entry entry) {
        if (this.debugSetting.isEnabled() && computeMinecraft().thePlayer != null && entry != null) {
            long now = System.currentTimeMillis();
            String str3 = str + str2;
            Long val = this.lookupMap.get(str3);
            if (val == null || now - val >= 1500L) {
                if (this.lookupMap.size() > 24) {
                    this.lookupMap.clear();
                }

                this.lookupMap.put(str3, now);
                GetCurrentScreenHelper.handleString3(
                    String.format(
                        Locale.ROOT,
                        "§d[Displace] §f%s: %s | id=%d distance=%.3f range=%.2f sprintBefore=%s sprintAfter=%s lastSprintRequest=%s kbBefore=%d hurtBefore=%d limit=%.0f",
                        str,
                        str2,
                        entry.count3,
                        entry.amount,
                        d,
                        entry.flag,
                        computeMinecraft().thePlayer.isSprinting(),
                        entry.flag2,
                        entry.count,
                        entry.count2,
                        numberSetting4.computeDouble()
                    )
                );
            }
        }
    }

    private static boolean checkState4() {
        EntityPlayerSP player = computeMinecraft().thePlayer;
        if (player == null) {
            return false;
        }
        if (booleanSetting4 != null && booleanSetting4.isEnabled() && player.hurtTime > 0) {
            return false;
        }
        return booleanSetting5 != null && booleanSetting5.isEnabled() && !KeepSprint.matchesEntityPlayer(player)
            ? false
            : booleanSetting6 == null || !booleanSetting6.isEnabled() || !FreshHelper.matchesWorld4(computeMinecraft().theWorld, player);
    }

    private void handleString(String str, EntityLivingBase living, String str2, double d) {
        if (this.debugSetting.isEnabled() && computeMinecraft().thePlayer != null) {
            this.handleString(str, living, str2, d, new Displace.Entry(living));
        }
    }

    private void process8() {
        int n = count2;
        count2 = 0;
        flag = false;
        count = 0;
        flag2 = false;
        this.flag6 = false;
        this.process6();
        if (n > 0 && this.entry7 != null) {
            this.entry7.close();
        }
    }

    public Float computeFloatType2() {
        return this.flag3 ? this.floatValue3 : null;
    }

    public static Minecraft computeMinecraft() {
        return Minecraft.getMinecraft();
    }

    public boolean checkState6() {
        return this.checkState44() && this.floatValue != null;
    }

    public float getFactor() {
        return this.factor;
    }

    public static void gl() {
        if (booleanSetting7.isEnabled()) {
            count2 = 2;
            instance.entry7.open();
        }
    }

    public static void process() {
        count = 3;
    }

    private static void process3() {
        entity = null;
        world = null;
        count5 = -1;
    }

    public void process5() {
        leftMode = Displace.LeftMode.valueOf(this.voidValueSetting.getText());
    }

    public void process4() {
        this.onDisable();
    }

    public static boolean matchesEntity(Entity entity, boolean flag, boolean flag2) {
        Minecraft mc = computeMinecraft();
        if (!(entity instanceof EntityLivingBase)) {
            return false;
        }
        if (!matchesEntity(entity)) {
            return false;
        }
        if (flag && ((EntityLivingBase)entity).hurtTime > numberSetting4.computeDouble()) {
            if (instance != null) {
                instance.handleString("skip", (EntityLivingBase)entity, "hurt time exceeds Hittable hurt time", 3.0);
            }

            return false;
        }
        if (booleanSetting10.isEnabled() && !(entity instanceof EntityPlayer)) {
            return false;
        }
        int knockbackModifier = EnchantmentHelper.getKnockbackModifier(mc.thePlayer);
        if (!mc.thePlayer.isSprinting() && knockbackModifier == 0) {
            if (instance != null) {
                instance.handleString("skip", (EntityLivingBase)entity, "no sprint state or Knockback enchantment", 3.0);
            }

            return false;
        }
        if (booleanSetting8.isEnabled() && knockbackModifier == 0) {
            if (instance != null) {
                instance.handleString("skip", (EntityLivingBase)entity, "Only knockback items requires the Knockback enchantment", 3.0);
            }

            return false;
        }
        if (flag2 && Math.abs(System.currentTimeMillis() - timestamp) < numberSetting2.computeDouble() * 1000.0 - 20.0) {
            if (instance != null) {
                instance.handleString("skip", (EntityLivingBase)entity, "waiting for cooldown", 3.0);
            }

            return false;
        }
        if (leftMode == Displace.LeftMode.Void && (count4 = count3 = computeInt(entity, count3)) == -1) {
            return false;
        }
        entityLivingBase = (EntityLivingBase)entity;
        return true;
    }

    private void process6() {
        this.entityLivingBase2 = null;
        this.flag4 = false;
        this.floatValue = this.floatValue2 = this.floatValue3 = null;
        this.flag5 = this.flag3 = false;
    }

    private static void handleEntityLivingBase(EntityLivingBase living, float f) {
        double rad = Math.toRadians(computeMinecraft().thePlayer.rotationYaw + f);
        amount4 = -Math.sin(rad);
        amount5 = Math.cos(rad);
        amount = living.posX + amount4 * 0.8;
        amount2 = living.posY + 1.2;
        amount3 = living.posZ + amount5 * 0.8;
        timestamp2 = System.currentTimeMillis();
        flag2 = true;
    }

    @Override
    public void process2() {
        process3();
        if (this.entry7 != null) {
            this.entry7.process();
        }

        count2 = 0;
        flag = false;
        count = 0;
        this.flag6 = false;
        this.process6();
        this.lookupMap.clear();
        flag2 = false;
        count4 = -1;
        count3 = -1;
        timestamp = 0L;
    }

    public static void clear() {
        timestamp = 0L;
    }

    public static float computeFloat() {
        float f = (float)numberSetting.computeDouble();
        if (count4 != -1) {
            if (count4 == 0) {
                f = -f;
            }

            count4 = -1;
        } else if (leftMode == Displace.LeftMode.Left || leftMode == Displace.LeftMode.Random && random.nextBoolean()) {
            f = -f;
        }

        float f2 = f + ColorMathUtil.Entry5.computeFloat(-5.0F, 5.0F);
        if (booleanSetting11.isEnabled() && entityLivingBase != null) {
            handleEntityLivingBase(entityLivingBase, f2);
        }

        return f2;
    }

    private static int computeInt(Entity entity2, int n) {
        Minecraft mc = computeMinecraft();
        if (entity != entity2
            || world != mc.theWorld
            || count5 != mc.thePlayer.ticksExisted
            || amount6 != entity2.posX
            || amount7 != entity2.posY
            || amount8 != entity2.posZ
            || amount9 != mc.thePlayer.posX
            || amount10 != mc.thePlayer.posY
            || amount11 != mc.thePlayer.posZ) {
            entity = entity2;
            world = mc.theWorld;
            count5 = mc.thePlayer.ticksExisted;
            amount6 = entity2.posX;
            amount7 = entity2.posY;
            amount8 = entity2.posZ;
            amount9 = mc.thePlayer.posX;
            amount10 = mc.thePlayer.posY;
            amount11 = mc.thePlayer.posZ;
            count6 = ColorMathUtil.Entry12.computeInt(entity2, n);
        }

        return count6;
    }

    private boolean checkState() {
        EntityLivingBase entityLivingBase2 = this.entityLivingBase2;
        boolean flag = this.flag4;
        Float floatValue2 = this.floatValue2;
        float[] floats = flag && KillAura.instance != null && KillAura.instance.floatValueArray5 != null
            ? KillAura.instance.floatValueArray5
            : new float[]{computeMinecraft().thePlayer.rotationYaw, computeMinecraft().thePlayer.rotationPitch};
        double d = flag && KillAura.instance != null ? KillAura.instance.computeDouble2() : 3.0;
        String str = entityLivingBase2 == null || entityLivingBase2.isDead
            ? "target lost/dead"
            : (
                floatValue2 == null
                    ? "displaced look was not committed"
                    : (
                        !this.checkState5()
                            ? "another module owns rotations"
                            : (
                                !matchesEntity(entityLivingBase2)
                                    ? "target filter changed"
                                    : (
                                        !flag || KillAura.instance != null && KillAura.instance.matchesEntityLivingBase4(entityLivingBase2)
                                            ? (
                                                !matchesEntityLivingBase(entityLivingBase2, floats[0], floats[1], d)
                                                    ? "target moved out of reach or off the fresh attack ray"
                                                    : null
                                            )
                                            : "Aura follow-up guard rejected (range, block, input or attack state)"
                                    )
                            )
                    )
            );
        this.process6();
        if (str != null) {
            clear();
            this.handleString("skip", entityLivingBase2, str, d);
            return false;
        }
        Displace.Entry entry = this.debugSetting.isEnabled() ? new Displace.Entry(entityLivingBase2) : null;
        this.flag3 = true;
        this.floatValue3 = floatValue2;

        boolean flag2;
        try {
            boolean flag3;
            if (flag) {
                flag3 = KillAura.instance.matchesEntityLivingBase2(entityLivingBase2, false);
            } else {
                computeMinecraft().thePlayer.swingItem();
                computeMinecraft().playerController.attackEntity(computeMinecraft().thePlayer, entityLivingBase2);
                flag3 = true;
            }

            if (flag3) {
                process();
                this.flag6 = flag;
            }

            this.handleString(
                flag3 ? "attempt" : "skip",
                entityLivingBase2,
                flag3 ? "attack path completed; server hit/knockback unconfirmed" : "Aura final attack guard rejected",
                d,
                entry
            );
            flag2 = flag3;
        } finally {
            this.floatValue3 = null;
            this.flag3 = false;
        }

        return flag2;
    }

    private static boolean matchesEntityLivingBase(EntityLivingBase living, double d) {
        return OnJumpHelper.computeFloat(living) <= d;
    }

    public static final class Entry {
        public final boolean flag = Minecraft.getMinecraft().thePlayer.isSprinting();
        public final boolean flag2 = Minecraft.getMinecraft().thePlayer.serverSprintState;
        public final int count = EnchantmentHelper.getKnockbackModifier(Minecraft.getMinecraft().thePlayer);
        public final int count2;
        public final int count3;
        public final double amount;

        public Entry(EntityLivingBase living) {
            this.count2 = living == null ? -1 : living.hurtTime;
            this.count3 = living == null ? -1 : living.getEntityId();
            this.amount = living == null ? Double.NaN : OnJumpHelper.computeFloat(living);
        }
    }

    public static enum LeftMode {
        Left,
        Right,
        Random,
        Void;

        private static Displace.LeftMode[] computeLeftModeArray() {
            return new Displace.LeftMode[]{Left, Right, Random, Void};
        }
    }
}
