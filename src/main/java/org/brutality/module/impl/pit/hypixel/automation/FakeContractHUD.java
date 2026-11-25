package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public class FakeContractHUD extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static FakeContractHUD instance;
    private final ModeSetting randomSetting = new ModeSetting("Contract Type", this, "Quick", Arrays.asList("Quick", "Big Time", "Random"));
    private final NumberSetting contractCountSetting = new NumberSetting("Contract Count", this, 1.0, 1.0, 3.0, 1.0);
    private final BooleanSetting randomizeOnEnableSetting = new BooleanSetting("Randomize on Enable", this, true);
    private final BooleanSetting autoRefreshSetting = new BooleanSetting("Auto Refresh", this, false);
    private final NumberSetting refreshIntervalMinSetting = new NumberSetting("Refresh Interval (min)", this, 5.0, 1.0, 60.0, 1.0);
    private final ModeSetting realisticSetting = new ModeSetting("Timer Mode", this, "Realistic", Arrays.asList("Static", "Countdown", "Realistic"));
    private final NumberSetting staticTimeMinSetting = new NumberSetting("Static Time (min)", this, 5.0, 1.0, 60.0, 1.0);
    private final NumberSetting countdownTimeMinSetting = new NumberSetting("Countdown Time (min)", this, 10.0, 1.0, 60.0, 1.0);
    private final BooleanSetting showSecondsSetting = new BooleanSetting("Show Seconds", this, true);
    private final KeybindListSetting noArmorContractsSetting = new KeybindListSetting(
        "Contract Filters",
        this,
        new BooleanSetting("Kill Contracts", this, true),
        new BooleanSetting("Streak Contracts", this, true),
        new BooleanSetting("Bow Contracts", this, true),
        new BooleanSetting("Lava Contracts", this, true),
        new BooleanSetting("Grass Contracts", this, true),
        new BooleanSetting("Obsidian Contracts", this, true),
        new BooleanSetting("Sneaking Contracts", this, true),
        new BooleanSetting("Gold Contracts", this, true),
        new BooleanSetting("Apple Contracts", this, true),
        new BooleanSetting("Bounty Contracts", this, true),
        new BooleanSetting("Chain Contracts", this, true),
        new BooleanSetting("Fishing Contracts", this, true),
        new BooleanSetting("Kung Fu Contracts", this, true),
        new BooleanSetting("No Armor Contracts", this, true)
    );
    private final KeybindListSetting progressSpeedSetting = new KeybindListSetting(
        "Progress Settings",
        this,
        new BooleanSetting("Show Progress", this, true),
        new BooleanSetting("Random Progress", this, false),
        new NumberSetting("Progress Percentage", this, 50.0, 0.0, 100.0, 5.0),
        new BooleanSetting("Simulate Progress", this, false),
        new NumberSetting("Progress Speed", this, 1.0, 1.0, 10.0, 1.0)
    );
    private final KeybindListSetting customContract3Setting = new KeybindListSetting(
        "Custom Contracts",
        this,
        new BooleanSetting("Enable Custom", this, false),
        new StringSetting("Custom Contract 1", this, "Kill 5 players"),
        new StringSetting("Custom Contract 2", this, ""),
        new StringSetting("Custom Contract 3", this, "")
    );
    private final KeybindListSetting colorCodeTimerSetting = new KeybindListSetting(
        "Display Settings",
        this,
        new BooleanSetting("Show Contract Label", this, true),
        new BooleanSetting("Show Timer Icon", this, true),
        new ModeSetting("Timer Format", this, "MM:SS", Arrays.asList("MM:SS", "M:SS", "Short")),
        new BooleanSetting("Color Code Timer", this, true)
    );
    private List<FakeContractHUD.Entry2> entries = new ArrayList<>();
    private long timestamp = 0L;
    private long timestamp2 = 0L;
    private static final Map<String, List<FakeContractHUD.Entry>> lookupMap = new HashMap<>();

    public void process4() {
        this.process3();
        this.timestamp = System.currentTimeMillis();
    }

    static {
        var list = Arrays.asList(
            new FakeContractHUD.Entry("Kill %d players", Arrays.asList(5, 10)),
            new FakeContractHUD.Entry("Kill %d players while standing on grass", Arrays.asList(1)),
            new FakeContractHUD.Entry("Kill %d players using a diamond sword", Arrays.asList(1)),
            new FakeContractHUD.Entry("Kill %d players using lava", Arrays.asList(1)),
            new FakeContractHUD.Entry("Kill %d players while standing on obsidian", Arrays.asList(1)),
            new FakeContractHUD.Entry("Kill %d players after shooting them", Arrays.asList(3, 4)),
            new FakeContractHUD.Entry("Kill %d players using a bow shot", Arrays.asList(5, 7, 8)),
            new FakeContractHUD.Entry("Kill %d players from 30+ blocks with bow", Arrays.asList(1)),
            new FakeContractHUD.Entry("Kill %d players while sneaking", Arrays.asList(1)),
            new FakeContractHUD.Entry("Have a kill streak of %d", Arrays.asList(5, 7)),
            new FakeContractHUD.Entry("Chain %d kills", Arrays.asList(2)),
            new FakeContractHUD.Entry("Collect %d golden ingots", Arrays.asList(10)),
            new FakeContractHUD.Entry("Eat %d golden apples", Arrays.asList(6)),
            new FakeContractHUD.Entry("Claim %d bounties", Arrays.asList(1)),
            new FakeContractHUD.Entry("Land %d bow shots", Arrays.asList(35))
        );
        var list2 = Arrays.asList(
            new FakeContractHUD.Entry("Kill %d players", Arrays.asList(15, 20)),
            new FakeContractHUD.Entry("Kill %d players while standing on grass", Arrays.asList(8, 12)),
            new FakeContractHUD.Entry("Kill %d players using a diamond sword", Arrays.asList(10, 15, 20)),
            new FakeContractHUD.Entry("Kill %d players using lava", Arrays.asList(5, 8)),
            new FakeContractHUD.Entry("Kill %d players while standing on obsidian", Arrays.asList(5, 8, 12)),
            new FakeContractHUD.Entry("Kill %d players after shooting them", Arrays.asList(8, 10, 12, 15)),
            new FakeContractHUD.Entry("Kill %d players without armor", Arrays.asList(3, 4)),
            new FakeContractHUD.Entry("Kill %d players while sneaking", Arrays.asList(3, 4)),
            new FakeContractHUD.Entry("Have a kill streak of %d", Arrays.asList(15, 20)),
            new FakeContractHUD.Entry("Chain %d kills", Arrays.asList(3, 4)),
            new FakeContractHUD.Entry("Collect %d golden ingots", Arrays.asList(20, 25, 30)),
            new FakeContractHUD.Entry("Eat %d golden apples", Arrays.asList(12, 16, 18)),
            new FakeContractHUD.Entry("Claim %d bounties", Arrays.asList(2, 3, 4)),
            new FakeContractHUD.Entry("Land %d bow shots", Arrays.asList(100, 125, 150)),
            new FakeContractHUD.Entry("Catch %d diamond swords fishing", Arrays.asList(1)),
            new FakeContractHUD.Entry("Kill %d players with fist in center", Arrays.asList(1, 2))
        );
        lookupMap.put("Quick", list);
        lookupMap.put("Big Time", list2);
    }

    private int computeInt(int n) {
        BooleanSetting boolSetting = (BooleanSetting)this.progressSpeedSetting.computeSetting("Show Progress");
        if (boolSetting != null && boolSetting.isEnabled()) {
            BooleanSetting boolSetting2 = (BooleanSetting)this.progressSpeedSetting.computeSetting("Random Progress");
            if (boolSetting2 != null && boolSetting2.isEnabled()) {
                return ThreadLocalRandom.current().nextInt(0, n);
            }
            NumberSetting numberSetting = (NumberSetting)this.progressSpeedSetting.computeSetting("Progress Percentage");
            return numberSetting != null ? (int)(n * (numberSetting.computeDouble() / 100.0)) : 0;
        }
        return 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timestamp2 = System.currentTimeMillis();
        this.timestamp = this.timestamp2;
        if (this.randomizeOnEnableSetting.isEnabled()) {
            this.process3();
        }
    }

    private void process5() {
        StringSetting stringSetting = (StringSetting)this.customContract3Setting.computeSetting("Custom Contract 1");
        StringSetting stringSetting2 = (StringSetting)this.customContract3Setting.computeSetting("Custom Contract 2");
        StringSetting stringSetting3 = (StringSetting)this.customContract3Setting.computeSetting("Custom Contract 3");
        this.handleStringSetting(stringSetting);
        this.handleStringSetting(stringSetting2);
        this.handleStringSetting(stringSetting3);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Generated
    public static FakeContractHUD getInstance() {
        return instance;
    }

    private boolean matchesEntry(FakeContractHUD.Entry entry) {
        String str = entry.text.toLowerCase();
        BooleanSetting boolSetting = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Kill Contracts");
        BooleanSetting boolSetting2 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Streak Contracts");
        BooleanSetting boolSetting3 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Bow Contracts");
        BooleanSetting boolSetting4 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Lava Contracts");
        BooleanSetting boolSetting5 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Grass Contracts");
        BooleanSetting boolSetting6 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Obsidian Contracts");
        BooleanSetting boolSetting7 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Sneaking Contracts");
        BooleanSetting boolSetting8 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Gold Contracts");
        BooleanSetting boolSetting9 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Apple Contracts");
        BooleanSetting boolSetting10 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Bounty Contracts");
        BooleanSetting boolSetting11 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Chain Contracts");
        BooleanSetting boolSetting12 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Fishing Contracts");
        BooleanSetting boolSetting13 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("Kung Fu Contracts");
        BooleanSetting boolSetting14 = (BooleanSetting)this.noArmorContractsSetting.computeSetting("No Armor Contracts");
        if (str.contains("bow") && boolSetting3 != null && !boolSetting3.isEnabled()) {
            return false;
        }
        if (str.contains("lava") && boolSetting4 != null && !boolSetting4.isEnabled()) {
            return false;
        }
        if (str.contains("grass") && boolSetting5 != null && !boolSetting5.isEnabled()) {
            return false;
        }
        if (str.contains("obsidian") && boolSetting6 != null && !boolSetting6.isEnabled()) {
            return false;
        }
        if (str.contains("sneaking") && boolSetting7 != null && !boolSetting7.isEnabled()) {
            return false;
        }
        if (str.contains("golden ingots") && boolSetting8 != null && !boolSetting8.isEnabled()) {
            return false;
        }
        if (str.contains("golden apples") && boolSetting9 != null && !boolSetting9.isEnabled()) {
            return false;
        }
        if (str.contains("bounties") && boolSetting10 != null && !boolSetting10.isEnabled()) {
            return false;
        }
        if (str.contains("chain") && boolSetting11 != null && !boolSetting11.isEnabled()) {
            return false;
        }
        if (str.contains("fishing") && boolSetting12 != null && !boolSetting12.isEnabled()) {
            return false;
        }
        if (str.contains("fist") && boolSetting13 != null && !boolSetting13.isEnabled()) {
            return false;
        }
        if (str.contains("without armor") && boolSetting14 != null && !boolSetting14.isEnabled()) {
            return false;
        }
        return str.contains("streak") && boolSetting2 != null && !boolSetting2.isEnabled() ? false : !str.contains("kill") || boolSetting == null || boolSetting.isEnabled();
    }

    public FakeContractHUD() {
        super("FakeContractHUD", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.randomSetting,
                this.contractCountSetting,
                this.randomizeOnEnableSetting,
                this.autoRefreshSetting,
                this.refreshIntervalMinSetting,
                this.realisticSetting,
                this.staticTimeMinSetting,
                this.countdownTimeMinSetting,
                this.showSecondsSetting,
                this.noArmorContractsSetting,
                this.progressSpeedSetting,
                this.customContract3Setting,
                this.colorCodeTimerSetting
            }
        );
        instance = this;
    }

    public void process() {
        if (this.checkState44() && !this.entries.isEmpty()) {
            long now = System.currentTimeMillis();
            if (this.autoRefreshSetting.isEnabled()) {
                long l = now - this.timestamp;
                long l2 = (long)(this.refreshIntervalMinSetting.computeDouble() * 60.0 * 1000.0);
                if (l >= l2) {
                    this.process3();
                    this.timestamp = now;
                    return;
                }
            }

            for (FakeContractHUD.Entry2 entry2 : this.entries) {
                this.handleEntry2(entry2, now);
            }
        }
    }

    private void handleEntry2(FakeContractHUD.Entry2 entry2, long l) {
        if (this.realisticSetting.getText().equals("Countdown")) {
            long l2 = l - entry2.startTime;
            entry2.timestamp = Math.max(0L, entry2.timestamp2 - l2);
        }

        BooleanSetting boolSetting = (BooleanSetting)this.progressSpeedSetting.computeSetting("Simulate Progress");
        if (boolSetting != null && boolSetting.isEnabled()) {
            NumberSetting numberSetting = (NumberSetting)this.progressSpeedSetting.computeSetting("Progress Speed");
            if (numberSetting != null && entry2.count2 < entry2.count) {
                long l3 = l - entry2.timestamp3;
                if (l3 >= 10000.0 / numberSetting.computeDouble()) {
                    entry2.count2 = Math.min(entry2.count, entry2.count2 + 1);
                    entry2.timestamp3 = l;
                }
            }
        }
    }

    private String computeString(long l) {
        if (l <= 0L) {
            return "00:00";
        }
        long l2 = l / 1000L;
        long l3 = l2 / 60L;
        long l4 = l2 % 60L;
        BooleanSetting boolSetting = (BooleanSetting)this.colorCodeTimerSetting.computeSetting("Color Code Timer");
        String str = "";
        if (boolSetting != null && boolSetting.isEnabled()) {
            if (l3 >= 5L) {
                str = EnumChatFormatting.GREEN.toString();
            } else if (l3 >= 2L) {
                str = EnumChatFormatting.YELLOW.toString();
            } else {
                str = EnumChatFormatting.RED.toString();
            }
        }

        ModeSetting modeSetting = (ModeSetting)this.colorCodeTimerSetting.computeSetting("Timer Format");
        String str2 = modeSetting != null ? modeSetting.getText() : "MM:SS";
        switch (str2) {
            case "M:SS":
                return String.format("%s%d:%02d", str, l3, l4);
            case "Short":
                if (l3 > 0L) {
                    return String.format("%s%dm", str, l3);
                }

                return String.format("%s%ds", str, l4);
            case "MM:SS":
            default:
                return !this.showSecondsSetting.isEnabled() ? String.format("%s%02d:00", str, l3) : String.format("%s%02d:%02d", str, l3, l4);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.entries.clear();
    }

    private void handleStringSetting(StringSetting stringSetting) {
        if (stringSetting != null && !stringSetting.getValue().trim().isEmpty()) {
            String str = stringSetting.getValue().trim();
            long l = this.computeLong();
            this.entries.add(new FakeContractHUD.Entry2(str, 1, 0, l, System.currentTimeMillis()));
        }
    }

    private List<FakeContractHUD.Entry> computeList(String str) {
        var list = lookupMap.get(str);
        if (list == null) {
            return new ArrayList<>();
        }
        List list2 = new ArrayList();

        for (FakeContractHUD.Entry entry : list) {
            if (this.matchesEntry(entry)) {
                list2.add(entry);
            }
        }

        return (List<FakeContractHUD.Entry>)(list2.isEmpty() ? list : list2);
    }

    public void process3() {
        this.entries.clear();
        BooleanSetting boolSetting = (BooleanSetting)this.customContract3Setting.computeSetting("Enable Custom");
        if (boolSetting != null && boolSetting.isEnabled()) {
            this.process5();
        } else {
            String text = this.randomSetting.getText();
            if (text.equals("Random")) {
                text = ThreadLocalRandom.current().nextBoolean() ? "Quick" : "Big Time";
            }

            var list = this.computeList(text);
            if (!list.isEmpty()) {
                int n = (int)this.contractCountSetting.computeDouble();
                HashSet set = new HashSet();

                for (int i = 0; i < n; i++) {
                    int n2 = 0;

                    FakeContractHUD.Entry entry;
                    do {
                        entry = (FakeContractHUD.Entry)list.get(ThreadLocalRandom.current().nextInt(list.size()));
                    } while (set.contains(entry) && ++n2 < 50);

                    set.add(entry);
                    int n3 = entry.entries.get(ThreadLocalRandom.current().nextInt(entry.entries.size()));
                    String str = String.format(entry.text, n3);
                    long l = this.computeLong();
                    int n4 = this.computeInt(n3);
                    this.entries.add(new FakeContractHUD.Entry2(str, n3, n4, l, System.currentTimeMillis()));
                }
            }
        }
    }

    public List<String> computeList() {
        if (this.checkState44() && !this.entries.isEmpty()) {
            this.process();
            List list = new ArrayList();
            BooleanSetting boolSetting = (BooleanSetting)this.colorCodeTimerSetting.computeSetting("Show Contract Label");
            if (boolSetting != null && boolSetting.isEnabled()) {
                EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
                EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                String str = this.computeString(this.entries.get(0).timestamp);
                EnumChatFormatting enumChatFormatting = GREEN;
                list.add(YELLOW + "Contract: " + enumChatFormatting + str);
            } else {
                for (FakeContractHUD.Entry2 entry2 : this.entries) {
                    EnumChatFormatting YELLOW2 = EnumChatFormatting.YELLOW;
                    EnumChatFormatting GREEN2 = EnumChatFormatting.GREEN;
                    String str2 = this.computeString(entry2.timestamp);
                    EnumChatFormatting enumChatFormatting2 = GREEN2;
                    String str3 = YELLOW2 + "Contract: " + enumChatFormatting2 + str2;
                    list.add(str3);
                }
            }

            for (FakeContractHUD.Entry2 entry22 : this.entries) {
                BooleanSetting boolSetting2 = (BooleanSetting)this.progressSpeedSetting.computeSetting("Show Progress");
                String str4 = "";
                if (boolSetting2 != null && boolSetting2.isEnabled() && entry22.count > 0) {
                    str4 = String.format(" %s%d%s/%s%d", EnumChatFormatting.GREEN, entry22.count2, EnumChatFormatting.GRAY, EnumChatFormatting.WHITE, entry22.count);
                }

                list.add(EnumChatFormatting.WHITE + entry22.description + str4);
            }

            return list;
        }
        return new ArrayList<>();
    }

    public boolean checkState() {
        return !this.entries.isEmpty();
    }

    private long computeLong() {
        String text = this.realisticSetting.getText();
        switch (text) {
            case "Static":
                return (long)(this.staticTimeMinSetting.computeDouble() * 60.0 * 1000.0);
            case "Countdown":
                return (long)(this.countdownTimeMinSetting.computeDouble() * 60.0 * 1000.0);
            case "Realistic":
            default:
                byte b = 2;
                byte b2 = 15;
                int n = ThreadLocalRandom.current().nextInt(b, b2 + 1);
                return n * 60 * 1000L;
        }
    }

    public static class Entry {
        public final String text;
        public final List<Integer> entries;

        public Entry(String text, List<Integer> entries) {
            this.text = text;
            this.entries = entries;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FakeContractHUD.Entry && ((FakeContractHUD.Entry)obj).text.equals(this.text);
        }

        @Override
        public int hashCode() {
            return this.text.hashCode();
        }
    }

    public static class Entry2 {
        public String description;
        public int count;
        public int count2;
        public long timestamp;
        public long timestamp2;
        public long startTime;
        public long timestamp3;

        public Entry2(String description, int count, int n, long timestamp, long startTime) {
            this.description = description;
            this.count = count;
            this.count2 = n;
            this.timestamp = timestamp;
            this.timestamp2 = timestamp;
            this.startTime = startTime;
            this.timestamp3 = startTime;
        }
    }
}
