package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum ClientSettings implements SettingProps{
    CLIENT_ADDRESS(
            false,
            Set.of(),
            Set.of(),
            SettingType.TEXT,
            "Address of client"
    )
    ;

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
