package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.Region;
import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum HumanSetting implements SettingProps{
    LANGUAGE(
            true,
            Set.of(Language.UZBEK.name(), Language.RUSSIAN.name()),
            Set.of(Language.UZBEK.name()),
            SettingType.SELECT,
            "Language of system"
    ),
    REGION(
            false,
            Arrays.stream(Region.values()).map(Region::name).collect(Collectors.toSet()),
            Set.of(Region.TASHKENT.name()),
            SettingType.SELECT,
            "Region of human"
    ),
    BIRTHDAY(
            false,
            Set.of(),
            Set.of(),
            SettingType.DATE,
            "Birthday of human"
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
