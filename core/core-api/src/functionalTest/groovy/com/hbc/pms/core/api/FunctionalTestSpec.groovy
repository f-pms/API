package com.hbc.pms.core.api


import com.hbc.pms.support.spock.test.AbstractFunctionalSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import spock.util.concurrent.PollingConditions


import static com.hbc.pms.core.api.util.DateTimeUtil.VIETNAM_TIMEZONE

@SpringBootTest(classes = [CoreApiApplication], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("functional-test")
class FunctionalTestSpec extends AbstractFunctionalSpec {
  protected static String ALARM_CONDITION_PATH = "/alarm-conditions"
  protected static String REPORT_PATH = "/reports"
  PollingConditions conditions = new PollingConditions(timeout: 20)

  @Autowired
  TestDataFixture dataFixture

  @LocalServerPort
  Integer port

  def setup() {
    dataFixture.populate()
  }

  def cleanup() {
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
