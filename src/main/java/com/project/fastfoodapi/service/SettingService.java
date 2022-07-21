package com.project.fastfoodapi.service;

import com.project.fastfoodapi.config.settings.*;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.SettingDto;
import com.project.fastfoodapi.dto.SettingsDto;
import com.project.fastfoodapi.dto.front.SettingFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.Setting;
import com.project.fastfoodapi.entity.enums.SettingType;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.SettingMapper;
import com.project.fastfoodapi.repository.*;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    private final HumanRepository humanRepository;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AttachmentRepository attachmentRepository;

    public List<Setting> initHumanSettings(UserType userType) {
        List<Setting> settings = new ArrayList<>();
        for (SettingProps props : getSettingsProps(userType)) {
            settings.add(Setting.builder()
                            .name(props.getName())
                            .currentValue(props.getDefaultValue())
                    .build());
        }
        return settings;
    }

    public List<SettingFrontDto> getSettingsProps(UserType userType) {
        List<SettingFrontDto> settingProps = new ArrayList<>(Arrays.stream(HumanSetting.values()).map(SettingFrontDto::parseProps).toList());
        switch (userType) {
            case SUPER_ADMIN, ADMIN -> settingProps.addAll(Arrays.stream(AdminSettings.values()).map(SettingFrontDto::parseProps).toList());
            case CLIENT -> settingProps.addAll(Arrays.stream(ClientSettings.values()).map(SettingFrontDto::parseProps).toList());
            case COURIER -> settingProps.addAll(Arrays.stream(CourierSettings.values()).map(SettingFrontDto::parseProps).toList());
            case OPERATOR -> settingProps.addAll(Arrays.stream(OperatorSettings.values()).map(SettingFrontDto::parseProps).toList());
        }
        return settingProps;
    }

    public ApiResponse<SettingFrontDto> editSetting(@NonNull String name, @NonNull SettingDto dto, @NonNull Human human, boolean reset) {
        Setting newSetting = null;
        for (int i = 0; i < human.getSettings().size(); i++) {
            Setting setting=human.getSettings().get(i);
            if (setting.getName().equalsIgnoreCase(name)) {
                SettingProps props = setting.getProps();
                if (reset) {
                    if (props.isRequired() && props.getDefaultValue().isEmpty()) {
                        return ApiResponse.<SettingFrontDto>builder()
                                .message("You can't reset this setting, because this is required field")
                                .build();
                    }
                    setting.setCurrentValue(props.getDefaultValue());
                } else {
                    ApiResponse<Set<String>> validate = props.getType().validate(props, new HashSet<>(dto.getValue()), name);
                    if(validate.isFailed()){
                        return ApiResponse.<SettingFrontDto>builder()
                                .message(validate.getMessage())
                                .build();
                    }
                    if(setting.getProps().getType()==SettingType.REFERENCE_TO_OBJECT){
                        String valueBody = dto.getValue().stream().toList().get(0);
                        String entityName = props.getValues().stream().toList().get(0);
                        JpaRepository<?, Long> repository = getRepository(entityName);
                        if (!repository.existsById(Long.parseLong(valueBody))) {
                            return ApiResponse.<SettingFrontDto>builder()
                                    .message("Object \""+entityName+"\" with id=(" +valueBody+ ") not found")
                                    .build();
                        }
                    }else if(props.getType()==SettingType.REFERENCE_TO_OBJECTS){
                        List<Long> ids = dto.getValue().stream().map(Long::parseLong).toList();
                        String entityName = props.getValues().stream().toList().get(0);
                        JpaRepository<?, Long> repository = getRepository(entityName);
                        for (Long id : ids) {
                            if (!repository.existsById(id)) {
                                return ApiResponse.<SettingFrontDto>builder()
                                        .message("Object \""+entityName+"\" with id=(" +id+ ") not found")
                                        .build();
                            }
                        }
                    }
                    setting.setCurrentValue(validate.getData());
                }
                newSetting = setting;
                settingRepository.save(setting);
                break;
            }
        }
        return ApiResponse.<SettingFrontDto>builder()
                .message("Success edited")
                .success(true)
                .data(settingMapper.toFrontDto(newSetting))
                .build();
    }

    public ApiResponse<List<SettingFrontDto>> editSettings(@NonNull List<SettingsDto> dto, @NonNull Human human) {
        List<Setting> settings = human.getSettings();
        for (Setting setting : settings) {
            List<SettingsDto> settingsDtos = dto.stream().filter(settingsDto -> settingsDto.getName().equalsIgnoreCase(setting.getName())).toList();
            if (settingsDtos.isEmpty()) {
                continue;
            }
            SettingsDto settingsDto = settingsDtos.get(0);
            ApiResponse<SettingFrontDto> changeSetting = editSetting(setting.getName(), new SettingDto(settingsDto.getValue()), human, false);
            if(changeSetting.isFailed()){
                return ApiResponse.<List<SettingFrontDto>>builder()
                        .message(changeSetting.getMessage())
                        .build();
            }
        }
        return ApiResponse.<List<SettingFrontDto>>builder()
                .message("Success!")
                .success(true)
                .data(settingMapper.toFrontDto(settings))
                .build();
    }

    public JpaRepository<?, Long> getRepository(String entityName){
        switch (entityName){
            case "branch"->{return branchRepository;}
            case "category"->{return categoryRepository;}
            case "product"->{return productRepository;}
            case "human"->{return humanRepository;}
            case "attachment"->{return attachmentRepository;}
        }
        return null;
    }
}
