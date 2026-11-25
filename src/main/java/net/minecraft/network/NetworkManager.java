package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.event.CompressionReorderEvent;
import de.florianmichael.viamcp.MCPVLBPipeline;
import de.florianmichael.viamcp.ViaMCP;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.Proxy.Type;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.packet.SoundPacketEvent;
import org.brutality.module.impl.client.LagbackDebug;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.exploit.Disabler;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.visual.TargetHUD;
import org.brutality.proxy.ProxyConfig;
import org.brutality.proxy.ProxyManager;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.UtilsHelper;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.performance.WorldQueryCache;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
    private int lastSlot = -1;
    static final Logger logger = LogManager.getLogger();
    public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
    public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
    public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyLoadBase<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
        protected EpollEventLoopGroup load() {
            return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
        protected LocalEventLoopGroup load() {
            return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }
    };
    private final EnumPacketDirection direction;
    private final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Channel channel;
    private SocketAddress socketAddress;
    private INetHandler packetListener;
    private IChatComponent terminationReason;
    private boolean isEncrypted;
    private boolean disconnected;
    private ProxyConfig connectionProxy;

    public NetworkManager(EnumPacketDirection packetDirection) {
        this.direction = packetDirection;
    }

    @Override
    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();

        try {
            this.setConnectionState(EnumConnectionState.HANDSHAKING);
        } catch (Throwable e) {
            logger.fatal(e);
        }
    }

    public void setConnectionState(EnumConnectionState newState) {
        this.channel.attr(attrKeyConnectionState).set(newState);
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    @Override
    public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {
        ChatComponentTranslation chatComponentTranslation;
        if (p_exceptionCaught_2_ instanceof TimeoutException) {
            chatComponentTranslation = new ChatComponentTranslation("disconnect.timeout");
        } else {
            chatComponentTranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_);
        }

        this.closeChannel(chatComponentTranslation);
    }

    public void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_) throws Exception {
        GetPacketEvent getPacketEvent = new GetPacketEvent((Packet)p_channelRead0_2_, PacketDirection.INCOMING);
        boolean flag = p_channelRead0_2_.getClass().getSimpleName().contains("Window")
            || p_channelRead0_2_.getClass().getSimpleName().contains("S30Packet")
            || p_channelRead0_2_.getClass().getSimpleName().equals("S2FPacketSetSlot");
        if (!flag) {
            getPacketEvent.process();
            if (getPacketEvent.cancel2()) {
                return;
            }
        }

        if (p_channelRead0_2_ instanceof S29PacketSoundEffect) {
            SoundPacketEvent soundPacketEvent = new SoundPacketEvent((S29PacketSoundEffect)p_channelRead0_2_);
            soundPacketEvent.process();
            if (soundPacketEvent.cancel2()) {
                return;
            }

            if (soundPacketEvent.getS29PacketSoundEffect() != p_channelRead0_2_) {
                p_channelRead0_2_ = soundPacketEvent.getS29PacketSoundEffect();
            }
        }

        EventPacketReceive event = new EventPacketReceive((Packet<?>)p_channelRead0_2_);
        event.process();
        if (!event.cancel2()) {
            if (!CountMovementHelper.getInstance().isFlag() || !CountMovementHelper.getInstance().matchesPacket((Packet<?>)p_channelRead0_2_)) {
                if (this.channel.isOpen()) {
                    try {
                        p_channelRead0_2_.processPacket(this.packetListener);
                    } catch (ThreadQuickExitException ignored) {
                    }
                }
            }
        }
    }

    public void setNetHandler(INetHandler handler) {
        Validate.notNull(handler, "packetListener");
        logger.debug("Set listener of {} to {}", this, handler);
        this.packetListener = handler;
    }

    public void sendPacket(Packet packetIn) {
        if (!WorldQueryCache.matchesPacket(packetIn)) {
            UtilsHelper.handlePacket(packetIn);
            if (packetIn instanceof C08PacketPlayerBlockPlacement) {
                NoSlowDown.getInstance().flag = true;
            }

            if (Disabler.getInstance() != null && Disabler.getInstance().checkState44()) {
                BooleanSetting boolSetting = (BooleanSetting)Disabler.getInstance().cancelC19Setting.computeSetting("Cancel C0B");
                if (boolSetting != null
                    && boolSetting.isEnabled()
                    && packetIn instanceof C0BPacketEntityAction packet
                    && (packet.getAction() == Action.START_SPRINTING || packet.getAction() == Action.STOP_SPRINTING)) {
                    return;
                }
            }

            EventPacketSend event = new EventPacketSend(packetIn);
            event.process();
            if (!event.isCancelled()) {
                CountMovementHelper instance = CountMovementHelper.getInstance();
                if (!instance.isFlag() || !instance.matchesPacket2(packetIn)) {
                    if (this.isChannelOpen()) {
                        this.flushOutboundQueue();
                        this.dispatchPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null);
                    } else {
                        this.readWriteLock.writeLock().lock();

                        try {
                            this.outboundPacketsQueue
                                .add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
                        } finally {
                            this.readWriteLock.writeLock().unlock();
                        }
                    }
                }
            }
        }
    }

    public void sendPacket(
        Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners
    ) {
        if (!WorldQueryCache.matchesPacket(packetIn)) {
            UtilsHelper.handlePacket(packetIn);
            EventPacketSend event = new EventPacketSend(packetIn);
            event.process();
            if (!event.isCancelled()) {
                CountMovementHelper instance = CountMovementHelper.getInstance();
                if (!instance.isFlag() || !instance.matchesPacket2(packetIn)) {
                    if (this.isChannelOpen()) {
                        this.flushOutboundQueue();
                        this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
                    } else {
                        this.readWriteLock.writeLock().lock();

                        try {
                            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
                        } finally {
                            this.readWriteLock.writeLock().unlock();
                        }
                    }
                }
            }
        }
    }

    private void dispatchPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        if (!WorldQueryCache.matchesPacket(inPacket)) {
            if (!(inPacket instanceof C09PacketHeldItemChange)) {
                this.observeDispatch(inPacket);
            }

            final EnumConnectionState fromPacket = EnumConnectionState.getFromPacket(inPacket);
            final EnumConnectionState enumConnectionState = this.channel.attr(attrKeyConnectionState).get();
            if (enumConnectionState != fromPacket) {
                logger.debug("Disabled auto read");
                this.channel.config().setAutoRead(false);
            }

            if (this.channel.eventLoop().inEventLoop()) {
                if (fromPacket != enumConnectionState) {
                    this.setConnectionState(fromPacket);
                }

                ChannelFuture channelFuture = this.writeDispatchedPacket(inPacket);
                if (futureListeners != null) {
                    channelFuture.addListeners(futureListeners);
                }

                channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } else {
                this.channel.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (fromPacket != enumConnectionState) {
                            NetworkManager.this.setConnectionState(fromPacket);
                        }

                        ChannelFuture channelFuture2 = NetworkManager.this.writeDispatchedPacket(inPacket);
                        if (futureListeners != null) {
                            channelFuture2.addListeners(futureListeners);
                        }

                        channelFuture2.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                });
            }
        }
    }

    ChannelFuture writeDispatchedPacket(Packet<?> packet) {
        if (packet instanceof C09PacketHeldItemChange packet2) {
            int slotId = packet2.getSlotId();
            if (slotId == this.lastSlot) {
                return this.channel.newSucceededFuture();
            }

            this.lastSlot = slotId;
            this.observeDispatch(packet);
        }

        return this.channel.writeAndFlush(packet);
    }

    public void onServerHeldSlot(int slot) {
        if (this.channel != null) {
            if (this.channel.eventLoop().inEventLoop()) {
                this.lastSlot = slot;
            } else {
                this.channel.eventLoop().execute(() -> this.lastSlot = slot);
            }
        }
    }

    private void observeDispatch(Packet<?> packet) {
        this.recordCombatDispatch(packet);
        LagbackDebug.handleNetworkManager(this, packet);
    }

    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.readWriteLock.readLock().lock();

            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    NetworkManager.InboundHandlerTuplePacketListener inboundHandlerTuplePacketListener = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(inboundHandlerTuplePacketListener.packet, inboundHandlerTuplePacketListener.futureListeners);
                }
            } finally {
                this.readWriteLock.readLock().unlock();
            }
        }
    }

    public void processReceivedPackets() {
        this.flushOutboundQueue();
        if (this.packetListener instanceof ITickable) {
            ((ITickable)this.packetListener).update();
        }

        this.channel.flush();
    }

    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    public void closeChannel(IChatComponent message) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.terminationReason = message;
        }
    }

    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public ProxyConfig getConnectionProxy() {
        return this.connectionProxy;
    }

    public static NetworkManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport) {
        final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        if (ProxyManager.checkState()) {
            final ProxyConfig proxyConfig = ProxyManager.getProxyConfig();
            networkManager.connectionProxy = proxyConfig;
            ProxyManager.process3();
            final Proxy proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxyConfig.computeString(), proxyConfig.getPort()));
            Bootstrap bootstrap = new Bootstrap();
            OioEventLoopGroup oioEventLoopGroup = new OioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Proxy IO #%d").setDaemon(true).build());
            bootstrap.group(oioEventLoopGroup)
                .channelFactory(new ChannelFactory<OioSocketChannel>() {
                    public OioSocketChannel newChannel() {
                        try {
                            Socket socket = new Socket(proxy);
                            socket.setSoTimeout(15000);
                            socket.setTcpNoDelay(true);
                            System.out.println("[NetworkManager] Created " + proxyConfig.computeSocks4Mode().name() + " proxy socket");
                            return new OioSocketChannel(socket);
                        } catch (Exception e) {
                            NetworkManager.logger.error("Failed to create proxy socket: " + e.getMessage(), (Throwable)e);
                            ProxyManager.process5();

                            try {
                                Socket socket2 = new Socket();
                                socket2.setSoTimeout(15000);
                                socket2.setTcpNoDelay(true);
                                return new OioSocketChannel(socket2);
                            } catch (Exception e2) {
                                throw new RuntimeException("Failed to create socket: " + e2.getMessage(), e2);
                            }
                        }
                    }
                })
                .handler(
                    new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel p_initChannel_1_) throws Exception {
                            try {
                                p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                                p_initChannel_1_.config().setOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000);
                            } catch (ChannelException ignored) {
                            }

                            p_initChannel_1_.pipeline()
                                .addLast("timeout", new ReadTimeoutHandler(30))
                                .addLast("splitter", new MessageDeserializer2())
                                .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
                                .addLast("prepender", new MessageSerializer2())
                                .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
                                .addLast("packet_handler", networkManager);
                            NetworkManager.addViaPipeline(p_initChannel_1_);
                        }
                    }
                );
            bootstrap.connect(address, serverPort).syncUninterruptibly();
        } else {
            Class<? extends io.netty.channel.socket.SocketChannel> clazz;
            LazyLoadBase lazyLoadBase;
            if (Epoll.isAvailable() && useNativeTransport) {
                clazz = EpollSocketChannel.class;
                lazyLoadBase = CLIENT_EPOLL_EVENTLOOP;
            } else {
                clazz = NioSocketChannel.class;
                lazyLoadBase = CLIENT_NIO_EVENTLOOP;
            }

            Bootstrap bootstrap2 = new Bootstrap()
                .group((EventLoopGroup)lazyLoadBase.getValue())
                .handler(
                    new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel p_initChannel_1_2) throws Exception {
                            try {
                                p_initChannel_1_2.config().setOption(ChannelOption.TCP_NODELAY, true);
                            } catch (ChannelException ignored2) {
                            }

                            p_initChannel_1_2.pipeline()
                                .addLast("timeout", new ReadTimeoutHandler(30))
                                .addLast("splitter", new MessageDeserializer2())
                                .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
                                .addLast("prepender", new MessageSerializer2())
                                .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
                                .addLast("packet_handler", networkManager);
                            NetworkManager.addViaPipeline(p_initChannel_1_2);
                        }
                    }
                )
                .channel(clazz);
            bootstrap2.connect(address, serverPort).syncUninterruptibly();
        }

        return networkManager;
    }

    static void addViaPipeline(Channel channel) {
        ViaLoadingBase instance = ViaLoadingBase.getInstance();
        if (ViaMCP.INSTANCE != null && instance != null && instance.getTargetVersion().getVersion() != 47) {
            UserConnectionImpl userConnectionImpl = new UserConnectionImpl(channel, true);
            new ProtocolPipelineImpl(userConnectionImpl);
            channel.pipeline().addLast(new MCPVLBPipeline(userConnectionImpl));
        }
    }

    public static NetworkManager provideLocalClient(SocketAddress address) {
        final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        new Bootstrap().group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel p_initChannel_1_) throws Exception {
                p_initChannel_1_.pipeline().addLast("packet_handler", networkManager);
            }
        }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
        return networkManager;
    }

    public void enableEncryption(SecretKey key) {
        if (!this.isEncrypted) {
            this.isEncrypted = true;
            if (this.channel.pipeline().get("decrypt") == null && this.channel.pipeline().get("encrypt") == null) {
                this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
                this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
            }
        }
    }

    public boolean getIsencrypted() {
        return this.isEncrypted;
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean hasNoChannel() {
        return this.channel == null;
    }

    public INetHandler getNetHandler() {
        return this.packetListener;
    }

    public IChatComponent getExitMessage() {
        return this.terminationReason;
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public void setCompressionTreshold(int treshold) {
        if (treshold >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                ((NettyCompressionEncoder)this.channel.pipeline().get("compress")).setCompressionTreshold(treshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }

        if (this.channel.pipeline().get("via-encoder") != null) {
            this.channel.pipeline().fireUserEventTriggered(new CompressionReorderEvent());
        }
    }

    public void checkDisconnected() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.disconnected) {
                this.disconnected = true;
                if (this.getExitMessage() != null) {
                    this.getNetHandler().onDisconnect(this.getExitMessage());
                } else if (this.getNetHandler() != null) {
                    this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
                }
            } else {
                logger.warn("handleDisconnection() called twice");
            }
        }
    }

    public void sendPacketNoEvent(Packet packetIn) {
        if (!WorldQueryCache.matchesPacket(packetIn)) {
            UtilsHelper.handlePacket(packetIn);
            if (this.isChannelOpen()) {
                this.flushOutboundQueue();
                this.dispatchPacket(packetIn, null);
            } else {
                this.readWriteLock.writeLock().lock();

                try {
                    this.outboundPacketsQueue
                        .add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
                } finally {
                    this.readWriteLock.writeLock().unlock();
                }
            }
        }
    }

    private void recordCombatDispatch(Packet<?> packet) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null && mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() == this) {
            KillAura instance = KillAura.instance;
            if (instance != null) {
                instance.handlePacket(packet);
            }

            WorldQueryCache.handlePacket(packet);
            TargetHUD.onAttackDispatched(packet);
            NoSlowDown.handlePacket(packet);
            KeepSprint instance2 = KeepSprint.instance;
            if (instance2 != null) {
                instance2.handlePacket(packet);
            }
        }
    }

    static class InboundHandlerTuplePacketListener {
        final Packet packet;
        final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
