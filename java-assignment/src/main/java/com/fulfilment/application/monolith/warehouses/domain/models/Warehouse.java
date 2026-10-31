package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.LocalDateTime;

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
}
