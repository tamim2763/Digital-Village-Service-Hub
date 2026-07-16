package com.digitalvillage.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalvillage.dto.UserDto;
import com.digitalvillage.mapper.UserMapper;
import com.digitalvillage.repository.UserRepository;
import com.digitalvillage.service.UserService;
import com.digitalvillage.util.AppConstants;
import lombok.RequiredArgsConstructor;

/**
 * Starter implementation for the sample user use case.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public UserDto getSampleUser() {
		return userRepository.findTopByOrderByCreatedAtDesc()
				.map(userMapper::toDto)
				.orElseGet(this::buildPlaceholderUser);
	}

	private UserDto buildPlaceholderUser() {
		return new UserDto(null, AppConstants.SAMPLE_USER_NAME, AppConstants.SAMPLE_USER_EMAIL, LocalDateTime.now());
	}
}
