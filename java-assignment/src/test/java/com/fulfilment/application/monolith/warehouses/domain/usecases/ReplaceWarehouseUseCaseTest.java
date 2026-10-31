package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.DefaultWarehousePlacementPolicy;
import com.fulfilment.application.monolith.warehouses.domain.validation.DefaultWarehouseValidator;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-10-31T10:15:30Z"), ZoneOffset.UTC);

  @Test
  void archivesCurrentWarehouseAndCreatesReplacementAtomically() {
    var current = warehouse(1L, "MWH.100", "AMSTERDAM-001", 50, 20);
    var store = new InMemoryWarehouseStore(current);
    var replacement = warehouse(null, "MWH.100", "AMSTERDAM-001", 60, 20);

    useCase(store).replace("MWH.100", replacement);

    assertNotNull(current.archivedAt);
    assertNotNull(replacement.createdAt);
    assertNotNull(replacement.getId());
    assertEquals("MWH.100", replacement.businessUnitCode);
    assertEquals(replacement, store.findByBusinessUnitCode("MWH.100"));
  }

  @Test
  void rejectsReplacementWhenCurrentWarehouseDoesNotExist() {
    var store = new InMemoryWarehouseStore();

    assertThrows(
        WarehouseNotFoundException.class,
        () ->
            useCase(store)
                .replace(
                    "MWH.404", warehouse(null, "MWH.404", "AMSTERDAM-001", 50, 20)));
  }

  @Test
  void rejectsReplacementWithDifferentBusinessUnitCode() {
    var current = warehouse(1L, "MWH.100", "AMSTERDAM-001", 50, 20);
    var store = new InMemoryWarehouseStore(current);

    assertThrows(
        WarehouseValidationException.class,
        () ->
            useCase(store)
                .replace(
                    "MWH.100", warehouse(null, "MWH.200", "AMSTERDAM-001", 50, 20)));

    assertNull(current.archivedAt);
  }

  @Test
  void rejectsReplacementWhenStockDoesNotMatch() {
    var current = warehouse(1L, "MWH.100", "AMSTERDAM-001", 50, 20);
    var store = new InMemoryWarehouseStore(current);

    assertThrows(
        WarehouseValidationException.class,
        () ->
            useCase(store)
                .replace(
                    "MWH.100", warehouse(null, "MWH.100", "AMSTERDAM-001", 50, 19)));

    assertNull(current.archivedAt);
  }

  @Test
  void rejectsReplacementThatCannotAccommodateCurrentStock() {
    var current = warehouse(1L, "MWH.100", "AMSTERDAM-001", 50, 20);
    var store = new InMemoryWarehouseStore(current);

    assertThrows(
        WarehouseValidationException.class,
        () ->
            useCase(store)
                .replace(
                    "MWH.100", warehouse(null, "MWH.100", "AMSTERDAM-001", 10, 20)));

    assertNull(current.archivedAt);
  }

  private static ReplaceWarehouseUseCase useCase(InMemoryWarehouseStore store) {
    var policy = new DefaultWarehousePlacementPolicy(store, new LocationGateway());
    return new ReplaceWarehouseUseCase(
        store, new DefaultWarehouseValidator(), policy, locations -> {}, CLOCK);
  }

  private static Warehouse warehouse(
      Long id, String businessUnitCode, String location, int capacity, int stock) {
    var warehouse = new Warehouse();
    if (id != null) {
      warehouse.assignId(id);
    }
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }

  private static final class InMemoryWarehouseStore implements WarehouseStore {
    private final List<Warehouse> warehouses = new ArrayList<>();
    private long nextId = 100;

    private InMemoryWarehouseStore(Warehouse... warehouses) {
      this.warehouses.addAll(List.of(warehouses));
    }

    @Override
    public List<Warehouse> getAll() {
      return warehouses.stream().filter(warehouse -> warehouse.archivedAt == null).toList();
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouse.assignId(nextId++);
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {}

    @Override
    public void remove(Warehouse warehouse) {}

    @Override
    public Warehouse findByBusinessUnitCode(String businessUnitCode) {
      return warehouses.stream()
          .filter(warehouse -> warehouse.archivedAt == null)
          .filter(warehouse -> warehouse.businessUnitCode.equals(businessUnitCode))
          .findFirst()
          .orElse(null);
    }

    @Override
    public Warehouse findActiveById(Long id) {
      return warehouses.stream()
          .filter(warehouse -> warehouse.archivedAt == null)
          .filter(warehouse -> Objects.equals(warehouse.getId(), id))
          .findFirst()
          .orElse(null);
    }

    @Override
    public long countActiveByLocation(String location) {
      return warehouses.stream()
          .filter(warehouse -> warehouse.archivedAt == null)
          .filter(warehouse -> warehouse.location.equals(location))
          .count();
    }

    @Override
    public int totalActiveCapacityByLocation(String location) {
      return warehouses.stream()
          .filter(warehouse -> warehouse.archivedAt == null)
          .filter(warehouse -> warehouse.location.equals(location))
          .mapToInt(warehouse -> warehouse.capacity)
          .sum();
    }
  }
}
