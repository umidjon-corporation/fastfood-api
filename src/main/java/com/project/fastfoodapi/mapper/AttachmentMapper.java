package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.AttachmentDto;
import com.project.fastfoodapi.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(source = "originalFilename", target = "name")
    @Mapping(source = "contentType", target = "type")
    Attachment toEntity(MultipartFile file) throws IOException;

    AttachmentDto toDto(Attachment file);

    @Mapping(source = "originalFilename", target = "name")
    @Mapping(source = "contentType", target = "type")
    void update(MultipartFile file, @MappingTarget Attachment attachment) throws IOException;
}
