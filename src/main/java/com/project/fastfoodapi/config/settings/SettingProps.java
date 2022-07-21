package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.SettingType;

import java.util.Set;

public interface SettingProps {
    Set<String> getDefaultValue();
    Set<String> getValues();
    SettingType getType();
    String getDescription();
    boolean isRequired();
    String getName();
}
