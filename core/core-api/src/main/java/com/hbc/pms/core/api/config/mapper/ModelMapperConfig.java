package com.hbc.pms.core.api.config.mapper;

import static com.hbc.pms.core.api.constant.ErrorMessageConstant.INVALID_ADDRESS;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.SensorConfigurationRequest;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationCommand;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.api.util.StringUtil;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.core.model.User;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.ValidationException;
import org.modelmapper.convention.MatchingStrategies;
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
                    return StringUtil.buildCronFromSeconds(seconds);
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
                    return StringUtil.buildCronFromSeconds(seconds);
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
                    if (StringUtil.isIncorrectPLCAddressFormat(address)) {
                      throw new CoreApiException(
                          ErrorType.BAD_REQUEST_ERROR, String.format(INVALID_ADDRESS, address));
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
                    if (StringUtil.isIncorrectPLCAddressFormat(address)) {
                      throw new CoreApiException(
                          ErrorType.BAD_REQUEST_ERROR, String.format(INVALID_ADDRESS, address));
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
