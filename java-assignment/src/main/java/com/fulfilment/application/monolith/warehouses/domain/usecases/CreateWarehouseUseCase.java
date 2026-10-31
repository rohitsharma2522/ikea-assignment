package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationAllocationLock;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehousePlacementPolicy;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;
  private final WarehousePlacementPolicy warehousePlacementPolicy;
  private final LocationAllocationLock locationAllocationLock;
  private final Clock clock;

  @Inject
  public CreateWarehouseUseCase(
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
  public void create(Warehouse warehouse) {
    warehouseValidator.validate(warehouse);
    locationAllocationLock.acquire(warehouse.location);
    warehousePlacementPolicy.validateForCreation(warehouse);

    warehouse.createdAt = LocalDateTime.now(clock);
    warehouseStore.create(warehouse);
  }
}
