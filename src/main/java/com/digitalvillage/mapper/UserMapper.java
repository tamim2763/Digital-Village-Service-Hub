package com.digitalvillage.mapper;

import org.springframework.stereotype.Component;

import com.digitalvillage.dto.UserDto;
import com.digitalvillage.entity.User;

/**
 * Minimal mapper for the sample user slice.
 */
@Component
public class UserMapper {

	public UserDto toDto(User user) {
		if (user == null) {
			return null;
		}
		return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
	}

	public User toEntity(UserDto dto) {
		if (dto == null) {
			return null;
		}
		return User.builder()
				.id(dto.id())
				.name(dto.name())
				.email(dto.email())
				.createdAt(dto.createdAt())
				.build();
	}
}
