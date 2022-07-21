package com.project.fastfoodapi.entity.enums;

import com.project.fastfoodapi.config.settings.SettingProps;
import com.project.fastfoodapi.dto.ApiResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public enum SettingType {
    SELECT {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" field is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .data(props.getDefaultValue())
                        .build();
            }
            String valueBody = value.toArray(new String[]{})[0];
            Set<String> values = props.getValues();
            List<String> haveValueList = values.stream().filter(s -> s.equals(valueBody)).toList();
            if (haveValueList.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("In field \"" + name + "\" haven't got value \"" + valueBody + "\"")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated")
                    .build();
        }
    },
    GROUP_SELECT {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" field is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .data(props.getDefaultValue())
                        .build();
            }
            Set<String> values = props.getValues();
            value.removeIf(Objects::isNull);
            if (!value.containsAll(values)) {
                System.err.println("Not all values contain");
                for (String s : value.toArray(new String[]{})) {
                    if (!values.contains(s)) {
                        return ApiResponse.<Set<String>>builder()
                                .message("In field \"" + name + "\" haven't got value \"" + s + "\"")
                                .build();
                    }
                }
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    },
    BOOLEAN {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .data(props.getDefaultValue())
                        .success(true)
                        .message("Returned default value")
                        .build();
            }
            String valueBody = value.toArray(new String[]{})[0];
            try {
                boolean aBoolean = Boolean.parseBoolean(valueBody);
                valueBody = Boolean.toString(aBoolean);
            } catch (Exception e) {
                return ApiResponse.<Set<String>>builder()
                        .message(name + " field type is boolean")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated")
                    .build();
        }
    },
    TEXT {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" field is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("Returned default value")
                        .success(true)
                        .data(props.getDefaultValue())
                        .build();
            }
            String valueBody = value.toArray(new String[]{})[0];
            if (props.isRequired() && valueBody.isBlank()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" field is required")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    },
    GROUP_BOOLEAN {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("Returned default value")
                        .success(true)
                        .data(props.getDefaultValue())
                        .build();
            }
            for (String s : value.stream().toList()) {
                if (!s.matches("(.+)=(true|false)")) {
                    return ApiResponse.<Set<String>>builder()
                            .message("\"" + name + "\" field values must be like \"{fieldName}={true|false}\"")
                            .build();
                }
                String valueName = s.substring(0, s.lastIndexOf("="));
                if (props.getValues().contains(valueName)) {
                    return ApiResponse.<Set<String>>builder()
                            .message("In field \"" + name + "\" haven't got value \"" + s + "\"")
                            .build();
                }
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    },
    NUMBER {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty() && props.getDefaultValue().isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message('"' + name + "\" field is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .data(props.getDefaultValue())
                        .message("Value is empty")
                        .build();
            }
            String valueBody = value.toArray(new String[]{})[0];
            try {
                int i = Integer.parseInt(valueBody);
                valueBody = i + "";
            } catch (Exception e) {
                return ApiResponse.<Set<String>>builder()
                        .message("Type of \"" + name + "\" field must be number")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated")
                    .build();
        }
    },
    DATETIME {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .message("Returned default value")
                        .data(props.getDefaultValue())
                        .build();
            }
            String valueBody = value.stream().toList().get(0);
            try {
                LocalDateTime time = LocalDateTime.parse(valueBody);
                valueBody = time.toString();
            } catch (Exception e) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" must be like \"yyyy-MM-ddTHH:mm\"")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated!")
                    .build();
        }
    },
    DATE {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .message("Returned default value")
                        .data(props.getDefaultValue())
                        .build();
            }
            String valueBody = value.stream().toList().get(0);
            try {
                LocalDate date = LocalDate.parse(valueBody);
                valueBody = date.toString();
            } catch (Exception e) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" must be like \"yyyy-MM-dd\"")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated!")
                    .build();
        }
    },
    TIME {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .message("Returned default value")
                        .data(props.getDefaultValue())
                        .build();
            }
            String valueBody = value.stream().toList().get(0);
            try {
                LocalTime time = LocalTime.parse(valueBody);
                valueBody = time.toString();
            } catch (Exception e) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" must be like \"HH:mm\"")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated!")
                    .build();
        }
    },
    REFERENCE_TO_OBJECT {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" field is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .data(props.getDefaultValue())
                        .message("Returned default")
                        .build();
            }

            String valueBody = value.stream().toList().get(0);
            try {
                Long.parseLong(valueBody);
            } catch (Exception e) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" type must be number")
                        .build();
            }
            String entityName = props.getValues().stream().toList().get(0);
            List<String> entities = List.of("branch", "human", "category", "product", "attachment");
            if (!entities.contains(entityName)) {
                return ApiResponse.<Set<String>>builder()
                        .message("Entity with name \"" + entityName + "\" not found")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(Set.of(valueBody))
                    .message("Success validated")
                    .build();
        }
    },
    REFERENCE_TO_OBJECTS {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message("\"" + name + "\" field is required")
                        .build();
            }
            if (value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .success(true)
                        .data(props.getDefaultValue())
                        .message("Returned default")
                        .build();
            }
            for (String s : value.stream().toList()) {
                try {
                    Long.parseLong(s);
                } catch (Exception e) {
                    return ApiResponse.<Set<String>>builder()
                            .message("\"" + name + "\" type must be number")
                            .build();
                }
            }
            String entityName = props.getValues().stream().toList().get(0);
            List<String> entities = List.of("branch", "human", "category", "product", "attachment");
            if (!entities.contains(entityName)) {
                return ApiResponse.<Set<String>>builder()
                        .message("Entity with name \"" + entityName + "\" not found")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    };

    public abstract ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name);
}
