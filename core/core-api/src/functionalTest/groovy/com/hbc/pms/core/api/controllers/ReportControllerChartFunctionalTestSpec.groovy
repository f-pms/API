package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.controller.v1.enums.ChartType
import com.hbc.pms.core.api.controller.v1.request.SearchMultiDayChartCommand
import com.hbc.pms.core.api.controller.v1.response.OneDayChartResponse
import com.hbc.pms.core.api.service.report.ReportPersistenceService
import com.hbc.pms.core.api.util.DateTimeUtil
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
    var start = OffsetDateTime.of(
            2024, 03, 28, 0, 1, 0, 0,
            ZoneOffset.of(DateTimeUtil.VIETNAM_ZONE_STR))

    var end = OffsetDateTime.of(
            2024, 03, 30, 0, 1, 0, 0,
            ZoneOffset.of(DateTimeUtil.VIETNAM_ZONE_STR))
    var searchChartCommand = SearchMultiDayChartCommand.builder()
            .chartType(ChartType.PIE)
            .start(start)
            .end(end)
            .build()

    when:
    def response = restClient.get(
            "${REPORT_PATH}/charts/multi-day",
            dataFixture.SUPERVISOR_USER,
            OneDayChartResponse)

    then:
    response.statusCode.is2xxSuccessful()
    with(response.body.data) {
      it.data.size() == 2
    }

  }

  def "Get multi day MULTI_LINE or STACKED_BAR chart figures - OK"() {

  }

  def "Get multi day chart summary figures - OK"() {

  }
}
