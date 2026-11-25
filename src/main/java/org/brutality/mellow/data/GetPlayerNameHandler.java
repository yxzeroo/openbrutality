package org.brutality.mellow.data;

import java.util.List;
import java.util.Locale;

public class GetPlayerNameHandler {
    private String playerName;
    private List<String> entries;
    private List<String> entries2;
    private String text;
    private String text2;
    private String text3;
    private String text4;
    private String text5;
    private String text6;
    private String text7;
    private String text8;
    private String text9;
    private String text10;
    private long timestamp;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getText2() {
        return this.text2;
    }

    public void handleDouble(double d) {
        this.text4 = this.computeString(d);
    }

    public GetPlayerNameHandler(String playerName) {
        this.playerName = playerName;
    }

    public void handleInt5(int n) {
        this.text7 = String.valueOf(n);
    }

    public void handleInt3(int n) {
        this.text = String.valueOf(n);
    }

    public String getText() {
        return this.text;
    }

    public void setText7(String str) {
        this.text7 = str;
    }

    public void setText2(String str) {
        this.text2 = str;
    }

    public void handleInt6(int n) {
        this.text10 = String.valueOf(n);
    }

    public void handleDouble2(double d) {
        this.text9 = String.format(Locale.ROOT, "%.2f", d);
    }

    public String getText5() {
        return this.text5;
    }

    public List<String> getEntries2() {
        return this.entries2;
    }

    public GetPlayerNameHandler() {
    }

    public void handleInt4(int n) {
        this.text6 = String.valueOf(n);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText6() {
        return this.text6;
    }

    public String getText9() {
        return this.text9;
    }

    private String computeString2(double d) {
        if (d < 1.0) {
            return "§c" + String.format("%.2f", d);
        }
        if (d < 4.0) {
            return "§f" + String.format("%.2f", d);
        }
        if (d < 10.0) {
            return "§a" + String.format("%.2f", d);
        }
        if (d < 25.0) {
            return "§2" + String.format("%.2f", d);
        }
        return d < 50.0 ? "§6" + String.format("%.2f", d) : "§5" + String.format("%.2f", d);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setEntries2(List<String> list) {
        this.entries2 = list;
    }

    public String getText10() {
        return this.text10;
    }

    public String getText7() {
        return this.text7;
    }

    public void setText8(String str) {
        this.text8 = str;
    }

    public void handleInt2(int n) {
        this.text8 = String.valueOf(n);
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getText4() {
        return this.text4;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public String getText3() {
        return this.text3;
    }

    public void handleInt(int n) {
        this.text3 = String.valueOf(n);
    }

    public boolean checkState() {
        return this.entries2 != null && !this.entries2.isEmpty();
    }

    private String computeString(double d) {
        if (d < 1.0) {
            return "§c" + String.format("%.2f", d);
        }
        if (d < 4.0) {
            return "§f" + String.format("%.2f", d);
        }
        if (d < 10.0) {
            return "§a" + String.format("%.2f", d);
        }
        return d < 25.0 ? "§2" + String.format("%.2f", d) : "§6" + String.format("%.2f", d);
    }

    public void handleDouble3(double d) {
        this.text2 = this.computeString2(d);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public boolean checkState2() {
        return this.entries != null && !this.entries.isEmpty();
    }

    public void setText6(String str) {
        this.text6 = str;
    }

    public void setText4(String str) {
        this.text4 = str;
    }

    public void setText5(String str) {
        this.text5 = str;
    }

    public String getText8() {
        return this.text8;
    }

    public List<String> getEntries() {
        return this.entries;
    }

    public void setText3(String str) {
        this.text3 = str;
    }
}
