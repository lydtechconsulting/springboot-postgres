package demo.integration;

import java.net.URI;

import demo.DemoConfiguration;
import demo.repository.ItemRepository;
import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.util.TestRestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { DemoConfiguration.class } )
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class EndToEndIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    /**
     * Send a request to create an item, and then retrieve it using the response location header.
     */
    @Test
    public void testCreateAndGetItem() {
        // Create item.
        CreateItemRequest request = TestRestData.buildCreateItemRequest("test-item");
        ResponseEntity<String> createItemResponse = restTemplate.postForEntity("/v1/items", request, String.class);
        assertThat(createItemResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
        URI location = createItemResponse.getHeaders().getLocation();
        assertThat(location, notNullValue());

        // Get item.
        ResponseEntity<GetItemResponse> getItemResponse = restTemplate.getForEntity("/v1/items/"+location, GetItemResponse.class);
        assertThat(getItemResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getItemResponse.getBody().getName(), equalTo(request.getName()));
    }

    /**
     * A 404 NOT FOUND is returned if the item being requested does not exist.
     */
    @Test
    public void testGetItem_NotFound() {
        ResponseEntity<GetItemResponse> getItemResponse = restTemplate.getForEntity("/v1/items/"+randomUUID(), GetItemResponse.class);
        assertThat(getItemResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }
}
