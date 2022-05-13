package com.project.fastfoodapi.dto;

import com.project.fastfoodapi.entity.enums.ClientStatus;
import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class HumanDto implements Serializable {
    private final String name;
    private final String number;
    private final ClientStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate birthdate;
    private final Region region;
    private final Language lang;
    private final MultipartFile photo;
}
