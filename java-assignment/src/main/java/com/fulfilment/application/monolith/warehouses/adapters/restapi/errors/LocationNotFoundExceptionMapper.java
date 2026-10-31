package com.fulfilment.application.monolith.warehouses.adapters.restapi.errors;

import com.fulfilment.application.monolith.location.exceptions.LocationNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LocationNotFoundExceptionMapper implements ExceptionMapper<LocationNotFoundException> {

  @Override
  public Response toResponse(LocationNotFoundException exception) {
    var status = Response.Status.BAD_REQUEST;
    return Response.status(status)
        .type(MediaType.APPLICATION_JSON)
        .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage()))
        .build();
  }
}
