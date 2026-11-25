package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;
import org.brutality.event.impl.player.EventKey;
import org.brutality.event.impl.player.PlayerEvent4;
import org.brutality.module.ModuleManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyBinding implements Comparable<KeyBinding> {
    public static final List<KeyBinding> keybindArray = Lists.newArrayList();
    public static final IntHashMap<KeyBinding> hash = new IntHashMap<>();
    public static final Set<String> keybindSet = Sets.newHashSet();
    public final String keyDescription;
    public final int keyCodeDefault;
    public final String keyCategory;
    public int keyCode;
    public boolean pressed;
    public int pressTime;
    private static final boolean[] mouseButtonStates = new boolean[16];
    private static final boolean[] lastMouseButtonStates = new boolean[16];
    private static long lastMouseUpdate = 0L;

    public static void onTick(int keyCode) {
        if (keyCode != 0) {
            KeyBinding keyBinding = hash.lookup(keyCode);
            if (keyBinding != null) {
                keyBinding.pressTime++;
            }
        }
    }

    public static void setKeyBindState(int keyCode, boolean pressed) {
        if (keyCode != 0) {
            KeyBinding keyBinding = hash.lookup(keyCode);
            if (keyBinding != null) {
                keyBinding.pressed = pressed;
            }
        }
    }

    public static void updateMouseStates() {
        long now = System.currentTimeMillis();
        if (now - lastMouseUpdate >= 16L) {
            lastMouseUpdate = now;
            System.arraycopy(mouseButtonStates, 0, lastMouseButtonStates, 0, 16);

            for (int i = 0; i < 16; i++) {
                try {
                    mouseButtonStates[i] = Mouse.isButtonDown(i);
                } catch (Exception e) {
                    mouseButtonStates[i] = false;
                }
            }

            for (KeyBinding keyBinding : keybindArray) {
                if (keyBinding.keyCode < 0) {
                    int n = keyBinding.keyCode + 100;
                    if (n >= 0 && n < 16) {
                        boolean flag = mouseButtonStates[n];
                        boolean flag2 = lastMouseButtonStates[n];
                        if (flag && !flag2) {
                            if (!keyBinding.pressed) {
                                keyBinding.pressed = true;
                                keyBinding.pressTime++;
                            }
                        } else if (!flag && flag2) {
                            keyBinding.pressed = false;
                        } else if (flag) {
                            keyBinding.pressed = true;
                        }
                    }
                }
            }
        }
    }

    public static boolean wasMouseButtonPressed(int mouseButton) {
        return mouseButton >= 0 && mouseButton < 16 ? mouseButtonStates[mouseButton] && !lastMouseButtonStates[mouseButton] : false;
    }

    public static void forceUpdateKeybinding(int keyCode) {
        KeyBinding keyBinding = hash.lookup(keyCode);
        if (keyBinding != null) {
            if (keyCode < 0) {
                int n = keyCode + 100;
                if (n >= 0 && n < 16) {
                    boolean flag = false;

                    try {
                        flag = Mouse.isButtonDown(n);
                    } catch (Exception e) {
                        flag = false;
                    }

                    if (flag && !keyBinding.pressed) {
                        keyBinding.pressed = true;
                        keyBinding.pressTime++;
                    } else if (!flag) {
                        keyBinding.pressed = false;
                    }
                }
            } else {
                boolean flag2 = false;

                try {
                    flag2 = keyCode > 0 && Keyboard.isKeyDown(keyCode);
                } catch (Exception e2) {
                    flag2 = false;
                }

                if (flag2 && !keyBinding.pressed) {
                    keyBinding.pressed = true;
                    keyBinding.pressTime++;
                } else if (!flag2) {
                    keyBinding.pressed = false;
                }
            }
        }
    }

    public static void unPressAllKeys() {
        for (KeyBinding keyBinding : keybindArray) {
            keyBinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash() {
        hash.clearMap();

        for (KeyBinding keyBinding : keybindArray) {
            hash.addKey(keyBinding.keyCode, keyBinding);
        }
    }

    public static Set<String> getKeybinds() {
        return keybindSet;
    }

    public KeyBinding(String description, int keyCode, String category) {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        keybindArray.add(this);
        hash.addKey(keyCode, this);
        keybindSet.add(category);
    }

    public boolean isKeyDown() {
        return this.pressed;
    }

    public String getKeyCategory() {
        return this.keyCategory;
    }

    public boolean isPressed() {
        if (this.pressTime == 0) {
            return false;
        }
        if (Minecraft.getMinecraft().currentScreen == null && ModuleManager.matchesInt(this.keyCode)) {
            this.pressTime = 0;
            this.pressed = false;
            return false;
        }
        this.pressTime--;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.gameSettings != null) {
            for (int i = 0; i < 9; i++) {
                KeyBinding keyBinding = mc.gameSettings.keyBindsHotbar[i];
                if (keyBinding != null && keyBinding.keyDescription.equals(this.keyDescription)) {
                    PlayerEvent4 playerEvent4 = new PlayerEvent4(i, 0);
                    playerEvent4.process();
                    if (playerEvent4.cancel2()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean isPressedEnhanced() {
        if (this.keyCode < 0) {
            int n = this.keyCode + 100;
            if (n >= 0 && n < 16 && wasMouseButtonPressed(n)) {
                this.pressTime = 1;
                return true;
            }
        }

        return this.isPressed();
    }

    private void unpressKey() {
        EventKey event = new EventKey(this.keyCode);
        event.process();
        if (!event.cancel2()) {
            this.pressTime = 0;
            this.pressed = false;
        }
    }

    public String getKeyDescription() {
        return this.keyDescription;
    }

    public int getKeyCodeDefault() {
        return this.keyCodeDefault;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int compareTo(KeyBinding p_compareTo_1_) {
        int n = I18n.format(this.keyCategory).compareTo(I18n.format(p_compareTo_1_.keyCategory));
        if (n == 0) {
            n = I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription));
        }

        return n;
    }

    @Generated
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
