package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.AttachmentDto;
import com.project.fastfoodapi.dto.EmployeeDto;
import com.project.fastfoodapi.dto.HumanDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {AttachmentMapper.class})
public interface HumanMapper {
    @Named("password")
    default String password(String password){
        if(password==null){
            return null;
        }
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    Human humanDtoToHuman(HumanDto humanDto);
    @Mapping(source = "type", target = "userType")
    @Mapping(source = "password", target = "password", qualifiedByName = "password")
    Human humanDtoToHuman(EmployeeDto humanDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHumanFromHumanDto(HumanDto humanDto, @MappingTarget Human human);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHumanFromHumanDto(EmployeeDto humanDto, @MappingTarget Human human);

    @Mapping(source = "userType", target = "type")
    HumanFrontDto humanToHumanFrontDto(Human human);
    List<HumanFrontDto> humanToHumanFrontDto(List<Human> humans);

    @AfterMapping
    default void url(@MappingTarget HumanFrontDto humanFrontDto, Human human){
        if(humanFrontDto.getPhoto()==null || human.getId()==null || human.getUserType()==null){
            humanFrontDto.setPhoto(AttachmentDto.builder()
                    .name("image-not-found")
                    .size(6306L)
                    .type("image/png")
                    .url("/api/assets/image-not-found.png")
                    .build());
            return;
        }
        humanFrontDto.getPhoto().setUrl("/api/assets/human/"+human.getId()+"/photo");
    }
}
