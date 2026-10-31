package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.ports.LocationAllocationLock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class PostgresLocationAllocationLock implements LocationAllocationLock {

  private final EntityManager entityManager;

  @Inject
  public PostgresLocationAllocationLock(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void acquire(String... locationIdentifiers) {
    orderedIdentifiers(locationIdentifiers)
        .forEach(
            identifier ->
                entityManager
                    .createNativeQuery(
                        "select pg_advisory_xact_lock(hashtextextended(cast(?1 as text), 0))")
                    .setParameter(1, identifier)
                    .getSingleResult());
  }

  static List<String> orderedIdentifiers(String... locationIdentifiers) {
    return Arrays.stream(locationIdentifiers)
        .filter(identifier -> identifier != null && !identifier.isBlank())
        .distinct()
        .sorted()
        .toList();
  }
}
