package com.fulfilment.application.monolith.warehouses.domain.exceptions;

/** Indicates that a warehouse command violates a business rule. */
public class WarehouseValidationException extends RuntimeException {

  public WarehouseValidationException(String message) {
    super(message);
  }
}
