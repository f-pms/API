package com.hbc.pms.core.api.controllers.report

import com.hbc.pms.core.api.controllers.ReportFunctionalTestSpec
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired

class ReportControllerOneDayChartFunctionalSpec extends ReportFunctionalTestSpec {
  @Autowired
  RestClient restClient

  def "Get one day chart figures for DAM type - OK"() {
    when:
    def response = restClient
            .get("$REPORT_PATH/1/charts/one-day", dataFixture.SUPERVISOR_USER, List<Map<String, Double>>)

    then:
    response.statusCode.is2xxSuccessful()
  }

  def "Get one day chart figures for DAM type - Not found and Bad request"() {
  }

  def "Get one day chart figures for BTP type - OK"() {

  }

  def "Get one day chart figures for BTP type - Not found and Bad request"() {

  }
}
