package org.brutality.notification.modern;

public class ModernHandler {
    public static enum LowMode {
        LOW(1, "§7"),
        NORMAL(2, "§f"),
        HIGH(3, "§e"),
        CRITICAL(4, "§c"),
        URGENT(5, "§4§l");

        private final int level2;
        private final String color;

        private static ModernHandler.LowMode[] computeLowModeArray() {
            return new ModernHandler.LowMode[]{LOW, NORMAL, HIGH, CRITICAL, URGENT};
        }

        public String getColor() {
            return this.color;
        }

        public int getLevel() {
            return this.level2;
        }

        private LowMode(int level2, String color) {
            this.level2 = level2;
            this.color = color;
        }
    }

    public static enum ModernMode {
        MODERN("Modern", true, true, true),
        OLD("Old", false, true, false),
        MINIMAL("Minimal", false, false, true),
        COMPACT("Compact", true, false, false);

        private final String name2;
        private final boolean hasAnimation;
        private final boolean hasBackground;
        private final boolean hasSound;

        public boolean hasAnimation() {
            return this.hasAnimation;
        }

        public boolean hasSound() {
            return this.hasSound;
        }

        public boolean hasBackground() {
            return this.hasBackground;
        }

        public String getName() {
            return this.name2;
        }

        private static ModernHandler.ModernMode[] computeModernModeArray() {
            return new ModernHandler.ModernMode[]{MODERN, OLD, MINIMAL, COMPACT};
        }

        private ModernMode(String name2, boolean hasAnimation, boolean hasBackground, boolean hasSound) {
            this.name2 = name2;
            this.hasAnimation = hasAnimation;
            this.hasBackground = hasBackground;
            this.hasSound = hasSound;
        }
    }

    public static enum NoneMode {
        NONE("", 0.0F),
        SOFT("random.orb", 0.3F),
        NORMAL("random.pop", 0.5F),
        LOUD("random.anvil_land", 1.0F),
        URGENT("mob.enderdragon.growl", 0.7F);

        private final String soundName2;
        private final float volume2;

        public String getSoundName() {
            return this.soundName2;
        }

        private static ModernHandler.NoneMode[] computeNoneModeArray() {
            return new ModernHandler.NoneMode[]{NONE, SOFT, NORMAL, LOUD, URGENT};
        }

        private NoneMode(String soundName2, float volume2) {
            this.soundName2 = soundName2;
            this.volume2 = volume2;
        }

        public float getVolume() {
            return this.volume2;
        }
    }

    public static enum PlayerJoinMode {
        PLAYER_JOIN("Player Join", "§8[§a+§8]", 5000L),
        PLAYER_LEAVE("Player Leave", "§8[§c-§8]", 4000L),
        PLAYER_JUMP("Player Jump", "§8[§e!§8]", 6000L),
        EQUIPMENT_CHANGE("Equipment Change", "§8[§b~§8]", 4500L),
        DARK_EQUIPMENT("Dark Equipment", "§8[§5D§8]", 7000L),
        FRIEND_ACTIVITY("Friend Activity", "§8[§aF§8]", 5500L),
        KOS_ACTIVITY("KOS Activity", "§8[§cK§8]", 6000L),
        SYSTEM("System", "§8[§7S§8]", 3000L),
        WARNING("Warning", "§8[§6W§8]", 8000L),
        ERROR("Error", "§8[§4E§8]", 10000L);

        private final String displayName2;
        private final String prefix2;
        private final long defaultDuration;

        private PlayerJoinMode(String displayName2, String prefix2, long defaultDuration) {
            this.displayName2 = displayName2;
            this.prefix2 = prefix2;
            this.defaultDuration = defaultDuration;
        }

        public String getPrefix() {
            return this.prefix2;
        }

        public String getDisplayName() {
            return this.displayName2;
        }

        public long getDefaultDuration() {
            return this.defaultDuration;
        }

        private static ModernHandler.PlayerJoinMode[] computePlayerJoinModeArray() {
            return new ModernHandler.PlayerJoinMode[]{
                PLAYER_JOIN, PLAYER_LEAVE, PLAYER_JUMP, EQUIPMENT_CHANGE, DARK_EQUIPMENT, FRIEND_ACTIVITY, KOS_ACTIVITY, SYSTEM, WARNING, ERROR
            };
        }
    }

    public static enum SlideInMode {
        SLIDE_IN,
        DISPLAYING,
        FADE_OUT,
        SLIDE_OUT,
        EXPIRED;

        private static ModernHandler.SlideInMode[] computeSlideInModeArray() {
            return new ModernHandler.SlideInMode[]{SLIDE_IN, DISPLAYING, FADE_OUT, SLIDE_OUT, EXPIRED};
        }
    }
}
