package com.fulfilment.application.monolith.stores;

/** Common immutable data exposed by events raised after a store changes. */
public interface StoreEvent {

  String name();

  int quantityProductsInStock();

  default Store toStore() {
    var store = new Store();
    store.name = name();
    store.quantityProductsInStock = quantityProductsInStock();
    return store;
  }
}
