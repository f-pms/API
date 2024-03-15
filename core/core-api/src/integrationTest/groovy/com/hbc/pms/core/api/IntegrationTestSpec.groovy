package com.hbc.pms.core.api

import com.hbc.pms.support.spock.test.AbstractFunctionalSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.util.concurrent.PollingConditions

@SpringBootTest(classes = [CoreApiApplication], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class IntegrationTestSpec extends AbstractFunctionalSpec {
  PollingConditions conditions = new PollingConditions(timeout: 20)

  @Autowired
  TestDataFixture dataFixture

  def setup() {
    dataFixture.populate()
  }
  def cleanup(){
    dataFixture.cleanup()
  }

  void assertPlcTagWithValue(Long id, String value) {
    conditions.eventually {
      eventCollector.getEvent { r ->
        {
          r.payload.get(id.toString()) == value
        }
      }
    }
  }
}
