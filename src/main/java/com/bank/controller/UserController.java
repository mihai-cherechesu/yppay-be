package com.bank.controller;

import com.bank.dto.response.UserProfileResponseDto;
import com.bank.security.SecurityUser;
import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal SecurityUser securityUser) {
        UserProfileResponseDto responseDto = userService.getProfileById(securityUser.getUser().getId());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
