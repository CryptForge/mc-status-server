package me.cryptforge.network;

import me.cryptforge.network.packet.Packet;
import me.cryptforge.network.packet.in.HandshakePacket;
import me.cryptforge.network.packet.in.LoginStartPacket;
import me.cryptforge.network.packet.in.PingRequestPacket;
import me.cryptforge.network.packet.in.StatusRequestPacket;
import me.cryptforge.network.packet.out.DisconnectPacket;
import me.cryptforge.network.packet.out.PingResponsePacket;
import me.cryptforge.network.packet.out.StatusResponsePacket;

import java.util.HashMap;
import java.util.Map;

public final class PacketRegistry {

    private PacketRegistry() {
    }

    record PacketId(int id, ConnectionState state) {
    }

    private static final Map<PacketId, PacketConstructor> registry = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> classIdMap = new HashMap<>();

    static {
        // in
        registerInPacket(0x00, ConnectionState.HANDSHAKE, HandshakePacket.class, HandshakePacket::new);

        registerInPacket(0x00, ConnectionState.STATUS, StatusRequestPacket.class, StatusRequestPacket::new);
        registerInPacket(0x01, ConnectionState.STATUS, PingRequestPacket.class, PingRequestPacket::new);

        registerInPacket(0x00, ConnectionState.LOGIN, LoginStartPacket.class, LoginStartPacket::new);

        // out
        // status
        registerOutPacket(0x00, StatusResponsePacket.class);
        registerOutPacket(0x01, PingResponsePacket.class);

        // login
        registerOutPacket(0x00, DisconnectPacket.class);
    }

    private static void registerInPacket(int id, ConnectionState state, Class<? extends Packet> clazz, PacketConstructor constructor) {
        registry.put(new PacketId(id, state), constructor);
        classIdMap.put(clazz, id);
    }

    private static void registerOutPacket(int id, Class<? extends Packet> clazz) {
        classIdMap.put(clazz, id);
    }

    public static PacketConstructor getConstructor(int id, ConnectionState state) {
        return registry.get(new PacketId(id, state));
    }

    public static boolean isValidId(int id, ConnectionState state) {
        return registry.containsKey(new PacketId(id, state));
    }

    public static int getId(Class<? extends Packet> clazz) {
        return classIdMap.get(clazz);
    }

    public interface PacketConstructor {
        Packet create(ByteBuf buffer);
    }

}
