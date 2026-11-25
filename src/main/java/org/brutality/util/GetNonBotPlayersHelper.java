package org.brutality.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class GetNonBotPlayersHelper {
    public static final int count = 81;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<Vec3> computeList(Block block, int n) {
        return computeList(block, n, n, n);
    }

    private static BlockPos computeBlockPos(GetNonBotPlayersHelper.FarmerMode farmerMode) {
        List villagersByProfession = getVillagersByProfession(farmerMode);
        return villagersByProfession != null && !villagersByProfession.isEmpty() ? ((EntityVillager)villagersByProfession.get(0)).getPosition().add(0.0, 1.5, 0.0) : null;
    }

    public static boolean checkState() {
        return computeInt() >= 81;
    }

    public static BlockPos computeBlockPos2() {
        return computeBlockPos(GetNonBotPlayersHelper.FarmerMode.BLACKSMITH);
    }

    public static List<EntityPlayer> computeList() {
        return mc.theWorld == null
            ? Collections.emptyList()
            : mc.theWorld.loadedEntityList.stream().filter(EntityPlayer.class::isInstance).map(EntityPlayer.class::cast).collect(Collectors.toList());
    }

    public static BlockPos computeBlockPos4() {
        BlockPos pos = computeBlockPos(GetNonBotPlayersHelper.FarmerMode.BLACKSMITH);
        List<EntityVillager> villagersByProfession = getVillagersByProfession(GetNonBotPlayersHelper.FarmerMode.FARMER);
        if (pos != null && villagersByProfession != null && !villagersByProfession.isEmpty()) {
            EntityVillager entity = null;
            double MAX_VALUE = Double.MAX_VALUE;

            for (EntityVillager entity2 : villagersByProfession) {
                double dist = entity2.getDistance(pos.getX(), pos.getY(), pos.getZ());
                if (dist < MAX_VALUE) {
                    MAX_VALUE = dist;
                    entity = entity2;
                }
            }

            if (entity != null) {
                return entity.getPosition().add(0.0, 1.5, 0.0);
            }
        }

        return computeBlockPos("UPGRADES");
    }

    public static BlockPos computeBlockPos5() {
        return computeBlockPos(GetNonBotPlayersHelper.FarmerMode.CLERIC);
    }

    public static List<EntityPlayer> getNonBotPlayers() {
        return mc.theWorld == null
            ? Collections.emptyList()
            : mc.theWorld
                .loadedEntityList
                .stream()
                .filter(EntityPlayer.class::isInstance)
                .map(EntityPlayer.class::cast)
                .filter(player -> !BEASTHelper.matchesEntityPlayer2(player))
                .collect(Collectors.toList());
    }

    public static List<Vec3> computeList(Block block, int n, int n2, int n3) {
        EntityPlayerSP player = mc.thePlayer;
        List list = new ArrayList();
        int n4 = (int)(player.posX - n);
        int n5 = (int)player.posX + n;
        int n6 = (int)(player.posY - n2);
        int n7 = (int)player.posY + n2;
        int n8 = (int)(player.posZ - n3);
        int n9 = (int)player.posZ + n3;

        for (int i = n4; i < n5; i++) {
            for (int j = n6; j < n7; j++) {
                for (int k = n8; k < n9; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (mc.theWorld.getBlockState(pos).getBlock() == block) {
                        list.add(new Vec3(i, j, k));
                    }
                }
            }
        }

        return list;
    }

    public static BlockPos computeBlockPos3() {
        return computeBlockPos(GetNonBotPlayersHelper.FarmerMode.LIBRARIAN);
    }

    public static List<BlockPos> getVillagerPositions() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            var list = mc.theWorld
                .loadedEntityList
                .stream()
                .filter(EntityVillager.class::isInstance)
                .map(EntityVillager.class::cast)
                .map(Entity::getPosition)
                .map(pos -> pos.add(0.0, 1.5, 0.0))
                .collect(Collectors.toList());
            return list.isEmpty() ? null : list;
        }
        return null;
    }

    private GetNonBotPlayersHelper() {
    }

    private static List<EntityVillager> getVillagersByProfession(GetNonBotPlayersHelper.FarmerMode farmerMode) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            List list = mc.theWorld
                .loadedEntityList
                .stream()
                .filter(EntityVillager.class::isInstance)
                .map(EntityVillager.class::cast)
                .filter(entity -> entity.getProfession() == farmerMode.professionId)
                .collect(Collectors.toList());
            return list.isEmpty() ? null : list;
        }
        return null;
    }

    public static List<BlockPos> computeList(Block block) {
        try {
            WorldClient world = mc.theWorld;
            if (world == null) {
                return Collections.emptyList();
            }
            List list = new ArrayList();

            for (TileEntity tileEntity : world.loadedTileEntityList) {
                if (tileEntity != null && !tileEntity.isInvalid()) {
                    BlockPos pos = tileEntity.getPos();
                    World world2 = tileEntity.getWorld();
                    if (pos != null && world2 != null && world2.isBlockLoaded(pos) && world2.getBlockState(pos).getBlock() == block) {
                        list.add(pos);
                    }
                }
            }

            return list;
        } catch (Exception e) {
            DebugHelper.error("Error while getting tile positions for block: " + block.getLocalizedName(), e);
            return Collections.emptyList();
        }
    }

    public static List<EntityItem> computeList3() {
        return mc.theWorld == null
            ? Collections.emptyList()
            : mc.theWorld.loadedEntityList.stream().filter(EntityItem.class::isInstance).map(EntityItem.class::cast).collect(Collectors.toList());
    }

    public static List<Entity> computeList2() {
        if (mc.theWorld == null) {
            return Collections.emptyList();
        }
        List list = new ArrayList();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityArmorStand && entity.isInvisible()) {
                list.add(entity);
            }
        }

        return list;
    }

    public static int computeInt() {
        if (mc.thePlayer != null && mc.theWorld != null && mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfoMap() != null) {
            HashSet set = new HashSet();

            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (playerInfo != null && playerInfo.getGameProfile() != null) {
                    String name = playerInfo.getGameProfile().getName();
                    if (name != null && !name.isEmpty()) {
                        set.add(name);
                    }
                }
            }

            return set.size();
        }
        return -1;
    }

    private static BlockPos computeBlockPos(String str) {
        if (mc.theWorld == null) {
            return null;
        }
        EntityVillager entity = null;
        double MAX_VALUE = Double.MAX_VALUE;
        String str2 = str.toUpperCase(Locale.ENGLISH);

        for (Entity entity2 : mc.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityArmorStand) {
                String str3 = ExecuteCommandHelper.computeString6(entity2.getCustomNameTag());
                if (str3 != null && str3.toUpperCase(Locale.ENGLISH).contains(str2)) {
                    for (Entity entity3 : mc.theWorld.loadedEntityList) {
                        if (entity3 instanceof EntityVillager) {
                            double dist = entity3.getDistanceSqToEntity(entity2);
                            if (dist <= 36.0 && dist < MAX_VALUE) {
                                MAX_VALUE = dist;
                                entity = (EntityVillager)entity3;
                            }
                        }
                    }
                }
            }
        }

        return entity == null ? null : entity.getPosition().add(0.0, 1.5, 0.0);
    }

    public static BlockPos computeBlockPos() {
        BlockPos pos = computeBlockPos(GetNonBotPlayersHelper.FarmerMode.UNEMPLOYED);
        return pos != null ? pos : computeBlockPos("ITEMS");
    }

    public static EntityPlayer computeEntityPlayer(String str) {
        if (mc.theWorld == null) {
            return null;
        }
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer player && player.getName().equalsIgnoreCase(str)) {
                return player;
            }
        }

        return null;
    }

    public static enum FarmerMode {
        FARMER(0),
        LIBRARIAN(1),
        CLERIC(2),
        BLACKSMITH(3),
        UNEMPLOYED(5);

        public final int professionId;

        private static GetNonBotPlayersHelper.FarmerMode[] computeFarmerModeArray() {
            return new GetNonBotPlayersHelper.FarmerMode[]{FARMER, LIBRARIAN, CLERIC, BLACKSMITH, UNEMPLOYED};
        }

        private FarmerMode(int professionId) {
            this.professionId = professionId;
        }
    }
}
