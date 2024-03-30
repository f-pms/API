package com.hbc.pms.core.api.config.report;

import jakarta.validation.constraints.NotNull;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "hbc.report")
@Getter
@Setter
@Validated
public class ReportConfiguration implements Validator {
  @NotNull private String dir;

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return ReportConfiguration.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    ReportConfiguration config = (ReportConfiguration) target;
    if (!Files.exists(Paths.get(config.getDir()))) {
      errors.reject("dir", "Report directory doesn't exist");
    }
  }
}
