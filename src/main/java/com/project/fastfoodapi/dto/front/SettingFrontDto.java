package com.project.fastfoodapi.dto.front;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.fastfoodapi.config.settings.SettingProps;
import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class SettingFrontDto implements SettingProps{
    private  Set<String> currentValue;
    private  String name;
    private  Set<String> defaultValue;
    private  Set<String> values;
    private  SettingType type;
    private  String description;
    private  boolean required;
    public static SettingFrontDto parseProps(SettingProps props){
        return new SettingFrontDto(
                null, props.getName(), props.getDefaultValue(),
                props.getValues(), props.getType(), props.getDescription(), props.isRequired()
        );
    }
}
