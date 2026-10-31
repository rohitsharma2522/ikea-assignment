package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseAlreadyArchivedException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-10-31T10:15:30Z"), ZoneOffset.UTC);

  @Test
  void archivesWarehouseUsingCurrentTimeAndPersistsIt() {
    var store = new RecordingWarehouseStore();
    var warehouse = warehouse(7L);
    var useCase = new ArchiveWarehouseUseCase(store, CLOCK);

    useCase.archive(warehouse);

    assertEquals(LocalDateTime.of(2026, 10, 31, 10, 15, 30), warehouse.archivedAt);
    assertEquals(warehouse, store.updatedWarehouse);
  }

  @Test
  void rejectsWarehouseWithoutIdentity() {
    var useCase = new ArchiveWarehouseUseCase(new RecordingWarehouseStore(), CLOCK);

    assertThrows(WarehouseValidationException.class, () -> useCase.archive(new Warehouse()));
  }

  @Test
  void rejectsWarehouseThatIsAlreadyArchived() {
    var store = new RecordingWarehouseStore();
    var warehouse = warehouse(7L);
    warehouse.archive(LocalDateTime.of(2025, 1, 1, 0, 0));
    var useCase = new ArchiveWarehouseUseCase(store, CLOCK);

    assertThrows(WarehouseAlreadyArchivedException.class, () -> useCase.archive(warehouse));
    assertNull(store.updatedWarehouse);
  }

  private static Warehouse warehouse(Long id) {
    var warehouse = new Warehouse();
    warehouse.assignId(id);
    return warehouse;
  }

  private static final class RecordingWarehouseStore implements WarehouseStore {
    private Warehouse updatedWarehouse;

    @Override
    public List<Warehouse> getAll() {
      return List.of();
    }

    @Override
    public void create(Warehouse warehouse) {}

    @Override
    public void update(Warehouse warehouse) {
      updatedWarehouse = warehouse;
    }

    @Override
    public void remove(Warehouse warehouse) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return null;
    }

    @Override
    public Warehouse findActiveById(Long id) {
      return null;
    }

    @Override
    public long countActiveByLocation(String location) {
      return 0;
    }

    @Override
    public int totalActiveCapacityByLocation(String location) {
      return 0;
    }
  }
}
