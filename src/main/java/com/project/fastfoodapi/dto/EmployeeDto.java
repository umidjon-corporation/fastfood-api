package com.project.fastfoodapi.dto;

import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.Region;
import com.project.fastfoodapi.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
public class EmployeeDto{
    private String name, password;
    private String number;
    private HumanStatus status;
    private MultipartFile photo;
    private UserType type;
    private List<SettingsDto> settings;
}
