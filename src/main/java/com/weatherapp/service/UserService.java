package com.weatherapp.service;

import com.weatherapp.dao.PasswordResetTokenRepository;
import com.weatherapp.dao.UserRepository;
import com.weatherapp.dao.VerificationTokenRepository;
import com.weatherapp.model.PasswordResetToken;
import com.weatherapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService implements IUserService {
  @Autowired private UserRepository userRepository;
  @Autowired private VerificationTokenRepository tokenRepository;
  @Autowired private PasswordResetTokenRepository passwordTokenRepository;

  @Override
  public void createPasswordResetTokenForUser(final User user, final String token) {
    final PasswordResetToken myToken = new PasswordResetToken(token, user);
    passwordTokenRepository.save(myToken);
  }
}
