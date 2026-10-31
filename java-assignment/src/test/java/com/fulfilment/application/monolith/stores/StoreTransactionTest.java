package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.verifyNoInteractions;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreTransactionTest {

  @InjectMock LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Test
  void doesNotSynchronizeLegacySystemWhenDatabaseTransactionRollsBack() {
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
              "name": "TONSTAD",
              "quantityProductsInStock": 1
            }
            """)
        .when()
        .post("/store")
        .then()
        .statusCode(500);

    verifyNoInteractions(legacyStoreManagerGateway);
  }
}
