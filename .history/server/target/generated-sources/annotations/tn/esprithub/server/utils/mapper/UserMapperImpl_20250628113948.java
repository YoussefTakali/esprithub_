package tn.esprithub.server.utils.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import tn.esprithub.server.user.dto.UserDto;
import tn.esprithub.server.user.*;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-28T11:38:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Ubuntu)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.email( user.getEmail() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.role( user.getRole() );
        userDto.githubUsername( user.getGithubUsername() );
        userDto.profilePicture( user.getProfilePicture() );
        userDto.enabled( user.getEnabled() );
        userDto.emailVerified( user.getEmailVerified() );
        userDto.createdAt( user.getCreatedAt() );
        userDto.updatedAt( user.getUpdatedAt() );

        userDto.hasGithubToken( user.getGithubToken() != null && !user.getGithubToken().isEmpty() );

        return userDto.build();
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDto.getId() );
        user.email( userDto.getEmail() );
        user.firstName( userDto.getFirstName() );
        user.lastName( userDto.getLastName() );
        user.role( userDto.getRole() );
        user.githubUsername( userDto.getGithubUsername() );
        user.profilePicture( userDto.getProfilePicture() );
        user.enabled( userDto.getEnabled() );
        user.emailVerified( userDto.getEmailVerified() );
        user.createdAt( userDto.getCreatedAt() );
        user.updatedAt( userDto.getUpdatedAt() );

        return user.build();
    }
}
