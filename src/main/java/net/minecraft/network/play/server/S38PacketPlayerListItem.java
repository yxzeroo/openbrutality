package net.minecraft.network.play.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.world.WorldSettings.GameType;

public class S38PacketPlayerListItem implements Packet<INetHandlerPlayClient> {
    private S38PacketPlayerListItem.Action action;
    private final List<S38PacketPlayerListItem.AddPlayerData> players = Lists.newArrayList();

    public S38PacketPlayerListItem() {
    }

    public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, EntityPlayerMP... players) {
        this.action = actionIn;

        for (EntityPlayerMP entity : players) {
            this.players
                .add(
                    new S38PacketPlayerListItem.AddPlayerData(
                        entity.getGameProfile(), entity.ping, entity.theItemInWorldManager.getGameType(), entity.getTabListDisplayName()
                    )
                );
        }
    }

    public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, Iterable<EntityPlayerMP> players) {
        this.action = actionIn;

        for (EntityPlayerMP entity : players) {
            this.players
                .add(
                    new S38PacketPlayerListItem.AddPlayerData(
                        entity.getGameProfile(), entity.ping, entity.theItemInWorldManager.getGameType(), entity.getTabListDisplayName()
                    )
                );
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.action = buf.readEnumValue(S38PacketPlayerListItem.Action.class);
        int n = buf.readVarIntFromBuffer();

        for (int i = 0; i < n; i++) {
            GameProfile gameProfile = null;
            int n2 = 0;
            GameType gameType = null;
            IChatComponent component = null;
            switch (this.action) {
                case ADD_PLAYER:
                    gameProfile = new GameProfile(buf.readUuid(), buf.readStringFromBuffer(16));
                    int n3 = buf.readVarIntFromBuffer();
                    int n4 = 0;

                    for (; n4 < n3; n4++) {
                        String str = buf.readStringFromBuffer(32767);
                        String str2 = buf.readStringFromBuffer(32767);
                        if (buf.readBoolean()) {
                            gameProfile.getProperties().put(str, new Property(str, str2, buf.readStringFromBuffer(32767)));
                        } else {
                            gameProfile.getProperties().put(str, new Property(str, str2));
                        }
                    }

                    gameType = GameType.getByID(buf.readVarIntFromBuffer());
                    n2 = buf.readVarIntFromBuffer();
                    if (buf.readBoolean()) {
                        component = buf.readChatComponent();
                    }
                    break;
                case UPDATE_GAME_MODE:
                    gameProfile = new GameProfile(buf.readUuid(), (String)null);
                    gameType = GameType.getByID(buf.readVarIntFromBuffer());
                    break;
                case UPDATE_LATENCY:
                    gameProfile = new GameProfile(buf.readUuid(), (String)null);
                    n2 = buf.readVarIntFromBuffer();
                    break;
                case UPDATE_DISPLAY_NAME:
                    gameProfile = new GameProfile(buf.readUuid(), (String)null);
                    if (buf.readBoolean()) {
                        component = buf.readChatComponent();
                    }
                    break;
                case REMOVE_PLAYER:
                    gameProfile = new GameProfile(buf.readUuid(), (String)null);
            }

            this.players.add(new S38PacketPlayerListItem.AddPlayerData(gameProfile, n2, gameType, component));
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
        buf.writeVarIntToBuffer(this.players.size());

        for (S38PacketPlayerListItem.AddPlayerData player : this.players) {
            switch (this.action) {
                case ADD_PLAYER:
                    buf.writeUuid(player.getProfile().getId());
                    buf.writeString(player.getProfile().getName());
                    buf.writeVarIntToBuffer(player.getProfile().getProperties().size());

                    for (Property property : player.getProfile().getProperties().values()) {
                        buf.writeString(property.getName());
                        buf.writeString(property.getValue());
                        if (property.hasSignature()) {
                            buf.writeBoolean(true);
                            buf.writeString(property.getSignature());
                        } else {
                            buf.writeBoolean(false);
                        }
                    }

                    buf.writeVarIntToBuffer(player.getGameMode().getID());
                    buf.writeVarIntToBuffer(player.getPing());
                    if (player.getDisplayName() == null) {
                        buf.writeBoolean(false);
                    } else {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(player.getDisplayName());
                    }
                    break;
                case UPDATE_GAME_MODE:
                    buf.writeUuid(player.getProfile().getId());
                    buf.writeVarIntToBuffer(player.getGameMode().getID());
                    break;
                case UPDATE_LATENCY:
                    buf.writeUuid(player.getProfile().getId());
                    buf.writeVarIntToBuffer(player.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    buf.writeUuid(player.getProfile().getId());
                    if (player.getDisplayName() == null) {
                        buf.writeBoolean(false);
                    } else {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(player.getDisplayName());
                    }
                    break;
                case REMOVE_PLAYER:
                    buf.writeUuid(player.getProfile().getId());
            }
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerListItem(this);
    }

    public List<S38PacketPlayerListItem.AddPlayerData> getEntries() {
        return this.players;
    }

    public S38PacketPlayerListItem.Action getAction() {
        return this.action;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
    }

    public static enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;
    }

    public class AddPlayerData {
        private final int ping;
        private final GameType gamemode;
        private final GameProfile profile;
        private final IChatComponent displayName;

        public AddPlayerData(GameProfile profile, int ping, GameType gamemode, IChatComponent displayName) {
            this.profile = profile;
            this.ping = ping;
            this.gamemode = gamemode;
            this.displayName = displayName;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getPing() {
            return this.ping;
        }

        public GameType getGameMode() {
            return this.gamemode;
        }

        public IChatComponent getDisplayName() {
            return this.displayName;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("latency", this.ping)
                .add("gameMode", this.gamemode)
                .add("profile", this.profile)
                .add("displayName", this.displayName == null ? null : Serializer.componentToJson(this.displayName))
                .toString();
        }
    }
}
