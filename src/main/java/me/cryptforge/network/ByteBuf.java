package me.cryptforge.network;

import me.cryptforge.Main;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteBuf {

    private static final Logger logger = LoggerFactory.getLogger(ByteBuf.class);

    private final ByteBuffer buffer;

    public ByteBuf(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void writeVarInt(int value) {
        final int i = writeVarInt(buffer.position(), value);
        buffer.position(buffer.position() + i);
    }

    public int writeVarInt(int index, int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buffer.put(index, (byte) value);
            return 1;
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            buffer.putShort(index, (short) ((value & 0x7F | 0x80) << 8 | (value >>> 7)));
            return 2;
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            buffer.put(index, (byte) (value & 0x7F | 0x80));
            buffer.put(index + 1, (byte) ((value >>> 7) & 0x7F | 0x80));
            buffer.put(index + 2, (byte) (value >>> 14));
            return 3;
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            buffer.putInt(index + 4, (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21));
            return 4;
        } else {
            buffer.putInt(index, (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80));
            buffer.put(index + 5, (byte) (value >>> 28));
            return 5;
        }
    }

    public void writeString(String string) {
        writeVarInt(string.length());
        buffer.put(string.getBytes(StandardCharsets.UTF_8));
    }

    public void writeVarIntHeader(int index, int value) {
        buffer.put(index, (byte) (value & 0x7F | 0x80));
        buffer.put(index + 1, (byte) ((value >>> 7) & 0x7F | 0x80));
        buffer.put(index + 2, (byte) (value >>> 14));
    }

    public void writeComponent(Component component) {
        final String json = Main.componentSerializer().serialize(component);
        writeString(json);
    }

    public boolean readBoolean() {
        return buffer.get() == 1;
    }

    public UUID readUuid() {
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    public int readVarInt() {
        int result = 0;
        for (int shift = 0; ; shift += 7) {
            byte b = buffer.get();
            result |= (b & 0x7f) << shift;
            if (b >= 0) {
                return result;
            }
        }
    }

    public String readString(int maxLength) {
        final int length = readVarInt();
        if (length > maxLength) {
            logger.warn("String length exceeds max length ({}/{})", length, maxLength);
        }
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    public boolean hasBytes(int length) {
        return buffer.limit() - buffer.position() >= length;
    }

    public ConnectionState readConnectionState() {
        final int id = readVarInt();
        return ConnectionState.getById(id);
    }

    public ByteBuffer nio() {
        return buffer;
    }

    public void skip(int amount) {
        final int bytesLeft = nio().limit() - nio().position();
        if (bytesLeft == 0) {
            return;
        }
        if (amount > bytesLeft)
            amount = bytesLeft;
        nio().position(nio().position() + amount);
    }

    public static ByteBuf allocate(int capacity) {
        return new ByteBuf(ByteBuffer.allocate(capacity));
    }
}
