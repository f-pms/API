package com.hbc.pms.core.api.support.data

import com.hbc.pms.core.api.constant.ChartConstant
import com.hbc.pms.core.model.Report
import com.hbc.pms.core.model.ReportRow
import com.hbc.pms.core.model.ReportType
import com.hbc.pms.core.model.enums.ReportRowShift
import java.time.OffsetDateTime
import java.time.ZoneOffset
import spock.lang.Specification

class ReportExcelProcessorTest extends Specification {
  ReportExcelProcessor processor = Spy(ReportExcelProcessor) {
    save(_, _, _) >> {}
  }

  def assertBaseValue1() {
    given: "Assert correct #typeName's sums at day #day"
    var type = ReportType.builder().id(1).name(typeName).build()
    var report = Report.builder().id(1).recordingDate(
            OffsetDateTime.of(2024, 2, day, 6, 0, 0, 0, ZoneOffset.ofHours(7)))
            .build()
    var rows = List.of(
            ReportRow.builder()
                    .id(1)
                    .indicator(typeName + "_1")
                    .shift(ReportRowShift.I)
                    .oldElectricValue(142.9509)
                    .newElectricValue1(142.9655)
                    .newElectricValue2(142.9736)
                    .newElectricValue3(142.9948)
                    .newElectricValue4(142.9987)
                    .build(),
            ReportRow.builder()
                    .id(2)
                    .indicator(typeName + "_1")
                    .shift(ReportRowShift.II)
                    .oldElectricValue(142.9987)
                    .newElectricValue1(143.0066)
                    .newElectricValue2(143.0148)
                    .newElectricValue3(143.04)
                    .newElectricValue4(143.0485)
                    .build()
    )

    when:
    var sums = processor.process(type, report, rows)

    then:
    sums.get(0).get(ChartConstant.SUM_PEAK) == sumPeak0
    sums.get(0).get(ChartConstant.SUM_OFFPEAK) == sumOffPeak0
    sums.get(0).get(ChartConstant.SUM_STANDARD) == sumStandard0
    sums.get(0).get(ChartConstant.SUM_TOTAL) == sumTotal0
    sums.get(1).get(ChartConstant.SUM_PEAK) == sumPeak1
    sums.get(1).get(ChartConstant.SUM_OFFPEAK) == sumOffPeak1
    sums.get(1).get(ChartConstant.SUM_STANDARD) == sumStandard1
    sums.get(1).get(ChartConstant.SUM_TOTAL) == sumTotal1

    where:
    typeName | day | sumPeak0 | sumOffPeak0 | sumStandard0 | sumTotal0 | sumPeak1 | sumOffPeak1 | sumStandard1 | sumTotal1
    "DAM"    | 23  | 12_000.0 | 0.0         | 35_800.0     | 47_800.0  | 7_900.0  | 25_200.0    | 16_700.0     | 49_800.0
    "BTP"    | 23  | 12_000.0 | 0.0         | 35_800.0     | 47_800.0  | 7_900.0  | 25_200.0    | 16_700.0     | 49_800.0
    "DAM"    | 24  | 12_000.0 | 0.0         | 35_800.0     | 47_800.0  | 7_900.0  | 25_200.0    | 16_700.0     | 49_800.0
    "BTP"    | 24  | 12_000.0 | 0.0         | 35_800.0     | 47_800.0  | 7_900.0  | 25_200.0    | 16_700.0     | 49_800.0
    "DAM"    | 25  | 0.0      | 0.0         | 47_800.0     | 47_800.0  | 0.0      | 25_200.0    | 24_600.0     | 49_800.0
    "BTP"    | 25  | 0.0      | 0.0         | 47_800.0     | 47_800.0  | 0.0      | 25_200.0    | 24_600.0     | 49_800.0
  }

  def assertBaseValue2() {
    given: "Assert correct #typeName's sums at day #day"
    var type = ReportType.builder().id(1).name(typeName).build()
    var report = Report.builder().id(1).recordingDate(
            OffsetDateTime.of(2024, 2, day, 6, 0, 0, 0, ZoneOffset.ofHours(7)))
            .build()
    var rows = List.of(
            ReportRow.builder()
                    .id(1)
                    .indicator(typeName + "_1")
                    .shift(ReportRowShift.I)
                    .oldElectricValue(8.211168)
                    .newElectricValue1(8.211736)
                    .newElectricValue2(8.212066)
                    .newElectricValue3(8.213164)
                    .newElectricValue4(8.213354)
                    .build(),
            ReportRow.builder()
                    .id(2)
                    .indicator(typeName + "_1")
                    .shift(ReportRowShift.II)
                    .oldElectricValue(8.213354)
                    .newElectricValue1(8.213599)
                    .newElectricValue2(8.213982)
                    .newElectricValue3(8.215005)
                    .newElectricValue4(8.215271)
                    .build()
    )

    when:
    var sums = processor.process(type, report, rows)

    then:
    sums.get(0).get(ChartConstant.SUM_PEAK) == sumPeak0
    sums.get(0).get(ChartConstant.SUM_OFFPEAK) == sumOffPeak0
    sums.get(0).get(ChartConstant.SUM_STANDARD) == sumStandard0
    sums.get(0).get(ChartConstant.SUM_TOTAL) == sumTotal0
    sums.get(1).get(ChartConstant.SUM_PEAK) == sumPeak1
    sums.get(1).get(ChartConstant.SUM_OFFPEAK) == sumOffPeak1
    sums.get(1).get(ChartConstant.SUM_STANDARD) == sumStandard1
    sums.get(1).get(ChartConstant.SUM_TOTAL) == sumTotal1

    where:
    typeName | day | sumPeak0 | sumOffPeak0 | sumStandard0 | sumTotal0 | sumPeak1 | sumOffPeak1 | sumStandard1 | sumTotal1
    "DAM"    | 23  | 520.0    | 0.0         | 1_666.0      | 2_186.0   | 245.0    | 1_023.0     | 649.0        | 1_917.0
    "BTP"    | 23  | 520.0    | 0.0         | 1_666.0      | 2_186.0   | 245.0    | 1_023.0     | 649.0        | 1_917.0
    "DAM"    | 24  | 520.0    | 0.0         | 1_666.0      | 2_186.0   | 245.0    | 1_023.0     | 649.0        | 1_917.0
    "BTP"    | 24  | 520.0    | 0.0         | 1_666.0      | 2_186.0   | 245.0    | 1_023.0     | 649.0        | 1_917.0
    "DAM"    | 25  | 0.0      | 0.0         | 2_186.0      | 2_186.0   | 0.0      | 1_023.0     | 894.0        | 1_917.0
    "BTP"    | 25  | 0.0      | 0.0         | 2_186.0      | 2_186.0   | 0.0      | 1_023.0     | 894.0        | 1_917.0
  }
}
