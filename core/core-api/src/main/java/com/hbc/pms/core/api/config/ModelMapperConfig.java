package com.hbc.pms.core.api.config;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationRequest;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.api.utils.StringUtils;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.core.model.SensorConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
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
        .setMatchingStrategy(MatchingStrategies.STRICT)
        .setSkipNullEnabled(true)
        .setDeepCopyEnabled(true);

    addCreateActionCommandToActionTypeMap();
    addCreateAlarmConditionCommandToAlarmConditionTypeMap();
    addUpdateAlarmConditionCommandToAlarmConditionTypeMap();

    addUpdateSensorConfigurationRequestToSensorConfigurationTypeMap();
    addSensorConfigurationToSensorConfigurationResponseTypeMap();
    addBlueprintToBlueprintResponseTypeMap();
    return modelMapper;
  }

  private void addCreateActionCommandToActionTypeMap() {
    TypeMap<CreateAlarmConditionCommand.AlarmActionCommand, AlarmAction> propertyMapper =
        modelMapper.createTypeMap(
            CreateAlarmConditionCommand.AlarmActionCommand.class, AlarmAction.class);
    propertyMapper.addMappings(
        mapping ->
            mapping.map(
                CreateAlarmConditionCommand.AlarmActionCommand::getRecipientIds,
                AlarmAction::setRecipients));
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
        .createTypeMap(UpdateSensorConfigurationRequest.class, SensorConfiguration.class)
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
                      Integer.parseInt(parts[1]),
                      parts[2].toUpperCase()
                    };
                  };

              @Override
              protected void configure() {
                using(toAddressParts).map(source.getAddress()).setFields(new Object[] {});
              }
            });
  }

  private void addBlueprintToBlueprintResponseTypeMap() {
    modelMapper
        .createTypeMap(Blueprint.class, BlueprintResponse.class)
        .addMappings(
            new PropertyMap<>() {
              private final Converter<
                      List<SensorConfiguration>,
                      List<BlueprintResponse.SensorConfigurationResponse>>
                  toSensorConfigurationRes =
                      c ->
                          c.getSource().stream()
                              .dropWhile(
                                  sc -> StringUtils.isIncorrectPLCAddressFormat(sc.getAddress()))
                              .map(
                                  e ->
                                      modelMapper.map(
                                          e, BlueprintResponse.SensorConfigurationResponse.class))
                              .toList();

              @Override
              protected void configure() {
                using(toSensorConfigurationRes)
                    .map(source.getSensorConfigurations())
                    .setSensorConfigurations(new ArrayList<>());
              }
            });
  }
}
