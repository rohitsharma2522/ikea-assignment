package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

/** Validates business rules that depend only on the warehouse being submitted. */
public interface WarehouseValidator {

  void validate(Warehouse warehouse);
}
