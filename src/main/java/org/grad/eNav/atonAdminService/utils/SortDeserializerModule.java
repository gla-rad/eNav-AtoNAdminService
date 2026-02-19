package org.grad.eNav.atonAdminService.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SortDeserializerModule extends com.fasterxml.jackson.databind.Module {

    @Override
    public String getModuleName() { return "SortDeserializerModule"; }

    @Override
    public Version version() { return Version.unknownVersion(); }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new SimpleDeserializers(Map.of(
                Sort.class, new SortDeserializer()
        )));
    }

    static class SortDeserializer extends JsonDeserializer<Sort> {
        @Override
        public Sort deserialize(JsonParser p, DeserializationContext ctx)
                throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            List<Sort.Order> orders = new ArrayList<>();
            for (JsonNode order : node) {
                Sort.Direction dir = Sort.Direction.fromString(
                        order.get("direction").asText()
                );
                orders.add(new Sort.Order(dir, order.get("property").asText()));
            }
            return Sort.by(orders);
        }
    }
}
