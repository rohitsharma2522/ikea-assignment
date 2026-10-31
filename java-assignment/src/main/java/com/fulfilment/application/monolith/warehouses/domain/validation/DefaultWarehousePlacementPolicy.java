package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.location.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DefaultWarehousePlacementPolicy implements WarehousePlacementPolicy {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public DefaultWarehousePlacementPolicy(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void validateForCreation(Warehouse warehouse) {
    Location location = resolveLocation(warehouse.location);

    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WarehouseValidationException(
          "An active warehouse already uses business unit code: " + warehouse.businessUnitCode);
    }

    validateLocationLimits(warehouse, location, 0, 0);
  }

  @Override
  public void validateForReplacement(
      Warehouse currentWarehouse, Warehouse replacementWarehouse) {
    Location location = resolveLocation(replacementWarehouse.location);
    boolean replacingAtSameLocation =
        currentWarehouse.location.equals(replacementWarehouse.location);

    long warehousesToExclude = replacingAtSameLocation ? 1 : 0;
    int capacityToExclude = replacingAtSameLocation ? currentWarehouse.capacity : 0;
    validateLocationLimits(
        replacementWarehouse, location, warehousesToExclude, capacityToExclude);
  }

  private Location resolveLocation(String identifier) {
    Location location = locationResolver.resolveByIdentifier(identifier);
    if (location == null) {
      throw new LocationNotFoundException(identifier);
    }
    return location;
  }

  private void validateLocationLimits(
      Warehouse warehouse,
      Location location,
      long warehousesToExclude,
      int capacityToExclude) {
    long activeWarehouseCount =
        warehouseStore.countActiveByLocation(warehouse.location) - warehousesToExclude;
    if (activeWarehouseCount >= location.maxNumberOfWarehouses) {
      throw new WarehouseValidationException(
          "Location has reached its maximum number of warehouses: " + warehouse.location);
    }

    int allocatedCapacity =
        warehouseStore.totalActiveCapacityByLocation(warehouse.location) - capacityToExclude;
    if (allocatedCapacity + warehouse.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Warehouse capacity exceeds the location maximum capacity: " + warehouse.location);
    }
  }
}
