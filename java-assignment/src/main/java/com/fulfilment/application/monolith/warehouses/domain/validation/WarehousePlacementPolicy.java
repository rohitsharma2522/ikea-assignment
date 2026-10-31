package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

/** Validates whether a warehouse can be placed at a location. */
public interface WarehousePlacementPolicy {

  void validateForCreation(Warehouse warehouse);

  void validateForReplacement(Warehouse currentWarehouse, Warehouse replacementWarehouse);
}
