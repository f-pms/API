package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.SensorConfigurationRequest
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationCommand
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.PendingFeature

import java.util.concurrent.ThreadLocalRandom

class BlueprintControllerFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  RestClient restClient

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  def "Get all blueprints - OK"() {
    given:
    def blueprintEntities = blueprintRepository.findAll()
    def blueprintEntityCount = blueprintEntities.size()

    when:
    def response
            = restClient.get("/blueprints", dataFixture.ADMIN_USER, ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == blueprintEntityCount
    blueprints.every {
      it["id"] as Long in blueprintEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get all blueprints by blueprintName - OK"() {
    given:
    def blueprintEntities = blueprintRepository
            .findAllByTypeAndName(null, "PREDEFINED")
    def blueprintEntityCount = blueprintEntities.size()

    when:
    def response
            = restClient.get("/blueprints?blueprintName=PREDEFINED", dataFixture.ADMIN_USER, ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == blueprintEntityCount
    blueprints.every {
      it["id"] as Long in blueprintEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get all blueprints by blueprintType - OK"() {
    given:
    def blueprintEntities = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, null)
    def blueprintEntityCount = blueprintEntities.size()

    when:
    def response
            = restClient.get("/blueprints?blueprintType=ALARM", dataFixture.ADMIN_USER, ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == blueprintEntityCount
    blueprints.every {
      it["id"] as Long in blueprintEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get all blueprints by blueprintType and blueprintName - OK"() {
    given:
    def blueprintEntities = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, "PREDEFINED")
    def blueprintEntityCount = blueprintEntities.size()

    when:
    def response
            = restClient.get("/blueprints?blueprintType=ALARM&blueprintName=PREDEFINED", dataFixture.ADMIN_USER, ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == blueprintEntityCount
    blueprints.every {
      it["id"] as Long in blueprintEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get all blueprints by blueprintType - Not existed type - Bad request error"() {
    when:
    def response
            = restClient.get("/blueprints?blueprintType=Random", dataFixture.ADMIN_USER, ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
  }

  def "Get all blueprints by blueprintName - Not existed name - OK with empty list"() {
    when:
    def response
            = restClient.get("/blueprints?blueprintName=Random", dataFixture.ADMIN_USER, ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == 0
  }

  def "Get blueprint by Id - OK"() {
    when:
    def response
            = restClient.get("/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID", dataFixture.ADMIN_USER, ApiResponse<BlueprintResponse>)
    def blueprintEntity = blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get()
    configurationRepository.save(
            TestDataFixture.createSensorConfiguration(blueprintEntity, TestDataFixture.PLC_ADDRESS_BOOL_01))

    then:
    response.statusCode.is2xxSuccessful()
    verifyAll(response.body.data as BlueprintResponse) {
      it.getId() == TestDataFixture.MONITORING_BLUEPRINT_ID
      it.getName() == blueprintEntity.name
      it.getDescription() == blueprintEntity.description
      it.getSensorConfigurations().size() == blueprintEntity.sensorConfigurations.size()
      it.sensorConfigurations.every {
        it.getId() in blueprintEntity
                .sensorConfigurations.stream().map(sc -> sc.getId()).toList()
      }
    }
  }

  def "Get blueprint by Id - Not found and Bad request"() {
    when:
    def response
            = restClient.get("/blueprints/123", dataFixture.ADMIN_USER, ApiResponse<RuntimeException>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
  }

  def "Get blueprint by Id - Invalid format input - Bad request"() {
    when:
    def response
            = restClient.get("/blueprints/abc", dataFixture.ADMIN_USER, ApiResponse<RuntimeException>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
  }

  @PendingFeature
  //No feature is using this endpoint yet
  def "Create blueprint - OK"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Create blueprint - Null name and description - Bad Request"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Update blueprint - OK"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Update blueprint - Null name and description - Bad Request"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Update blueprint - Not existed blueprint - Not found and Bad request"() {}

  def "Create sensor config - OK"() {
    given:
    def configRequest = createSensorConfigurationRequest()
    def configCountBefore = configurationRepository.findAll().size()

    when:
    def response = restClient
            .post(
                    "/blueprints/$TestDataFixture.CUSTOM_ALARM_BLUEPRINT_ID/sensor-configurations", configRequest, dataFixture.ADMIN_USER,
                    ApiResponse<Boolean>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data == true
    def configCountAfter = configurationRepository.findAll().size()
    configCountBefore + 1 == configCountAfter
  }

  @PendingFeature
  def "Create sensor config - Null address - Bad request"() {}

  @PendingFeature
  def "Create sensor config - Not existed blueprint - Not found and Bad request"() {}

  def "Update sensor config - Using address field - OK"() {
    given:
    def updateConfigRequest
            = UpdateSensorConfigurationCommand.builder()
            .address(TestDataFixture.PLC_ADDRESS_REAL_01)
            .build()
    def sensorConfig = configurationRepository.save(
            TestDataFixture.createSensorConfiguration(
                    blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get(),
                    TestDataFixture.PLC_ADDRESS_REAL_01)
    )

    when:
    def response = restClient
            .put(
                    "/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID/sensor-configurations/$sensorConfig.id",
                    updateConfigRequest, dataFixture.ADMIN_USER, ApiResponse<Boolean>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data == true
    def updatedConfig
            = configurationRepository.findById(sensorConfig.id).get()
    verifyAll(updatedConfig) {
      getAddress() == updateConfigRequest.address
      getX() == sensorConfig.x
      getY() == sensorConfig.y
    }
  }

  def "Update sensor config - Using 3 fields aggregating - OK"() {
    given:
    def updateConfigRequest
            = UpdateSensorConfigurationCommand.builder()
            .db(9)
            .offset(13548)
            .dataType("REAL")
            .build()
    def sensorConfig = configurationRepository.save(
            TestDataFixture.createSensorConfiguration(
                    blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get(),
                    TestDataFixture.PLC_ADDRESS_REAL_01)
    )

    when:
    def response = restClient
            .put(
                    "/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID/sensor-configurations/$sensorConfig.id",
                    updateConfigRequest, dataFixture.ADMIN_USER, ApiResponse<Boolean>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data == true
    def updatedConfig
            = configurationRepository.findById(sensorConfig.id).get()
    verifyAll(updatedConfig) {
      getAddress() == TestDataFixture.PLC_ADDRESS_REAL_01
      getX() == sensorConfig.x
      getY() == sensorConfig.y
    }
  }

  @PendingFeature
  // not having this validation yet
  def "Update sensor config - Using 3 fields aggregating with data type is null - Exception thrown"() {
    given:
    def updateConfigRequest
            = UpdateSensorConfigurationCommand.builder()
            .db(9)
            .offset(13548)
            .dataType(null)
            .build()
    def sensorConfig = configurationRepository.save(
            TestDataFixture.createSensorConfiguration(
                    blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get(),
                    TestDataFixture.PLC_ADDRESS_REAL_01)
    )

    when:
    def response = restClient
            .put(
                    "/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID/sensor-configurations/$sensorConfig.id",
                    updateConfigRequest, dataFixture.ADMIN_USER, ApiResponse<Boolean>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data == true
    def updatedConfig
            = configurationRepository.findById(sensorConfig.id).get()
    verifyAll(updatedConfig) {
      getAddress() == TestDataFixture.PLC_ADDRESS_REAL_01
      getX() == sensorConfig.x
      getY() == sensorConfig.y
    }
  }

  def "Update sensor config - Using 3 fields aggregating with offset is null - Internal Server Error"() {
    given:
    def updateConfigRequest
            = UpdateSensorConfigurationCommand.builder()
            .db(9)
            .build()
    def sensorConfig = configurationRepository.save(
            TestDataFixture.createSensorConfiguration(
                    blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get(),
                    TestDataFixture.PLC_ADDRESS_REAL_01)
    )

    when:
    def response = restClient
            .put(
                    "/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID/sensor-configurations/$sensorConfig.id",
                    updateConfigRequest, dataFixture.ADMIN_USER, ApiResponse<Boolean>)

    then:
    response.statusCode.is5xxServerError()
    response.body.error["code"] == ErrorCode.E500.toString()
  }

  @PendingFeature
  //Not having this feature yet
  def "Update sensor config - Not existed blueprint - Not found and Bad request"() {
    given:
    def updateConfigRequest
            = UpdateSensorConfigurationCommand.builder().build()
    def sensorConfig = configurationRepository.save(
            TestDataFixture.createSensorConfiguration(
                    blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get(),
                    TestDataFixture.PLC_ADDRESS_REAL_01)
    )

    when:
    def response = restClient
            .put(
                    "/blueprints/123/sensor-configurations/$sensorConfig.id",
                    updateConfigRequest, dataFixture.ADMIN_USER, ApiResponse<Boolean>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
  }

  def "Update sensor config - Not existed sensor config - Not found and Bad request"() {
    given:
    def updateConfigRequest
            = UpdateSensorConfigurationCommand.builder()
            .db(9)
            .offset(13548)
            .dataType("REAL")
            .build()
    def sensorConfig = configurationRepository.save(
            TestDataFixture.createSensorConfiguration(
                    blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get(),
                    TestDataFixture.PLC_ADDRESS_REAL_01)
    )

    when:
    def response = restClient
            .put(
                    "/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID/sensor-configurations/123456",
                    updateConfigRequest, dataFixture.ADMIN_USER, ApiResponse<Boolean>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
  }

  def createSensorConfigurationRequest() {
    return SensorConfigurationRequest.builder()
            .address(TestDataFixture.PLC_ADDRESS_REAL_01)
            .x(ThreadLocalRandom.current().nextDouble(1, 500))
            .y(ThreadLocalRandom.current().nextDouble(1, 500))
            .build()
  }
}
