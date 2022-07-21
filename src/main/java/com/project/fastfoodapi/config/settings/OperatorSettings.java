package com.project.fastfoodapi.config.settings;

import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum OperatorSettings implements SettingProps {
    OPERATOR_BRANCH_ID(
            true,
            Set.of("branch"),
            Set.of(),
            SettingType.REFERENCE_TO_OBJECT,
            "Operator branch id"
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
