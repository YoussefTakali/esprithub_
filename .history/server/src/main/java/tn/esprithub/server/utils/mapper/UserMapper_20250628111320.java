package tn.esprithub.server.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import tn.esprithub.server.user.dto.UserDto;
import tn.esprithub.server.user.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "hasGithubToken", expression = "java(user.getGithubToken() != null && !user.getGithubToken().isEmpty())")
    UserDto toDto(User user);
    
    User toEntity(UserDto userDto);
}
