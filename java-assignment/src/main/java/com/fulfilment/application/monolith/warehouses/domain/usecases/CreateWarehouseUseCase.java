package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;
  private final WarehouseValidator warehouseValidator;

  @Inject
  public CreateWarehouseUseCase(
      WarehouseStore warehouseStore,
      LocationResolver locationResolver,
      WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void create(Warehouse warehouse) {
    warehouseValidator.validate(warehouse);

    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new LocationNotFoundException(warehouse.location);
    }

    validateBusinessUnitCodeIsAvailable(warehouse);
    validateLocationLimits(warehouse, location);

    warehouseStore.create(warehouse);
  }

  private void validateBusinessUnitCodeIsAvailable(Warehouse warehouse) {
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WarehouseValidationException(
          "An active warehouse already uses business unit code: " + warehouse.businessUnitCode);
    }
  }

  private void validateLocationLimits(Warehouse warehouse, Location location) {
    if (warehouseStore.countActiveByLocation(warehouse.location) >= location.maxNumberOfWarehouses) {
      throw new WarehouseValidationException(
          "Location has reached its maximum number of warehouses: " + warehouse.location);
    }

    int allocatedCapacity = warehouseStore.totalActiveCapacityByLocation(warehouse.location);
    if (allocatedCapacity + warehouse.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Warehouse capacity exceeds the location maximum capacity: " + warehouse.location);
    }
  }
}
