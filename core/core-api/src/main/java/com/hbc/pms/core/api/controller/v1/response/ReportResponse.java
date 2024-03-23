package com.hbc.pms.core.api.controller.v1.response;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportType;
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
    var indicatorFormatter = "%s_%s";
    rows.forEach(
        row -> {
          var indicator = row.getIndicator();
          var shift = row.getShift();
          if (shift == 1) {
            shift1Map.put(
                String.format(indicatorFormatter, indicator, "0", shift),
                row.getOldElectricValue());
            shift1Map.put(
                String.format(indicatorFormatter, indicator, "1", shift),
                row.getNewElectricValue1());
            shift1Map.put(
                String.format(indicatorFormatter, indicator, "3", shift),
                row.getNewElectricValue2());
            shift1Map.put(
                String.format(indicatorFormatter, indicator, "5", shift),
                row.getNewElectricValue3());
            shift1Map.put(
                String.format(indicatorFormatter, indicator, "7", shift),
                row.getNewElectricValue4());
          } else if (shift == 2) {
            shift2Map.put(
                String.format(indicatorFormatter, indicator, "0", shift),
                row.getOldElectricValue());
            shift2Map.put(
                String.format(indicatorFormatter, indicator, "1", shift),
                row.getNewElectricValue1());
            shift2Map.put(
                String.format(indicatorFormatter, indicator, "3", shift),
                row.getNewElectricValue2());
            shift2Map.put(
                String.format(indicatorFormatter, indicator, "5", shift),
                row.getNewElectricValue3());
            shift2Map.put(
                String.format(indicatorFormatter, indicator, "7", shift),
                row.getNewElectricValue4());
          }
        });
    return List.of(shift1Map, shift2Map);
  }

  private Map<String, Double> deserializeSum() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.readValue(sumJson, new TypeReference<>() {});
  }
}
