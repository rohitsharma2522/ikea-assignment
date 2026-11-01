package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.event.Event;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StoreResourceTest {

  @Test
  void createsAStoreAndPublishesAnImmutableCreatedEvent() {
    StoreResource resource = new StoreResource();
    Event<StoreCreatedEvent> createdEvents = event();
    resource.storeCreatedEvents = createdEvents;
    resource.storeUpdatedEvents = event();
    Store store = mock(Store.class);
    store.name = "BILLY";
    store.quantityProductsInStock = 8;
    doNothing().when(store).persist();

    Response response = resource.create(store);

    assertEquals(201, response.getStatus());
    assertSame(store, response.getEntity());
    verify(store).persist();
    var event = ArgumentCaptor.forClass(StoreCreatedEvent.class);
    verify(createdEvents).fire(event.capture());
    assertEquals(new StoreCreatedEvent("BILLY", 8), event.getValue());
  }

  @Test
  void rejectsAStoreWithClientAssignedIdBeforePersistingOrPublishing() {
    StoreResource resource = new StoreResource();
    Event<StoreCreatedEvent> createdEvents = event();
    resource.storeCreatedEvents = createdEvents;
    Store store = mock(Store.class);
    store.id = 1L;

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.create(store));

    assertEquals(422, exception.getResponse().getStatus());
    verifyNoInteractions(createdEvents);
  }

  @Test
  void mapsApplicationAndUnexpectedErrors() {
    var mapper = new StoreResource.ErrorMapper();
    mapper.objectMapper = new ObjectMapper();

    assertEquals(422, mapper.toResponse(new WebApplicationException("Invalid", 422)).getStatus());
    assertEquals(500, mapper.toResponse(new IllegalStateException("Unexpected")).getStatus());
  }

  @Test
  void legacyGatewayProcessesCreatedAndUpdatedStores() {
    var store = new Store("BILLY");
    store.quantityProductsInStock = 8;
    var gateway = new LegacyStoreManagerGateway();

    gateway.createStoreOnLegacySystem(store);
    gateway.updateStoreOnLegacySystem(store);
  }

  @SuppressWarnings("unchecked")
  private static <T> Event<T> event() {
    return mock(Event.class);
  }
}
