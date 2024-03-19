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
  private List<ReportRow> rows;

  @JsonIgnore private String sumJson;

  public Map<String, Double> getSum() {
    return Try.of(this::deserializeSum).getOrElse(Map.of());
  }

  public Map<String, Double> getRowsMap() {
    if (isNull(rows)) {
      return Map.of();
    }

    var map = new HashMap<String, Double>();
    var indicatorFormatter = "%s.%s";
    rows.forEach(
        row -> {
          var indicator = row.getIndicator();
          map.put(String.format(indicatorFormatter, indicator, "0"), row.getOldElectricValue());
          map.put(String.format(indicatorFormatter, indicator, "1"), row.getNewElectricValue1());
          map.put(String.format(indicatorFormatter, indicator, "2"), row.getNewElectricValue2());
          map.put(String.format(indicatorFormatter, indicator, "3"), row.getNewElectricValue3());
          map.put(String.format(indicatorFormatter, indicator, "4"), row.getNewElectricValue4());
        });
    return map;
  }

  private Map<String, Double> deserializeSum() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.readValue(sumJson, new TypeReference<>() {});
  }
}
