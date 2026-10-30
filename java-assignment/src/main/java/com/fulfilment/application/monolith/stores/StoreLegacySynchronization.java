package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreLegacySynchronization {

  private final LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Inject
  public StoreLegacySynchronization(LegacyStoreManagerGateway legacyStoreManagerGateway) {
    this.legacyStoreManagerGateway = legacyStoreManagerGateway;
  }

  void synchronizeCreatedStoreAfterSuccessfulCommit(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreCreatedEvent event) {
    legacyStoreManagerGateway.createStoreOnLegacySystem(event.toStore());
  }

  void synchronizeUpdatedStoreAfterSuccessfulCommit(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreUpdatedEvent event) {
    legacyStoreManagerGateway.updateStoreOnLegacySystem(event.toStore());
  }
}
