package demo.component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import demo.rest.api.CreateItemRequest;
import demo.util.TestRestData;
import dev.lydtech.component.framework.client.database.PostgresClient;
import dev.lydtech.component.framework.client.service.ServiceClient;
import dev.lydtech.component.framework.extension.TestContainersSetupExtension;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@ExtendWith(TestContainersSetupExtension.class)
@ActiveProfiles("component-test")
public class EndToEndCT {

    private Connection dbConnection;

    @BeforeEach
    public void setup() throws Exception {
        String serviceBaseUrl = ServiceClient.getInstance().getBaseUrl();
        log.info("Service base URL is: {}", serviceBaseUrl);
        RestAssured.baseURI = serviceBaseUrl;
        dbConnection = PostgresClient.getInstance().getConnection("test", "demo", "user", "password");
    }

    @AfterEach
    public void tearDown() throws Exception {
        PostgresClient.getInstance().close(dbConnection);
    }

    @Test
    public void testCreateAndRetrieveItem() throws Exception {
        CreateItemRequest request = TestRestData.buildCreateItemRequest(RandomStringUtils.randomAlphabetic(12));
        Response createItemResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post("/v1/items")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();
        String location = createItemResponse.header("Location");
        assertThat(location, notNullValue());
        log.info("Create item response location header: "+location);

        given()
                .pathParam("id", location)
                .when()
                .get("/v1/items/{id}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .and()
                .body("name", containsString(request.getName()));

        // Query the database directly to demonstrate using the PostgresClient utility class.
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from item where name = '"+request.getName()+"'");
        assertThat(resultSet.next(), is(true));
        UUID result = (UUID)resultSet.getObject("id");
        assert result != null;
    }
}
