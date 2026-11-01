package com.fulfilment.application.monolith.warehouses.adapters.restapi.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fulfilment.application.monolith.location.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseAlreadyArchivedException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class WarehouseExceptionMapperTest {

  @Test
  void mapsDomainExceptionsToExpectedHttpResponses() {
    assertError(
        new LocationNotFoundExceptionMapper().toResponse(new LocationNotFoundException("UNKNOWN")),
        400);
    assertError(
        new WarehouseValidationExceptionMapper().toResponse(new WarehouseValidationException("Invalid")),
        400);
    assertError(
        new WarehouseNotFoundExceptionMapper().toResponse(new WarehouseNotFoundException("MWH.001")),
        404);
    assertError(
        new WarehouseAlreadyArchivedExceptionMapper()
            .toResponse(new WarehouseAlreadyArchivedException(1L)),
        409);
  }

  private static void assertError(Response response, int expectedStatus) {
    assertEquals(expectedStatus, response.getStatus());
    var error = (ErrorResponse) response.getEntity();
    assertEquals(expectedStatus, error.status());
  }
}
