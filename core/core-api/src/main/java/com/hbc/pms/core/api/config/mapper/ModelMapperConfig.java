package com.hbc.pms.core.api.config.mapper;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.SensorConfigurationRequest;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationCommand;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.api.utils.StringUtils;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.core.model.User;
import java.util.Collections;
import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.ValidationException;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  private final ModelMapper modelMapper;

  public ModelMapperConfig() {
    this.modelMapper = new ModelMapper();
  }

  @Bean
  public ModelMapper modelMapper() throws ValidationException {
    modelMapper
        .getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
        .setMatchingStrategy(MatchingStrategies.STRICT)
        .setSkipNullEnabled(true);

    // prevent ModelMapper do map the lazy loaded collection
    modelMapper
        .getConfiguration()
        .setPropertyCondition(
            context ->
                !(context.getSource() instanceof PersistentCollection)
                    || ((PersistentCollection<?>) context.getSource()).wasInitialized());

    addCreateAlarmConditionCommandToAlarmConditionTypeMap();
    addUpdateAlarmConditionCommandToAlarmConditionTypeMap();
    addSensorConfigurationConfigToSensorConfigurationTypeMap();

    addUpdateSensorConfigurationRequestToSensorConfigurationTypeMap();
    addSensorConfigurationToSensorConfigurationResponseTypeMap();

    modelMapper
        .typeMap(User.class, User.class)
        .setPropertyCondition(MapperConditions.mapIfSourceNotNull());

    return modelMapper;
  }

  private void addCreateAlarmConditionCommandToAlarmConditionTypeMap() {
    modelMapper
        .createTypeMap(CreateAlarmConditionCommand.class, AlarmCondition.class)
        .addMappings(
            new PropertyMap<>() {
              private final Converter<Integer, String> fromAddress =
                  c -> {
                    int seconds = c.getSource();
                    return StringUtils.buildCronFromSeconds(seconds);
                  };

              @Override
              protected void configure() {
                using(fromAddress).map(source.getCheckInterval()).setCron("");
              }
            });
  }

  private void addUpdateAlarmConditionCommandToAlarmConditionTypeMap() {
    modelMapper
        .createTypeMap(UpdateAlarmConditionCommand.class, AlarmCondition.class)
        .addMappings(
            new PropertyMap<>() {
              private final Converter<Integer, String> fromAddress =
                  c -> {
                    int seconds = c.getSource();
                    return StringUtils.buildCronFromSeconds(seconds);
                  };

              @Override
              protected void configure() {
                using(fromAddress).map(source.getCheckInterval()).setCron("");
              }
            });
  }

  private void addUpdateSensorConfigurationRequestToSensorConfigurationTypeMap() {
    modelMapper
        .createTypeMap(UpdateSensorConfigurationCommand.class, SensorConfiguration.class)
        .addMappings(
            new PropertyMap<>() {
              private final Converter<String, String> fromAddress =
                  c -> {
                    String address = c.getSource().toUpperCase();
                    if (StringUtils.isIncorrectPLCAddressFormat(address)) {
                      throw new ValidationException(
                          Collections.singletonList(
                              new ErrorMessage("Invalid PLC " + "Address: " + address)));
                    }

                    return address;
                  };

              @Override
              protected void configure() {
                using(fromAddress).map(source.getAddress()).setAddress("");
              }
            });
  }

  private void addSensorConfigurationConfigToSensorConfigurationTypeMap() {
    modelMapper
        .createTypeMap(SensorConfigurationRequest.class, SensorConfiguration.class)
        .addMappings(
            new PropertyMap<>() {
              private final Converter<String, String> fromAddress =
                  c -> {
                    String address = c.getSource().toUpperCase();
                    if (StringUtils.isIncorrectPLCAddressFormat(address)) {
                      throw new ValidationException(
                          Collections.singletonList(
                              new ErrorMessage("Invalid PLC " + "Address: " + address)));
                    }

                    return address;
                  };

              @Override
              protected void configure() {
                using(fromAddress).map(source.getAddress()).setAddress("");
              }
            });
  }

  private void addSensorConfigurationToSensorConfigurationResponseTypeMap() {
    modelMapper
        .createTypeMap(
            SensorConfiguration.class, BlueprintResponse.SensorConfigurationResponse.class)
        .addMappings(
            new PropertyMap<>() {
              private final Converter<String, Object[]> toAddressParts =
                  c -> {
                    var parts = c.getSource().split(":");
                    return new Object[] {
                      Integer.parseInt(parts[0].substring(3)),
                      Double.parseDouble(parts[1]),
                      parts[2].toUpperCase()
                    };
                  };

              @Override
              protected void configure() {
                using(toAddressParts).map(source.getAddress()).setFields(new Object[] {});
              }
            });
  }
}
