package com.hbc.pms.support.spock.test

import groovy.util.logging.Slf4j
import org.apache.commons.math3.random.RandomDataGenerator
import org.apache.plc4x.java.api.types.PlcResponseCode
import org.apache.plc4x.java.api.value.PlcValue
import org.apache.plc4x.java.mock.connection.MockDevice
import org.apache.plc4x.java.spi.messages.utils.ResponseItem
import org.apache.plc4x.java.spi.values.PlcBOOL
import org.apache.plc4x.java.spi.values.PlcINT
import org.apache.plc4x.java.spi.values.PlcREAL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.support.AbstractSubscribableChannel
import spock.lang.Specification

@Slf4j
class AbstractFunctionalSpec extends Specification {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate

    @Autowired
    EventCollector eventCollector

    @Autowired
    private AbstractSubscribableChannel clientInboundChannel

    @Autowired
    private AbstractSubscribableChannel clientOutboundChannel

    @Autowired
    private AbstractSubscribableChannel brokerChannel

    private TestChannelInterceptor clientOutboundChannelInterceptor
    private TestChannelInterceptor brokerChannelInterceptor
    protected PlcValueTestFactory plcValueTestFactory = new PlcValueTestFactory()

    def setup() {
        this.brokerChannelInterceptor = new TestChannelInterceptor(eventCollector)
        this.clientOutboundChannelInterceptor = new TestChannelInterceptor(eventCollector)
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor)
        mockDevice.read(_ as String) >> { String address ->
            plcValueTestFactory.respondItem(address)
        }
    }

    def cleanup() {
        eventCollector.clear()
    }
    @Autowired
    MockDevice mockDevice
    RandomDataGenerator random = new RandomDataGenerator()

    void mockRead(String variable, PlcValue value) {
        mockDevice.read(variable) >> new ResponseItem<PlcValue>(PlcResponseCode.OK, value)
    }

    void mockRead(String variable, Float min, Float max) {
        mockRead(variable, new PlcREAL(random.nextF(min, max)))
    }

    void mockRead(String variable, Float value) {
        mockRead(variable, new PlcREAL(value))
    }

    void mockRead(String variable, Integer value) {
        mockRead(variable, new PlcINT(value))
    }

    void mockRead(String variable, Integer min, Integer max) {
        mockRead(variable, new PlcINT(random.nextInt(min, max)))
    }

    void mockRead(String variable, Boolean flag) {
        mockRead(variable, new PlcBOOL(flag))
    }
}
