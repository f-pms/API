package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import groovy.util.logging.Slf4j

@Slf4j
class WebsocketServiceSpec extends FunctionalTestSpec {

    def "Websocket sends correct PLC values"() {
        def target = "%DB9:13548:REAL"
        when: "Set tag to 5f"
        plcValueTestFactory.setCurrentValue(target, 5f)

        then: "Received event with value = 5.0"
        assertPlcTagWithValue(sensorConfiguration.id, "5.0")

        when: "Set tag to 15f"
        plcValueTestFactory.setCurrentValue(target, 15f)

        then: "Received event with value = 15.0"
        assertPlcTagWithValue(sensorConfiguration.id, "15.0")
    }
}
