package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PostgresLocationAllocationLockTest {

  @Test
  void ordersAndDeduplicatesLocationLocksToPreventDeadlocks() {
    var locations =
        PostgresLocationAllocationLock.orderedIdentifiers(
            "ZWOLLE-001", "AMSTERDAM-001", "ZWOLLE-001", null, " ");

    assertEquals(List.of("AMSTERDAM-001", "ZWOLLE-001"), locations);
  }
}
