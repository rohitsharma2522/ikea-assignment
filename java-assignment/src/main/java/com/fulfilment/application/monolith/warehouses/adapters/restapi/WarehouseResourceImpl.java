package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.jboss.resteasy.reactive.ResponseStatus;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private final WarehouseStore warehouseStore;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;
  private final CreateWarehouseOperation createWarehouseOperation;
  private final ReplaceWarehouseOperation replaceWarehouseOperation;

  @Inject
  public WarehouseResourceImpl(
      WarehouseStore warehouseStore,
      ArchiveWarehouseOperation archiveWarehouseOperation,
      CreateWarehouseOperation createWarehouseOperation,
      ReplaceWarehouseOperation replaceWarehouseOperation) {
    this.warehouseStore = warehouseStore;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
    this.createWarehouseOperation = createWarehouseOperation;
    this.replaceWarehouseOperation = replaceWarehouseOperation;
  }

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseStore.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @ResponseStatus(201)
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    var warehouse = toDomainWarehouse(data);
    createWarehouseOperation.create(warehouse);
    return toWarehouseResponse(warehouse);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    return toWarehouseResponse(findActiveWarehouse(id));
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    archiveWarehouseOperation.archive(findActiveWarehouse(id));
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    var warehouse = toDomainWarehouse(data);
    replaceWarehouseOperation.replace(businessUnitCode, warehouse);
    return toWarehouseResponse(warehouse);
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    if (warehouse.getId() != null) {
      response.setId(warehouse.getId().toString());
    }
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }

  private Long parseWarehouseId(String id) {
    try {
      long parsedId = Long.parseLong(id);
      if (parsedId <= 0) {
        throw new NumberFormatException();
      }
      return parsedId;
    } catch (NumberFormatException exception) {
      throw new BadRequestException("Warehouse ID must be a positive number: " + id);
    }
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse
      findActiveWarehouse(String id) {
    Long warehouseId = parseWarehouseId(id);
    var warehouse = warehouseStore.findActiveById(warehouseId);
    if (warehouse == null) {
      throw new WarehouseNotFoundException(id);
    }
    return warehouse;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainWarehouse(
      Warehouse request) {
    var warehouse =
        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = request.getBusinessUnitCode();
    warehouse.location = request.getLocation();
    warehouse.capacity = request.getCapacity();
    warehouse.stock = request.getStock();
    return warehouse;
  }
}
