package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.BranchDto;
import com.project.fastfoodapi.entity.Branch;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    Branch branchDtoToBranch(BranchDto branchDto);

    BranchDto branchToBranchDto(Branch branch);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBranchFromBranchDto(BranchDto branchDto, @MappingTarget Branch branch);
}
