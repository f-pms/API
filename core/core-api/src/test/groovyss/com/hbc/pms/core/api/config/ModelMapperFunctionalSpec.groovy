package com.hbc.pms.core.api.config

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationCommand
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse
import com.hbc.pms.core.api.controller.v1.response.SensorConfigurationResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.AlarmCondition
import com.hbc.pms.core.model.Blueprint
import com.hbc.pms.core.model.SensorConfiguration
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import org.modelmapper.MappingException
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.PendingFeature

class ModelMapperFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  private ModelMapper mapper

  @Autowired
  SensorConfigurationRepository sensorConfigurationRepository

  def createDefaultAlarmActionCommand() {
    return new CreateAlarmConditionCommand.AlarmActionCommand(type: AlarmActionType.EMAIL,
            message: "Email action's message",
            recipients: new HashSet<String>() {
              {
                add("thisisemail@gmail.com")
                add("haiz@metqua.com")
              }
            })
  }

  def createDefaultAlarmConditionCommand(sensorConfiguration) {
    return new CreateAlarmConditionCommand(sensorConfigurationId: sensorConfiguration.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 30,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [createDefaultAlarmActionCommand()])
  }

  //TODO: wait for HuyN's input
  @PendingFeature
  def "Mapper CreateAlarmConditionCommand to AlarmCondition - Map checkInterval to cron - Correct"() {
    given:
    def conditionCommand = CreateAlarmConditionCommand.builder().checkInterval(checkIntervalVal)

    when:
    def condition = mapper.map(conditionCommand, AlarmCondition.class)

    then:
    1 == 1

    where:
    cronVal       | checkIntervalVal
    "*/1 * * * *" | 1
    "* */1 * * *" | 60
  }

  //TODO: wait for HuyN's input
  @PendingFeature
  def "Mapper UpdateAlarmConditionCommand to AlarmCondition mapper - Map checkInterval to cron - Corrected"() {}

  def "Mapper UpdateSensorConfigurationCommand to SensorConfiguration - Check address - Correct"() {
    given:
    def sensorConfigCommand = UpdateSensorConfigurationCommand.builder()
            .address(addressVal)
            .db(dbVal)
            .offset(offsetVal)
            .dataType(dataTypeVal)
            .build()
    sensorConfigCommand.aggregatePlcAddress()

    when:
    def sensorConfig = mapper.map(sensorConfigCommand, SensorConfiguration.class)

    then:
    sensorConfig.getAddress() == expectedAddressVal

    where:
    addressVal        | dbVal | offsetVal | dataTypeVal | expectedAddressVal
    null              | 112   | 112       | "REAL"      | "%DB112:112:REAL"
    "%DB112:112:REAL" | 12    | 11        | "INT"       | "%DB112:112:REAL"
    "%DB112:112:REAL" | null  | null      | null        | "%DB112:112:REAL"
  }

  def "Mapper UpdateSensorConfigurationCommand to SensorConfiguration - Map invalid addresses - Correct"() {
    given:
    def sensorConfigCommand = UpdateSensorConfigurationCommand.builder()
            .address(addressVal)
            .db(dbVal)
            .offset(offsetVal)
            .dataType(dataTypeVal)
            .build()

    when:
    def sensorConfig = mapper.map(sensorConfigCommand, SensorConfiguration.class)


    then:
    thrown(MappingException)

    where:
    addressVal         | dbVal | offsetVal | dataTypeVal
    "%DB11a:112:REAL2" | null  | null      | null
    "%DB112:a:REAL"    | null  | null      | null
    "%DB11:112:REAL2"  | null  | null      | null
    null               | 112   | 112       | "RA33L"
  }

  def "Mapper SensorConfiguration to SensorConfigurationResponse - Check address - Correct"() {
    given:
    def sensorConfig = SensorConfiguration.builder()
            .address("%DB$dbVal:$offsetVal:$dataTypeVal")
            .build()

    when:
    def sensorConfigResponse = mapper.map(sensorConfig, SensorConfigurationResponse.class)

    then:
    sensorConfigResponse.getAddress() == expectedAddressVal

    where:
    dbVal | offsetVal | dataTypeVal | expectedAddressVal
    112   | 112       | "REAL"      | "%DB112:112:REAL"
    12    | 11        | "INT"       | "%DB12:11:INT"
  }

  def "Mapper Blueprint to BlueprintResponse - Map list - Correct"() {
    given:
    def blueprint = Blueprint.builder().sensorConfigurations(
            new ArrayList<SensorConfiguration>(Arrays.asList(
                    SensorConfiguration.builder().address(TestDataFixture.PLC_ADDRESS_REAL_01).build(),
                    SensorConfiguration.builder().address(TestDataFixture.PLC_ADDRESS_REAL_02).build()
            ))
    ).build()

    when:
    def blueprintResponse = mapper.map(blueprint, BlueprintResponse.class)

    then:
    blueprint.getSensorConfigurations().size() == blueprintResponse.getSensorConfigurations().size()
    blueprint.getSensorConfigurations().eachWithIndex { item, index ->
      item.getAddress() == blueprintResponse.getSensorConfigurations()[index].getAddress()
    }
    noExceptionThrown()
  }

  def "Mapper Blueprint to BlueprintResponse - Map list - Failed"() {
    given:
    def blueprint = Blueprint.builder().sensorConfigurations(
            new ArrayList<SensorConfiguration>(Arrays.asList(
                    SensorConfiguration.builder().address("").build(),
                    SensorConfiguration.builder().address(TestDataFixture.PLC_ADDRESS_REAL_02).build()
            ))
    ).build()

    when:
    def blueprintResponse = mapper.map(blueprint, BlueprintResponse.class)

    then:
    thrown(MappingException)

    where:
    addressVal << ["%DB11a:112:REAL2", "%DB112:a:REAL", "%DB11:112:REAL2", null]
  }
}
