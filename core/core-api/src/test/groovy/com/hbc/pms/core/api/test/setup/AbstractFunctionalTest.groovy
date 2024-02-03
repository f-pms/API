package com.hbc.pms.core.api.test.setup

import com.hbc.pms.core.api.CoreApiApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest(classes = [CoreApiApplication])
@ActiveProfiles("test")
@Import(value = FunctionalConfiguration.class)
class AbstractFunctionalTest extends Specification{
  def conditions = new PollingConditions(timeout: 40, initialDelay: 1.5)
}
