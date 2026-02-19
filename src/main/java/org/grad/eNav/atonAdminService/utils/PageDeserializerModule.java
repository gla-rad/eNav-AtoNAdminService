package org.grad.eNav.atonAdminService.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PageDeserializerModule extends com.fasterxml.jackson.databind.Module {

    @Override
    public String getModuleName() { return "PageDeserializerModule"; }

    @Override
    public Version version() { return Version.unknownVersion(); }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new SimpleDeserializers(Map.of(
                Page.class, new PageDeserializer()
        )));
    }

    static class PageDeserializer extends JsonDeserializer<Page<?>> {
        @Override
        public Page<?> deserialize(JsonParser p, DeserializationContext ctx)
                throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            int number = node.get("number").intValue();
            int size = node.get("size").intValue();
            long totalElements = node.get("totalElements").longValue();
            JavaType contentType = ctx.getTypeFactory()
                    .constructCollectionType(List.class, Object.class);
            List<?> content = ctx.readTreeAsValue(node.get("content"), contentType);
            return new PageImpl<>(content, PageRequest.of(number, size), totalElements);
        }
    }
}