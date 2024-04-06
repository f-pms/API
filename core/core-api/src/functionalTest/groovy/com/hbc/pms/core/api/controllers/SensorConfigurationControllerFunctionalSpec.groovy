package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse
import com.hbc.pms.core.api.controller.v1.response.SensorConfigurationResponse
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import com.hbc.pms.support.web.error.ErrorCode
import org.springframework.beans.factory.annotation.Autowired

class SensorConfigurationControllerFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  RestClient restClient

  def setup() {
    def monitoringBlueprint = blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get()
    def predefinedBlueprint = blueprintRepository.findById(TestDataFixture.PREDEFINED_ALARM_BLUEPRINT_ID).get()
    def customBlueprint = blueprintRepository.findById(TestDataFixture.CUSTOM_ALARM_BLUEPRINT_ID).get()
    def monitoringConfig = configurationRepository
            .save(TestDataFixture.createSensorConfiguration(
                    monitoringBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01))

    def predefinedConfig = configurationRepository
            .save(TestDataFixture.createSensorConfiguration(
                    predefinedBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01))

    def customConfig = configurationRepository
            .save(TestDataFixture.createSensorConfiguration(
                    customBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01))
  }

  def "Get sensor configurations - OK"() {
    given:
    def configEntities = configurationRepository.findAll()
    def configCountBefore = configEntities.size()

    when:
    def response = restClient.get(SENSOR_CONFIG_PATH, dataFixture.ADMIN_USER, List<SensorConfigurationResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def configs = response.body.data as List<SensorConfigurationResponse>
    configs.size() == configCountBefore
    configs.every {
      it.id as Long in configEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get sensor configurations - By blueprintType - OK"() {
    given:
    def configEntities
            = configurationRepository
            .findAllByBlueprint_TypeAndBlueprint_Name(BlueprintType.MONITORING, null)
    def configCountBefore = configEntities.size()

    when:
    def response = restClient.get("$SENSOR_CONFIG_PATH?blueprintType=MONITORING", dataFixture.ADMIN_USER, List<SensorConfigurationResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def configs = response.body.data as List<SensorConfigurationResponse>
    configs.size() == configCountBefore
    configs.every {
      it["id"] as Long in configEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get sensor configurations - By blueprintType and not found - OK with empty list"() {
    when:
    def response
            = restClient.get("$SENSOR_CONFIG_PATH?blueprintType=Random", dataFixture.ADMIN_USER,
            List<SensorConfigurationResponse>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error.code == ErrorCode.E400.toString()
  }

  def "Get sensor configurations - By blueprintName - OK"() {
    given:
    def configEntities
            = configurationRepository
            .findAllByBlueprint_TypeAndBlueprint_Name(null, "PREDEFINED")
    def configCountBefore = configEntities.size()

    when:
    def response = restClient.get("$SENSOR_CONFIG_PATH?blueprintName=PREDEFINED", dataFixture.ADMIN_USER, List<SensorConfigurationResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def configs = response.body.data as List<SensorConfigurationResponse>
    configs.size() == configCountBefore
    configs.every {
      it.id as Long in configEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get sensor configurations - By blueprintName and not found - OK with empty list"() {
    when:
    def response
            = restClient.get("$SENSOR_CONFIG_PATH?blueprintName=Random", dataFixture.ADMIN_USER,
            List<SensorConfigurationResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def configs = response.body.data as List<SensorConfigurationResponse>
    configs.size() == 0
  }

  def "Get sensor configurations - By blueprintName and blueprintType - OK"() {
    given:
    def configEntities = configurationRepository
            .findAllByBlueprint_TypeAndBlueprint_Name(BlueprintType.ALARM, "PREDEFINED")
    def configCountBefore = configEntities.size()

    when:
    def response
            = restClient.get("$SENSOR_CONFIG_PATH?blueprintType=ALARM&blueprintName=PREDEFINED", dataFixture.ADMIN_USER, List<BlueprintResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<SensorConfigurationResponse>
    blueprints.size() == configCountBefore
    blueprints.every {
      it.id as Long in configEntities.asList().stream().map(sc -> sc.getId()).toList()
    }
  }
}
