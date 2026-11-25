package org.brutality.notification.modern;

public class GetMessageHandler implements GetMessageApi {
    private final String message;
    private final long timestamp;
    private final ModernHandler.PlayerJoinMode playerJoinMode;
    private final ModernHandler.LowMode lowMode;
    private final long timestamp2;
    private final ModernHandler.NoneMode noneMode;
    private final String playerName;
    private final boolean flag;
    private ModernHandler.SlideInMode slideInMode;
    private long timestamp3;
    private float factor;
    private int posX;
    private int offsetY2;

    public void handleLong(long l) {
        long l2 = l - this.timestamp;
        long l3 = l - this.timestamp3;
        switch (this.slideInMode) {
            case SLIDE_IN:
                if (l3 >= 300L) {
                    this.slideInMode = ModernHandler.SlideInMode.DISPLAYING;
                    this.timestamp3 = l;
                    this.factor = 1.0F;
                    this.posX = 2;
                } else {
                    double d = l3 / 300.0;
                    this.posX = (int)(-10.0 + 12.0 * d);
                    this.factor = (float)(0.1 + 0.9 * d);
                }
                break;
            case DISPLAYING:
                if (l2 >= this.timestamp2 - 500L) {
                    this.slideInMode = ModernHandler.SlideInMode.FADE_OUT;
                    this.timestamp3 = l;
                }
                break;
            case FADE_OUT:
                if (l3 >= 500L) {
                    this.slideInMode = ModernHandler.SlideInMode.EXPIRED;
                } else {
                    double d2 = l3 / 500.0;
                    this.factor = (float)(1.0 - d2);
                    this.offsetY2 = (int)(6.0 * d2 * d2);
                }
            case EXPIRED:
        }
    }

    public ModernHandler.SlideInMode getSlideInMode() {
        return this.slideInMode;
    }

    public ModernHandler.NoneMode getNoneMode() {
        return this.noneMode;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public GetMessageHandler(String str, ModernHandler.PlayerJoinMode playerJoinMode) {
        this(str, playerJoinMode, ModernHandler.LowMode.NORMAL, null, true);
    }

    public int getOffsetY() {
        return this.offsetY2;
    }

    public int getPosX() {
        return this.posX;
    }

    public GetMessageHandler(String str, ModernHandler.PlayerJoinMode playerJoinMode, ModernHandler.LowMode lowMode, String playerName, boolean flag) {
        this.message = this.computeString(str, playerJoinMode, lowMode);
        this.timestamp = System.currentTimeMillis();
        this.playerJoinMode = playerJoinMode;
        this.lowMode = lowMode;
        this.playerName = playerName;
        this.flag = flag;
        this.timestamp2 = this.computeLong();
        this.noneMode = this.computeNoneMode();
        this.slideInMode = ModernHandler.SlideInMode.SLIDE_IN;
        this.timestamp3 = this.timestamp;
        this.factor = 0.1F;
        this.posX = -10;
        this.offsetY2 = 0;
    }

    private long computeLong() {
        long defaultDuration = this.playerJoinMode.getDefaultDuration();
        switch (this.lowMode) {
            case LOW:
                return defaultDuration - 1000L;
            case HIGH:
                return defaultDuration + 2000L;
            case CRITICAL:
                return defaultDuration + 4000L;
            case URGENT:
                return defaultDuration + 6000L;
            default:
                return defaultDuration;
        }
    }

    @Override
    public ModernHandler.LowMode computeLowMode() {
        return this.lowMode;
    }

    public boolean checkState() {
        return this.flag && this.noneMode != ModernHandler.NoneMode.NONE;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    @Override
    public boolean matchesLong(long l) {
        return this.slideInMode == ModernHandler.SlideInMode.EXPIRED || l - this.timestamp >= this.timestamp2;
    }

    private String computeString(String str, ModernHandler.PlayerJoinMode playerJoinMode, ModernHandler.LowMode lowMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(playerJoinMode.getPrefix()).append(" ");
        sb.append(lowMode.getColor());
        sb.append(str);
        sb.append("§f");
        return sb.toString();
    }

    public float getFactor() {
        return this.factor;
    }

    private ModernHandler.NoneMode computeNoneMode() {
        if (!this.flag) {
            return ModernHandler.NoneMode.NONE;
        }
        switch (this.lowMode) {
            case LOW:
                return ModernHandler.NoneMode.SOFT;
            case HIGH:
                return ModernHandler.NoneMode.LOUD;
            case CRITICAL:
            case URGENT:
                return ModernHandler.NoneMode.URGENT;
            case NORMAL:
                return this.playerJoinMode == ModernHandler.PlayerJoinMode.PLAYER_JUMP ? ModernHandler.NoneMode.LOUD : ModernHandler.NoneMode.NORMAL;
            default:
                return ModernHandler.NoneMode.NORMAL;
        }
    }

    @Override
    public long getTimestamp2() {
        return this.timestamp2;
    }

    @Override
    public ModernHandler.PlayerJoinMode computePlayerJoinMode() {
        return this.playerJoinMode;
    }

    public GetMessageHandler(String str, ModernHandler.PlayerJoinMode playerJoinMode, ModernHandler.LowMode lowMode) {
        this(str, playerJoinMode, lowMode, null, true);
    }
}
