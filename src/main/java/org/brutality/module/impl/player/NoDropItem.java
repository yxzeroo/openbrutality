package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;

public class NoDropItem extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private long timestamp = -1L;
    private boolean flag = false;
    private final NumberSetting doubleTapDelaySetting = new NumberSetting("Double Tap Delay", this, 350.0, 0.0, 500.0, 50.0);
    private final KeybindListSetting diamondSwordSetting = new KeybindListSetting(
        "Swords",
        this,
        new BooleanSetting("Wooden Sword", this, true),
        new BooleanSetting("Stone Sword", this, true),
        new BooleanSetting("Iron Sword", this, true),
        new BooleanSetting("Golden Sword", this, true),
        new BooleanSetting("Diamond Sword", this, true)
    );
    private final KeybindListSetting diamondHelmetSetting = new KeybindListSetting(
        "Helmets",
        this,
        new BooleanSetting("Leather Helmet", this, true),
        new BooleanSetting("Chainmail Helmet", this, true),
        new BooleanSetting("Iron Helmet", this, true),
        new BooleanSetting("Golden Helmet", this, true),
        new BooleanSetting("Diamond Helmet", this, true)
    );
    private final KeybindListSetting diamondChestplateSetting = new KeybindListSetting(
        "Chestplates",
        this,
        new BooleanSetting("Leather Chestplate", this, true),
        new BooleanSetting("Chainmail Chestplate", this, true),
        new BooleanSetting("Iron Chestplate", this, true),
        new BooleanSetting("Golden Chestplate", this, true),
        new BooleanSetting("Diamond Chestplate", this, true)
    );
    private final KeybindListSetting diamondLeggingsSetting = new KeybindListSetting(
        "Leggings",
        this,
        new BooleanSetting("Leather Leggings", this, true),
        new BooleanSetting("Chainmail Leggings", this, true),
        new BooleanSetting("Iron Leggings", this, true),
        new BooleanSetting("Golden Leggings", this, true),
        new BooleanSetting("Diamond Leggings", this, true)
    );
    private final KeybindListSetting diamondBootsSetting = new KeybindListSetting(
        "Boots",
        this,
        new BooleanSetting("Leather Boots", this, true),
        new BooleanSetting("Chainmail Boots", this, true),
        new BooleanSetting("Iron Boots", this, true),
        new BooleanSetting("Golden Boots", this, true),
        new BooleanSetting("Diamond Boots", this, true)
    );

    private String computeString(ItemStack stack) {
        try {
            String str = null;
            if (Item.itemRegistry != null) {
                str = Item.itemRegistry.getNameForObject(stack.getItem()).toString();
            }

            if (str == null || str.isEmpty()) {
                str = stack.getItem().getUnlocalizedName();
                if (str.startsWith("item.")) {
                    str = str.substring(5);
                }
            }

            if (str == null || str.isEmpty()) {
                str = stack.getItem().getClass().getSimpleName().toLowerCase();
            }

            if (str != null) {
                str = str.replace("minecraft:", "").toLowerCase();
            }

            return str;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean matchesString(String str) {
        if (str.contains("leather")) {
            return ((BooleanSetting)this.diamondChestplateSetting.computeSetting("Leather Chestplate")).isEnabled();
        }
        if (str.contains("chainmail") || str.contains("chain")) {
            return ((BooleanSetting)this.diamondChestplateSetting.computeSetting("Chainmail Chestplate")).isEnabled();
        }
        if (str.contains("iron")) {
            return ((BooleanSetting)this.diamondChestplateSetting.computeSetting("Iron Chestplate")).isEnabled();
        }
        if (str.contains("gold")) {
            return ((BooleanSetting)this.diamondChestplateSetting.computeSetting("Golden Chestplate")).isEnabled();
        }
        return str.contains("diamond") ? ((BooleanSetting)this.diamondChestplateSetting.computeSetting("Diamond Chestplate")).isEnabled() : false;
    }

    public NoDropItem() {
        super("NoDropItem", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{
                this.doubleTapDelaySetting,
                this.diamondSwordSetting,
                this.diamondHelmetSetting,
                this.diamondChestplateSetting,
                this.diamondLeggingsSetting,
                this.diamondBootsSetting
            }
        );
    }

    private boolean matchesString5(String str) {
        if (str.contains("leather")) {
            return ((BooleanSetting)this.diamondHelmetSetting.computeSetting("Leather Helmet")).isEnabled();
        }
        if (str.contains("chainmail") || str.contains("chain")) {
            return ((BooleanSetting)this.diamondHelmetSetting.computeSetting("Chainmail Helmet")).isEnabled();
        }
        if (str.contains("iron")) {
            return ((BooleanSetting)this.diamondHelmetSetting.computeSetting("Iron Helmet")).isEnabled();
        }
        if (str.contains("gold")) {
            return ((BooleanSetting)this.diamondHelmetSetting.computeSetting("Golden Helmet")).isEnabled();
        }
        return str.contains("diamond") ? ((BooleanSetting)this.diamondHelmetSetting.computeSetting("Diamond Helmet")).isEnabled() : false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.checkState44()) {
                if (event instanceof EventPacketSend && this.mc.thePlayer != null) {
                    EventPacketSend event2 = (EventPacketSend)event;
                    if (event2.getPacket() instanceof C07PacketPlayerDigging) {
                        C07PacketPlayerDigging packet = (C07PacketPlayerDigging)event2.getPacket();
                        if (packet.getStatus() == Action.DROP_ITEM || packet.getStatus() == Action.DROP_ALL_ITEMS) {
                            ItemStack heldItem = this.mc.thePlayer.getHeldItem();
                            if (heldItem != null) {
                                String str = this.computeString(heldItem);
                                if (str != null) {
                                    if (this.matchesString3(str)) {
                                        if (this.flag) {
                                            this.flag = false;
                                            this.timestamp = -1L;
                                            this.sendMessage("Dropped " + this.computeString(str));
                                        } else {
                                            long now = System.currentTimeMillis();
                                            int n = (int)this.doubleTapDelaySetting.computeDouble();
                                            if (this.timestamp != -1L && now - this.timestamp <= n) {
                                                this.flag = true;
                                                this.sendMessage("Double-tap detected - dropping " + this.computeString(str));
                                            } else {
                                                this.timestamp = now;
                                                event2.setCancelled(true);
                                                this.sendMessage("Blocked drop of " + this.computeString(str) + " (double-tap within " + n + "ms to drop)");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timestamp = -1L;
        this.flag = false;
        this.sendMessage("Enabled - Items protected from accidental drops");
    }

    private boolean matchesString6(String str) {
        if (str.contains("wood")) {
            return ((BooleanSetting)this.diamondSwordSetting.computeSetting("Wooden Sword")).isEnabled();
        }
        if (str.contains("stone")) {
            return ((BooleanSetting)this.diamondSwordSetting.computeSetting("Stone Sword")).isEnabled();
        }
        if (str.contains("iron")) {
            return ((BooleanSetting)this.diamondSwordSetting.computeSetting("Iron Sword")).isEnabled();
        }
        if (str.contains("gold")) {
            return ((BooleanSetting)this.diamondSwordSetting.computeSetting("Golden Sword")).isEnabled();
        }
        return str.contains("diamond") ? ((BooleanSetting)this.diamondSwordSetting.computeSetting("Diamond Sword")).isEnabled() : false;
    }

    private boolean matchesString3(String str) {
        if (str == null) {
            return false;
        }
        String str2 = str.toLowerCase();
        if (str2.contains("sword")) {
            return this.matchesString6(str2);
        }
        if (str2.contains("helmet")) {
            return this.matchesString5(str2);
        }
        if (str2.contains("chestplate")) {
            return this.matchesString(str2);
        }
        if (str2.contains("legging")) {
            return this.matchesString2(str2);
        }
        return str2.contains("boots") ? this.matchesString4(str2) : false;
    }

    private boolean matchesString4(String str) {
        if (str.contains("leather")) {
            return ((BooleanSetting)this.diamondBootsSetting.computeSetting("Leather Boots")).isEnabled();
        }
        if (str.contains("chainmail") || str.contains("chain")) {
            return ((BooleanSetting)this.diamondBootsSetting.computeSetting("Chainmail Boots")).isEnabled();
        }
        if (str.contains("iron")) {
            return ((BooleanSetting)this.diamondBootsSetting.computeSetting("Iron Boots")).isEnabled();
        }
        if (str.contains("gold")) {
            return ((BooleanSetting)this.diamondBootsSetting.computeSetting("Golden Boots")).isEnabled();
        }
        return str.contains("diamond") ? ((BooleanSetting)this.diamondBootsSetting.computeSetting("Diamond Boots")).isEnabled() : false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.timestamp = -1L;
        this.flag = false;
        this.sendMessage("Disabled - Items can be dropped normally");
    }

    private void process() {
        this.timestamp = -1L;
        this.flag = false;
    }

    public void onTick() {
        if (this.timestamp != -1L) {
            long now = System.currentTimeMillis();
            int n = (int)this.doubleTapDelaySetting.computeDouble();
            if (now - this.timestamp > n * 2) {
                this.process();
            }
        }
    }

    private void sendMessage(String str) {
        try {
            String str2 = MessageUtils.text15;
            String str3 = MessageUtils.text9;
            String str4 = MessageUtils.text15;
            MessageUtils.x(MessageUtils.text9 + "[" + str4 + "NoDropItem" + str3 + "] " + str2 + str);
        } catch (Exception ignored) {
        }
    }

    private boolean matchesString2(String str) {
        if (str.contains("leather")) {
            return ((BooleanSetting)this.diamondLeggingsSetting.computeSetting("Leather Leggings")).isEnabled();
        }
        if (str.contains("chainmail") || str.contains("chain")) {
            return ((BooleanSetting)this.diamondLeggingsSetting.computeSetting("Chainmail Leggings")).isEnabled();
        }
        if (str.contains("iron")) {
            return ((BooleanSetting)this.diamondLeggingsSetting.computeSetting("Iron Leggings")).isEnabled();
        }
        if (str.contains("gold")) {
            return ((BooleanSetting)this.diamondLeggingsSetting.computeSetting("Golden Leggings")).isEnabled();
        }
        return str.contains("diamond") ? ((BooleanSetting)this.diamondLeggingsSetting.computeSetting("Diamond Leggings")).isEnabled() : false;
    }

    private String computeString(String str) {
        if (str == null) {
            return "Unknown Item";
        }
        String[] parts = str.replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();

        for (String str2 : parts) {
            if (sb.length() > 0) {
                sb.append(" ");
            }

            if (str2.length() > 0) {
                sb.append(Character.toUpperCase(str2.charAt(0)));
                if (str2.length() > 1) {
                    sb.append(str2.substring(1).toLowerCase());
                }
            }
        }

        return sb.toString();
    }
}
