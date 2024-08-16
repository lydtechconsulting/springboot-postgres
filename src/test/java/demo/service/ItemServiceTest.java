package demo.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import demo.domain.Item;
import demo.exception.ItemNotFoundException;
import demo.repository.ItemRepository;
import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;
import demo.util.TestDomainData;
import demo.util.TestRestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemServiceTest {

    private ItemRepository itemRepositoryMock;
    private ItemService service;

    @BeforeEach
    public void setUp() {
        itemRepositoryMock = mock(ItemRepository.class);
        service = new ItemService(itemRepositoryMock);
    }

    @Test
    public void testCreateItem() {
        UUID itemId = randomUUID();
        CreateItemRequest request = TestRestData.buildCreateItemRequest(randomAlphabetic(8));
        when(itemRepositoryMock.save(any(Item.class))).thenReturn(TestDomainData.buildItem(itemId, request.getName()));

        UUID newItemId = service.createItem(request);

        assertThat(itemId, equalTo(newItemId));
        verify(itemRepositoryMock, times(1)).save(any(Item.class));
    }

    @Test
    public void testUpdateItem() {
        UUID itemId = randomUUID();
        UpdateItemRequest request = TestRestData.buildUpdateItemRequest(randomAlphabetic(8));
        when(itemRepositoryMock.findById(itemId)).thenReturn(Optional.of(TestDomainData.buildItem(itemId, request.getName())));

        service.updateItem(itemId, request);

        verify(itemRepositoryMock, times(1)).save(any(Item.class));
    }

    @Test
    public void testUpdateItem_NotFound() {
        UUID itemId = randomUUID();
        UpdateItemRequest request = TestRestData.buildUpdateItemRequest(randomAlphabetic(8));
        when(itemRepositoryMock.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.updateItem(itemId, request));
    }

    @Test
    public void testGetItem() {
        UUID itemId = randomUUID();
        when(itemRepositoryMock.findById(itemId)).thenReturn(Optional.of(TestDomainData.buildItem(itemId, "test-item")));

        GetItemResponse item = service.getItem(itemId);

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo("test-item"));
        verify(itemRepositoryMock, times(1)).findById(itemId);
    }

    @Test
    public void testGetItem_NotFound() {
        UUID itemId = randomUUID();
        when(itemRepositoryMock.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.getItem(itemId));
    }

    @Test
    public void testGetItems() {
        when(itemRepositoryMock.findAll()).thenReturn(Arrays.asList(TestDomainData.buildItem(randomUUID(), "test-item"), TestDomainData.buildItem(randomUUID(), "test-item2")));

        GetItemsResponse items = service.getItems();

        assertThat(items.getItemResponses().size(), equalTo(2));
        assertThat(items.getItemResponses().get(0).getName(), equalTo("test-item"));
        assertThat(items.getItemResponses().get(1).getName(), equalTo("test-item2"));
        verify(itemRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testDeleteItem() {
        UUID itemId = randomUUID();
        Item item = TestDomainData.buildItem(itemId, "test-item");
        when(itemRepositoryMock.findById(itemId)).thenReturn(Optional.of(item));

        service.deleteItem(itemId);

        verify(itemRepositoryMock, times(1)).delete(item);
    }

    @Test
    public void testDeleteItem_NotFound() {
        UUID itemId = randomUUID();
        when(itemRepositoryMock.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(itemId));
    }
}
