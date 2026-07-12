package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.UserDto;
import com.antigravity.commerce.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T21:30:41+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.roles( mapRoles( user.getRoles() ) );
        userDto.id( user.getId() );
        userDto.email( user.getEmail() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.phone( user.getPhone() );
        userDto.createdAt( user.getCreatedAt() );
        userDto.updatedAt( user.getUpdatedAt() );

        return userDto.build();
    }
}
