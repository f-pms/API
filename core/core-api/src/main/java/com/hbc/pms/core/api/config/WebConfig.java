package com.hbc.pms.core.api.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${hbc.origins}")
  private String allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(Arrays.stream(allowedOrigins.split(",")).toArray(String[]::new))
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowCredentials(true);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    var resolver =
        new PathResourceResolver() {
          @Override
          protected Resource getResource(String path, Resource location)
              throws IOException {
            Resource requestedResource = location.createRelative(path);
            return requestedResource.exists() && requestedResource.isReadable()
                ? requestedResource
                : new ClassPathResource("/ui/index.html");
          }
        };
    registry
        .addResourceHandler("/**/*")
        .addResourceLocations("classpath:/ui/")
        .resourceChain(true)
        .addResolver(resolver);
  }

  @Bean
  ErrorViewResolver notFoundFallback() {
    return (request, status, model) ->
        status == HttpStatus.NOT_FOUND
            ? new ModelAndView("index.html", Collections.emptyMap(), HttpStatus.OK)
            : null;
  }
}
