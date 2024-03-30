package com.hbc.pms.core.api.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.integration.db.entity.ReportEntity
import com.hbc.pms.integration.db.entity.ReportTypeEntity
import com.hbc.pms.integration.db.repository.ReportRepository
import java.time.OffsetDateTime
import org.springframework.beans.factory.annotation.Autowired

class ReportFunctionalTestSpec extends FunctionalTestSpec {
  @Autowired
  private ReportRepository reportRepository;

  protected static Double SUM_TOTAL = SUM_PEAK + SUM_OFFPEAK + SUM_STANDARD
  protected static Double SUM_PEAK = 20
  protected static Double SUM_OFFPEAK = 60
  protected static Double SUM_STANDARD = 50
  protected static Double SUM_SPECIFIC = 25

  protected static Map<String, Double> DAM_SUM = [
          "SUM_TOTAL"     : SUM_TOTAL,
          "SUM_PEAK"      : SUM_PEAK,
          "SUM_OFFPEAK"   : SUM_OFFPEAK,
          "SUM_STANDARD"  : SUM_STANDARD,
          "SUM_SPECIFIC_1": SUM_SPECIFIC,
          "SUM_SPECIFIC_2": SUM_SPECIFIC,
          "SUM_SPECIFIC_3": SUM_SPECIFIC
  ]

  protected static Map<String, Double> BTP_SUM = [
          "SUM_TOTAL"      : SUM_TOTAL,
          "SUM_PEAK"       : SUM_PEAK,
          "SUM_OFFPEAK"    : SUM_OFFPEAK,
          "SUM_STANDARD"   : SUM_STANDARD,
          "SUM_SPECIFIC_1" : SUM_SPECIFIC,
          "SUM_SPECIFIC_2" : SUM_SPECIFIC,
          "SUM_SPECIFIC_3" : SUM_SPECIFIC,
          "SUM_SPECIFIC_4" : SUM_SPECIFIC,
          "SUM_SPECIFIC_5" : SUM_SPECIFIC,
          "SUM_SPECIFIC_6" : SUM_SPECIFIC,
          "SUM_SPECIFIC_7" : SUM_SPECIFIC,
          "SUM_SPECIFIC_8" : SUM_SPECIFIC,
          "SUM_SPECIFIC_9" : SUM_SPECIFIC,
          "SUM_SPECIFIC_11": SUM_SPECIFIC,
          "SUM_SPECIFIC_12": SUM_SPECIFIC,
          "SUM_SPECIFIC_13": SUM_SPECIFIC,
          "SUM_SPECIFIC_14": SUM_SPECIFIC,
          "SUM_SPECIFIC_15": SUM_SPECIFIC,
          "SUM_SPECIFIC_16": SUM_SPECIFIC,
  ]

  protected static List<Map<String, Double>> DAM_SUM_JSON =
          [
                  DAM_SUM, DAM_SUM
          ]

  protected static List<Map<String, Double>> BTP_SUM_JSON =
          [
                  BTP_SUM, BTP_SUM
          ]

  private static Map<Long, List<Map<String, Double>>> TYPE_SUM_JSON = [
          1: DAM_SUM_JSON,
          2: BTP_SUM_JSON
  ]


  def setupSpec() {
    populate6YearsReports()
  }

  def cleanupSpec() {
    reportRepository.deleteAll()
  }

  void populate6YearsReports() {
    def yearCount = 6
    def reports = []
    ObjectMapper mapper = new ObjectMapper()

    for (def reportCount = 1; reportCount <= yearCount * 365; reportCount++) {
      def recordingDate = OffsetDateTime.now().minusDays(reportCount)

      for (def type_id = 1; type_id <= 2; type_id++) {
        reports.add(ReportEntity.builder()
                .sumJson(mapper.writeValueAsString(TYPE_SUM_JSON.get(type_id)))
                .recordingDate(recordingDate)
                .type(ReportTypeEntity.builder().id(type_id).build())
                .build())
      }
    }

    reportRepository.saveAll(reports)
  }
}
