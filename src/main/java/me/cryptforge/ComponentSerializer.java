package me.cryptforge;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.kyori.adventure.text.Component;

import java.io.IOException;

public class ComponentSerializer extends StdSerializer<Component> {

    protected ComponentSerializer() {
        super(Component.class);
    }

    @Override
    public void serialize(Component value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        final String json = Main.componentSerializer().serialize(value);
        gen.writeTree(Main.objectMapper().readTree(json));
    }
}
