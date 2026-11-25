package org.brutality.script.packet;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;

public class C00PacketHandshakeHandler {
    private static final Map<String, Class<?>> lookupMap = new HashMap<>();
    private static final Map<String, Class<?>> lookupMap2 = new HashMap<>();

    public static boolean matchesString2(String str) {
        return lookupMap.containsKey(str);
    }

    static {
        lookupMap2.put("C00PacketHandshake", C00Handshake.class);
        lookupMap2.put("C01PacketChatMessage", C01PacketChatMessage.class);
        lookupMap2.put("C02PacketUseEntity", C02PacketUseEntity.class);
        lookupMap2.put("C03PacketPlayer", C03PacketPlayer.class);
        lookupMap2.put("C07PacketPlayerDigging", C07PacketPlayerDigging.class);
        lookupMap2.put("C08PacketPlayerBlockPlacement", C08PacketPlayerBlockPlacement.class);
        lookupMap2.put("C09PacketHeldItemChange", C09PacketHeldItemChange.class);
        lookupMap2.put("C0APacketAnimation", C0APacketAnimation.class);
        lookupMap2.put("C0BPacketEntityAction", C0BPacketEntityAction.class);
        lookupMap2.put("C0CPacketInput", C0CPacketInput.class);
        lookupMap2.put("C0DPacketCloseWindow", C0DPacketCloseWindow.class);
        lookupMap2.put("C0EPacketClickWindow", C0EPacketClickWindow.class);
        lookupMap2.put("C0FPacketConfirmTransaction", C0FPacketConfirmTransaction.class);
        lookupMap2.put("C10PacketCreativeInventoryAction", C10PacketCreativeInventoryAction.class);
        lookupMap2.put("C11PacketEnchantItem", C11PacketEnchantItem.class);
        lookupMap2.put("C12PacketUpdateSign", C12PacketUpdateSign.class);
        lookupMap2.put("C13PacketPlayerAbilities", C13PacketPlayerAbilities.class);
        lookupMap2.put("C14PacketTabComplete", C14PacketTabComplete.class);
        lookupMap2.put("C15PacketClientSettings", C15PacketClientSettings.class);
        lookupMap2.put("C16PacketClientStatus", C16PacketClientStatus.class);
        lookupMap2.put("C17PacketCustomPayload", C17PacketCustomPayload.class);
        lookupMap.put("S00PacketKeepAlive", S00PacketKeepAlive.class);
        lookupMap.put("S01PacketJoinGame", S01PacketJoinGame.class);
        lookupMap.put("S02PacketChat", S02PacketChat.class);
        lookupMap.put("S03PacketTimeUpdate", S03PacketTimeUpdate.class);
        lookupMap.put("S04PacketEntityEquipment", S04PacketEntityEquipment.class);
        lookupMap.put("S05PacketSpawnPosition", S05PacketSpawnPosition.class);
        lookupMap.put("S06PacketUpdateHealth", S06PacketUpdateHealth.class);
        lookupMap.put("S07PacketRespawn", S07PacketRespawn.class);
        lookupMap.put("S08PacketPlayerPosLook", S08PacketPlayerPosLook.class);
        lookupMap.put("S09PacketHeldItemChange", S09PacketHeldItemChange.class);
        lookupMap.put("S0APacketUseBed", S0APacketUseBed.class);
        lookupMap.put("S0BPacketAnimation", S0BPacketAnimation.class);
        lookupMap.put("S0CPacketSpawnPlayer", S0CPacketSpawnPlayer.class);
        lookupMap.put("S0DPacketCollectItem", S0DPacketCollectItem.class);
        lookupMap.put("S0EPacketSpawnObject", S0EPacketSpawnObject.class);
        lookupMap.put("S0FPacketSpawnMob", S0FPacketSpawnMob.class);
        lookupMap.put("S10PacketSpawnPainting", S10PacketSpawnPainting.class);
        lookupMap.put("S11PacketSpawnExperienceOrb", S11PacketSpawnExperienceOrb.class);
        lookupMap.put("S12PacketEntityVelocity", S12PacketEntityVelocity.class);
        lookupMap.put("S13PacketDestroyEntities", S13PacketDestroyEntities.class);
        lookupMap.put("S14PacketEntity", S14PacketEntity.class);
        lookupMap.put("S18PacketEntityTeleport", S18PacketEntityTeleport.class);
        lookupMap.put("S19PacketEntityHeadLook", S19PacketEntityHeadLook.class);
        lookupMap.put("S1BPacketEntityAttach", S1BPacketEntityAttach.class);
        lookupMap.put("S1CPacketEntityMetadata", S1CPacketEntityMetadata.class);
        lookupMap.put("S1DPacketEntityEffect", S1DPacketEntityEffect.class);
        lookupMap.put("S1EPacketRemoveEntityEffect", S1EPacketRemoveEntityEffect.class);
        lookupMap.put("S1FPacketSetExperience", S1FPacketSetExperience.class);
        lookupMap.put("S20PacketEntityProperties", S20PacketEntityProperties.class);
        lookupMap.put("S21PacketChunkData", S21PacketChunkData.class);
        lookupMap.put("S22PacketMultiBlockChange", S22PacketMultiBlockChange.class);
        lookupMap.put("S23PacketBlockChange", S23PacketBlockChange.class);
        lookupMap.put("S24PacketBlockAction", S24PacketBlockAction.class);
        lookupMap.put("S25PacketBlockBreakAnim", S25PacketBlockBreakAnim.class);
        lookupMap.put("S26PacketMapChunkBulk", S26PacketMapChunkBulk.class);
        lookupMap.put("S27PacketExplosion", S27PacketExplosion.class);
        lookupMap.put("S28PacketEffect", S28PacketEffect.class);
        lookupMap.put("S29PacketSoundEffect", S29PacketSoundEffect.class);
        lookupMap.put("S2APacketParticles", S2APacketParticles.class);
        lookupMap.put("S2BPacketChangeGameState", S2BPacketChangeGameState.class);
        lookupMap.put("S2CPacketSpawnGlobalEntity", S2CPacketSpawnGlobalEntity.class);
        lookupMap.put("S2DPacketOpenWindow", S2DPacketOpenWindow.class);
        lookupMap.put("S2EPacketCloseWindow", S2EPacketCloseWindow.class);
        lookupMap.put("S2FPacketSetSlot", S2FPacketSetSlot.class);
        lookupMap.put("S30PacketWindowItems", S30PacketWindowItems.class);
        lookupMap.put("S31PacketWindowProperty", S31PacketWindowProperty.class);
        lookupMap.put("S32PacketConfirmTransaction", S32PacketConfirmTransaction.class);
        lookupMap.put("S33PacketUpdateSign", S33PacketUpdateSign.class);
        lookupMap.put("S34PacketMaps", S34PacketMaps.class);
        lookupMap.put("S35PacketUpdateTileEntity", S35PacketUpdateTileEntity.class);
        lookupMap.put("S36PacketSignEditorOpen", S36PacketSignEditorOpen.class);
        lookupMap.put("S37PacketStatistics", S37PacketStatistics.class);
        lookupMap.put("S38PacketPlayerListItem", S38PacketPlayerListItem.class);
        lookupMap.put("S39PacketPlayerAbilities", S39PacketPlayerAbilities.class);
        lookupMap.put("S3APacketTabComplete", S3APacketTabComplete.class);
        lookupMap.put("S3BPacketScoreboardObjective", S3BPacketScoreboardObjective.class);
        lookupMap.put("S3CPacketUpdateScore", S3CPacketUpdateScore.class);
        lookupMap.put("S3DPacketDisplayScoreboard", S3DPacketDisplayScoreboard.class);
        lookupMap.put("S3EPacketTeams", S3EPacketTeams.class);
        lookupMap.put("S3FPacketCustomPayload", S3FPacketCustomPayload.class);
        lookupMap.put("S40PacketDisconnect", S40PacketDisconnect.class);
        lookupMap.put("S41PacketServerDifficulty", S41PacketServerDifficulty.class);
        lookupMap.put("S42PacketCombatEvent", S42PacketCombatEvent.class);
        lookupMap.put("S43PacketCamera", S43PacketCamera.class);
        lookupMap.put("S44PacketWorldBorder", S44PacketWorldBorder.class);
        lookupMap.put("S45PacketTitle", S45PacketTitle.class);
        lookupMap.put("S46PacketSetCompressionLevel", S46PacketSetCompressionLevel.class);
        lookupMap.put("S47PacketPlayerListHeaderFooter", S47PacketPlayerListHeaderFooter.class);
        lookupMap.put("S48PacketResourcePackSend", S48PacketResourcePackSend.class);
        lookupMap.put("S49PacketUpdateEntityNBT", S49PacketUpdateEntityNBT.class);
    }

    public static boolean matchesClassType(Class<?> clazz) {
        return lookupMap.containsValue(clazz);
    }

    public static Class<?> computeClassType2(String str) {
        return lookupMap2.get(str);
    }

    public static boolean matchesString(String str) {
        return lookupMap2.containsKey(str);
    }

    public static boolean matchesClassType2(Class<?> clazz) {
        return lookupMap2.containsValue(clazz);
    }

    public static Class<?> computeClassType(String str) {
        return lookupMap.get(str);
    }
}
