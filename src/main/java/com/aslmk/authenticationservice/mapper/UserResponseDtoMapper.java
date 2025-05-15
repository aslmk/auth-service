package com.aslmk.authenticationservice.mapper;

import com.aslmk.authenticationservice.dto.UserResponseDto;
import com.aslmk.authenticationservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserResponseDtoMapper {

    @Mapping(target = "role", ignore = true)
    UserResponseDto mapToUserResponseDto(UserEntity userEntity);
}
