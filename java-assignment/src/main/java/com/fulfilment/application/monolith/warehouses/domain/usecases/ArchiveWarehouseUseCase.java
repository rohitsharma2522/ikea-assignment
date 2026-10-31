package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final Clock clock;

  @Inject
  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore, Clock clock) {
    this.warehouseStore = warehouseStore;
    this.clock = clock;
  }

  @Override
  @Transactional
  public void archive(Warehouse warehouse) {
    if (warehouse == null) {
      throw new WarehouseValidationException("Warehouse is required");
    }
    if (warehouse.getId() == null) {
      throw new WarehouseValidationException("Warehouse ID is required to archive it");
    }

    warehouse.archive(LocalDateTime.now(clock));
    warehouseStore.update(warehouse);
  }
}
