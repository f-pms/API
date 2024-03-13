package com.hbc.pms.core.api.test.setup

import com.hbc.pms.core.api.CoreApiApplication
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.support.spock.test.AbstractFunctionalSpec
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

@SpringBootTest(classes = [CoreApiApplication], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableSharedInjection
class FunctionalTestSpec extends AbstractFunctionalSpec {
  PollingConditions conditions = new PollingConditions(timeout: 20)

  @Autowired
  @Shared
  TestDataFixture dataFixture

  def setupSpec() {
    dataFixture.populate()
  }
  def cleanupSpec(){
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
