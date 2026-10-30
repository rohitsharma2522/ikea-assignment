package com.fulfilment.application.monolith.location.exceptions;

/** Indicates that an operation requires a location that does not exist. */
public class LocationNotFoundException extends RuntimeException {

  public LocationNotFoundException(String identifier) {
    super("Location does not exist: " + identifier);
  }
}
