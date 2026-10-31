package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.location.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.DefaultWarehouseValidator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {

  @Test
  void createsWarehouseWhenAllBusinessRulesAreMet() {
    var store = new InMemoryWarehouseStore();
    var useCase = new CreateWarehouseUseCase(store, new LocationGateway(), new DefaultWarehouseValidator());
    var warehouse = warehouse("MWH.100", "EINDHOVEN-001", 60, 20);

    useCase.create(warehouse);

    assertEquals(List.of(warehouse), store.getAll());
  }

  @Test
  void rejectsAnUnknownLocationWithADomainValidationError() {
    var useCase =
        new CreateWarehouseUseCase(
            new InMemoryWarehouseStore(), new LocationGateway(), new DefaultWarehouseValidator());

    var exception =
        assertThrows(
            LocationNotFoundException.class,
            () -> useCase.create(warehouse("MWH.100", "UNKNOWN-001", 10, 5)));

    assertEquals("Location does not exist: UNKNOWN-001", exception.getMessage());
  }

  @Test
  void rejectsAnActiveDuplicateBusinessUnitCode() {
    var store = new InMemoryWarehouseStore(warehouse("MWH.100", "EINDHOVEN-001", 20, 5));
    var useCase = new CreateWarehouseUseCase(store, new LocationGateway(), new DefaultWarehouseValidator());

    assertThrows(
        WarehouseValidationException.class,
        () -> useCase.create(warehouse("MWH.100", "AMSTERDAM-001", 20, 5)));
  }

  @Test
  void permitsReusingTheBusinessUnitCodeOfAnArchivedWarehouse() {
    var archivedWarehouse = warehouse("MWH.100", "EINDHOVEN-001", 20, 5);
    archivedWarehouse.archivedAt = LocalDateTime.now();
    var store = new InMemoryWarehouseStore(archivedWarehouse);
    var useCase = new CreateWarehouseUseCase(store, new LocationGateway(), new DefaultWarehouseValidator());

    useCase.create(warehouse("MWH.100", "EINDHOVEN-001", 20, 5));

    assertEquals(2, store.getAll().size());
  }

  @Test
  void rejectsCreationWhenTheLocationHasReachedItsWarehouseLimit() {
    var store = new InMemoryWarehouseStore(warehouse("MWH.001", "ZWOLLE-001", 10, 5));
    var useCase = new CreateWarehouseUseCase(store, new LocationGateway(), new DefaultWarehouseValidator());

    assertThrows(
        WarehouseValidationException.class,
        () -> useCase.create(warehouse("MWH.002", "ZWOLLE-001", 10, 5)));
  }

  @Test
  void rejectsCreationWhenTheLocationCapacityWouldBeExceeded() {
    var store = new InMemoryWarehouseStore(warehouse("MWH.001", "AMSTERDAM-001", 80, 5));
    var useCase = new CreateWarehouseUseCase(store, new LocationGateway(), new DefaultWarehouseValidator());

    assertThrows(
        WarehouseValidationException.class,
        () -> useCase.create(warehouse("MWH.002", "AMSTERDAM-001", 30, 5)));
  }

  @Test
  void rejectsStockThatExceedsWarehouseCapacity() {
    var useCase =
        new CreateWarehouseUseCase(
            new InMemoryWarehouseStore(), new LocationGateway(), new DefaultWarehouseValidator());

    assertThrows(
        WarehouseValidationException.class,
        () -> useCase.create(warehouse("MWH.100", "EINDHOVEN-001", 10, 11)));
  }

  private static Warehouse warehouse(String businessUnitCode, String location, int capacity, int stock) {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }

  private static final class InMemoryWarehouseStore implements WarehouseStore {
    private final List<Warehouse> warehouses = new ArrayList<>();

    private InMemoryWarehouseStore(Warehouse... initialWarehouses) {
      warehouses.addAll(List.of(initialWarehouses));
    }

    @Override
    public List<Warehouse> getAll() {
      return warehouses;
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Warehouse warehouse) {
      throw new UnsupportedOperationException();
    }

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
