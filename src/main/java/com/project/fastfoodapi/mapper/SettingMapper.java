package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.config.settings.SettingProps;
import com.project.fastfoodapi.dto.front.SettingFrontDto;
import com.project.fastfoodapi.entity.Setting;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SettingMapper {
    @Mapping(source = "props.description", target = "description")
    @Mapping(source = "props.defaultValue", target = "defaultValue")
    @Mapping(source = "props.values", target = "values")
    @Mapping(source = "props.type", target = "type")
    @Mapping(source = "props.required", target = "required")
    SettingFrontDto toFrontDto(Setting setting);

    List<SettingFrontDto> toFrontDto(List<Setting> settings);
}
