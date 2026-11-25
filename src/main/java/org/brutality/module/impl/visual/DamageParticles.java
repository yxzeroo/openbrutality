package org.brutality.module.impl.visual;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.GetEntityEvent2;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.opengl.GL11;

public class DamageParticles extends Module {
    private final Random random = new Random();
    private final HashMap<EntityLivingBase, Float> lookupMap = new HashMap<>();
    private final ArrayDeque<DamageParticles.Entry2> arrayDeque = new ArrayDeque<>();

    @Override
    public void process2() {
        this.arrayDeque.clear();
        this.lookupMap.clear();
    }

    public static double computeDouble(double d, int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(d).setScale(n, RoundingMode.HALF_UP).doubleValue();
    }

    private void onGetEntityEvent2(GetEntityEvent2 getEntityEvent2) {
        Entity entity = getEntityEvent2.getEntity();
        if (entity != null && entity instanceof EntityLivingBase) {
            if (entity != mc.thePlayer) {
                EntityLivingBase living = (EntityLivingBase)entity;
                if (!this.lookupMap.containsKey(living)) {
                    this.lookupMap.put(living, living.getHealth());
                } else {
                    float f = this.lookupMap.get(living);
                    float health = living.getHealth();
                    if (f != health) {
                        boolean flag = health > f;
                        boolean flag2 = living.hurtResistantTime < 18 || mc.thePlayer.motionY < 0.0 && !mc.thePlayer.onGround;
                        String str = flag ? "§a" : (flag2 ? "§c" : "§e");
                        String str2 = f - health < 0.0F ? str + computeDouble((f - health) * -1.0F, 1) : str + computeDouble(f - health, 1);
                        DamageParticles.Entry entry = new DamageParticles.Entry(living);
                        entry.setY(living.getEntityBoundingBox().minY + (living.getEntityBoundingBox().maxY - living.getEntityBoundingBox().minY) / 2.0);
                        entry.setX(entry.getX() - 0.5 + this.random.nextInt(5) * 0.1);
                        entry.setZ(entry.getZ() - 0.5 + this.random.nextInt(5) * 0.1);
                        this.arrayDeque.add(new DamageParticles.Entry2(entry, str2));
                        this.lookupMap.put(living, living.getHealth());
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (!(event instanceof RenderEvent4) && mc.theWorld != null && mc.thePlayer != null) {
                if (event instanceof GetEntityEvent2) {
                    this.onGetEntityEvent2((GetEntityEvent2)event);
                } else if (event instanceof EventRender3D) {
                    this.process();
                } else if (event instanceof EventUpdate) {
                    this.handleUpdate();
                }
            } else {
                this.onDisable();
            }
        }
    }

    public DamageParticles() {
        super("Damage Particles", 0, Category.VISUAL);
        this.process18();
    }

    @Override
    public void onDisable() {
        this.arrayDeque.clear();
        this.lookupMap.clear();
    }

    private void handleUpdate() {
        Iterator it = this.arrayDeque.iterator();

        while (it.hasNext()) {
            DamageParticles.Entry2 entry2 = (DamageParticles.Entry2)it.next();
            entry2.count++;
            if (entry2.count <= 10) {
                entry2.entry.setY(entry2.entry.getY() + entry2.count * 0.005);
            }

            if (entry2.count > 20) {
                it.remove();
            }
        }

        this.lookupMap
            .entrySet()
            .removeIf(
                entry -> {
                    EntityLivingBase key = entry.getKey();
                    return key == null
                        || key.isDead
                        || !key.isEntityAlive()
                        || key.worldObj != mc.theWorld
                        || mc.theWorld.getEntityByID(key.getEntityId()) != key;
                }
            );
    }

    private void process() {
        for (DamageParticles.Entry2 entry2 : this.arrayDeque) {
            double x = entry2.entry.getX();
            double d = x - mc.getRenderManager().renderPosX;
            double y = entry2.entry.getY();
            double d2 = y - mc.getRenderManager().renderPosY;
            double z = entry2.entry.getZ();
            double d3 = z - mc.getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
            GlStateManager.translate((float)d, (float)d2, (float)d3);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            float f = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
            GlStateManager.rotate(mc.getRenderManager().playerViewX, f, 0.0F, 0.0F);
            GlStateManager.scale(-0.03, -0.03, 0.03);
            GL11.glDepthMask(false);
            computeFontRenderer(DamageParticles.class)
                .drawStringWithShadow(
                    entry2.text,
                    -(computeFontRenderer(DamageParticles.class).getStringWidth(entry2.text) / 2),
                    -(computeFontRenderer(DamageParticles.class).FONT_HEIGHT - 1),
                    0
                );
            computeFontRenderer(DamageParticles.class)
                .drawStringWithShadow(
                    entry2.text,
                    -(computeFontRenderer(DamageParticles.class).getStringWidth(entry2.text) / 2),
                    -(computeFontRenderer(DamageParticles.class).FONT_HEIGHT - 1),
                    0
                );
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(true);
            GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static class Entry {
        private double x2;
        private double y2;
        private double z2;
        private float yaw2;
        private float pitch2;

        public double getX() {
            return this.x2;
        }

        public void setYaw(float yaw2) {
            this.yaw2 = yaw2;
        }

        public Entry(EntityLivingBase living) {
            this.x2 = living.posX;
            this.y2 = living.posY;
            this.z2 = living.posZ;
            this.yaw2 = 0.0F;
            this.pitch2 = 0.0F;
        }

        public void setPitch(float pitch2) {
            this.pitch2 = pitch2;
        }

        public void setY(double y2) {
            this.y2 = y2;
        }

        public double getZ() {
            return this.z2;
        }

        public float getPitch() {
            return this.pitch2;
        }

        public Entry(double x2, double y2, double z2, float yaw2, float pitch2) {
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.yaw2 = yaw2;
            this.pitch2 = pitch2;
        }

        public double getY() {
            return this.y2;
        }

        public float getYaw() {
            return this.yaw2;
        }

        public void setX(double x2) {
            this.x2 = x2;
        }

        public void setZ(double z2) {
            this.z2 = z2;
        }
    }

    public static class Entry2 {
        public int count;
        public DamageParticles.Entry entry;
        public String text;

        public Entry2(DamageParticles.Entry entry, String text) {
            this.entry = entry;
            this.text = text;
            this.count = 0;
        }
    }
}
