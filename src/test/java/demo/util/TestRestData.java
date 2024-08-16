package demo.util;

import java.util.Arrays;
import java.util.UUID;

import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;

import static java.util.UUID.randomUUID;

public class TestRestData {

    public static CreateItemRequest buildCreateItemRequest(String name) {
        return CreateItemRequest.builder()
                .name(name)
                .build();
    }

    public static UpdateItemRequest buildUpdateItemRequest(String name) {
        return UpdateItemRequest.builder()
                .name(name)
                .build();
    }

    public static GetItemResponse buildGetItemResponse(UUID id, String name) {
        return GetItemResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static GetItemsResponse buildGetItemsResponse() {
        return GetItemsResponse.builder()
                .itemResponses(Arrays.asList(buildGetItemResponse(randomUUID(), "test-item1"), buildGetItemResponse(randomUUID(), "test-item2")))
                .build();
    }
}
