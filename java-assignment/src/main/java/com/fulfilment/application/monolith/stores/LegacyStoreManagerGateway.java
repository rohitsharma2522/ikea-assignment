package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void createStoreOnLegacySystem(Store store) {
    // just to emulate as this would send this to a legacy system, let's write a temp file with the
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    // just to emulate as this would send this to a legacy system, let's write a temp file with the
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    try {
      // Step 1: Create a temporary file
      Path tempFile;

      tempFile = Files.createTempFile(store.name, ".txt");
      LOGGER.debug("Created legacy synchronization file for store " + store.name);

      // Step 2: Write data to the temporary file
      String content =
          "Store created. [ name ="
              + store.name
              + " ] [ items on stock ="
              + store.quantityProductsInStock
              + "]";
      Files.write(tempFile, content.getBytes());
      LOGGER.debug("Wrote legacy synchronization data for store " + store.name);

      // Step 3: Optionally, read the data back to verify
      Files.readAllBytes(tempFile);
      LOGGER.debug("Verified legacy synchronization data for store " + store.name);

      // Step 4: Delete the temporary file when done
      Files.delete(tempFile);
      LOGGER.debug("Deleted legacy synchronization file for store " + store.name);

    } catch (Exception e) {
      LOGGER.error("Legacy synchronization failed for store " + store.name, e);
    }
  }
}
