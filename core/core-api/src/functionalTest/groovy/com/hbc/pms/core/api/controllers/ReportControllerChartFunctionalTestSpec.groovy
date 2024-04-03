package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.constant.ChartConstant
import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType
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
            = restClient.get("${REPORT_PATH}/${expectedReportEntity.id}/charts/one-day",
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
    def response = restClient.get("${REPORT_PATH}/charts/multi-day?start=${start}&end=${end}&chartType=${ChartType.PIE}",
            dataFixture.SUPERVISOR_USER,
            MultiDayChartResponse)

    then:
    response.statusCode.is2xxSuccessful()
    with(response.body.data) {
      it.labelSteps == null
      it.data["DAM"][ChartConstant.SUM_TOTAL].get(0) == TestDataFixture.DEFAULT_SUM_TOTAL * dayNum * shiftNum
      it.data["BTP"][ChartConstant.SUM_TOTAL].get(0) == TestDataFixture.DEFAULT_SUM_TOTAL * dayNum * shiftNum
    }
  }

  def "Get multi day MULTI_LINE or STACKED_BAR chart figures - OK"() {
    given:
    var timeUnitCount = 2
    var shiftNum = 2
    var end = OffsetDateTime.of(
            2024, 3, 30, 23, 0, 0, 0,
            ZoneOffset.UTC)

    var start = switch (queryType) {
      case ChartQueryType.DAY -> OffsetDateTime.of(
              2024, 3, 29, 1, 0, 0, 0,
              ZoneOffset.UTC)
      case ChartQueryType.WEEK -> OffsetDateTime.of(
              2024, 3, 17, 1, 0, 0, 0,
              ZoneOffset.UTC)
      case ChartQueryType.MONTH -> OffsetDateTime.of(
              2024, 1, 31, 1, 0, 0, 0,
              ZoneOffset.UTC)
      case ChartQueryType.YEAR -> OffsetDateTime.of(
              2022, 3, 31, 1, 0, 0, 0,
              ZoneOffset.UTC)
    }

    when:
    def response = restClient.get("${REPORT_PATH}/charts/multi-day"
            + "?start=${start}&end=${end}" + "&chartType=${ChartType.STACKED_BAR}"
            + "&queryType=${queryType}",
            dataFixture.SUPERVISOR_USER,
            MultiDayChartResponse)

    then:
    response.statusCode.is2xxSuccessful()
    with(response.body.data) { MultiDayChartResponse chartResponse ->
      chartResponse.labelSteps.size() == timeUnitCount
      TestDataFixture.DAM_INDICATORS.forEach { indic ->
        {
          var damChartRes = chartResponse.data["DAM"]
          var btpChartRes = chartResponse.data["BTP"]
          assert damChartRes[indic].size() == timeUnitCount
          assert btpChartRes[indic].size() == timeUnitCount
        }
      }
    }


    where:
    queryType            | _
    ChartQueryType.DAY   | _
    ChartQueryType.WEEK  | _
    ChartQueryType.MONTH | _
    ChartQueryType.YEAR  | _
  }

  def "Get multi day chart summary figures - OK"() {

  }
}
