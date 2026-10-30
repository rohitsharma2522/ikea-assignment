package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StoreLegacySynchronizationTest {

  @Test
  void sendsCreatedStoresToTheLegacyCreateOperation() {
    var legacyGateway = new RecordingLegacyStoreManagerGateway();
    var synchronization = new StoreLegacySynchronization(legacyGateway);

    synchronization.synchronizeCreatedStoreAfterSuccessfulCommit(StoreCreatedEvent.from(store("HEMNES", 10)));

    assertEquals(1, legacyGateway.createdStores);
    assertEquals(0, legacyGateway.updatedStores);
  }

  @Test
  void sendsUpdatedStoresToTheLegacyUpdateOperation() {
    var legacyGateway = new RecordingLegacyStoreManagerGateway();
    var synchronization = new StoreLegacySynchronization(legacyGateway);

    synchronization.synchronizeUpdatedStoreAfterSuccessfulCommit(StoreUpdatedEvent.from(store("HEMNES", 15)));

    assertEquals(0, legacyGateway.createdStores);
    assertEquals(1, legacyGateway.updatedStores);
  }

  private static Store store(String name, int stock) {
    var store = new Store();
    store.name = name;
    store.quantityProductsInStock = stock;
    return store;
  }

  private static final class RecordingLegacyStoreManagerGateway extends LegacyStoreManagerGateway {
    private int createdStores;
    private int updatedStores;

    @Override
    public void createStoreOnLegacySystem(Store store) {
      createdStores++;
    }

    @Override
    public void updateStoreOnLegacySystem(Store store) {
      updatedStores++;
    }
  }
}
