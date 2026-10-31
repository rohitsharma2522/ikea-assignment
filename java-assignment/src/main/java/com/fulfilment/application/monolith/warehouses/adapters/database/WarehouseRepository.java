package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return find("archivedAt is null").list().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    var dbWarehouse = DbWarehouse.fromWarehouse(warehouse);
    persist(dbWarehouse);
    warehouse.assignId(dbWarehouse.id);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse dbWarehouse = findById(warehouse.getId());
    if (dbWarehouse == null) {
      throw new IllegalArgumentException("Warehouse does not exist: " + warehouse.getId());
    }

    dbWarehouse.updateFrom(warehouse);
  }

  @Override
  public void remove(Warehouse warehouse) {
    warehouse.archivedAt = LocalDateTime.now();
    update(warehouse);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    return find("businessUnitCode = ?1 and archivedAt is null", buCode)
        .firstResultOptional()
        .map(DbWarehouse::toWarehouse)
        .orElse(null);
  }

  @Override
  public Warehouse findActiveById(Long id) {
    if (id == null) {
      return null;
    }

    return find("id = ?1 and archivedAt is null", id)
        .firstResultOptional()
        .map(DbWarehouse::toWarehouse)
        .orElse(null);
  }

  @Override
  public long countActiveByLocation(String location) {
    return count("location = ?1 and archivedAt is null", location);
  }

  @Override
  public int totalActiveCapacityByLocation(String location) {
    Long totalCapacity =
        getEntityManager()
            .createQuery(
                "select coalesce(sum(warehouse.capacity), 0) "
                    + "from DbWarehouse warehouse "
                    + "where warehouse.location = :location and warehouse.archivedAt is null",
                Long.class)
            .setParameter("location", location)
            .getSingleResult();
    return totalCapacity.intValue();
  }
}
