package com.hbc.pms.core.api.config;

import com.hbc.pms.core.api.Utils;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.model.SensorConfiguration;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.ValidationException;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(SensorConfiguration.class, BlueprintResponse.SensorConfigurationResponse.class)
            .addMappings(
                new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        var addressParts = Utils.splitAddress(super.source.getAddress());
                        map().setDb(1);
                        map().setOffset(1);
                        map().setDataType("addressParts[2].toUpperCase()");
                    }
                }
            );
        modelMapper
            .getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true)
            .setDeepCopyEnabled(true);

        return modelMapper;
    }
}
