package com.weatherapp.controller;

import com.weatherapp.dao.RoleRepository;
import com.weatherapp.dao.UserRepository;
import com.weatherapp.dto.AuthenticationResponseDto;
import com.weatherapp.dto.LoginDto;
import com.weatherapp.dto.SignUpDto;
import com.weatherapp.model.Role;
import com.weatherapp.model.User;
import com.weatherapp.service.UserService;
import com.weatherapp.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class HomeController {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
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
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    String jwtToken = jwtUtil.generateToken(loginDto.getUsername());

    // Create a response object
    AuthenticationResponseDto response =
        new AuthenticationResponseDto("Login Successful", jwtToken);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
    // checking for username exists in a database
    LOGGER.debug("Registering user account with information: {}", signUpDto);
    if (userRepository.existsByUsername(signUpDto.getUsername())) {
      return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
    }

    // checking for email exists in a database
    if (userRepository.existsByEmail(signUpDto.getEmail())) {
      return new ResponseEntity<>("Email is already taken", HttpStatus.BAD_REQUEST);
    }

    // creating user object
    User user = new User();
    user.setUsername(signUpDto.getUsername());
    user.setEmail(signUpDto.getEmail());
    user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

    Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");
    if (!optionalRole.isPresent()) {
      return new ResponseEntity<>("Role not found", HttpStatus.BAD_REQUEST);
    } else {
      user.setRoles(Collections.singleton(optionalRole.get()));
    }
    userRepository.save(user);

    return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(null);
      request.getSession().invalidate();
    }
    return new ResponseEntity<>("Logout Successful", HttpStatus.OK);
  }

  // Reset password
  @PostMapping("/resetPassword")
  public ResponseEntity<String> resetPassword(
      HttpServletRequest request, @RequestParam("email") String userEmail) {
    User user = userRepository.findByEmail(userEmail);
    if (user == null) {
      return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
    } else {
      String token = UUID.randomUUID().toString();
      userService.createPasswordResetTokenForUser(user, token);
      mailSender.send(
          constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
    }
    String token = request.getHeader("Authorization");
    if (token == null) {
      return new ResponseEntity<>("Token not found", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
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
