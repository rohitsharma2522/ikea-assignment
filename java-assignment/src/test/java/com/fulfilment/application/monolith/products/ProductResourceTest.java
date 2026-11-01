package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductResourceTest {

  private final ProductRepository productRepository = mock(ProductRepository.class);
  private final ProductResource resource = new ProductResource();

  @BeforeEach
  void setUp() {
    resource.productRepository = productRepository;
  }

  @Test
  void listsAndRetrievesProducts() {
    var product = product("TONSTAD", 10);
    product.id = 1L;
    when(productRepository.listAll(any())).thenReturn(List.of(product));
    when(productRepository.findById(1L)).thenReturn(product);

    assertEquals(List.of(product), resource.get());
    assertSame(product, resource.getSingle(1L));
  }

  @Test
  void rejectsMissingProduct() {
    assertStatus(404, assertThrows(WebApplicationException.class, () -> resource.getSingle(99L)));
    assertStatus(404, assertThrows(WebApplicationException.class, () -> resource.delete(99L)));
  }

  @Test
  void createsOnlyProductsWithoutClientAssignedIds() {
    var product = product("BILLY", 5);

    Response response = resource.create(product);

    assertEquals(201, response.getStatus());
    assertSame(product, response.getEntity());
    verify(productRepository).persist(product);

    product.id = 99L;
    assertStatus(422, assertThrows(WebApplicationException.class, () -> resource.create(product)));
  }

  @Test
  void updatesAnExistingProduct() {
    var existing = product("OLD", 1);
    var request = product("NEW", 9);
    request.description = "Updated description";
    request.price = new BigDecimal("12.50");
    when(productRepository.findById(1L)).thenReturn(existing);

    Product result = resource.update(1L, request);

    assertSame(existing, result);
    assertEquals("NEW", existing.name);
    assertEquals("Updated description", existing.description);
    assertEquals(new BigDecimal("12.50"), existing.price);
    assertEquals(9, existing.stock);
    verify(productRepository).persist(existing);
  }

  @Test
  void rejectsAnUpdateWithoutAName() {
    var request = new Product();

    assertStatus(422, assertThrows(WebApplicationException.class, () -> resource.update(1L, request)));
  }

  @Test
  void deletesAnExistingProduct() {
    var product = product("BILLY", 5);
    when(productRepository.findById(1L)).thenReturn(product);

    Response response = resource.delete(1L);

    assertEquals(204, response.getStatus());
    verify(productRepository).delete(product);
  }

  @Test
  void mapsApplicationAndUnexpectedErrors() {
    var mapper = new ProductResource.ErrorMapper();
    mapper.objectMapper = new ObjectMapper();

    assertEquals(404, mapper.toResponse(new WebApplicationException("Missing", 404)).getStatus());
    assertEquals(500, mapper.toResponse(new IllegalStateException("Unexpected")).getStatus());
  }

  private static Product product(String name, int stock) {
    var product = new Product(name);
    product.stock = stock;
    return product;
  }

  private static void assertStatus(int expectedStatus, WebApplicationException exception) {
    assertEquals(expectedStatus, exception.getResponse().getStatus());
  }
}
