package com.project.fastfoodapi.entity.enums;

import com.project.fastfoodapi.config.settings.SettingProps;
import com.project.fastfoodapi.dto.ApiResponse;

import java.util.List;
import java.util.Set;

public enum SettingType {
    SELECT{
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            if (props.isRequired() && value.isEmpty()) {
                return ApiResponse.<Set<String>>builder()
                        .message(name+" is required")
                        .build();
            }
            if(value.isEmpty()){
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
                        .message("In field \""+name+"\" haven't got value \""+valueBody+"\"")
                        .build();
            }
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    },
    GROUP_SELECT{
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
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
            if(value.isEmpty()){
                return ApiResponse.<Set<String>>builder()
                        .data(props.getDefaultValue())
                        .success(true)
                        .build();
            }
            String valueBody = value.toArray(new String[]{})[0];
            try {
                boolean aBoolean = Boolean.parseBoolean(valueBody);
                valueBody= Boolean.toString(aBoolean);
            }catch (Exception e){
                return ApiResponse.<Set<String>>builder()
                        .message(name+" field type is boolean")
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
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    },
    REFERENCE_TO_OBJECT {
        @Override
        public ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name) {
            return ApiResponse.<Set<String>>builder()
                    .success(true)
                    .data(value)
                    .message("Success validated")
                    .build();
        }
    };

    public abstract ApiResponse<Set<String>> validate(SettingProps props, Set<String> value, String name);
}
