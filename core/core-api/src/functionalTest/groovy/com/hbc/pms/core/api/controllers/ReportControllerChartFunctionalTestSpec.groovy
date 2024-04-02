package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.controller.v1.enums.ChartType
import com.hbc.pms.core.api.controller.v1.request.SearchMultiDayChartCommand
import com.hbc.pms.core.api.controller.v1.response.OneDayChartResponse
import com.hbc.pms.core.api.service.report.ReportPersistenceService
import com.hbc.pms.integration.db.repository.ReportRepository
import com.hbc.pms.support.spock.test.RestClient
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
    var searchChartCommand = SearchMultiDayChartCommand.builder()
            .chartType(ChartType.PIE)
            .start()
            .build()


  }

  def "Get multi day MULTI_LINE or STACKED_BAR chart figures - OK"() {

  }

  def "Get multi day chart summary figures - OK"() {

  }
}
