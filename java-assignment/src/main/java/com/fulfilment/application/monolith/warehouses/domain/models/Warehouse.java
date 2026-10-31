package com.fulfilment.application.monolith.warehouses.domain.models;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseAlreadyArchivedException;
import java.time.LocalDateTime;
import java.util.Objects;

public class Warehouse {

  // database identifier
  private Long id;

  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

  public Long getId() {
    return id;
  }

  public void assignId(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Warehouse ID cannot be null");
    }
    if (this.id != null) {
      throw new IllegalStateException("Warehouse ID is already assigned");
    }

    this.id = id;
  }

  public void archive(LocalDateTime archivedAt) {
    if (this.archivedAt != null) {
      throw new WarehouseAlreadyArchivedException(id);
    }

    this.archivedAt = Objects.requireNonNull(archivedAt, "Archive timestamp cannot be null");
  }
}
