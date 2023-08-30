package com.weatherapp.service;

import com.weatherapp.model.User;

public interface IUserService {
    void createPasswordResetTokenForUser(User user, String token);
}
