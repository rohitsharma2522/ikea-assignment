package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationAllocationLock;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehousePlacementPolicy;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;
  private final WarehousePlacementPolicy warehousePlacementPolicy;
  private final LocationAllocationLock locationAllocationLock;
  private final Clock clock;

  @Inject
  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore,
      WarehouseValidator warehouseValidator,
      WarehousePlacementPolicy warehousePlacementPolicy,
      LocationAllocationLock locationAllocationLock,
      Clock clock) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
    this.warehousePlacementPolicy = warehousePlacementPolicy;
    this.locationAllocationLock = locationAllocationLock;
    this.clock = clock;
  }

  @Override
  @Transactional
  public void replace(String businessUnitCode, Warehouse newWarehouse) {
    Warehouse currentWarehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);
    if (currentWarehouse == null) {
      throw new WarehouseNotFoundException(businessUnitCode);
    }

    warehouseValidator.validate(newWarehouse);
    locationAllocationLock.acquire(currentWarehouse.location, newWarehouse.location);

    currentWarehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);
    if (currentWarehouse == null) {
      throw new WarehouseNotFoundException(businessUnitCode);
    }

    if (!businessUnitCode.equals(newWarehouse.businessUnitCode)) {
      throw new WarehouseValidationException(
          "Replacement business unit code must match path: " + businessUnitCode);
    }
    if (!Objects.equals(currentWarehouse.stock, newWarehouse.stock)) {
      throw new WarehouseValidationException(
          "Replacement stock must match the current warehouse stock");
    }
    if (newWarehouse.capacity < currentWarehouse.stock) {
      throw new WarehouseValidationException(
          "Replacement capacity cannot accommodate the current warehouse stock");
    }

    warehousePlacementPolicy.validateForReplacement(currentWarehouse, newWarehouse);

    LocalDateTime now = LocalDateTime.now(clock);
    currentWarehouse.archive(now);
    warehouseStore.update(currentWarehouse);

    newWarehouse.createdAt = now;
    warehouseStore.create(newWarehouse);
  }
}
