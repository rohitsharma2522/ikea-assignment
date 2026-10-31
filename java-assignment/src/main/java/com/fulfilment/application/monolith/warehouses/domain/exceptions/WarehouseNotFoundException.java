package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class WarehouseNotFoundException extends RuntimeException {

  public WarehouseNotFoundException(String identifier) {
    super("Active warehouse does not exist: " + identifier);
  }
}
