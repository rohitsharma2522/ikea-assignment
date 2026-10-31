package com.fulfilment.application.monolith.warehouses.domain.ports;

/** Serializes warehouse allocation changes for the supplied locations. */
public interface LocationAllocationLock {

  /**
   * Acquires transaction-scoped locks for the supplied locations before allocation limits are
   * evaluated. Implementations must acquire multiple locks in a deterministic order to prevent
   * deadlocks; locks are released automatically when the current transaction commits or rolls
   * back.
   */
  void acquire(String... locationIdentifiers);
}
