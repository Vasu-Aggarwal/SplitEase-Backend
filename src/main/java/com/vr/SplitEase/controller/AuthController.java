package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.request.JwtRequest;
import com.vr.SplitEase.dto.request.RefreshTokenRequest;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.dto.response.JwtResponse;
import com.vr.SplitEase.entity.RefreshToken;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.BadCredentialException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.UserRepository;
import com.vr.SplitEase.security.JwtHelper;
import com.vr.SplitEase.service.RefreshTokenService;
import com.vr.SplitEase.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtHelper helper;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid JwtRequest request) {

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        JwtResponse response = JwtResponse.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .token(token)
                .userUuid(user.getUserUuid())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<CreateUserResponse> registerNewUser(@RequestBody @Valid CreateUserRequest userRegisterRequest) {
        CreateUserResponse user = this.userService.addUpdateUser(userRegisterRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
//            refreshTokenService.deleteByUserName(authentication.getName());
        }
        return ResponseEntity.ok("Logout successful");
    }

//    @PostMapping("/registerAdminUser")
//    public ResponseEntity<UserRegisterResponse> registerAdminUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
//        UserRegisterResponse user = this.userService.registerAminUser(userRegisterRequest);
//        return new ResponseEntity<>(user, HttpStatus.CREATED);
//    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();
        String token = helper.generateToken(user);
        return new ResponseEntity<>(JwtResponse.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .token(token)
                .build(), HttpStatus.OK);
    }

    private void doAuthenticate(String username, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialException e) {
            throw new BadCredentialException();
        }

    }

}
