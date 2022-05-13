package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.FilialDto;
import com.project.fastfoodapi.entity.Filial;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FilialMapper {
    Filial filialDtoToFilial(FilialDto filialDto);

    FilialDto filialToFilialDto(Filial filial);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFilialFromFilialDto(FilialDto filialDto, @MappingTarget Filial filial);
}
