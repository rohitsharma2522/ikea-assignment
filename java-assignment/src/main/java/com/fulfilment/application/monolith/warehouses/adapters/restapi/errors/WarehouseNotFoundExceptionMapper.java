package com.fulfilment.application.monolith.warehouses.adapters.restapi.errors;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WarehouseNotFoundExceptionMapper
    implements ExceptionMapper<WarehouseNotFoundException> {

  @Override
  public Response toResponse(WarehouseNotFoundException exception) {
    return response(Response.Status.NOT_FOUND, exception.getMessage());
  }

  private Response response(Response.Status status, String message) {
    return Response.status(status)
        .type(MediaType.APPLICATION_JSON)
        .entity(new ErrorResponse(status.getStatusCode(), message))
        .build();
  }
}
