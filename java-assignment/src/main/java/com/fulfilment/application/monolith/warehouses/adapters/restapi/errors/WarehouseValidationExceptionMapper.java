package com.fulfilment.application.monolith.warehouses.adapters.restapi.errors;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WarehouseValidationExceptionMapper
    implements ExceptionMapper<WarehouseValidationException> {

  @Override
  public Response toResponse(WarehouseValidationException exception) {
    var status = Response.Status.BAD_REQUEST;
    return Response.status(status)
        .type(MediaType.APPLICATION_JSON)
        .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage()))
        .build();
  }
}
