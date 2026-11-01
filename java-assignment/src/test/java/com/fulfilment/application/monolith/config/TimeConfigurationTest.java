package com.fulfilment.application.monolith.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class TimeConfigurationTest {

  @Test
  void providesUtcClock() {
    assertEquals(ZoneOffset.UTC, new TimeConfiguration().clock().getZone());
  }
}
