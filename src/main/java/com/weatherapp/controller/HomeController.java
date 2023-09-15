package com.weatherapp.controller;

import com.weatherapp.dao.RoleRepository;
import com.weatherapp.dao.UserRepository;
import com.weatherapp.dto.*;
import com.weatherapp.model.Role;
import com.weatherapp.model.User;
import com.weatherapp.service.UserService;
import com.weatherapp.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class HomeController {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;
  @Autowired private UserService userService;
  @Autowired private RoleRepository roleRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JavaMailSender mailSender;
  @Autowired private MessageSource messages;
  @Autowired private Environment env;
  @Autowired private JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
    Authentication authentication;
    try {
      logger.debug("Authenticating user with information: {}", loginDto);
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginDto.getUsername(), loginDto.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (BadCredentialsException e) {
      logger.error("Error authenticating user: {}", e.getMessage());
      ErrorResponseDto errorResponse = new ErrorResponseDto("Invalid username or password");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Extract roles from the authentication object
    List<String> roles =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    // Generate JWT token
    String jwtToken = jwtUtil.generateToken(loginDto.getUsername(), roles);

    // Create a response object
    AuthenticationResponseDto response =
        new AuthenticationResponseDto("Login Successful", jwtToken);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
    // checking for username exists in a database
    logger.debug("Registering user account with information: {}", signUpDto);
    if (userRepository.existsByUsername(signUpDto.getUsername())) {
      ErrorResponseDto errorResponse = new ErrorResponseDto("Username is already taken");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // checking for email exists in a database
    if (userRepository.existsByEmail(signUpDto.getEmail())) {
      ErrorResponseDto errorResponse = new ErrorResponseDto("Email is already taken");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // creating user object
    User user = new User();
    user.setUsername(signUpDto.getUsername());
    user.setEmail(signUpDto.getEmail());
    user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

    Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");
    if (!optionalRole.isPresent()) {
      ErrorResponseDto errorResponse = new ErrorResponseDto("Role not found");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } else {
      user.setRoles(Collections.singleton(optionalRole.get()));
    }
    userRepository.save(user);

    return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(null);
      request.getSession().invalidate();
    }
    return new ResponseEntity<>("Logout Successful", HttpStatus.OK);
  }

  // Reset password
  @PostMapping("/resetPassword")
  public ResponseEntity<?> resetPassword(
      HttpServletRequest request, @RequestParam("email") String userEmail) {
    User user = userRepository.findByEmail(userEmail);
    if (user == null) {
      ErrorResponseDto errorResponse = new ErrorResponseDto("User not found");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } else {
      String token = UUID.randomUUID().toString();
      userService.createPasswordResetTokenForUser(user, token);
      mailSender.send(
          constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
    }
    String token = request.getHeader("Authorization");
    if (token == null) {
      ErrorResponseDto errorResponse = new ErrorResponseDto("Token not found");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Principal principal) {
    User user = userRepository.findByUsername(principal.getName());
    if (user == null) {
      ErrorResponseDto errorResponse = new ErrorResponseDto("User not found");
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    UserDto userDto = new UserDto();
    userDto.setUsername(user.getUsername());
    userDto.setEmail(user.getEmail());

    return new ResponseEntity<>(userDto, HttpStatus.OK);
  }

  // ============== NON-API ============

  private SimpleMailMessage constructResetTokenEmail(
      final String contextPath, final Locale locale, final String token, final User user) {
    final String url = contextPath + "/user/changePassword?token=" + token;
    final String message = messages.getMessage("message.resetPassword", null, locale);
    return constructEmail("Reset Password", message + " \r\n" + url, user);
  }

  private SimpleMailMessage constructEmail(String subject, String body, User user) {
    final SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject(subject);
    email.setText(body);
    email.setTo(user.getEmail());
    email.setFrom(env.getProperty("support.email"));
    return email;
  }

  private String getAppUrl(HttpServletRequest request) {
    return "http://"
        + request.getServerName()
        + ":"
        + request.getServerPort()
        + request.getContextPath();
  }
}
