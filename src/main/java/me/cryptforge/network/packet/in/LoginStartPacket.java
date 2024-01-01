package me.cryptforge.network.packet.in;

import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.packet.Packet;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class LoginStartPacket implements Packet {

    private final String name;
    private final boolean hasSigData;
    private final long timestamp;
    private final byte[] publicKey;
    private final byte[] signature;
    private final boolean hasUUID;
    private final UUID uuid;

    public LoginStartPacket(ByteBuf buffer) {
        this.name = buffer.readString(16);
        this.hasSigData = buffer.readBoolean();
        this.timestamp = hasSigData ? buffer.nio().getLong() : -1;
        final int keyLength = hasSigData ? buffer.readVarInt() : -1;
        this.publicKey = hasSigData ? buffer.readBytes(keyLength) : null;
        final int signatureLength = hasSigData ? buffer.readVarInt() : -1;
        this.signature = hasSigData ? buffer.readBytes(signatureLength) : null;
        this.hasUUID = buffer.readBoolean();
        this.uuid = hasUUID ? buffer.readUuid() : null;
    }

    public String username() {
        return name;
    }

    public boolean hasSigData() {
        return hasSigData;
    }

    public long timestamp() {
        return timestamp;
    }

    public byte[] publicKey() {
        return publicKey;
    }

    public byte[] signature() {
        return signature;
    }

    public boolean hasUUID() {
        return hasUUID;
    }

    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LoginStartPacket that = (LoginStartPacket) o;
        return hasSigData == that.hasSigData && timestamp == that.timestamp && hasUUID == that.hasUUID && Objects.equals(name, that.name) && Arrays.equals(publicKey, that.publicKey) && Arrays.equals(signature, that.signature) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, hasSigData, timestamp, hasUUID, uuid);
        result = 31 * result + Arrays.hashCode(publicKey);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
}
