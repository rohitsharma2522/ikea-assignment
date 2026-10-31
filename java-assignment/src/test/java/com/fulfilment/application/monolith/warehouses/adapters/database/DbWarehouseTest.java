package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DbWarehouseTest {

  @Test
  void mapsDomainWarehouseToDatabaseEntityAndBack() {
    var createdAt = LocalDateTime.of(2026, 10, 30, 10, 0);
    var warehouse = warehouse(7L, "MWH.100", "EINDHOVEN-001", 60, 20, createdAt);

    var dbWarehouse = DbWarehouse.fromWarehouse(warehouse);
    var mappedWarehouse = dbWarehouse.toWarehouse();

    assertEquals(warehouse.getId(), mappedWarehouse.getId());
    assertEquals(warehouse.businessUnitCode, mappedWarehouse.businessUnitCode);
    assertEquals(warehouse.location, mappedWarehouse.location);
    assertEquals(warehouse.capacity, mappedWarehouse.capacity);
    assertEquals(warehouse.stock, mappedWarehouse.stock);
    assertEquals(warehouse.createdAt, mappedWarehouse.createdAt);
  }

  @Test
  void updatesAllPersistedWarehouseFields() {
    var dbWarehouse = DbWarehouse.fromWarehouse(warehouse(7L, "MWH.100", "EINDHOVEN-001", 60, 20, null));
    var archivedAt = LocalDateTime.of(2026, 10, 30, 11, 0);
    var replacement = warehouse(7L, "MWH.101", "AMSTERDAM-001", 80, 30, archivedAt);
    replacement.archivedAt = archivedAt;

    dbWarehouse.updateFrom(replacement);

    assertEquals("MWH.101", dbWarehouse.businessUnitCode);
    assertEquals("AMSTERDAM-001", dbWarehouse.location);
    assertEquals(80, dbWarehouse.capacity);
    assertEquals(30, dbWarehouse.stock);
    assertEquals(archivedAt, dbWarehouse.archivedAt);
  }

  private static Warehouse warehouse(
      Long id, String businessUnitCode, String location, int capacity, int stock, LocalDateTime createdAt) {
    var warehouse = new Warehouse();
    warehouse.assignId(id);
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    warehouse.createdAt = createdAt;
    return warehouse;
  }
}
