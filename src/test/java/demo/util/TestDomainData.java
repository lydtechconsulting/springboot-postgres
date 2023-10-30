package demo.util;

import java.util.UUID;

import demo.domain.Item;

public class TestDomainData {

    public static Item buildItem(UUID id, String name) {
        return Item.builder()
                .id(id)
                .name(name)
                .build();
    }
}
