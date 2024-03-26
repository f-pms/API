package com.hbc.pms.core.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Report {
  private Long id;
  private String sumJson;
  private OffsetDateTime recordingDate;
  private ReportType type;
  private List<ReportRow> rows;

  public List<Map<String, Double>> getSums() {
    try {
      var mapper = new ObjectMapper();
      return mapper.readValue(sumJson, new TypeReference<>() {});
    } catch (Exception ex) {
      return List.of();
    }
  }
}
