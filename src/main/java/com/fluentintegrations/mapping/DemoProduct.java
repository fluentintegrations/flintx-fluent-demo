package com.fluentintegrations.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = DemoProduct.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DemoProduct {
    @JsonProperty("id")
    String id;
    @JsonProperty("status")
    String status;
    @JsonProperty("type")
    String type;
    @JsonProperty("name")
    String name;
    @JsonProperty("summary")
    String summary;
    @JsonProperty("gtin")
    String gtin;
    @JsonProperty("parentProduct")
    String parentProduct;
    @JsonProperty("categories")
    List<String> categories;
}
