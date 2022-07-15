package com.project.fastfoodapi.service;

import com.project.fastfoodapi.config.settings.*;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.SettingDto;
import com.project.fastfoodapi.dto.SettingsDto;
import com.project.fastfoodapi.dto.front.SettingFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.Setting;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.SettingMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository settingRepository;
    private final HumanRepository humanRepository;
    private final SettingMapper settingMapper;

    public List<Setting> initHumanSettings(UserType userType) {
        List<Setting> settings = new ArrayList<>();
        for (HumanSetting value : HumanSetting.values()) {
            settings.add(Setting.builder()
                    .name(value.name())
                    .currentValue(value.getDefaultValue())
                    .build());
        }
        if (userType == UserType.SUPER_ADMIN || userType == UserType.ADMIN) {
            for (AdminSettings value : AdminSettings.values()) {
                settings.add(Setting.builder()
                        .currentValue(value.getDefaultValue())
                        .name(value.name())
                        .build());
            }
        }
        return settings;
    }

    public List<SettingProps> getSettingsProps(UserType userType) {
        List<SettingProps> settingProps = new ArrayList<>(Arrays.stream(HumanSetting.values()).toList());
        switch (userType) {
            case SUPER_ADMIN, ADMIN -> settingProps.addAll(Arrays.stream(AdminSettings.values()).toList());
            case CLIENT -> settingProps.addAll(Arrays.stream(ClientSettings.values()).toList());
            case COURIER -> settingProps.addAll(Arrays.stream(CourierSettings.values()).toList());
            case OPERATOR -> settingProps.addAll(Arrays.stream(OperatorSettings.values()).toList());
        }
        return settingProps;
    }

    public ApiResponse<SettingFrontDto> changeSetting(String name, SettingDto dto, Human human, boolean reset) {
        Setting newSetting = null;
        for (Setting setting : human.getSettings()) {
            if (setting.getName().equalsIgnoreCase(name)) {
                if (reset) {
                    if (setting.getProps().isRequired()) {
                        return ApiResponse.<SettingFrontDto>builder()
                                .message("You can't reset this setting, because this is required field")
                                .build();
                    }
                    setting.setCurrentValue(setting.getProps().getDefaultValue());
                } else {
                    ApiResponse<Set<String>> validate = setting.getProps().getType().validate(setting.getProps(), dto.getValue(), name);
                    if(validate.isFailed()){
                        return ApiResponse.<SettingFrontDto>builder()
                                .message(validate.getMessage())
                                .build();
                    }
                    setting.setCurrentValue(validate.getData());
                }
                newSetting = setting;
            }
        }
        humanRepository.save(human);
        return ApiResponse.<SettingFrontDto>builder()
                .message("Success edited")
                .success(true)
                .data(settingMapper.toFrontDto(newSetting))
                .build();
    }

    public ApiResponse<List<SettingFrontDto>> editSettings(List<SettingsDto> dto, Human human) {
        dto.removeIf(Objects::isNull);
        List<Setting> settings = human.getSettings();
        for (Setting setting : settings) {
            List<SettingsDto> settingsDtos = dto.stream().filter(settingsDto -> settingsDto.getName().equalsIgnoreCase(setting.getName())).toList();
            if (settingsDtos.isEmpty()) {
                continue;
            }
            ApiResponse<Set<String>> validateValue = setting.getProps().getType().validate(setting.getProps(), settingsDtos.get(0).getValue(), setting.getName());
            if(validateValue.isFailed()){
                return ApiResponse.<List<SettingFrontDto>>builder()
                        .message(validateValue.getMessage())
                        .build();
            }
            setting.setCurrentValue(validateValue.getData());
        }
        settingRepository.saveAll(settings);
        return ApiResponse.<List<SettingFrontDto>>builder()
                .message("Success!")
                .success(true)
                .data(settingMapper.toFrontDto(settings))
                .build();
    }
}
