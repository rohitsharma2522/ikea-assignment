package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultWarehouseValidator implements WarehouseValidator {

  @Override
  public void validate(Warehouse warehouse) {
    if (warehouse == null) {
      throw new WarehouseValidationException("Warehouse is required");
    }
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WarehouseValidationException("Business unit code is required");
    }
    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new WarehouseValidationException("Warehouse capacity must be greater than zero");
    }
    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new WarehouseValidationException("Warehouse stock cannot be negative");
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new WarehouseValidationException("Warehouse stock cannot exceed its capacity");
    }
  }
}
