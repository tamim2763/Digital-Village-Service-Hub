package com.digitalvillage.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Sample transfer object for the user module.
 */
public record UserDto(
		Long id,
		@NotBlank @Size(max = 100) String name,
		@NotBlank @Email @Size(max = 150) String email,
		LocalDateTime createdAt) {
}
