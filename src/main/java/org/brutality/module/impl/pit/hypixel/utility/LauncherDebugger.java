package org.brutality.module.impl.pit.hypixel.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.apache.logging.log4j.LogManager;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;

public final class LauncherDebugger extends Module {
    private static final String text = "Brutality/debug/launchers.log";
    private static final int count = 1200;
    private static final int count2 = 10;
    private static final double amount = 20.0;
    private static LauncherDebugger instance;
    private final BooleanSetting chatSetting = new BooleanSetting("Chat", this, true);
    private final BooleanSetting saveLogSetting = new BooleanSetting("Save Log", this, true);
    private final ArrayDeque<LauncherDebugger.CXEntry> arrayDeque = new ArrayDeque<>(10);
    private EntityPlayerSP entityPlayerSP;
    private LauncherDebugger.Entry entry;

    @Override
    public void process2() {
        this.entry = null;
        this.entityPlayerSP = null;
        this.arrayDeque.clear();
    }

    public static Vec3 computeVec3(Entity entity) {
        return new Vec3(entity.motionX, entity.motionY, entity.motionZ);
    }

    public static String computeString(Vec3 vec) {
        return String.format(Locale.ROOT, "(%.4f, %.4f, %.4f)", vec.xCoord, vec.yCoord, vec.zCoord);
    }

    private void handleEntry(LauncherDebugger.Entry entry, String str) {
        if (this.chatSetting.isEnabled() || this.saveLogSetting.isEnabled()) {
            EntityPlayerSP entityPlayerSP = entry.entityPlayerSP;
            Vec3 positionVector = entityPlayerSP.getPositionVector();
            String str2 = computeString(entityPlayerSP);
            double d = entry.computeDouble() / 1000.0;
            String str3 = entry.count > 1
                ? String.format(Locale.ROOT, "%.2f blocks/s peak horizontal at 20 TPS", entry.amount * 20.0)
                : "too few ticks to measure speed";
            String str4 = String.format(
                Locale.ROOT,
                "LauncherDebugger: %s ride from %s to %s. %s at %s after %.2fs (%d sampled ticks, %s).",
                entry.text,
                entry.text2,
                str2,
                str,
                computeString(positionVector),
                d,
                entry.count,
                str3
            );
            if (this.chatSetting.isEnabled()) {
                MessageUtils.x(str4);
            }

            if (this.saveLogSetting.isEnabled()) {
                this.handleEntry(entry, str4, positionVector);
            }
        }
    }

    public LauncherDebugger() {
        super("LauncherDebugger", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.chatSetting, this.saveLogSetting});
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventTick event2 && event2.getState() == EventState.POST) {
            if (!mc.isGamePaused()) {
                EntityPlayerSP player = mc.thePlayer;
                this.handleEntityPlayerSP(player, false);
                if (this.entry != null) {
                    this.entry.process();
                } else if (this.entityPlayerSP != null && player.ridingEntity == null && this.saveLogSetting.isEnabled()) {
                    if (this.arrayDeque.size() == 10) {
                        this.arrayDeque.removeFirst();
                    }

                    this.arrayDeque.addLast(LauncherDebugger.CXEntry.computeCXEntry(player));
                } else {
                    this.arrayDeque.clear();
                }
            }
        }
    }

    public static String computeString(EntityPlayerSP player) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(FreshHelper.computeString(player, player.worldObj));
    }

    public static void handleEntityPlayerSP(EntityPlayerSP player) {
        LauncherDebugger launcherDebugger = instance;
        if (launcherDebugger != null && launcherDebugger.checkState44()) {
            launcherDebugger.handleEntityPlayerSP(player, true);
        }
    }

    public static String computeString(EntityPlayerSP player, BlockPos pos) {
        Block block = player.worldObj.getBlockState(pos).getBlock();
        Object nameForObject = Block.blockRegistry.getNameForObject(block);
        int x = pos.getX();
        int z = pos.getZ();
        int y = pos.getY();
        int n = x;
        return nameForObject + " at " + n + ", " + y + ", " + z;
    }

    @Override
    public void onDisable() {
        this.process2();
        super.onDisable();
    }

    private void handleEntityPlayerSP(EntityPlayerSP entityPlayerSP, boolean flag) {
        if (entityPlayerSP != null && entityPlayerSP == mc.thePlayer && mc.theWorld != null && entityPlayerSP.worldObj == mc.theWorld) {
            if (this.entityPlayerSP != entityPlayerSP) {
                this.entry = null;
                this.arrayDeque.clear();
                this.entityPlayerSP = entityPlayerSP;
            }

            Entity ridingEntity = entityPlayerSP.ridingEntity;
            if (this.entry == null || this.entry.entity != ridingEntity) {
                if (this.entry != null) {
                    LauncherDebugger.Entry entry = this.entry;
                    this.entry = null;
                    this.handleEntry(entry, ridingEntity == null ? "dismounted" : "changed mount");
                }

                if (ridingEntity != null) {
                    if (ConnectToServerHandler.checkState2()) {
                        LauncherDebugger.CXEntry cxEntry = flag ? LauncherDebugger.CXEntry.computeCXEntry(entityPlayerSP) : null;
                        this.entry = new LauncherDebugger.Entry(entityPlayerSP, ridingEntity, cxEntry, flag ? List.copyOf(this.arrayDeque) : List.of());
                    }

                    this.arrayDeque.clear();
                }
            }
        } else {
            this.process2();
        }
    }

    private void handleEntry(LauncherDebugger.Entry entry, String str, Vec3 vec) {
        Vec3 vec2 = vec.subtract(entry.vec3);
        double deg = Math.toDegrees(Math.atan2(-vec2.xCoord, vec2.zCoord));
        String str2 = String.format(
            Locale.ROOT,
            "%n=== %s ===%n%s%nstart player=%s viewYaw=%.2f viewPitch=%.2f mount=%s mountMotion=%s blockBelow=%s%nend mount=%s mountMotion=%s playerOnGround=%s playerSneaking=%s%ndisplacement=%s travelYaw=%.2f peakHorizontal=%.4f peak3D=%.4f (blocks/tick)%n",
            entry.instant,
            str,
            computeString(entry.vec3),
            entry.factor,
            entry.factor2,
            computeString(entry.vec32),
            computeString(entry.vec33),
            entry.text3,
            computeString(entry.entity.getPositionVector()),
            computeString(computeVec3(entry.entity)),
            entry.entityPlayerSP.onGround,
            entry.entityPlayerSP.isSneaking(),
            computeString(vec2),
            deg,
            entry.amount,
            entry.amount2
        );
        String str3 = entry.count > 1200 ? entry.count - 1200 + " additional samples omitted from trace.\n" : "";

        try {
            Path path = mc.mcDataDir.toPath().resolve("Brutality/debug/launchers.log");
            Files.createDirectories(path.getParent());
            String str4 = "tick elapsedMs playerXYZ mountXYZ mountMotionXYZ mountDeltaXYZ horizontalSpeed 3DSpeed\nMotion, delta and speed columns use blocks per client tick.\n";
            Files.writeString(path, str2 + entry.computeString() + str4 + entry.stringBuilder + str3, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LogManager.getLogger(LauncherDebugger.class).warn("Could not save launcher trace", (Throwable)e);
            if (this.chatSetting.isEnabled()) {
                MessageUtils.x("LauncherDebugger could not write Brutality/debug/launchers.log");
            }
        }
    }

    public record CXEntry(long cX, Vec3 o00o, AxisAlignedBB Il1I, boolean fD) {
        public String computeString() {
            BlockPos pos = this.computeBlockPos();
            return String.format(
                Locale.ROOT,
                "feet=%s feetCell=(%d, %d, %d) hitboxMin=%s hitboxMax=%s onGround=%s",
                LauncherDebugger.computeString(this.o00o),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                LauncherDebugger.computeString(new Vec3(this.Il1I.minX, this.Il1I.minY, this.Il1I.minZ)),
                LauncherDebugger.computeString(new Vec3(this.Il1I.maxX, this.Il1I.maxY, this.Il1I.maxZ)),
                this.fD
            );
        }

        public BlockPos computeBlockPos() {
            return new BlockPos(this.o00o.xCoord, this.o00o.yCoord, this.o00o.zCoord);
        }

        public static LauncherDebugger.CXEntry computeCXEntry(EntityPlayerSP player) {
            return new LauncherDebugger.CXEntry(System.nanoTime(), player.getPositionVector(), player.getEntityBoundingBox(), player.onGround);
        }
    }

    public static final class Entry {
        public final EntityPlayerSP entityPlayerSP;
        public final Entity entity;
        public final String text;
        public final String text2;
        public final Vec3 vec3;
        public final float factor;
        public final float factor2;
        public final Vec3 vec32;
        public final Vec3 vec33;
        public final String text3;
        private final LauncherDebugger.CXEntry cxEntry;
        private final String text4;
        private final List<LauncherDebugger.CXEntry> entries;
        public final Instant instant = Instant.now();
        private final long timestamp = System.nanoTime();
        public final StringBuilder stringBuilder = new StringBuilder();
        private Vec3 vec34;
        public int count;
        public double amount;
        public double amount2;

        public void process() {
            Vec3 positionVector = this.entity.getPositionVector();
            this.count++;
            String str = "- - -";
            if (this.vec34 != null) {
                Vec3 vec = positionVector.subtract(this.vec34);
                double d = Math.hypot(vec.xCoord, vec.zCoord);
                double d2 = Math.hypot(d, vec.yCoord);
                this.amount = Math.max(this.amount, d);
                this.amount2 = Math.max(this.amount2, d2);
                if (this.count <= 1200) {
                    str = String.format(Locale.ROOT, "%s %.4f %.4f", LauncherDebugger.computeString(vec), d, d2);
                }
            }

            this.vec34 = positionVector;
            if (this.count <= 1200) {
                this.stringBuilder
                    .append(
                        String.format(
                            Locale.ROOT,
                            "%d %.2f %s %s %s %s%n",
                            this.count,
                            this.computeDouble(),
                            LauncherDebugger.computeString(this.entityPlayerSP.getPositionVector()),
                            LauncherDebugger.computeString(positionVector),
                            LauncherDebugger.computeString(LauncherDebugger.computeVec3(this.entity)),
                            str
                        )
                    );
            }
        }

        public Entry(EntityPlayerSP entityPlayerSP, Entity entity, LauncherDebugger.CXEntry cxEntry, List<LauncherDebugger.CXEntry> entries) {
            this.entityPlayerSP = entityPlayerSP;
            this.entity = entity;
            String entityString = EntityList.getEntityString(entity);
            this.text = entityString == null ? entity.getName() : entityString;
            this.text2 = LauncherDebugger.computeString(entityPlayerSP);
            this.vec3 = entityPlayerSP.getPositionVector();
            this.factor = entityPlayerSP.rotationYaw;
            this.factor2 = entityPlayerSP.rotationPitch;
            this.vec32 = entity.getPositionVector();
            this.vec33 = LauncherDebugger.computeVec3(entity);
            this.text3 = LauncherDebugger.computeString(entityPlayerSP, new BlockPos(entityPlayerSP.posX, entityPlayerSP.posY - 0.01, entityPlayerSP.posZ));
            this.cxEntry = cxEntry;
            this.entries = entries;
            this.text4 = cxEntry == null ? "" : LauncherDebugger.computeString(entityPlayerSP, cxEntry.computeBlockPos());
        }

        public String computeString() {
            if (this.cxEntry == null) {
                return "trigger candidate unavailable (already riding at first observation)\n";
            }
            StringBuilder sb = new StringBuilder("trigger candidate at client mount: ")
                .append(this.cxEntry.computeString())
                .append(" block=")
                .append(this.text4)
                .append('\n');

            for (LauncherDebugger.CXEntry cxEntry : this.entries) {
                double d = (this.cxEntry.cX() - cxEntry.cX()) / 1000000.0;
                sb.append(String.format(Locale.ROOT, "approach %.2f ms before mount: %s%n", d, cxEntry.computeString()));
            }

            return sb.toString();
        }

        public double computeDouble() {
            return (System.nanoTime() - this.timestamp) / 1000000.0;
        }
    }
}
