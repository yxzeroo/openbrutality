package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.lwjgl.opengl.GL11;

public class PitNPC extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Vec3[] vec3Array = new Vec3[]{
        new Vec3(36.0, 76.0, -70.0),
        new Vec3(-104.0, 83.0, -52.0),
        new Vec3(-108.5, 83.0, -54.5),
        new Vec3(-58.0, 82.0, 0.0),
        new Vec3(70.0, 66.0, 25.0),
        new Vec3(-50.0, 76.0, 35.0),
        new Vec3(-15.0, 67.0, 95.0),
        new Vec3(-68.0, 87.0, 80.0)
    };
    private static final String[] stringArray = new String[]{"Farms", "Forest", "Forest", "Forest", "City", "Port", "Port", "Port"};
    private static final PitNPC.Entry[] entryArray = new PitNPC.Entry[]{
        new PitNPC.Entry("Items", new Vec3(-13.0, 115.0, -7.0), "Spawn South", null),
        new PitNPC.Entry("Upgrades", new Vec3(-13.0, 115.0, 7.0), "Spawn South", null),
        new PitNPC.Entry("Quest Master", new Vec3(7.0, 115.0, -13.0), "Spawn East", null),
        new PitNPC.Entry("Stats", new Vec3(7.0, 115.0, 13.0), "Spawn East", null),
        new PitNPC.Entry("Prestige", new Vec3(13.0, 115.0, 0.0), "Spawn North", null),
        new PitNPC.Entry("The Keeper", new Vec3(-7.0, 115.0, 0.0), "Spawn West", null),
        new PitNPC.Entry("Leaderboard Picker", new Vec3(0.0, 115.0, -7.0), "Spawn East", null),
        new PitNPC.Entry("The King", new Vec3(-110.0, 79.0, -6.0), "Port", "kings"),
        new PitNPC.Entry("Hay", new Vec3(50.0, 68.0, 91.0), "City", "kings"),
        new PitNPC.Entry("Bread", new Vec3(89.0, 66.0, 56.0), "City", "kings"),
        new PitNPC.Entry("Fish", new Vec3(33.0, 66.0, 61.0), "City", "kings"),
        new PitNPC.Entry("Angel", new Vec3(-12.0, 86.0, 18.0), "Spawn South", "genesis"),
        new PitNPC.Entry("Demon", new Vec3(12.0, 86.0, -17.0), "Spawn North", "genesis")
    };
    private ColorSetting npcColorSetting = new ColorSetting("NPC Color", this, 0.33333334F);
    private KeybindListSetting spawnNPCsSetting;
    private BooleanSetting itemsSetting;
    private BooleanSetting upgradesSetting;
    private BooleanSetting questMasterSetting;
    private BooleanSetting statsSetting;
    private BooleanSetting prestigeSetting;
    private BooleanSetting theKeeperSetting;
    private BooleanSetting leaderboardPickerSetting;
    private KeybindListSetting kingsMapNPCsSetting;
    private BooleanSetting theKingSetting;
    private BooleanSetting bakerSetting;
    private BooleanSetting haySalesmanSetting;
    private BooleanSetting breadSalesmanSetting;
    private BooleanSetting fishSalesmanSetting;
    private KeybindListSetting genesisMapNPCsSetting;
    private BooleanSetting angelSetting;
    private BooleanSetting demonSetting;
    private Vec3 vec3 = null;
    private int count = -1;
    private long timestamp = 0L;
    private static final long timestamp2 = 2000L;
    private String text = "";
    private boolean flag = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final File file = new File("Brutality/Data");
    private static final File file2 = new File(file, "baker_location.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private void handleVec32(Vec3 vec, int n) {
        if (!this.flag) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), 1.0F));
            this.flag = true;
        }

        String str = stringArray[n];
        String str2 = MessageUtils.computeString("Baker found in ", MessageUtils.text11)
            + MessageUtils.computeString(str, MessageUtils.text15)
            + MessageUtils.computeString(" at coords ", MessageUtils.text11)
            + MessageUtils.computeString(String.format("(%.1f %.1f %.1f)", vec.xCoord, vec.yCoord, vec.zCoord), MessageUtils.text12);
        MessageUtils.x(str2);
    }

    private void onEventRender3D(EventRender3D event) {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            if (FreshHelper.flag) {
                String str = FreshHelper.computeString(this.mc.theWorld);
                Color color = this.npcColorSetting.computeColor();

                for (PitNPC.Entry entry : entryArray) {
                    if (this.matchesEntry(entry, str)) {
                        Vec3 vec = this.computeVec3(entry);
                        this.handleVec3(vec != null ? vec : entry.position, color);
                    }
                }

                if (this.bakerSetting.isEnabled() && "kings".equals(str) && this.vec3 != null) {
                    this.handleVec3(this.vec3, color);
                }
            }
        }
    }

    private String computeString(String str) {
        return str == null
            ? ""
            : EnumChatFormatting.getTextWithoutFormattingCodes(str).toLowerCase(Locale.ROOT).replace("npc", "").replaceAll("[^a-z0-9]", "");
    }

    private void process() {
        if (file2.exists()) {
            file2.delete();
        }
    }

    private void handleVec3(Vec3 vec, Color color) {
        double d = vec.xCoord - this.mc.getRenderManager().viewerPosX;
        double d2 = vec.yCoord - this.mc.getRenderManager().viewerPosY;
        double d3 = vec.zCoord - this.mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.handleDouble(d, d2, d3, color);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void init() {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.onUpdate();
            } else if (event instanceof EventRender3D) {
                this.onEventRender3D((EventRender3D)event);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            FreshHelper.process();
            if (FreshHelper.flag) {
                String str = FreshHelper.computeString(this.mc.theWorld);
                boolean flag = this.text == null && str != null || this.text != null && !this.text.equals(str);
                if (flag) {
                    if ("kings".equals(this.text)) {
                        this.vec3 = null;
                        this.count = -1;
                        this.flag = false;
                        this.process();
                    }

                    this.text = str != null ? str : "";
                }

                if ("kings".equals(str) && this.bakerSetting.isEnabled()) {
                    long now = System.currentTimeMillis();
                    if (now - this.timestamp > 2000L) {
                        this.timestamp = now;
                        this.process4();
                    }
                }
            }
        }
    }

    private Vec3 computeVec3(PitNPC.Entry entry) {
        String str = this.computeString(entry.name);
        double d = 24.0;
        Vec3 vec = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity.hasCustomName()) {
                String str2 = this.computeString(entity.getCustomNameTag());
                if (str2.contains(str)) {
                    double dist = entity.getDistance(entry.position.xCoord, entry.position.yCoord, entry.position.zCoord);
                    if (dist <= d && dist < MAX_VALUE) {
                        MAX_VALUE = dist;
                        vec = new Vec3(entity.posX, entity.posY, entity.posZ);
                    }
                }
            }
        }

        return vec;
    }

    private boolean matchesEntry(PitNPC.Entry entry, String str) {
        if (entry.text2 != null && !entry.text2.equals(str)) {
            return false;
        }
        String name = entry.name;
        switch (name) {
            case "Items":
                return this.itemsSetting.isEnabled();
            case "Upgrades":
                return this.upgradesSetting.isEnabled();
            case "Quest Master":
                return this.questMasterSetting.isEnabled();
            case "Stats":
                return this.statsSetting.isEnabled();
            case "Prestige":
                return this.prestigeSetting.isEnabled();
            case "The Keeper":
                return this.theKeeperSetting.isEnabled();
            case "Leaderboard Picker":
                return this.leaderboardPickerSetting.isEnabled();
            case "The King":
                return this.theKingSetting.isEnabled();
            case "Hay":
                return this.haySalesmanSetting.isEnabled();
            case "Bread":
                return this.breadSalesmanSetting.isEnabled();
            case "Fish":
                return this.fishSalesmanSetting.isEnabled();
            case "Angel":
                return this.angelSetting.isEnabled();
            case "Demon":
                return this.demonSetting.isEnabled();
            default:
                return false;
        }
    }

    public PitNPC() {
        super("PitNPC", 0, Category.PIT);
        this.itemsSetting = new BooleanSetting("Items", this, false);
        this.upgradesSetting = new BooleanSetting("Upgrades", this, false);
        this.questMasterSetting = new BooleanSetting("Quest Master", this, false);
        this.statsSetting = new BooleanSetting("Stats", this, false);
        this.prestigeSetting = new BooleanSetting("Prestige", this, false);
        this.theKeeperSetting = new BooleanSetting("The Keeper", this, false);
        this.leaderboardPickerSetting = new BooleanSetting("Leaderboard Picker", this, false);
        this.spawnNPCsSetting = new KeybindListSetting(
            "Spawn NPCs",
            this,
            this.itemsSetting,
            this.upgradesSetting,
            this.questMasterSetting,
            this.statsSetting,
            this.prestigeSetting,
            this.theKeeperSetting,
            this.leaderboardPickerSetting
        );
        this.theKingSetting = new BooleanSetting("The King", this, false);
        this.bakerSetting = new BooleanSetting("Baker", this, true);
        this.haySalesmanSetting = new BooleanSetting("Hay (Salesman)", this, false);
        this.breadSalesmanSetting = new BooleanSetting("Bread (Salesman)", this, false);
        this.fishSalesmanSetting = new BooleanSetting("Fish (Salesman)", this, false);
        this.kingsMapNPCsSetting = new KeybindListSetting(
            "Kings Map NPCs", this, this.theKingSetting, this.bakerSetting, this.haySalesmanSetting, this.breadSalesmanSetting, this.fishSalesmanSetting
        );
        this.angelSetting = new BooleanSetting("Angel", this, false);
        this.demonSetting = new BooleanSetting("Demon", this, false);
        this.genesisMapNPCsSetting = new KeybindListSetting("Genesis Map NPCs", this, this.angelSetting, this.demonSetting);
        this.handleSettingArray(new Setting[]{this.npcColorSetting, this.spawnNPCsSetting, this.kingsMapNPCsSetting, this.genesisMapNPCsSetting});
        this.init();
    }

    private void handleVec3(Vec3 vec, int n) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("x", vec.xCoord);
            json.addProperty("y", vec.yCoord);
            json.addProperty("z", vec.zCoord);
            json.addProperty("index", n);
            json.addProperty("map", "kings");
            if (!file.exists()) {
                file.mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {
        this.vec3 = null;
        this.count = -1;
        this.flag = false;
    }

    private void process3() {
        if (file2.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null) {
                        if (json.has("map") && json.get("map").getAsString().equals("kings")) {
                            double asDouble = json.get("x").getAsDouble();
                            double asDouble2 = json.get("y").getAsDouble();
                            double asDouble3 = json.get("z").getAsDouble();
                            int asInt = json.get("index").getAsInt();
                            this.vec3 = new Vec3(asDouble, asDouble2, asDouble3);
                            this.count = asInt;
                            this.flag = true;
                        }

                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean matchesVec3(Vec3 vec) {
        double d = 10.0;

        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityArmorStand entity2) {
                double dist = entity.getDistance(vec.xCoord, vec.yCoord, vec.zCoord);
                if (dist <= d && entity2.hasCustomName()) {
                    String customNameTag = entity2.getCustomNameTag();
                    if (customNameTag.contains("BAKER") || customNameTag.contains("Baker")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void handleDouble(double d, double d2, double d3, Color color) {
        AxisAlignedBB box = new AxisAlignedBB(d - 0.5, d2, d3 - 0.5, d + 0.5, d2 + 2.0, d3 + 0.5);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.3F);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        tessellator.draw();
        GL11.glLineWidth(2.0F);
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        tessellator.draw();
    }

    private void process4() {
        if (this.vec3 != null) {
            if (!this.matchesVec3(this.vec3)) {
                this.vec3 = null;
                this.count = -1;
                this.flag = false;
                this.process();
            }
        } else {
            for (int i = 0; i < vec3Array.length; i++) {
                Vec3 vec = vec3Array[i];
                if (this.matchesVec3(vec)) {
                    this.vec3 = vec;
                    this.count = i;
                    this.handleVec32(vec, i);
                    this.handleVec3(vec, i);
                    return;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.process3();
    }

    public static class Entry {
        public String name;
        public Vec3 position;
        public String text;
        public String text2;

        public Entry(String name, Vec3 position, String text, String str) {
            this.name = name;
            this.position = position;
            this.text = text;
            this.text2 = str;
        }
    }
}
