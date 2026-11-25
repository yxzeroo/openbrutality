package org.brutality.core.itemstats;

import net.minecraft.util.EnumChatFormatting;

public class SerializeHandler {
    private int count;
    private int count2;
    private int count3;
    private double amount;
    private double amount2;

    public String serialize() {
        return this.count + "," + this.count2 + "," + this.count3 + "," + this.amount + "," + this.amount2;
    }

    public void handleInt2(int n) {
        this.count3 += n;
    }

    public void handleInt3(int n) {
        this.count2 += n;
    }

    public void handleDouble(double d) {
        this.amount2 += d;
    }

    public static SerializeHandler computeSerializeHandler(String str) {
        String[] parts = str.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid item stats");
        }
        SerializeHandler serializeHandler = new SerializeHandler();
        serializeHandler.count = Integer.parseInt(parts[0]);
        serializeHandler.count2 = Integer.parseInt(parts[1]);
        serializeHandler.count3 = Integer.parseInt(parts[2]);
        serializeHandler.amount = Double.parseDouble(parts[3]);
        serializeHandler.amount2 = Double.parseDouble(parts[4]);
        return serializeHandler;
    }

    private static String computeString(double d) {
        if (d >= 1000000.0) {
            return String.format("%.2fM", d / 1000000.0);
        }
        if (d >= 1000.0) {
            return String.format("%.2fK", d / 1000.0);
        }
        return d == Math.rint(d) ? String.valueOf((int)d) : String.valueOf(d);
    }

    public void handleInt(int n) {
        this.count += n;
    }

    public void handleDouble2(double d) {
        this.amount += d;
    }

    public boolean isEmpty() {
        return this.count == 0 && this.count2 == 0 && this.count3 == 0 && this.amount == 0.0 && this.amount2 == 0.0;
    }

    public String computeString() {
        String str = EnumChatFormatting.WHITE + " | ";
        EnumChatFormatting RED = EnumChatFormatting.RED;
        int count = this.count;
        EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
        int n = this.count2;
        EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
        String str2 = computeString(this.amount);
        EnumChatFormatting AQUA = EnumChatFormatting.AQUA;
        String str3 = computeString(this.amount2);
        int n2 = this.count3;
        EnumChatFormatting LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE;
        String str4 = str3;
        EnumChatFormatting enumChatFormatting = AQUA;
        String str5 = str2;
        EnumChatFormatting enumChatFormatting2 = GOLD;
        int n3 = n;
        EnumChatFormatting enumChatFormatting3 = GREEN;
        int n4 = count;
        return RED
            + "Kills: "
            + n4
            + str
            + enumChatFormatting3
            + "Assists: "
            + n3
            + "\n"
            + enumChatFormatting2
            + "Gold: "
            + str5
            + str
            + enumChatFormatting
            + "XP: "
            + str4
            + str
            + LIGHT_PURPLE
            + "Mystics: "
            + n2;
    }
}
