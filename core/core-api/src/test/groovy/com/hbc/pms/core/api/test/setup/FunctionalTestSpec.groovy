package com.hbc.pms.core.api.test.setup

import com.hbc.pms.core.api.CoreApiApplication
import com.hbc.pms.support.spock.test.AbstractFunctionalSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.util.concurrent.PollingConditions

@SpringBootTest(classes = [CoreApiApplication])
@ActiveProfiles("test")
@Import(value = FunctionalConfiguration.class)
class FunctionalTestSpec extends AbstractFunctionalSpec {
  PollingConditions conditions = new PollingConditions(timeout: 10)

  void assertPlcTagWithValue(String address, String value) {
    conditions.eventually {
      eventCollector.getEvent { r ->
        {
          r.payload.get(address) == value
        }
      }
    }
  }
}
