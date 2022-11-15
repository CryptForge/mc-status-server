package me.cryptforge.network;

import java.util.NoSuchElementException;

public enum ConnectionState {
    HANDSHAKE(0),
    STATUS(1),
    LOGIN(2),
    PLAY(3);

    private static final ConnectionState[] values = values();
    private final int id;

    ConnectionState(int id) {
        this.id = id;
    }

    public static ConnectionState getById(int id) {
        for (ConnectionState value : values) {
            if (value.id == id) {
                return value;
            }
        }
        throw new NoSuchElementException();
    }
}
