package com.hbc.pms.core.api.controller.v1.response;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.enums.ReportRowShift;
import io.vavr.control.Try;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ReportResponse {
  private Long id;
  private OffsetDateTime recordingDate;
  private ReportType type;

  @JsonIgnore private List<ReportRow> rows;

  @JsonIgnore private String sumJson;

  public Map<String, Double> getSum() {
    return Try.of(this::deserializeSum).getOrElse(Map.of());
  }

  public List<Map<String, Double>> getRowsMaps() {
    if (isNull(rows)) {
      return null;
    }

    var shift1Map = new HashMap<String, Double>();
    var shift2Map = new HashMap<String, Double>();
    var indicatorPattern = "%s_%s";
    rows.forEach(
        row -> {
          var indicator = row.getIndicator();
          var shift = row.getShift();
          var shiftMap = shift.equals(ReportRowShift.I) ? shift1Map : shift2Map;
          shiftMap.put(
              String.format(indicatorPattern, indicator, "0", shift), row.getOldElectricValue());
          shiftMap.put(
              String.format(indicatorPattern, indicator, "1", shift), row.getNewElectricValue1());
          shiftMap.put(
              String.format(indicatorPattern, indicator, "3", shift), row.getNewElectricValue2());
          shiftMap.put(
              String.format(indicatorPattern, indicator, "5", shift), row.getNewElectricValue3());
          shiftMap.put(
              String.format(indicatorPattern, indicator, "7", shift), row.getNewElectricValue4());
        });
    return List.of(shift1Map, shift2Map);
  }

  private Map<String, Double> deserializeSum() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.readValue(sumJson, new TypeReference<>() {});
  }
}
