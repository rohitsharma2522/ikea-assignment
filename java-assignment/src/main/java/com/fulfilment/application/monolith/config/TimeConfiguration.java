package com.fulfilment.application.monolith.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;

@ApplicationScoped
public class TimeConfiguration {

  @Produces
  Clock clock() {
    return Clock.systemUTC();
  }
}
