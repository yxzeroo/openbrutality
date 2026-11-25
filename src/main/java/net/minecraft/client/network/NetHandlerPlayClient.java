package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.client.stream.MetadataCombat;
import net.minecraft.client.stream.MetadataPlayerDeath;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.DataWatcher.WatchableObject;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.client.C19PacketResourcePackStatus.Action;
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
import net.minecraft.network.play.server.S19PacketEntityStatus;
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
import net.minecraft.network.play.server.S20PacketEntityProperties.Snapshot;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.network.play.server.S42PacketCombatEvent.Event;
import net.minecraft.network.play.server.S45PacketTitle.Type;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.Brutality;
import org.brutality.event.impl.attack.AttackEvent2;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.GetHealthEvent;
import org.brutality.event.impl.velocity.GetEntityIdEvent;
import org.brutality.gui.ChangelogScreen;
import org.brutality.gui.chat.ParseIntComponent;
import org.brutality.module.impl.client.LagbackDebug;
import org.brutality.module.impl.client.PotionEffectDebug;
import org.brutality.module.impl.combat.Velocity;
import org.brutality.module.impl.movement.Stasis;
import org.brutality.module.impl.pit.hypixel.automation.AutoPitSpawn;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.pit.hypixel.utility.JumpBoostDetector;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;
import org.brutality.module.impl.pit.hypixel.utility.PodDetection;
import org.brutality.utils.combat.CombatHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.SomberHelper;
import org.brutality.utils.player.TickHelper;
import org.brutality.utils.render.UpdateAnimationsHelper;
import org.brutality.utils.world.WorldDownloadController;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
    private static final Logger logger = LogManager.getLogger();
    final NetworkManager netManager;
    private final GameProfile profile;
    private final GuiScreen guiScreenServer;
    Minecraft gameController;
    private WorldClient clientWorldController;
    private volatile boolean cleanedUp;
    private boolean doneLoadingTerrain;
    private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
    public int currentServerMaxPlayers = 20;
    private boolean field_147308_k = false;
    private final Random avRandomizer = new Random();

    public boolean isCleanedUp() {
        return this.cleanedUp;
    }

    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
        this.gameController = mcIn;
        this.guiScreenServer = p_i46300_2_;
        this.netManager = p_i46300_3_;
        this.profile = p_i46300_4_;
    }

    public void cleanup() {
        this.cleanedUp = true;
        this.clientWorldController = null;
    }

    @Override
    public void handleJoinGame(S01PacketJoinGame packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()),
            packetIn.getDimension(),
            packetIn.getDifficulty(),
            this.gameController.mcProfiler
        );
        this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.thePlayer.dimension = packetIn.getDimension();
        this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
        this.currentServerMaxPlayers = packetIn.getMaxPlayers();
        this.gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
        this.gameController.playerController.setGameType(packetIn.getGameType());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager
            .sendPacket(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
    }

    @Override
    public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            double d = packetIn.getX() / 32.0;
            double d2 = packetIn.getY() / 32.0;
            double d3 = packetIn.getZ() / 32.0;
            Object obj = null;
            if (packetIn.getType() == 10) {
                obj = EntityMinecart.func_180458_a(this.clientWorldController, d, d2, d3, EnumMinecartType.byNetworkID(packetIn.func_149009_m()));
            } else if (packetIn.getType() == 90) {
                Entity entityByID = this.clientWorldController.getEntityByID(packetIn.func_149009_m());
                if (entityByID instanceof EntityPlayer) {
                    obj = new EntityFishHook(this.clientWorldController, d, d2, d3, (EntityPlayer)entityByID);
                }

                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 60) {
                obj = new EntityArrow(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 61) {
                obj = new EntitySnowball(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 71) {
                obj = new EntityItemFrame(
                    this.clientWorldController,
                    new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3)),
                    EnumFacing.getHorizontal(packetIn.func_149009_m())
                );
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 77) {
                obj = new EntityLeashKnot(
                    this.clientWorldController, new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3))
                );
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 65) {
                obj = new EntityEnderPearl(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 72) {
                obj = new EntityEnderEye(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 76) {
                obj = new EntityFireworkRocket(this.clientWorldController, d, d2, d3, (ItemStack)null);
            } else if (packetIn.getType() == 63) {
                obj = new EntityLargeFireball(
                    this.clientWorldController, d, d2, d3, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
                );
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 64) {
                obj = new EntitySmallFireball(
                    this.clientWorldController, d, d2, d3, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
                );
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 66) {
                obj = new EntityWitherSkull(
                    this.clientWorldController, d, d2, d3, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
                );
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 62) {
                obj = new EntityEgg(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 73) {
                obj = new EntityPotion(this.clientWorldController, d, d2, d3, packetIn.func_149009_m());
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 75) {
                obj = new EntityExpBottle(this.clientWorldController, d, d2, d3);
                packetIn.func_149002_g(0);
            } else if (packetIn.getType() == 1) {
                obj = new EntityBoat(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 50) {
                obj = new EntityTNTPrimed(this.clientWorldController, d, d2, d3, (EntityLivingBase)null);
            } else if (packetIn.getType() == 78) {
                obj = new EntityArmorStand(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 51) {
                obj = new EntityEnderCrystal(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 2) {
                obj = new EntityItem(this.clientWorldController, d, d2, d3);
            } else if (packetIn.getType() == 70) {
                obj = new EntityFallingBlock(this.clientWorldController, d, d2, d3, Block.getStateById(packetIn.func_149009_m() & 65535));
                packetIn.func_149002_g(0);
            }

            if (obj != null) {
                ((Entity)obj).serverPosX = packetIn.getX();
                ((Entity)obj).serverPosY = packetIn.getY();
                ((Entity)obj).serverPosZ = packetIn.getZ();
                ((Entity)obj).rotationPitch = packetIn.getPitch() * 360 / 256.0F;
                ((Entity)obj).rotationYaw = packetIn.getYaw() * 360 / 256.0F;
                Entity[] parts = ((Entity)obj).getParts();
                if (parts != null) {
                    int n = packetIn.getEntityID() - ((Entity)obj).getEntityId();

                    for (int i = 0; i < parts.length; i++) {
                        parts[i].setEntityId(parts[i].getEntityId() + n);
                    }
                }

                ((Entity)obj).setEntityId(packetIn.getEntityID());
                this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), (Entity)obj);
                if (packetIn.func_149009_m() > 0) {
                    if (packetIn.getType() == 60) {
                        Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.func_149009_m());
                        if (entityByID2 instanceof EntityLivingBase && obj instanceof EntityArrow) {
                            ((EntityArrow)obj).shootingEntity = entityByID2;
                        }
                    }

                    ((Entity)obj).setVelocity(packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0);
                }

                if (obj instanceof EntityArrow) {
                    TickHelper.handleEntityArrow((EntityArrow)obj);
                }
            }
        }
    }

    @Override
    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            EntityXPOrb entity = new EntityXPOrb(this.clientWorldController, packetIn.getX() / 32.0, packetIn.getY() / 32.0, packetIn.getZ() / 32.0, packetIn.getXPValue());
            entity.serverPosX = packetIn.getX();
            entity.serverPosY = packetIn.getY();
            entity.serverPosZ = packetIn.getZ();
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.setEntityId(packetIn.getEntityID());
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
        }
    }

    @Override
    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            double d = packetIn.func_149051_d() / 32.0;
            double d2 = packetIn.func_149050_e() / 32.0;
            double d3 = packetIn.func_149049_f() / 32.0;
            EntityLightningBolt entity = null;
            if (packetIn.func_149053_g() == 1) {
                entity = new EntityLightningBolt(this.clientWorldController, d, d2, d3);
            }

            if (entity != null) {
                entity.serverPosX = packetIn.func_149051_d();
                entity.serverPosY = packetIn.func_149050_e();
                entity.serverPosZ = packetIn.func_149049_f();
                entity.rotationYaw = 0.0F;
                entity.rotationPitch = 0.0F;
                entity.setEntityId(packetIn.func_149052_c());
                this.clientWorldController.addWeatherEffect(entity);
            }
        }
    }

    @Override
    public void handleSpawnPainting(S10PacketSpawnPainting packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            EntityPainting entity = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
        }
    }

    @Override
    public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
            if (entityByID != null) {
                if (entityByID == Minecraft.getMinecraft().thePlayer) {
                    LagbackDebug.handleString("S12 received", packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0);
                    Stasis.handleFlag(false);
                    GetEntityIdEvent getEntityIdEvent = new GetEntityIdEvent(
                        packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0, packetIn.getEntityID()
                    );
                    getEntityIdEvent.process();
                    if (getEntityIdEvent.cancel2()) {
                        return;
                    }

                    entityByID.setVelocity(getEntityIdEvent.getX(), getEntityIdEvent.getY(), getEntityIdEvent.getZ());
                    Velocity.process4();
                } else {
                    entityByID.setVelocity(packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0);
                }
            }
        }
    }

    @Override
    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
            if (entityByID != null && packetIn.func_149376_c() != null) {
                float f = entityByID instanceof EntityLivingBase ? ((EntityLivingBase)entityByID).getHealth() : Float.NaN;
                if (entityByID instanceof EntityPlayer player) {
                    JumpBoostDetector.handleEntityPlayer(player);
                }

                entityByID.getDataWatcher().updateWatchedObjectsFromList(packetIn.func_149376_c());
                if (entityByID instanceof EntityPlayer player2) {
                    JumpBoostDetector.handleEntityPlayer(player2);
                }

                if (entityByID instanceof EntityLivingBase) {
                    for (WatchableObject watchableObject : packetIn.func_149376_c()) {
                        if (watchableObject.getDataValueId() == 6) {
                            new GetHealthEvent((EntityLivingBase)entityByID, f).process();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            double d = packetIn.getX() / 32.0;
            double d2 = packetIn.getY() / 32.0;
            double d3 = packetIn.getZ() / 32.0;
            float f = packetIn.getYaw() * 360 / 256.0F;
            float f2 = packetIn.getPitch() * 360 / 256.0F;
            if (this.getPlayerInfo(packetIn.getPlayer()) != null) {
                EntityOtherPlayerMP otherPlayer = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
                otherPlayer.prevPosX = otherPlayer.lastTickPosX = otherPlayer.serverPosX = packetIn.getX();
                otherPlayer.prevPosY = otherPlayer.lastTickPosY = otherPlayer.serverPosY = packetIn.getY();
                otherPlayer.prevPosZ = otherPlayer.lastTickPosZ = otherPlayer.serverPosZ = packetIn.getZ();
                int currentItemID = packetIn.getCurrentItemID();
                if (currentItemID == 0) {
                    otherPlayer.inventory.mainInventory[otherPlayer.inventory.currentItem] = null;
                } else {
                    otherPlayer.inventory.mainInventory[otherPlayer.inventory.currentItem] = new ItemStack(Item.getItemById(currentItemID), 1, 0);
                }

                otherPlayer.setPositionAndRotation(d, d2, d3, f, f2);
                this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), otherPlayer);
                var watchableObjects = packetIn.func_148944_c();
                if (watchableObjects != null) {
                    otherPlayer.getDataWatcher().updateWatchedObjectsFromList(watchableObjects);
                }
            }
        }
    }

    @Override
    public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
            if (entityByID != null) {
                double d = entityByID.serverPosX / 32.0;
                double d2 = entityByID.serverPosY / 32.0;
                double d3 = entityByID.serverPosZ / 32.0;
                entityByID.serverPosX = packetIn.getX();
                entityByID.serverPosY = packetIn.getY();
                entityByID.serverPosZ = packetIn.getZ();
                double d4 = entityByID.serverPosX / 32.0;
                double d5 = entityByID.serverPosY / 32.0;
                double d6 = entityByID.serverPosZ / 32.0;
                if (entityByID instanceof EntityPlayer) {
                    SomberHelper.handleEntityPlayer((EntityPlayer)entityByID, d, d2, d3, d4, d5, d6);
                }

                float f = packetIn.getYaw() * 360 / 256.0F;
                float f2 = packetIn.getPitch() * 360 / 256.0F;
                if (Math.abs(entityByID.posX - d4) < 0.03125 && Math.abs(entityByID.posY - d5) < 0.015625 && Math.abs(entityByID.posZ - d6) < 0.03125) {
                    entityByID.setPositionAndRotation2(entityByID.posX, entityByID.posY, entityByID.posZ, f, f2, 3, true);
                } else {
                    entityByID.setPositionAndRotation2(d4, d5, d6, f, f2, 3, true);
                }

                entityByID.onGround = packetIn.getOnGround();
            }
        }
    }

    @Override
    public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
            int heldItemHotbarIndex = packetIn.getHeldItemHotbarIndex();
            this.gameController.thePlayer.inventory.currentItem = heldItemHotbarIndex;
            this.gameController.playerController.onServerHeldSlot(heldItemHotbarIndex);
            this.netManager.onServerHeldSlot(heldItemHotbarIndex);
        }
    }

    @Override
    public void handleEntityMovement(S14PacketEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entity = packetIn.getEntity(this.clientWorldController);
            if (entity != null) {
                entity.serverPosX = entity.serverPosX + packetIn.func_149062_c();
                entity.serverPosY = entity.serverPosY + packetIn.func_149061_d();
                entity.serverPosZ = entity.serverPosZ + packetIn.func_149064_e();
                double d = entity.serverPosX / 32.0;
                double d2 = entity.serverPosY / 32.0;
                double d3 = entity.serverPosZ / 32.0;
                float f = packetIn.func_149060_h() ? packetIn.func_149066_f() * 360 / 256.0F : entity.rotationYaw;
                float f2 = packetIn.func_149060_h() ? packetIn.func_149063_g() * 360 / 256.0F : entity.rotationPitch;
                entity.setPositionAndRotation2(d, d2, d3, f, f2, 3, false);
                entity.onGround = packetIn.getOnGround();
            }
        }
    }

    @Override
    public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entity = packetIn.getEntity(this.clientWorldController);
            if (entity != null) {
                float f = packetIn.getYaw() * 360 / 256.0F;
                entity.setRotationYawHead(f);
            }
        }
    }

    @Override
    public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            for (int i = 0; i < packetIn.getEntityIDs().length; i++) {
                TickHelper.handleInt(packetIn.getEntityIDs()[i]);
                this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[i]);
            }
        }
    }

    @Override
    public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP player = this.gameController.thePlayer;
        LagbackDebug.handleS08PacketPlayerPosLook(packetIn, this.doneLoadingTerrain);
        Stasis.handleFlag(true);
        double x = packetIn.getX();
        double y = packetIn.getY();
        double z = packetIn.getZ();
        float yaw = packetIn.getYaw();
        float pitch = packetIn.getPitch();
        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
            x += player.posX;
        } else {
            player.motionX = 0.0;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
            y += player.posY;
        } else {
            player.motionY = 0.0;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
            z += player.posZ;
        } else {
            player.motionZ = 0.0;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += player.rotationPitch;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += player.rotationYaw;
        }

        float f = yaw % 360.0F;
        float f2 = pitch % 360.0F;
        if (this.doneLoadingTerrain) {
            SomberHelper.handleEntityPlayer(player, player.posX, player.posY, player.posZ, x, y, z);
        }

        player.setPositionAndRotation(x, y, z, yaw, pitch);
        this.netManager
            .sendPacket(
                new C03PacketPlayer.C06PacketPlayerPosLook(player.posX, player.getEntityBoundingBox().minY, player.posZ, player.rotationYaw, player.rotationPitch, false)
            );
        if (!this.doneLoadingTerrain) {
            this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
            this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
            this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen((GuiScreen)null);
        }
    }

    @Override
    public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            for (BlockUpdateData changedBlock : packetIn.getChangedBlocks()) {
                this.clientWorldController.invalidateRegionAndSetBlock(changedBlock.getPos(), changedBlock.getBlockState());
            }
        }
    }

    @Override
    public void handleChunkData(S21PacketChunkData packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            if (packetIn.func_149274_i()) {
                if (packetIn.getExtractedSize() == 0) {
                    this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), false);
                    return;
                }

                this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
            }

            this.clientWorldController
                .invalidateBlockReceiveRegion(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
            Chunk chunkFromChunkCoords = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
            chunkFromChunkCoords.fillChunk(packetIn.func_149272_d(), packetIn.getExtractedSize(), packetIn.func_149274_i());
            WorldDownloadController.handleWorld(this.clientWorldController, chunkFromChunkCoords);
            this.clientWorldController
                .markBlockRangeForRenderUpdate(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
            if (!packetIn.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
                chunkFromChunkCoords.resetRelightChecks();
            }
        }
    }

    @Override
    public void handleBlockChange(S23PacketBlockChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
        }
    }

    @Override
    public void handleDisconnect(S40PacketDisconnect packetIn) {
        this.netManager.closeChannel(packetIn.getReason());
    }

    @Override
    public void onDisconnect(IChatComponent reason) {
        this.gameController.loadWorld((WorldClient)null);
        if (this.guiScreenServer != null) {
            if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
                this.gameController
                    .displayGuiScreen(
                        new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).func_154321_a(), "disconnect.lost", reason).getProxy()
                    );
            } else {
                this.gameController
                    .displayGuiScreen(
                        new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason).withConnectionProxy(this.netManager.getConnectionProxy())
                    );
            }
        } else {
            this.gameController
                .displayGuiScreen(
                    new GuiDisconnected(new GuiMultiplayer(new ChangelogScreen()), "disconnect.lost", reason)
                        .withConnectionProxy(this.netManager.getConnectionProxy())
                );
        }
    }

    public void addToSendQueue(Packet p_147297_1_) {
        this.netManager.sendPacket(p_147297_1_);
    }

    @Override
    public void handleCollectItem(S0DPacketCollectItem packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
            Object entityByID2 = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.getEntityID());
            if (entityByID2 == null) {
                entityByID2 = this.gameController.thePlayer;
            }

            if (entityByID != null) {
                if (entityByID instanceof EntityXPOrb) {
                    this.clientWorldController
                        .playSoundAtEntity(entityByID, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                } else {
                    this.clientWorldController
                        .playSoundAtEntity(entityByID, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }

                this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, entityByID, (Entity)entityByID2, 0.5F));
                this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
            }
        }
    }

    @Override
    public void handleChat(S02PacketChat packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        LagbackDebug.handleString2(packetIn.getChatComponent() == null ? null : packetIn.getChatComponent().getUnformattedText());
        CombatHelper.handleInt(packetIn.getType(), packetIn.getChatComponent() == null ? null : packetIn.getChatComponent().getUnformattedText());
        if (packetIn.getType() != 2 && packetIn.getChatComponent() != null) {
            AutoPitSpawn.handleString2(packetIn.getChatComponent().getUnformattedText());
        }

        ChatEvent chatEvent = new ChatEvent(packetIn.getType(), packetIn.getChatComponent());
        chatEvent.process();
        if (Brutality.onChatReceivedHelper != null) {
            Brutality.onChatReceivedHelper.onChatReceived(chatEvent);
        }

        if (packetIn.getType() != 2 && Brutality.getBanDetector() != null && chatEvent.message != null) {
            try {
                Brutality.getBanDetector().handleString(chatEvent.message.getUnformattedText());
            } catch (Exception ignored) {
            }
        }

        if (packetIn.getType() != 2 && chatEvent.message != null) {
            try {
                FreshHelper.handleString(chatEvent.message.getUnformattedText());
            } catch (Exception ignored2) {
            }
        }

        if (!chatEvent.cancel2()) {
            if (packetIn.getType() == 2) {
                this.gameController.ingameGUI.setRecordPlaying(packetIn.getChatComponent(), false);
            } else {
                IChatComponent message = chatEvent.message;
                message = ParseIntComponent.computeIChatComponent(message);
                UpdateAnimationsHelper.handleIChatComponent2(message);
                this.gameController.ingameGUI.getChatGUI().printChatMessage(message);
            }
        }
    }

    @Override
    public void handleAnimation(S0BPacketAnimation packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
            if (entityByID != null) {
                try {
                    if (packetIn.getAnimationType() == 0) {
                        if (entityByID instanceof EntityLivingBase living) {
                            living.swingItem();
                        }
                    } else if (packetIn.getAnimationType() == 1) {
                        entityByID.performHurtAnimation();
                    } else if (packetIn.getAnimationType() == 2) {
                        if (entityByID instanceof EntityPlayer player) {
                            player.wakeUpPlayer(false, false, false);
                        }
                    } else if (packetIn.getAnimationType() == 4) {
                        this.gameController.effectRenderer.emitParticleAtEntity(entityByID, EnumParticleTypes.CRIT);
                    } else if (packetIn.getAnimationType() == 5) {
                        this.gameController.effectRenderer.emitParticleAtEntity(entityByID, EnumParticleTypes.CRIT_MAGIC);
                    }
                } catch (Exception e) {
                    logger.warn("Error handling animation for entity " + packetIn.getEntityID() + ": " + e.getMessage());
                }
            } else {
                logger.warn("Received animation packet for non-existent entity ID: " + packetIn.getEntityID());
            }
        }
    }

    @Override
    public void handleUseBed(S0APacketUseBed packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
        }
    }

    @Override
    public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            double d = packetIn.getX() / 32.0;
            double d2 = packetIn.getY() / 32.0;
            double d3 = packetIn.getZ() / 32.0;
            float f = packetIn.getYaw() * 360 / 256.0F;
            float f2 = packetIn.getPitch() * 360 / 256.0F;
            EntityLivingBase living = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.theWorld);
            living.serverPosX = packetIn.getX();
            living.serverPosY = packetIn.getY();
            living.serverPosZ = packetIn.getZ();
            living.renderYawOffset = living.rotationYawHead = packetIn.getHeadPitch() * 360 / 256.0F;
            Entity[] parts = living.getParts();
            if (parts != null) {
                int n = packetIn.getEntityID() - living.getEntityId();

                for (int i = 0; i < parts.length; i++) {
                    parts[i].setEntityId(parts[i].getEntityId() + n);
                }
            }

            living.setEntityId(packetIn.getEntityID());
            living.setPositionAndRotation(d, d2, d3, f, f2);
            living.motionX = packetIn.getVelocityX() / 8000.0F;
            living.motionY = packetIn.getVelocityY() / 8000.0F;
            living.motionZ = packetIn.getVelocityZ() / 8000.0F;
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), living);
            var watchableObjects = packetIn.func_149027_c();
            if (watchableObjects != null) {
                living.getDataWatcher().updateWatchedObjectsFromList(watchableObjects);
            }
        }
    }

    @Override
    public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            this.gameController.theWorld.setTotalWorldTime(packetIn.getTotalWorldTime());
            this.gameController.theWorld.setWorldTime(packetIn.getWorldTime());
        }
    }

    @Override
    public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            this.gameController.thePlayer.setSpawnPoint(packetIn.getSpawnPos(), true);
            this.gameController.theWorld.getWorldInfo().setSpawn(packetIn.getSpawnPos());
        }
    }

    @Override
    public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            var entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
            Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());
            if (packetIn.getLeash() == 0) {
                boolean flag = false;
                if (packetIn.getEntityId() == this.gameController.thePlayer.getEntityId()) {
                    entityByID = this.gameController.thePlayer;
                    if (entityByID2 instanceof EntityBoat) {
                        ((EntityBoat)entityByID2).setIsBoatEmpty(false);
                    }

                    flag = ((Entity)entityByID).ridingEntity == null && entityByID2 != null;
                } else if (entityByID2 instanceof EntityBoat) {
                    ((EntityBoat)entityByID2).setIsBoatEmpty(true);
                }

                if (entityByID == null) {
                    return;
                }

                ((Entity)entityByID).mountEntity(entityByID2);
                if (entityByID != this.gameController.thePlayer) {
                    PitDebugger.handleEntity((Entity)entityByID);
                }

                if (flag) {
                    GameSettings settings = this.gameController.gameSettings;
                    this.gameController
                        .ingameGUI
                        .setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode())), false);
                }
            } else if (packetIn.getLeash() == 1 && entityByID instanceof EntityLiving) {
                if (entityByID2 != null) {
                    ((EntityLiving)entityByID).setLeashedToEntity(entityByID2, false);
                } else {
                    ((EntityLiving)entityByID).clearLeashed(false, false);
                }
            }
        }
    }

    @Override
    public void handleEntityStatus(S19PacketEntityStatus packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entity = packetIn.getEntity(this.clientWorldController);
            if (entity != null) {
                if (packetIn.getOpCode() == 21) {
                    this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
                } else {
                    if (packetIn.getOpCode() == 9 && entity instanceof EntityPlayer player) {
                        JumpBoostDetector.handleEntityPlayer3(player);
                    }

                    entity.handleHealthUpdate(packetIn.getOpCode());
                    if (packetIn.getOpCode() == 2 && entity instanceof EntityLivingBase living) {
                        new AttackEvent2(living).process();
                    }

                    if (packetIn.getOpCode() == 2 && entity instanceof EntityPlayer player2) {
                        TickHelper.handleEntityPlayer(player2);
                        VenomedTimer.handleEntityPlayer3(player2);
                    }
                }
            }
        }
    }

    @Override
    public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        float health = this.gameController.thePlayer.getHealth();
        this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
        new GetHealthEvent(this.gameController.thePlayer, health).process();
        this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
    }

    @Override
    public void handleSetExperience(S1FPacketSetExperience packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.thePlayer.setXPStats(packetIn.func_149397_c(), packetIn.getTotalExperience(), packetIn.getLevel());
    }

    @Override
    public void handleRespawn(S07PacketRespawn packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            if (packetIn.getDimensionID() != this.gameController.thePlayer.dimension) {
                this.doneLoadingTerrain = false;
                Scoreboard scoreboard = this.clientWorldController.getScoreboard();
                this.clientWorldController = new WorldClient(
                    this,
                    new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()),
                    packetIn.getDimensionID(),
                    packetIn.getDifficulty(),
                    this.gameController.mcProfiler
                );
                this.clientWorldController.setWorldScoreboard(scoreboard);
                this.gameController.loadWorld(this.clientWorldController);
                this.gameController.thePlayer.dimension = packetIn.getDimensionID();
                this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
            }

            this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
            this.gameController.playerController.setGameType(packetIn.getGameType());
        }
    }

    @Override
    public void handleExplosion(S27PacketExplosion packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Explosion explosion = new Explosion(
            this.gameController.theWorld, (Entity)null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions()
        );
        explosion.doExplosionB(true);
        LagbackDebug.handleString("S27 received", packetIn.func_149149_c(), packetIn.func_149144_d(), packetIn.func_149147_e());
        Stasis.handleFlag(false);
        this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + packetIn.func_149149_c();
        this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + packetIn.func_149144_d();
        this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + packetIn.func_149147_e();
    }

    @Override
    public void handleOpenWindow(S2DPacketOpenWindow packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            EntityPlayerSP player = this.gameController.thePlayer;
            if ("minecraft:container".equals(packetIn.getGuiId())) {
                player.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
                player.openContainer.windowId = packetIn.getWindowId();
            } else if ("minecraft:villager".equals(packetIn.getGuiId())) {
                player.displayVillagerTradeGui(new NpcMerchant(player, packetIn.getWindowTitle()));
                player.openContainer.windowId = packetIn.getWindowId();
            } else if ("EntityHorse".equals(packetIn.getGuiId())) {
                Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
                if (entityByID instanceof EntityHorse) {
                    player.displayGUIHorse((EntityHorse)entityByID, new AnimalChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
                    player.openContainer.windowId = packetIn.getWindowId();
                }
            } else if (!packetIn.hasSlots()) {
                player.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
                player.openContainer.windowId = packetIn.getWindowId();
            } else {
                ContainerLocalMenu containerLocalMenu = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
                player.displayGUIChest(containerLocalMenu);
                player.openContainer.windowId = packetIn.getWindowId();
            }
        }
    }

    @Override
    public void handleSetSlot(S2FPacketSetSlot packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP player = this.gameController.thePlayer;
        if (packetIn.func_149175_c() == -1) {
            player.inventory.setItemStack(packetIn.func_149174_e());
        } else {
            boolean flag = false;
            if (this.gameController.currentScreen instanceof GuiContainerCreative gui) {
                flag = gui.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (packetIn.func_149175_c() == 0 && packetIn.func_149173_d() >= 36 && packetIn.func_149173_d() < 45) {
                ItemStack stack = player.inventoryContainer.getSlot(packetIn.func_149173_d()).getStack();
                if (packetIn.func_149174_e() != null && (stack == null || stack.stackSize < packetIn.func_149174_e().stackSize)) {
                    packetIn.func_149174_e().animationsToGo = 5;
                }

                player.inventoryContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            } else if (packetIn.func_149175_c() == player.openContainer.windowId && (packetIn.func_149175_c() != 0 || !flag)) {
                player.openContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            }
        }
    }

    @Override
    public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        ViaLoadingBase instance = ViaLoadingBase.getInstance();
        if (instance != null && instance.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_17)) {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), (short)0, false));
        } else {
            Container container = null;
            EntityPlayerSP player = this.gameController.thePlayer;
            if (player != null) {
                if (packetIn.getWindowId() == 0) {
                    container = player.inventoryContainer;
                } else if (packetIn.getWindowId() == player.openContainer.windowId) {
                    container = player.openContainer;
                }

                if (container != null && !packetIn.func_148888_e()) {
                    this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
                }
            }
        }
    }

    @Override
    public void handleWindowItems(S30PacketWindowItems packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP player = this.gameController.thePlayer;
        if (player != null) {
            if (packetIn.func_148911_c() == 0) {
                player.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
            } else if (packetIn.func_148911_c() == player.openContainer.windowId) {
                player.openContainer.putStacksInSlots(packetIn.getItemStacks());
            }
        }
    }

    @Override
    public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            var tileEntity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());
            if (!(tileEntity instanceof TileEntitySign)) {
                tileEntity = new TileEntitySign();
                tileEntity.setWorldObj(this.clientWorldController);
                tileEntity.setPos(packetIn.getSignPosition());
            }

            this.gameController.thePlayer.openEditSign((TileEntitySign)tileEntity);
        }
    }

    @Override
    public void handleUpdateSign(S33PacketUpdateSign packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        boolean flag = false;
        if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())
            && this.gameController.theWorld.getTileEntity(packetIn.getPos()) instanceof TileEntitySign tileEntitySign) {
            if (tileEntitySign.getIsEditable()) {
                System.arraycopy(packetIn.getLines(), 0, tileEntitySign.signText, 0, 4);
                tileEntitySign.markDirty();
            }

            flag = true;
        }

        if (!flag && this.gameController.thePlayer != null) {
            this.gameController
                .thePlayer
                .addChatMessage(
                    new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ())
                );
        }
    }

    @Override
    public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())) {
            TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
            int tileEntityType = packetIn.getTileEntityType();
            if (tileEntityType == 1 && tileEntity instanceof TileEntityMobSpawner
                || tileEntityType == 2 && tileEntity instanceof TileEntityCommandBlock
                || tileEntityType == 3 && tileEntity instanceof TileEntityBeacon
                || tileEntityType == 4 && tileEntity instanceof TileEntitySkull
                || tileEntityType == 5 && tileEntity instanceof TileEntityFlowerPot
                || tileEntityType == 6 && tileEntity instanceof TileEntityBanner) {
                tileEntity.readFromNBT(packetIn.getNbtCompound());
            }
        }
    }

    @Override
    public void handleWindowProperty(S31PacketWindowProperty packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP player = this.gameController.thePlayer;
        if (player != null) {
            if (player.openContainer != null && player.openContainer.windowId == packetIn.getWindowId()) {
                player.openContainer.updateProgressBar(packetIn.getVarIndex(), packetIn.getVarValue());
            }
        }
    }

    @Override
    public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
            if (entityByID != null) {
                if (entityByID instanceof EntityPlayer player) {
                    JumpBoostDetector.handleEntityPlayer2(player);
                }

                entityByID.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
            }
        }
    }

    @Override
    public void handleCloseWindow(S2EPacketCloseWindow packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.thePlayer != null) {
            this.gameController.thePlayer.closeScreenAndDropStack();
        }
    }

    @Override
    public void handleBlockAction(S24PacketBlockAction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            this.gameController.theWorld.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
        }
    }

    @Override
    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            this.gameController.theWorld.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
        }
    }

    @Override
    public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            for (int i = 0; i < packetIn.getChunkCount(); i++) {
                int chunkX = packetIn.getChunkX(i);
                int chunkZ = packetIn.getChunkZ(i);
                this.clientWorldController.doPreChunk(chunkX, chunkZ, true);
                this.clientWorldController.invalidateBlockReceiveRegion(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
                Chunk chunkFromChunkCoords = this.clientWorldController.getChunkFromChunkCoords(chunkX, chunkZ);
                chunkFromChunkCoords.fillChunk(packetIn.getChunkBytes(i), packetIn.getChunkSize(i), true);
                WorldDownloadController.handleWorld(this.clientWorldController, chunkFromChunkCoords);
                this.clientWorldController.markBlockRangeForRenderUpdate(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
                if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
                    chunkFromChunkCoords.resetRelightChecks();
                }
            }
        }
    }

    @Override
    public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            EntityPlayerSP player = this.gameController.thePlayer;
            int gameState = packetIn.getGameState();
            if (gameState == 6) {
                CombatHelper.process2();
                TickHelper.confirmedSelfHit();
            }

            float f = packetIn.func_149137_d();
            int n = MathHelper.floor_float(f + 0.5F);
            if (gameState >= 0 && gameState < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[gameState] != null && player != null) {
                player.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[gameState]));
            }

            if (gameState == 1) {
                this.clientWorldController.getWorldInfo().setRaining(true);
                this.clientWorldController.setRainStrength(0.0F);
            } else if (gameState == 2) {
                this.clientWorldController.getWorldInfo().setRaining(false);
                this.clientWorldController.setRainStrength(1.0F);
            } else if (gameState == 3) {
                this.gameController.playerController.setGameType(GameType.getByID(n));
            } else if (gameState == 4) {
                this.gameController.displayGuiScreen(new GuiWinGame());
            } else if (gameState == 5) {
                GameSettings settings = this.gameController.gameSettings;
                if (f == 0.0F) {
                    this.gameController.displayGuiScreen(new GuiScreenDemo());
                } else if (f == 101.0F) {
                    this.gameController
                        .ingameGUI
                        .getChatGUI()
                        .printChatMessage(
                            new ChatComponentTranslation(
                                "demo.help.movement",
                                GameSettings.getKeyDisplayString(settings.keyBindForward.getKeyCode()),
                                GameSettings.getKeyDisplayString(settings.keyBindLeft.getKeyCode()),
                                GameSettings.getKeyDisplayString(settings.keyBindBack.getKeyCode()),
                                GameSettings.getKeyDisplayString(settings.keyBindRight.getKeyCode())
                            )
                        );
                } else if (f == 102.0F) {
                    this.gameController
                        .ingameGUI
                        .getChatGUI()
                        .printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(settings.keyBindJump.getKeyCode())));
                } else if (f == 103.0F) {
                    this.gameController
                        .ingameGUI
                        .getChatGUI()
                        .printChatMessage(
                            new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(settings.keyBindInventory.getKeyCode()))
                        );
                }
            } else if (gameState == 6) {
                this.clientWorldController.playSound(player.posX, player.posY + player.getEyeHeight(), player.posZ, "random.successful_hit", 0.18F, 0.45F, false);
            } else if (gameState == 7) {
                this.clientWorldController.setRainStrength(f);
            } else if (gameState == 8) {
                this.clientWorldController.setThunderStrength(f);
            } else if (gameState == 10) {
                this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, player.posX, player.posY, player.posZ, 0.0, 0.0, 0.0, new int[0]);
                this.clientWorldController.playSound(player.posX, player.posY, player.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    public void handleMaps(S34PacketMaps packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        MapData mapData = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
        packetIn.setMapdataTo(mapData);
        this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(mapData);
    }

    @Override
    public void handleEffect(S28PacketEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            if (packetIn.isSoundServerwide()) {
                this.gameController.theWorld.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
            } else {
                this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
            }
        }
    }

    @Override
    public void handleStatistics(S37PacketStatistics packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        boolean flag = false;

        for (Entry entry : packetIn.func_148974_c().entrySet()) {
            StatBase key = (StatBase)entry.getKey();
            int val = (Integer)entry.getValue();
            if (key.isAchievement() && val > 0) {
                if (this.field_147308_k && this.gameController.thePlayer.getStatFileWriter().readStat(key) == 0) {
                    Achievement achievement = (Achievement)key;
                    this.gameController.guiAchievement.displayAchievement(achievement);
                    this.gameController.getTwitchStream().func_152911_a(new MetadataAchievement(achievement), 0L);
                    if (key == AchievementList.openInventory) {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                flag = true;
            }

            this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, key, val);
        }

        if (!this.field_147308_k && !flag && this.gameController.gameSettings.showInventoryAchievementHint) {
            this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
        }

        this.field_147308_k = true;
        if (this.gameController.currentScreen instanceof IProgressMeter) {
            ((IProgressMeter)this.gameController.currentScreen).doneLoading();
        }
    }

    @Override
    public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
            PotionEffectDebug.handleEntity(entityByID, packetIn.getEntityId(), packetIn.getEffectId() & 255, packetIn.getAmplifier() & 255, packetIn.getDuration(), false);
            if (entityByID instanceof EntityLivingBase) {
                PotionEffect effect = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier(), false, packetIn.func_179707_f());
                effect.setPotionDurationMax(packetIn.func_149429_c());
                PotionEffect activePotionEffect = ((EntityLivingBase)entityByID).getActivePotionEffect(Potion.poison);
                int n = activePotionEffect == null ? 0 : activePotionEffect.getDuration();
                ((EntityLivingBase)entityByID).addPotionEffect(effect);
                if (entityByID instanceof EntityPlayer && packetIn.getEffectId() == Potion.regeneration.id) {
                    PodDetection.handleEntityPlayer((EntityPlayer)entityByID, packetIn.getAmplifier(), packetIn.getDuration(), false);
                }

                if (entityByID == this.gameController.thePlayer && packetIn.getEffectId() == Potion.poison.id) {
                    VenomedTimer.handleInt(n, packetIn.getDuration());
                }
            }
        }
    }

    @Override
    public void handleCombatEvent(S42PacketCombatEvent packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.field_179775_c);
            EntityLivingBase living = entityByID instanceof EntityLivingBase ? (EntityLivingBase)entityByID : null;
            if (packetIn.eventType == Event.END_COMBAT) {
                long l = 1000 * packetIn.field_179772_d / 20;
                MetadataCombat metadataCombat = new MetadataCombat(this.gameController.thePlayer, living);
                this.gameController.getTwitchStream().func_176026_a(metadataCombat, 0L - l, 0L);
            } else if (packetIn.eventType == Event.ENTITY_DIED) {
                Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.field_179774_b);
                if (entityByID2 instanceof EntityPlayer) {
                    MetadataPlayerDeath metadataPlayerDeath = new MetadataPlayerDeath((EntityPlayer)entityByID2, living);
                    metadataPlayerDeath.func_152807_a(packetIn.deathMessage);
                    this.gameController.getTwitchStream().func_152911_a(metadataPlayerDeath, 0L);
                }
            }
        }
    }

    @Override
    public void handleServerDifficulty(S41PacketServerDifficulty packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            this.gameController.theWorld.getWorldInfo().setDifficulty(packetIn.getDifficulty());
            this.gameController.theWorld.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
        }
    }

    @Override
    public void handleCamera(S43PacketCamera packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entity = packetIn.getEntity(this.clientWorldController);
            if (entity != null) {
                this.gameController.setRenderViewEntity(entity);
            }
        }
    }

    @Override
    public void handleWorldBorder(S44PacketWorldBorder packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            packetIn.func_179788_a(this.clientWorldController.getWorldBorder());
        }
    }

    @Override
    public void handleTitle(S45PacketTitle packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Type type = packetIn.getType();
        String str = null;
        String str2 = null;
        String str3 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";
        switch (type) {
            case TITLE:
                str = str3;
                break;
            case SUBTITLE:
                str2 = str3;
                break;
            case RESET:
                this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
                this.gameController.ingameGUI.func_175177_a();
                return;
        }

        this.gameController.ingameGUI.displayTitle(str, str2, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
    }

    @Override
    public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn) {
        if (!this.netManager.isLocalChannel()) {
            this.netManager.setCompressionTreshold(packetIn.func_179760_a());
        }
    }

    @Override
    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn) {
        this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().length() == 0 ? null : packetIn.getHeader());
        this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().length() == 0 ? null : packetIn.getFooter());
    }

    @Override
    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
            PotionEffectDebug.handleEntity(entityByID, packetIn.getEntityId(), packetIn.getEffectId() & 0xFF, 0, 0, true);
            if (entityByID instanceof EntityLivingBase) {
                ((EntityLivingBase)entityByID).removePotionEffectClient(packetIn.getEffectId());
                if (entityByID instanceof EntityPlayer && packetIn.getEffectId() == Potion.regeneration.id) {
                    PodDetection.handleEntityPlayer((EntityPlayer)entityByID, 0, 0, true);
                }
            }
        }
    }

    @Override
    public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packetIn.getEntries()) {
            if (packetIn.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                this.playerInfoMap.remove(addPlayerData.getProfile().getId());
            } else {
                NetworkPlayerInfo playerInfo = this.playerInfoMap.get(addPlayerData.getProfile().getId());
                if (packetIn.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    playerInfo = new NetworkPlayerInfo(addPlayerData);
                    this.playerInfoMap.put(playerInfo.getGameProfile().getId(), playerInfo);
                }

                if (playerInfo != null) {
                    switch (packetIn.getAction()) {
                        case ADD_PLAYER:
                            playerInfo.setGameType(addPlayerData.getGameMode());
                            playerInfo.setResponseTime(addPlayerData.getPing());
                            break;
                        case UPDATE_GAME_MODE:
                            playerInfo.setGameType(addPlayerData.getGameMode());
                            break;
                        case UPDATE_LATENCY:
                            playerInfo.setResponseTime(addPlayerData.getPing());
                            break;
                        case UPDATE_DISPLAY_NAME:
                            playerInfo.setDisplayName(addPlayerData.getDisplayName());
                    }
                }
            }
        }
    }

    @Override
    public void handleKeepAlive(S00PacketKeepAlive packetIn) {
        this.addToSendQueue(new C00PacketKeepAlive(packetIn.func_149134_c()));
    }

    @Override
    public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP player = this.gameController.thePlayer;
        if (player != null) {
            player.capabilities.isFlying = packetIn.isFlying();
            player.capabilities.isCreativeMode = packetIn.isCreativeMode();
            player.capabilities.disableDamage = packetIn.isInvulnerable();
            player.capabilities.allowFlying = packetIn.isAllowFlying();
            player.capabilities.setFlySpeed(packetIn.getFlySpeed());
            player.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
        }
    }

    @Override
    public void handleTabComplete(S3APacketTabComplete packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        String[] parts = packetIn.func_149630_c();
        if (this.gameController.currentScreen instanceof GuiChat gui) {
            gui.onAutocompleteResponse(parts);
        }
    }

    @Override
    public void handleSoundEffect(S29PacketSoundEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.gameController.theWorld != null) {
            this.gameController.theWorld.playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSoundName(), packetIn.getVolume(), packetIn.getPitch(), false);
        }
    }

    @Override
    public void handleResourcePack(S48PacketResourcePackSend packetIn) {
        final String url = packetIn.getURL();
        final String hash = packetIn.getHash();
        if (url.startsWith("level://")) {
            String str = url.substring("level://".length());
            File file = new File(this.gameController.mcDataDir, "saves");
            File file2 = new File(file, str);
            if (file2.isFile()) {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.ACCEPTED));
                Futures.addCallback(this.gameController.getResourcePackRepository().setResourcePackInstance(file2), new FutureCallback<Object>() {
                    @Override
                    public void onSuccess(Object p_onSuccess_1_) {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.SUCCESSFULLY_LOADED));
                    }

                    @Override
                    public void onFailure(Throwable p_onFailure_1_) {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.FAILED_DOWNLOAD));
                    }
                });
            } else {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.FAILED_DOWNLOAD));
            }
        } else if (this.gameController.getCurrentServerData() != null
            && this.gameController.getCurrentServerData().getResourceMode() == ServerResourceMode.ENABLED) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.ACCEPTED));
            Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(url, hash), new FutureCallback<Object>() {
                @Override
                public void onSuccess(Object p_onSuccess_1_2) {
                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.SUCCESSFULLY_LOADED));
                }

                @Override
                public void onFailure(Throwable p_onFailure_1_2) {
                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.FAILED_DOWNLOAD));
                }
            });
        } else if (this.gameController.getCurrentServerData() != null
            && this.gameController.getCurrentServerData().getResourceMode() != ServerResourceMode.PROMPT) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.DECLINED));
        } else {
            this.gameController
                .addScheduledTask(
                    new Runnable() {
                        @Override
                        public void run() {
                            NetHandlerPlayClient.this.gameController
                                .displayGuiScreen(
                                    new GuiYesNo(
                                        new GuiYesNoCallback() {
                                            @Override
                                            public void confirmClicked(boolean result, int id) {
                                                NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                                                if (result) {
                                                    if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                                        NetHandlerPlayClient.this.gameController
                                                            .getCurrentServerData()
                                                            .setResourceMode(ServerResourceMode.ENABLED);
                                                    }

                                                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.ACCEPTED));
                                                    Futures.addCallback(
                                                        NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(url, hash),
                                                        new FutureCallback<Object>() {
                                                            @Override
                                                            public void onSuccess(Object p_onSuccess_1_3) {
                                                                NetHandlerPlayClient.this.netManager
                                                                    .sendPacket(new C19PacketResourcePackStatus(hash, Action.SUCCESSFULLY_LOADED));
                                                            }

                                                            @Override
                                                            public void onFailure(Throwable p_onFailure_1_3) {
                                                                NetHandlerPlayClient.this.netManager
                                                                    .sendPacket(new C19PacketResourcePackStatus(hash, Action.FAILED_DOWNLOAD));
                                                            }
                                                        }
                                                    );
                                                } else {
                                                    if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                                        NetHandlerPlayClient.this.gameController
                                                            .getCurrentServerData()
                                                            .setResourceMode(ServerResourceMode.DISABLED);
                                                    }

                                                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, Action.DECLINED));
                                                }

                                                ServerList.func_147414_b(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                                                NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                                            }
                                        },
                                        I18n.format("multiplayer.texturePrompt.line1"),
                                        I18n.format("multiplayer.texturePrompt.line2"),
                                        0
                                    )
                                );
                        }
                    }
                );
        }
    }

    @Override
    public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entity = packetIn.getEntity(this.clientWorldController);
            if (entity != null) {
                entity.clientUpdateEntityNBT(packetIn.getTagCompound());
            }
        }
    }

    @Override
    public void handleCustomPayload(S3FPacketCustomPayload packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if ("MC|TrList".equals(packetIn.getChannelName())) {
            PacketBuffer bufferData = packetIn.getBufferData();

            try {
                int n = bufferData.readInt();
                GuiScreen screen = this.gameController.currentScreen;
                if (screen != null && screen instanceof GuiMerchant && n == this.gameController.thePlayer.openContainer.windowId) {
                    IMerchant merchant = ((GuiMerchant)screen).getMerchant();
                    MerchantRecipeList merchantRecipeList = MerchantRecipeList.readFromBuf(bufferData);
                    merchant.setRecipes(merchantRecipeList);
                }
            } catch (IOException e) {
                logger.error("Couldn't load trade info", (Throwable)e);
            } finally {
                bufferData.release();
            }
        } else if ("MC|Brand".equals(packetIn.getChannelName())) {
            this.gameController.thePlayer.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
        } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
            ItemStack currentEquippedItem = this.gameController.thePlayer.getCurrentEquippedItem();
            if (currentEquippedItem != null && currentEquippedItem.getItem() == Items.written_book) {
                this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, currentEquippedItem, false));
            }
        }
    }

    @Override
    public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            if (packetIn.func_149338_e() == 0) {
                ScoreObjective objective = scoreboard.addScoreObjective(packetIn.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
                objective.setDisplayName(packetIn.func_149337_d());
                objective.setRenderType(packetIn.func_179817_d());
            } else {
                ScoreObjective objective2 = scoreboard.getObjective(packetIn.func_149339_c());
                if (packetIn.func_149338_e() == 1) {
                    scoreboard.removeObjective(objective2);
                } else if (packetIn.func_149338_e() == 2) {
                    objective2.setDisplayName(packetIn.func_149337_d());
                    objective2.setRenderType(packetIn.func_179817_d());
                }
            }
        }
    }

    @Override
    public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            ScoreObjective objective = scoreboard.getObjective(packetIn.getObjectiveName());
            if (packetIn.getScoreAction() == net.minecraft.network.play.server.S3CPacketUpdateScore.Action.CHANGE) {
                Score valueFromObjective = scoreboard.getValueFromObjective(packetIn.getPlayerName(), objective);
                valueFromObjective.setScorePoints(packetIn.getScoreValue());
            } else if (packetIn.getScoreAction() == net.minecraft.network.play.server.S3CPacketUpdateScore.Action.REMOVE) {
                if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName())) {
                    scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
                } else if (objective != null) {
                    scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), objective);
                }
            }
        }
    }

    @Override
    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            if (packetIn.func_149370_d().length() == 0) {
                scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), (ScoreObjective)null);
            } else {
                ScoreObjective objective = scoreboard.getObjective(packetIn.func_149370_d());
                scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), objective);
            }
        }
    }

    @Override
    public void handleTeams(S3EPacketTeams packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            ScorePlayerTeam team;
            if (packetIn.getAction() == 0) {
                team = scoreboard.createTeam(packetIn.getName());
            } else {
                team = scoreboard.getTeam(packetIn.getName());
            }

            if (team != null) {
                if (packetIn.getAction() == 0 || packetIn.getAction() == 2) {
                    team.setTeamName(packetIn.getDisplayName());
                    team.setNamePrefix(packetIn.func_149311_e());
                    team.setNameSuffix(packetIn.func_149309_f());
                    team.setChatFormat(EnumChatFormatting.func_175744_a(packetIn.func_179813_h()));
                    team.func_98298_a(packetIn.func_149308_i());
                    EnumVisible enumVisible = EnumVisible.func_178824_a(packetIn.func_179814_i());
                    if (enumVisible != null) {
                        team.setNameTagVisibility(enumVisible);
                    }
                }

                if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
                    for (String player : packetIn.getPlayers()) {
                        scoreboard.addPlayerToTeam(player, packetIn.getName());
                    }
                }

                if (packetIn.getAction() == 4) {
                    for (String player2 : packetIn.getPlayers()) {
                        if (scoreboard.getPlayersTeam(player2) == team) {
                            scoreboard.removePlayerFromTeam(player2, team);
                        }
                    }
                }

                if (packetIn.getAction() == 1) {
                    scoreboard.removeTeam(team);
                }
            }
        }
    }

    @Override
    public void handleParticles(S2APacketParticles packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            if (packetIn.getParticleCount() == 0) {
                double d = packetIn.getParticleSpeed() * packetIn.getXOffset();
                double d2 = packetIn.getParticleSpeed() * packetIn.getYOffset();
                double d3 = packetIn.getParticleSpeed() * packetIn.getZOffset();

                try {
                    this.clientWorldController
                        .spawnParticle(
                            packetIn.getParticleType(),
                            packetIn.isLongDistance(),
                            packetIn.getXCoordinate(),
                            packetIn.getYCoordinate(),
                            packetIn.getZCoordinate(),
                            d,
                            d2,
                            d3,
                            packetIn.getParticleArgs()
                        );
                } catch (Throwable e) {
                    logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
                }
            } else {
                for (int i = 0; i < packetIn.getParticleCount(); i++) {
                    double d4 = this.avRandomizer.nextGaussian() * packetIn.getXOffset();
                    double d5 = this.avRandomizer.nextGaussian() * packetIn.getYOffset();
                    double d6 = this.avRandomizer.nextGaussian() * packetIn.getZOffset();
                    double d7 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
                    double d8 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
                    double d9 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();

                    try {
                        this.clientWorldController
                            .spawnParticle(
                                packetIn.getParticleType(),
                                packetIn.isLongDistance(),
                                packetIn.getXCoordinate() + d4,
                                packetIn.getYCoordinate() + d5,
                                packetIn.getZCoordinate() + d6,
                                d7,
                                d8,
                                d9,
                                packetIn.getParticleArgs()
                            );
                    } catch (Throwable e2) {
                        logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
                        return;
                    }
                }
            }
        }
    }

    public static void applyServerAttributeModifiers(IAttributeInstance attribute, Collection<AttributeModifier> modifiers) {
        attribute.removeAllModifiers();

        for (AttributeModifier attributeModifier : modifiers) {
            AttributeModifier modifier = attribute.getModifier(attributeModifier.getID());
            if (modifier != null) {
                attribute.removeModifier(modifier);
            }

            attribute.applyModifier(attributeModifier);
        }
    }

    @Override
    public void handleEntityProperties(S20PacketEntityProperties packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        if (this.clientWorldController != null) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
            if (entityByID != null) {
                if (!(entityByID instanceof EntityLivingBase)) {
                    throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entityByID + ")");
                }

                BaseAttributeMap attributeMap = ((EntityLivingBase)entityByID).getAttributeMap();

                for (Snapshot snapshot : packetIn.func_149441_d()) {
                    IAttributeInstance attributeInstanceByName = attributeMap.getAttributeInstanceByName(snapshot.func_151409_a());
                    if (attributeInstanceByName == null) {
                        attributeInstanceByName = attributeMap.registerAttribute(new RangedAttribute((IAttribute)null, snapshot.func_151409_a(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
                    }

                    attributeInstanceByName.setBaseValue(snapshot.func_151410_b());
                    applyServerAttributeModifiers(attributeInstanceByName, snapshot.func_151408_c());
                }
            }
        }
    }

    private Entity getSafeEntity(int entityId) {
        return this.clientWorldController == null ? null : this.clientWorldController.getEntityByID(entityId);
    }

    public NetworkManager getNetworkManager() {
        return this.netManager;
    }

    public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
        return this.playerInfoMap.values();
    }

    public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_) {
        return this.playerInfoMap.get(p_175102_1_);
    }

    public NetworkPlayerInfo getPlayerInfo(String p_175104_1_) {
        for (NetworkPlayerInfo playerInfo : this.playerInfoMap.values()) {
            if (playerInfo.getGameProfile().getName().equals(p_175104_1_)) {
                return playerInfo;
            }
        }

        return null;
    }

    public GameProfile getGameProfile() {
        return this.profile;
    }
}
