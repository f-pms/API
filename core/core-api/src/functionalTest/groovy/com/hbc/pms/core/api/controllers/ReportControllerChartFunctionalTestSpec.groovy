package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.constant.ChartConstant
import com.hbc.pms.core.api.controller.v1.enums.ChartType
import com.hbc.pms.core.api.controller.v1.response.MultiDayChartResponse
import com.hbc.pms.core.api.controller.v1.response.OneDayChartResponse
import com.hbc.pms.core.api.service.report.ReportPersistenceService
import com.hbc.pms.integration.db.repository.ReportRepository
import com.hbc.pms.support.spock.test.RestClient
import java.time.OffsetDateTime
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Autowired

class ReportControllerChartFunctionalTestSpec extends FunctionalTestSpec {
  @Autowired
  private ReportRepository reportRepository

  @Autowired
  private ReportPersistenceService reportPersistenceService

  @Autowired
  private RestClient restClient

  def "Get one day chart figures - OK"() {
    given:
    var expectedReportEntity = reportRepository.findAllByType_Name(reportTypeName).first()
    var expectedReportModel = reportPersistenceService.getById(expectedReportEntity.id)

    when:
    def response
            = restClient.get(
            "${REPORT_PATH}/${expectedReportEntity.id}/charts/one-day",
            dataFixture.SUPERVISOR_USER,
            OneDayChartResponse)

    then:
    response.statusCode.is2xxSuccessful()
    with(response.body.data) {
      //TODO: check if this fail in CI
      it.recordingDate.toLocalDate() == expectedReportModel.recordingDate.toLocalDate()
      it.data == expectedReportModel.sums
      it.data.size() == expectedReportModel.sums.size()
    }

    where:
    reportTypeName << ["DAM", "BTP"]
  }

  def "Get multi day PIE chart figures - OK"() {
    given:
    var dayNum = 4
    var shiftNum = 2
    var start = OffsetDateTime.of(2024, 3, 27, 1, 0, 0, 0, ZoneOffset.UTC)
    var end = start.plusDays(dayNum).withHour(23).withMinute(59).withSecond(59)

    when:
    def response = restClient.get(
            "${REPORT_PATH}/charts/multi-day?start=${start}&end=${end}&chartType=${ChartType.PIE}",
            dataFixture.SUPERVISOR_USER,
            MultiDayChartResponse
    )

    then:
    response.statusCode.is2xxSuccessful()
    with(response.body.data) {
      it.labelSteps == null
      it.data["DAM"][ChartConstant.SUM_TOTAL].get(0)
              == TestDataFixture.DEFAULT_SUM_TOTAL * dayNum * shiftNum
      it.data["BTP"][ChartConstant.SUM_TOTAL].get(0)
              == TestDataFixture.DEFAULT_SUM_TOTAL * dayNum * shiftNum
    }
  }

  def "Get multi day MULTI_LINE or STACKED_BAR chart figures - OK"() {

  }

  def "Get multi day chart summary figures - OK"() {

  }
}
