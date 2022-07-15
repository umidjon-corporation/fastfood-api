package com.project.fastfoodapi.dto.front;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SettingFrontDto {
    private  Set<String> currentValue;
    private  String name;
    private  Set<String> defaultValue;
    private  Set<String> values;
    private  SettingType type;
    private  String description;
    private  boolean required;
}
