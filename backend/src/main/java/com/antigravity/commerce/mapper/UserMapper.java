package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.UserDto;
import com.antigravity.commerce.entity.Role;
import com.antigravity.commerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserDto toDto(User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
