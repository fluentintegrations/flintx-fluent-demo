package com.fluentintegrations.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.Exchange;

import java.util.Map;

@RegisterForReflection
public class DemoProductMappingBean {

    public void convert(Exchange exchange) {
        ObjectMapper objectMapper = new ObjectMapper();
        DemoProduct demoProduct = objectMapper.convertValue(
                exchange.getIn().getBody(),
                DemoProduct.class
        );
        com.fluentintegrations.mapping.product.flintx.Product flintxProduct = DemoProductMapper.INSTANCE.map(demoProduct);

        Map map = objectMapper.convertValue(
                flintxProduct,
                Map.class
        );
        exchange.getIn().setBody(map);
    }
}
