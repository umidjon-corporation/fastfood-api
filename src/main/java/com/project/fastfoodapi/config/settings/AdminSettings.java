package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum AdminSettings implements SettingProps{
    AFTER_LOGIN_URL(
            false,
            Set.of("orders", "products"),
            Set.of("orders"),
            SettingType.SELECT,
            "Url of redirect after success authentication"
    ),
    AUTO_UPDATE(
            false,
            Set.of(),
            Set.of("true"),
            SettingType.BOOLEAN,
            "Auto update data by websocket"
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
