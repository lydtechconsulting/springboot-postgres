# Spring Boot Demo With Postgres

Spring Boot application demonstrating using Postgres as the datatabase for reading and writing records.

Build Spring Boot application with Java 17:
```
mvn clean install
```

Start Docker containers:
```
docker-compose up -d
```

Start Spring Boot application:
```
java -jar target/springboot-postgres-1.0.0.jar
```

In a terminal window use curl to submit a POST REST request to the application to create an item:
```
curl -i -X POST localhost:9001/v1/items -H "Content-Type: application/json" -d '{"name": "test-item"}'
```

A response should be returned with the 201 CREATED status code and the new item id in the Location header:
```
HTTP/1.1 201 
Location: 7c738768-7c29-43f4-bb39-e1638cffe97f
```

The Spring Boot application should log the successful item persistence:
```
Item created with id: 7c738768-7c29-43f4-bb39-e1638cffe97f
```

Get the item that has been created using curl:
```
curl -i -X GET localhost:9001/v1/items/7c738768-7c29-43f4-bb39-e1638cffe97f
```

A response should be returned with the 200 SUCCESS status code and the item in the response body:
```
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 28 Oct 2023 15:19:36 GMT

{"id":"7c738768-7c29-43f4-bb39-e1638cffe97f","name":"test-item"}
```

In a terminal window use curl to submit a PUT REST request to the application to update the item:
```
curl -i -X PUT localhost:9001/v1/items/7c738768-7c29-43f4-bb39-e1638cffe97f -H "Content-Type: application/json" -d '{"name": "test-item-update"}'
```

A response should be returned with the 204 NO CONTENT status code:
```
HTTP/1.1 204 
```

The Spring Boot application should log the successful update of the item:
```
Item updated with id: 7c738768-7c29-43f4-bb39-e1638cffe97f - name: test-item-update
```

Delete the item using curl:
```
curl -i -X DELETE localhost:9001/v1/items/7c738768-7c29-43f4-bb39-e1638cffe97f
```

The Spring Boot application should log the successful deletion of the item:
```
Deleted item with id: 7c738768-7c29-43f4-bb39-e1638cffe97f
```

Stop containers:
```
docker-compose down
```

## Component Tests

The test demonstrates the application reading and writing records to Postgres.

For more on the component tests see: https://github.com/lydtechconsulting/component-test-framework

Build Spring Boot application jar:
```
mvn clean install
```

Build Docker container:
```
docker build -t ct/springboot-postgres:latest .
```

Run tests:
```
mvn test -Pcomponent
```

Run tests leaving containers up:
```
mvn test -Pcomponent -Dcontainers.stayup
```

Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

## Docker Clean Up

Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

Further docker clean up if network/other issues:
```
docker system prune
docker volume prune
```
