package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class WarehouseAlreadyArchivedException extends WarehouseValidationException {

  public WarehouseAlreadyArchivedException(Long id) {
    super("Warehouse is already archived: " + id);
  }
}
