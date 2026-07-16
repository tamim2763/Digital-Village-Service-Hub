package com.digitalvillage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalvillage.dto.UserDto;
import com.digitalvillage.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Sample REST controller for the user slice only.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/sample")
	public ResponseEntity<UserDto> sampleUser() {
		return ResponseEntity.ok(userService.getSampleUser());
	}
}
