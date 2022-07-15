package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum HumanSetting implements SettingProps{
    LANGUAGE(
            false,
            Set.of(Language.UZBEK.name(), Language.RUSSIAN.name()),
            Set.of(Language.UZBEK.name()),
            SettingType.SELECT,
            "Language of system"
    )
    ;

    private final boolean required;
    private final Set<String> values;
    private final Set<String> defaultValue;
    private final SettingType type;
    private final String description;
}
