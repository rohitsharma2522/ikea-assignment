package com.fulfilment.application.monolith.stores;

/** Raised when a store has been created within the current transaction. */
public record StoreCreatedEvent(String name, int quantityProductsInStock) implements StoreEvent {

  public static StoreCreatedEvent from(Store store) {
    return new StoreCreatedEvent(store.name, store.quantityProductsInStock);
  }
}
