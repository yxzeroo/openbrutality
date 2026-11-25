package org.brutality.module.impl.pit.hypixel.automation;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.brutality.command.impl.DenickCommand;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.modules.AttemptDenickWithRetriesHelper;

public class AutoDenicker extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting playSoundSetting = new BooleanSetting("Play Sound", this, true);
    public static final Map<String, String> lookupMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "AutoDenicker-Scheduler");
        thread.setDaemon(true);
        return thread;
    });
    private ScheduledFuture<?> scheduledFuture;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private boolean flag = false;

    private void process() {
        if (mc.getNetHandler() != null) {
            int n = 0;

            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (playerInfo != null) {
                    GameProfile gameProfile = playerInfo.getGameProfile();
                    if (gameProfile != null && gameProfile.getId() != null) {
                        String name = gameProfile.getName();
                        if (!lookupMap.containsKey(name)) {
                            if (AttemptDenickWithRetriesHelper.matchesString(name)) {
                                lookupMap.put(name, AttemptDenickWithRetriesHelper.computeString(name));
                            } else if (gameProfile.getId().version() != 1) {
                                lookupMap.put(name, name);
                            } else {
                                lookupMap.put(name, "PENDING");
                                this.queue.add(name);
                                n++;
                            }
                        }
                    }
                }
            }

            if (n > 0) {
                String str = n == 1 ? "person is" : "people are";
                MessageUtils.x(MessageUtils.computeString(n + " " + str + " nicked in this lobby, trying to denick...", MessageUtils.text7));
                if (this.playSoundSetting.isEnabled()) {
                    try {
                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1.0F));
                    } catch (Exception ignored) {
                    }
                }

                this.process3();
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process6();
        this.process4();
    }

    public static boolean matchesNetworkPlayerInfo(NetworkPlayerInfo playerInfo) {
        if (playerInfo == null) {
            return false;
        }
        GameProfile gameProfile = playerInfo.getGameProfile();
        return gameProfile != null && gameProfile.getId() != null && gameProfile.getId().version() == 1;
    }

    private void process4() {
        if (this.scheduledFuture == null || this.scheduledFuture.isCancelled()) {
            this.scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
                FreshHelper.process();
                if (FreshHelper.flag) {
                    if (mc.thePlayer != null && mc.theWorld != null && mc.getNetHandler() != null) {
                        this.process();
                    }
                }
            }, 15L, 15L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.onRenderEvent4((RenderEvent4)event);
            }
        }
    }

    private void process5() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }
    }

    public static String computeString(String str) {
        String str2 = AttemptDenickWithRetriesHelper.computeString(str);
        if (str2 != null) {
            return str2;
        }
        String str3 = lookupMap.get(str);
        return str3 != null && !str3.equals("PENDING") ? str3 : str;
    }

    private void onRenderEvent4(RenderEvent4 renderEvent4) {
        this.process6();
        this.process5();
        if (renderEvent4.world != null) {
            scheduledExecutorService.schedule(() -> {
                FreshHelper.process();
                if (FreshHelper.flag) {
                    this.process();
                    this.process4();
                }
            }, 3L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process6();
        this.process5();
    }

    private void process3() {
        if (!this.flag) {
            if (!this.queue.isEmpty()) {
                this.flag = true;
                String str = this.queue.poll();
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    try {
                        if (mc.theWorld == null) {
                            this.flag = false;
                            this.process3();
                            return;
                        }

                        if (AttemptDenickWithRetriesHelper.matchesString(str)) {
                            lookupMap.put(str, AttemptDenickWithRetriesHelper.computeString(str));
                            this.flag = false;
                            this.process3();
                            return;
                        }

                        EntityPlayer playerEntityByName = mc.theWorld.getPlayerEntityByName(str);
                        AttemptDenickWithRetriesHelper.handleString(str, playerEntityByName, str2 -> Minecraft.getMinecraft().addScheduledTask(() -> {
                            if (mc.thePlayer != null && str2 != null) {
                                String str3 = str2.replaceFirst("^§8\\[§c§8\\] - ", "");
                                MessageUtils.x(str3);
                            }

                            AttemptDenickWithRetriesHelper.Entry entry = AttemptDenickWithRetriesHelper.computeEntry(str);
                            if (entry != null) {
                                lookupMap.put(str, entry.text);
                                DenickCommand.handleEntry(entry);
                            } else {
                                lookupMap.put(str, str);
                            }

                            this.flag = false;
                            this.process3();
                        }));
                    } catch (Exception e) {
                        this.flag = false;
                        this.process3();
                    }
                });
            }
        }
    }

    public AutoDenicker() {
        super("AutoDenicker", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.playSoundSetting});
    }

    private void process6() {
        lookupMap.clear();
        this.queue.clear();
        this.flag = false;
    }
}
