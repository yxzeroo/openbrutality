package org.brutality.utils.notifications;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.brutality.notification.InfoHandler;
import org.brutality.utils.hypixel.BanDetector;
import org.brutality.utils.hypixel.HypixelBan;

public class GetReasonHelper {
    private final String username;
    private final HypixelBan hypixelBan;
    private final String text;
    private final String reason;
    private final BanDetector.TemporaryBanMode temporaryBanMode;
    private boolean active2;
    private long timestamp;
    private static final long timestamp2 = 10000L;
    private static final long timestamp3 = 400L;
    private static final long timestamp4 = 800L;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public String getReason() {
        return this.reason;
    }

    public void render() {
        if (this.active2) {
            long l = System.currentTimeMillis() - this.timestamp;
            if (l > 10000L) {
                this.active2 = false;
            } else {
                float f;
                if (l < 400L) {
                    f = (float)l / 400.0F;
                } else if (l > 9200L) {
                    f = (float)(10000L - l) / 800.0F;
                } else {
                    f = 1.0F;
                }

                float f2 = Math.max(0.0F, Math.min(1.0F, f));
                float f3 = l < 400L ? (float)l / 400.0F : 1.0F;
                float f4 = 1.0F - (1.0F - f3) * (1.0F - f3) * (1.0F - f3);
                ScaledResolution sr = new ScaledResolution(mc);
                int scaledWidth = sr.getScaledWidth();
                int n = Math.min(310, scaledWidth - 24);
                if (n >= 120 && mc.fontRendererObj != null) {
                    String str = switch (this.temporaryBanMode) {
                        case ACCOUNT_BLOCKED -> "Security Ban";
                        case TEMPORARY_BAN -> "Temporary Ban";
                        default -> this.temporaryBanMode.getDisplayName();
                    };
                    String str2 = this.username + "  •  " + (this.reason != null && !this.reason.isBlank() ? this.reason : "Reason unavailable");
                    if (this.text != null && !this.text.isBlank() && !"Unknown".equalsIgnoreCase(this.text)) {
                        String str3 = this.temporaryBanMode == BanDetector.TemporaryBanMode.ACCOUNT_BLOCKED ? "Block ID " : "Ban ID ";
                        String text = this.text;
                        String str4 = str3;
                        str2 = str2 + "  •  " + str4 + text;
                    }

                    FontRenderer fontRenderer = mc.fontRendererObj;
                    ResourceLocation location = InfoHandler.computeResourceLocation("warning");
                    int n2 = InfoHandler.computeInt(fontRenderer, str2, n);
                    InfoHandler.handleFontRenderer(
                        fontRenderer,
                        scaledWidth - n - 12,
                        12.0F - n2 * (1.0F - f4),
                        n,
                        str,
                        str2,
                        location,
                        this.temporaryBanMode.getColor(),
                        f2,
                        1.0F - (float)l / 10000.0F
                    );
                }
            }
        }
    }

    public boolean isActive() {
        return this.active2;
    }

    public BanDetector.TemporaryBanMode getTemporaryBanMode() {
        return this.temporaryBanMode;
    }

    public String getUsername() {
        return this.username;
    }

    public String x() {
        return this.text;
    }

    public GetReasonHelper(String username, HypixelBan hypixelBan, String text, String reason, BanDetector.TemporaryBanMode temporaryBanMode) {
        this.username = username;
        this.hypixelBan = hypixelBan;
        this.text = text;
        this.reason = reason;
        this.temporaryBanMode = temporaryBanMode;
        this.active2 = false;
    }

    public void process() {
        this.active2 = true;
        this.timestamp = System.currentTimeMillis();
    }

    public HypixelBan getHypixelBan() {
        return this.hypixelBan;
    }
}
