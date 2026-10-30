package com.fulfilment.application.monolith.warehouses.domain.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

class DefaultWarehouseValidatorTest {

  private final WarehouseValidator validator = new DefaultWarehouseValidator();

  @Test
  void acceptsAValidWarehouse() {
    assertDoesNotThrow(() -> validator.validate(warehouse("MWH.100", 10, 5)));
  }

  @Test
  void rejectsMissingWarehouse() {
    assertThrows(WarehouseValidationException.class, () -> validator.validate(null));
  }

  @Test
  void rejectsBlankBusinessUnitCode() {
    assertThrows(WarehouseValidationException.class, () -> validator.validate(warehouse(" ", 10, 5)));
  }

  @Test
  void rejectsNonPositiveCapacity() {
    assertThrows(WarehouseValidationException.class, () -> validator.validate(warehouse("MWH.100", 0, 0)));
  }

  @Test
  void rejectsNegativeStockAndStockAboveCapacity() {
    assertThrows(WarehouseValidationException.class, () -> validator.validate(warehouse("MWH.100", 10, -1)));
    assertThrows(WarehouseValidationException.class, () -> validator.validate(warehouse("MWH.100", 10, 11)));
  }

  private static Warehouse warehouse(String businessUnitCode, int capacity, int stock) {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}
