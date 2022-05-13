package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.front.DeliveryFrontDto;
import com.project.fastfoodapi.entity.Delivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, HumanMapper.class})
public interface DeliveryMapper {
    DeliveryFrontDto toDto(Delivery delivery);
}
