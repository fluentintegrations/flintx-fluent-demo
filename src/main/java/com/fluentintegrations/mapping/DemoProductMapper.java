package com.fluentintegrations.mapping;

import com.fluentintegrations.mapping.common.flintx.ValueLocale;
import com.fluentintegrations.mapping.common.flintx.ValueType;
import com.fluentintegrations.mapping.common.flintx.ValueTypeName;
import com.fluentintegrations.mapping.product.flintx.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public abstract class DemoProductMapper {
    public static final DemoProductMapper INSTANCE = Mappers.getMapper(DemoProductMapper.class);

    @Mapping(target = "identifier", source="id")
    @Mapping(target = "type", source="type")
    @Mapping(target = "status", source="status")
    @Mapping(target = "name", source="name")
    @Mapping(target = "summary", source="summary")
    @Mapping(target = "gtins", source="gtin")
    @Mapping(target = "parents", source="parentProduct")
    @Mapping(target = "categories", source="categories")
    public abstract Product map(DemoProduct demoProduct);

    public List<ValueLocale> mapValueLocale(String value) {
        return Collections.singletonList(
                ValueLocale.builder().value(value).locale("en_US").build()
        );
    }

    public List<ValueType> mapValueType(String value) {
        return Collections.singletonList(
                ValueType.builder().value(value).type("STRING").build()
        );
    }

    public List<ValueTypeName> mapValueTypeName(String value) {
        return Collections.singletonList(
                ValueTypeName.builder().name("GTIN").value(value).type("STRING").build()
        );
    }
}
