package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.Test;

class WarehouseResourceImplTest {

  @Test
  void returnsAnActiveWarehouseById() {
    var warehouse = warehouse(7L);
    warehouse.businessUnitCode = "MWH.007";
    warehouse.location = "EINDHOVEN-001";
    warehouse.capacity = 60;
    warehouse.stock = 20;
    var resource =
        resource(new StubWarehouseStore(warehouse), new RecordingArchiveOperation());

    var response = resource.getAWarehouseUnitByID("7");

    assertEquals("7", response.getId());
    assertEquals("MWH.007", response.getBusinessUnitCode());
    assertEquals("EINDHOVEN-001", response.getLocation());
    assertEquals(60, response.getCapacity());
    assertEquals(20, response.getStock());
  }

  @Test
  void getRejectsInvalidIdAndReturnsNotFoundForMissingWarehouse() {
    var resource =
        resource(new StubWarehouseStore(null), new RecordingArchiveOperation());

    assertThrows(BadRequestException.class, () -> resource.getAWarehouseUnitByID("invalid"));
    assertThrows(WarehouseNotFoundException.class, () -> resource.getAWarehouseUnitByID("7"));
  }

  @Test
  void delegatesArchiveOfAnActiveWarehouse() {
    var warehouse = warehouse(7L);
    var archiveOperation = new RecordingArchiveOperation();
    var resource = resource(new StubWarehouseStore(warehouse), archiveOperation);

    resource.archiveAWarehouseUnitByID("7");

    assertEquals(warehouse, archiveOperation.archivedWarehouse);
  }

  @Test
  void rejectsAnInvalidWarehouseId() {
    var resource =
        resource(new StubWarehouseStore(null), new RecordingArchiveOperation());

    assertThrows(BadRequestException.class, () -> resource.archiveAWarehouseUnitByID("invalid"));
    assertThrows(BadRequestException.class, () -> resource.archiveAWarehouseUnitByID("0"));
  }

  @Test
  void returnsNotFoundWhenActiveWarehouseDoesNotExist() {
    var resource =
        resource(new StubWarehouseStore(null), new RecordingArchiveOperation());

    assertThrows(WarehouseNotFoundException.class, () -> resource.archiveAWarehouseUnitByID("7"));
  }

  @Test
  void delegatesCreationAndReturnsThePersistedWarehouse() {
    var createOperation = new RecordingCreateOperation();
    var resource =
        new WarehouseResourceImpl(
            new StubWarehouseStore(null),
            new RecordingArchiveOperation(),
            createOperation,
            new RecordingReplaceOperation());
    var request = request("MWH.100", "EINDHOVEN-001", 60, 20);

    var response = resource.createANewWarehouseUnit(request);

    assertEquals("MWH.100", createOperation.createdWarehouse.businessUnitCode);
    assertEquals("9", response.getId());
  }

  @Test
  void delegatesReplacementUsingThePathBusinessUnitCode() {
    var replaceOperation = new RecordingReplaceOperation();
    var resource =
        new WarehouseResourceImpl(
            new StubWarehouseStore(null),
            new RecordingArchiveOperation(),
            new RecordingCreateOperation(),
            replaceOperation);
    var request = request("MWH.100", "EINDHOVEN-001", 60, 20);

    var response = resource.replaceTheCurrentActiveWarehouse("MWH.100", request);

    assertEquals("MWH.100", replaceOperation.businessUnitCode);
    assertEquals("9", response.getId());
  }

  private static WarehouseResourceImpl resource(
      WarehouseStore store, ArchiveWarehouseOperation archiveOperation) {
    return new WarehouseResourceImpl(
        store, archiveOperation, warehouse -> {}, (businessUnitCode, warehouse) -> {});
  }

  private static com.warehouse.api.beans.Warehouse request(
      String businessUnitCode, String location, int capacity, int stock) {
    var request = new com.warehouse.api.beans.Warehouse();
    request.setBusinessUnitCode(businessUnitCode);
    request.setLocation(location);
    request.setCapacity(capacity);
    request.setStock(stock);
    return request;
  }

  private static Warehouse warehouse(Long id) {
    var warehouse = new Warehouse();
    warehouse.assignId(id);
    return warehouse;
  }

  private static final class RecordingArchiveOperation implements ArchiveWarehouseOperation {
    private Warehouse archivedWarehouse;

    @Override
    public void archive(Warehouse warehouse) {
      archivedWarehouse = warehouse;
    }
  }

  private static final class RecordingCreateOperation implements CreateWarehouseOperation {
    private Warehouse createdWarehouse;

    @Override
    public void create(Warehouse warehouse) {
      createdWarehouse = warehouse;
      warehouse.assignId(9L);
    }
  }

  private static final class RecordingReplaceOperation implements ReplaceWarehouseOperation {
    private String businessUnitCode;

    @Override
    public void replace(String businessUnitCode, Warehouse warehouse) {
      this.businessUnitCode = businessUnitCode;
      warehouse.assignId(9L);
    }
  }

  private static final class StubWarehouseStore implements WarehouseStore {
    private final Warehouse warehouse;

    private StubWarehouseStore(Warehouse warehouse) {
      this.warehouse = warehouse;
    }

    @Override
    public List<Warehouse> getAll() {
      return warehouse == null ? List.of() : List.of(warehouse);
    }

    @Override
    public void create(Warehouse warehouse) {}

    @Override
    public void update(Warehouse warehouse) {}

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return null;
    }

    @Override
    public Warehouse findActiveById(Long id) {
      return warehouse != null && id.equals(warehouse.getId()) ? warehouse : null;
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
