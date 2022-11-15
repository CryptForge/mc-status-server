package me.cryptforge;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.cryptforge.game.ServerStatus;

import java.io.IOException;

public class ServerStatusSerializer extends StdSerializer<ServerStatus> {

    protected ServerStatusSerializer() {
        super(ServerStatus.class);
    }

    @Override
    public void serialize(ServerStatus value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("version");
        gen.writeStartObject();
        gen.writeStringField("name", value.version().name());
        gen.writeNumberField("protocol", value.version().protocol());
        gen.writeEndObject();
        if (!value.hidePlayers()) {
            gen.writeObjectField("players", value.players());
        }
        gen.writeObjectField("description", value.description());
        // TODO: favicon
        gen.writeBooleanField("previewsChat", value.previewsChat());
        gen.writeBooleanField("enforcesSecureChat", value.enforcesSecureChat());
        gen.writeEndObject();
    }
}
