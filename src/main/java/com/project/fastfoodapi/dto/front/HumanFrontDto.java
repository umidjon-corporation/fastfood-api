package com.project.fastfoodapi.dto.front;

import com.project.fastfoodapi.dto.AttachmentDto;
import com.project.fastfoodapi.dto.SettingsDto;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.Region;
import com.project.fastfoodapi.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class HumanFrontDto {
    private Long id;
    private String name;
    private String number;
    private HumanStatus status;
    private UserType type;
    private AttachmentDto photo;
    private List<SettingFrontDto> settings;
}
