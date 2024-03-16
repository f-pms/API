package com.hbc.pms.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportType;
import io.vavr.control.Try;
import java.time.OffsetDateTime;
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

  public Map<String, Long> getSum() {
    return Try.of(this::deserializeSum).getOrElse(Map.of());
  }

  private Map<String, Long> deserializeSum() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.readValue(sumJson, new TypeReference<>() {});
  }
}
