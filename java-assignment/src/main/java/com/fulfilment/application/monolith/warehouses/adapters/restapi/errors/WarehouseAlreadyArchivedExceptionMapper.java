package com.fulfilment.application.monolith.warehouses.adapters.restapi.errors;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseAlreadyArchivedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WarehouseAlreadyArchivedExceptionMapper
    implements ExceptionMapper<WarehouseAlreadyArchivedException> {

  @Override
  public Response toResponse(WarehouseAlreadyArchivedException exception) {
    var status = Response.Status.CONFLICT;
    return Response.status(status)
        .type(MediaType.APPLICATION_JSON)
        .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage()))
        .build();
  }
}
