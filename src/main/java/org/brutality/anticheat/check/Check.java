package org.brutality.anticheat.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.brutality.Brutality;
import org.brutality.anticheat.data.CollectSampleHandler;
import org.brutality.anticheat.data.DataHandler;
import org.brutality.module.impl.misc.AntiCheat;

public abstract class Check {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected final String name;
    protected final String description;
    protected boolean enabled2 = true;
    protected int count = 10;
    private final Map<UUID, Double> lookupMap = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lookupMap2 = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lookupMap3 = new ConcurrentHashMap<>();
    private static final int count2 = 100;
    private static final long timestamp = 2500L;
    private static final double amount = 16.0;
    private static final long timestamp2 = 5000L;

    public String getDescription() {
        return this.description;
    }

    protected DataHandler computeDataHandler(EntityPlayer player) {
        return CollectSampleHandler.computeDataHandler(player);
    }

    protected void handleEntityPlayer(EntityPlayer player, double d, String str) {
        if (this.enabled2) {
            try {
                double d2 = this.computeDouble(player);
                double d3 = d2 + d;
                this.handleEntityPlayer2(player, d3);
                if (d3 >= this.count) {
                    this.handleEntityPlayer(player, str);
                    this.handleEntityPlayer2(player, 0.0);
                }
            } catch (Exception e) {
                this.handleString("Error adding VL: " + e.getMessage());
            }
        }
    }

    private void handleEntityPlayer2(EntityPlayer player, double d) {
        if (this.lookupMap.size() > 100 && !this.lookupMap.containsKey(player.getUniqueID())) {
            this.process();
        }

        this.lookupMap.put(player.getUniqueID(), d);
    }

    private void handleEntityPlayer(EntityPlayer player, String str) {
        try {
            UUID uniqueID = player.getUniqueID();
            long now = System.currentTimeMillis();
            Long val = this.lookupMap2.get(uniqueID);
            if (val == null || now - val >= 5000L) {
                String text = player.getDisplayName().getFormattedText();
                String name = player.getName();

                try {
                    int count = this.count;
                    String name2 = this.name;
                    ChatComponentText chatComponentText = new ChatComponentText("§8[§4AC§8] §f" + text + " §7failed §c" + name2 + " §7[VL:" + count + "]");
                    AntiCheat antiCheat = this.computeAntiCheat();
                    if (antiCheat != null && antiCheat.checkState()) {
                        ChatComponentText chatComponentText2 = new ChatComponentText(" §c[WDR]");
                        ChatStyle chatStyle = new ChatStyle();
                        chatStyle.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wdr " + name + " cheating"));
                        chatStyle.setChatHoverEvent(
                            new HoverEvent(
                                net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§bClick to report " + text + " §bfor cheating")
                            )
                        );
                        chatComponentText2.setChatStyle(chatStyle);
                        chatComponentText.appendSibling(chatComponentText2);
                    }

                    if (mc.thePlayer != null) {
                        mc.thePlayer.addChatMessage(chatComponentText);
                        if (antiCheat != null && antiCheat.checkState2()) {
                            mc.thePlayer.playSound("note.pling", 1.0F, 1.0F);
                        }
                    }
                } catch (Exception e) {
                    this.sendMessage("§8[§4AC§8] §f" + text + " §7failed §c" + this.name);
                    this.handleString("Error sending chat: " + e.getMessage());
                }

                this.lookupMap2.put(uniqueID, now);
                if (this.lookupMap2.size() > 100) {
                    this.process();
                }
            }
        } catch (Exception e2) {
            this.handleString("Error flagging player: " + e2.getMessage());
        }
    }

    protected void sendMessage(String str) {
        try {
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText(str));
            }
        } catch (Exception e) {
            this.handleString("Error sending message: " + e.getMessage());
        }
    }

    protected void handleString(String str) {
    }

    protected boolean matchesEntityPlayer(EntityPlayer player, int n) {
        try {
            BlockPos pos = player.getPosition();
            BlockPos[] posArray = new BlockPos[]{pos.add(0, 0, 1), pos.add(0, 0, -1), pos.add(1, 0, 0), pos.add(-1, 0, 0)};

            for (BlockPos pos2 : posArray) {
                boolean flag = false;
                int n2 = 0;

                while (true) {
                    if (n2 < n) {
                        BlockPos pos3 = pos2.add(0, n2, 0);
                        if (mc.theWorld.isAirBlock(pos3)) {
                            n2++;
                            continue;
                        }

                        flag = true;
                    }

                    if (flag) {
                        return true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            this.handleString("Error checking surrounding blocks: " + e.getMessage());
        }

        return false;
    }

    public void handleEntityPlayer(EntityPlayer player) {
        if (player != null) {
            UUID uniqueID = player.getUniqueID();
            this.lookupMap2.remove(uniqueID);
            this.lookupMap.remove(uniqueID);
            this.lookupMap3.remove(uniqueID);
        } else {
            this.lookupMap.clear();
            this.lookupMap2.clear();
            this.lookupMap3.clear();
        }
    }

    private AntiCheat computeAntiCheat() {
        try {
            return (AntiCheat)Brutality.moduleManager.computeModule("AntiCheat");
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() {
        return this.name;
    }

    protected double computeDouble(EntityPlayer player) {
        return this.lookupMap.getOrDefault(player.getUniqueID(), 0.0);
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected void handleEntityPlayer(EntityPlayer player, double d) {
        try {
            double d2 = this.computeDouble(player);
            if (d2 > 0.0) {
                this.handleEntityPlayer2(player, Math.max(0.0, d2 - d));
            }
        } catch (Exception e) {
            this.handleString("Error decaying VL: " + e.getMessage());
        }
    }

    private void process() {
        if (this.lookupMap2.size() > 100) {
            ArrayList list = new ArrayList<>(this.lookupMap2.entrySet());
            list.sort(Entry.comparingByValue());
            int n = Math.max(1, this.lookupMap2.size() - 50);

            for (int i = 0; i < n && i < list.size(); i++) {
                UUID key = (UUID)((Entry)list.get(i)).getKey();
                this.lookupMap2.remove(key);
                this.lookupMap.remove(key);
            }
        }
    }

    protected float computeFloat(double d, double d2, float f) {
        float f2 = this.computeFloat(d, d2);
        float f3 = f2 - f;
        float f4 = (f3 % 360.0F + 360.0F) % 360.0F;
        if (f4 > 180.0F) {
            f4 -= 360.0F;
        }

        return f4;
    }

    public Check(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected boolean matchesEntityPlayer2(EntityPlayer player) {
        DataHandler dataHandler = this.computeDataHandler(player);
        DataHandler dataHandler2 = this.computeDataHandler2(player);
        if (dataHandler == null || dataHandler2 == null) {
            return false;
        }
        if (!CollectSampleHandler.matchesEntityPlayer(player)) {
            this.handleEntityPlayer2(player);
            return true;
        }
        double d = dataHandler.posX - dataHandler2.posX;
        double d2 = dataHandler.posY - dataHandler2.posY;
        double d3 = dataHandler.posZ - dataHandler2.posZ;
        double d4 = d * d + d2 * d2 + d3 * d3;
        if (d4 > 256.0) {
            this.handleEntityPlayer2(player);
            return true;
        }
        return false;
    }

    protected double computeDouble(double d, double d2) {
        return Math.sqrt(d * d + d2 * d2);
    }

    protected DataHandler computeDataHandler(EntityPlayer player, int n) {
        return CollectSampleHandler.computeDataHandler(player, n);
    }

    public void onUpdate(EntityPlayer player) {
    }

    protected boolean matchesEntityPlayer(EntityPlayer player) {
        long now = System.currentTimeMillis();
        Long val = this.lookupMap3.get(player.getUniqueID());
        if (val != null && now < val) {
            return true;
        }
        this.lookupMap3.remove(player.getUniqueID());
        return false;
    }

    public int getCount() {
        return this.count;
    }

    protected float computeFloat(double d, double d2) {
        return Math.abs(d) < 1.0E-8 && Math.abs(d2) < 1.0E-8 ? 0.0F : (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
    }

    private void handleEntityPlayer2(EntityPlayer player) {
        long now = System.currentTimeMillis();
        this.lookupMap3.put(player.getUniqueID(), now + 2500L);
    }

    public void setEnabled(boolean enabled2) {
        this.enabled2 = enabled2;
    }

    protected List<DataHandler> computeList(EntityPlayer player) {
        return CollectSampleHandler.computeList(player);
    }

    public boolean isEnabled() {
        return this.enabled2;
    }

    public void handlePacket(Packet<?> packet) {
    }

    protected DataHandler computeDataHandler2(EntityPlayer player) {
        return CollectSampleHandler.computeDataHandler2(player);
    }
}
