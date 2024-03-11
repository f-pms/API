package com.hbc.pms.core.api.test.setup

import com.hbc.pms.core.api.CoreApiApplication
import com.hbc.pms.core.api.service.BlueprintPersistenceService
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService
import com.hbc.pms.core.model.Blueprint
import com.hbc.pms.core.model.SensorConfiguration
import com.hbc.pms.plc.api.PlcConnector
import com.hbc.pms.support.spock.test.AbstractFunctionalSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.util.concurrent.PollingConditions

@SpringBootTest(classes = [CoreApiApplication])
@ActiveProfiles("test")
class FunctionalTestSpec extends AbstractFunctionalSpec {
  PollingConditions conditions = new PollingConditions(timeout: 20)
  @Autowired
  PlcConnector connector

  @Autowired
  BlueprintPersistenceService blueprintPersistenceService
  @Autowired
  SensorConfigurationPersistenceService configurationPersistenceService

  SensorConfiguration sensorConfiguration

  def setup() {
    Blueprint blueprint = Blueprint.builder().name("Tsafestt")
            .description("desc")
            .build()
    var createdBlueprint = blueprintPersistenceService.create(blueprint)
    configurationPersistenceService.create(createdBlueprint.id, new SensorConfiguration(address: "%DB9:13548:REAL", x: 0, y: 0))
    sensorConfiguration = blueprintPersistenceService.getById(1).sensorConfigurations[0]
    connector.updateScheduler()
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
