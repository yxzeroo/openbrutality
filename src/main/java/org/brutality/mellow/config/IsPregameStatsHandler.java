package org.brutality.mellow.config;

import lombok.Generated;
import org.brutality.mellow.Mellow;

public class IsPregameStatsHandler {
    public boolean autoWho = true;
    public boolean tabStats = true;
    public boolean showTags = false;
    public boolean printStats = false;
    public boolean pregameStats = true;
    public boolean autoSkinDenick = true;
    public int minFkdr = -1;
    public int statsProvider = 0;
    public boolean urchinEnabled = false;
    public boolean showUrchinTags = true;
    public String urchinKey = "";
    public boolean seraphEnabled = false;
    public boolean showSeraphTags = true;
    public String seraphKey = "";
    public int pingProvider = 0;
    public String polsuKey = "";
    public boolean numberDenicker = false;
    public String auroraKey = "";
    public boolean fuzzyMatch = true;

    @Generated
    public boolean isPregameStats() {
        return this.pregameStats;
    }

    @Generated
    public boolean isAutoSkinDenick() {
        return this.autoSkinDenick;
    }

    @Generated
    public boolean isShowSeraphTags() {
        return this.showSeraphTags;
    }

    @Generated
    public String getAuroraKey() {
        return this.auroraKey;
    }

    @Generated
    public boolean isSeraphEnabled() {
        return this.seraphEnabled;
    }

    @Generated
    public boolean isNumberDenicker() {
        return this.numberDenicker;
    }

    @Generated
    public boolean isPrintStats() {
        return this.printStats;
    }

    @Generated
    public int getMinFkdr() {
        return this.minFkdr;
    }

    @Generated
    public boolean isShowTags() {
        return this.showTags;
    }

    @Generated
    public boolean isShowUrchinTags() {
        return this.showUrchinTags;
    }

    @Generated
    public boolean isUrchinEnabled() {
        return this.urchinEnabled;
    }

    @Generated
    public boolean isAutoWho() {
        return this.autoWho;
    }

    @Generated
    public String getUrchinKey() {
        return this.urchinKey;
    }

    @Generated
    public boolean isTabStats() {
        return this.tabStats;
    }

    @Generated
    public String getPolsuKey() {
        return this.polsuKey;
    }

    @Generated
    public String getSeraphKey() {
        return this.seraphKey;
    }

    @Generated
    public int getPingProvider() {
        return this.pingProvider;
    }

    @Generated
    public int getStatsProvider() {
        return this.statsProvider;
    }

    @Generated
    public boolean isFuzzyMatch() {
        return this.fuzzyMatch;
    }

    public static IsPregameStatsHandler computeIsPregameStatsHandler(Mellow mellow) {
        IsPregameStatsHandler isPregameStatsHandler = new IsPregameStatsHandler();
        isPregameStatsHandler.autoWho = mellow.getAutoWhoSetting().isEnabled();
        isPregameStatsHandler.tabStats = mellow.getTabStatsSetting().isEnabled();
        isPregameStatsHandler.showTags = mellow.getShowTagsSetting().isEnabled();
        isPregameStatsHandler.printStats = mellow.getPrintStatsSetting().isEnabled();
        isPregameStatsHandler.pregameStats = mellow.getPregameStatsSetting().isEnabled();
        isPregameStatsHandler.autoSkinDenick = mellow.getAutoSkinDenickSetting().isEnabled();
        isPregameStatsHandler.minFkdr = (int)mellow.getMinFKDRSetting().computeDouble();
        String text = mellow.getAbyssSetting().getText();
        isPregameStatsHandler.statsProvider = text.equals("Abyss") ? 1 : 0;
        isPregameStatsHandler.urchinEnabled = mellow.getUrchinEnabledSetting().isEnabled();
        isPregameStatsHandler.showUrchinTags = mellow.getShowUrchinTagsSetting().isEnabled();
        isPregameStatsHandler.urchinKey = mellow.getUrchinKeySetting().getValue();
        isPregameStatsHandler.seraphEnabled = mellow.getSeraphEnabledSetting().isEnabled();
        isPregameStatsHandler.showSeraphTags = mellow.getShowSeraphTagsSetting().isEnabled();
        isPregameStatsHandler.seraphKey = mellow.getSeraphKeySetting().getValue();
        String text2 = mellow.getUrchinSetting().getText();
        isPregameStatsHandler.pingProvider = text2.equals("Polsu") ? 1 : (text2.equals("Urchin") ? 2 : 0);
        isPregameStatsHandler.polsuKey = mellow.getPolsuKeySetting().getValue();
        isPregameStatsHandler.numberDenicker = mellow.getNumberDenickerSetting().isEnabled();
        isPregameStatsHandler.auroraKey = mellow.getAuroraKeySetting().getValue();
        isPregameStatsHandler.fuzzyMatch = mellow.getFuzzyMatchSetting().isEnabled();
        return isPregameStatsHandler;
    }
}
