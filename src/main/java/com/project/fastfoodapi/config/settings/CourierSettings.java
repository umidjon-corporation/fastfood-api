package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum CourierSettings implements SettingProps{
    COURIER_BRANCH_ID(
            true,
            Set.of("branch"),
            Set.of(""),
            SettingType.REFERENCE_TO_OBJECT,
            "Courier working branch name"
    );
    private final boolean required;
    private final Set<String> values;
    private final Set<String> defaultValue;
    private final SettingType type;
    private final String description;
    @Override
    public String getName() {
        return this.name();
    }
}
