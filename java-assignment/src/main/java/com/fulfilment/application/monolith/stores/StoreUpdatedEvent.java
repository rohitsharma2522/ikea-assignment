package com.fulfilment.application.monolith.stores;

/** Raised when a store has been updated within the current transaction. */
public record StoreUpdatedEvent(String name, int quantityProductsInStock) implements StoreEvent {

  public static StoreUpdatedEvent from(Store store) {
    return new StoreUpdatedEvent(store.name, store.quantityProductsInStock);
  }
}
