package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.config.exceptions.BadRequestException;
import com.nguyenphuong.PackageTransfer.config.exceptions.RecordNotFoundException;
import com.nguyenphuong.PackageTransfer.modals.entities.UserEntity;
import com.nguyenphuong.PackageTransfer.repositories.UserRepository;
import com.nguyenphuong.PackageTransfer.utils.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  @Autowired
  private UserRepository userRepository;

  public UserEntity getUserByEmail(String email) {
    if (!EmailValidator.getInstance().isValid(email)) {
      throw new BadRequestException("The email is not valid");
    }
    return userRepository.findUserEntityByEmailIgnoreCase(email);
  }

  public UserEntity getUserByUsername(String username) {
    UserEntity user = userRepository.findUserEntityByUsernameIgnoreCase(username);
    if (user == null) {
      throw new RecordNotFoundException("User not found with username: " + username);
    }
    return user;
  }

  @Override
  public User loadUserByUsername(String username) {
    UserEntity user = userRepository.findUserEntityByUsernameIgnoreCase(username);
    if (user == null) {
      throw new RecordNotFoundException("User not found with username: " + username);
    }
    return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
  }

  public UserEntity createUser(UserEntity user) {
    String email = user.getEmail();
    if (StringUtils.isBlank(email)) {
      throw new BadRequestException("Email is required");
    }
    if (StringUtils.isBlank(user.getUsername())) {
      throw new BadRequestException("Username is required");
    }
    validateUserName(user);
    if (user.getUsername().length() <= 3) {
      throw new BadRequestException("Username must be at least 3 characters");
    }
    if (!EmailValidator.getInstance().isValid(email)) {
      throw new BadRequestException("The email is not valid");
    }
    if (userRepository.findUserEntityByUsernameIgnoreCase(user.getUsername()) != null) {
      throw new BadRequestException("This username is already taken");
    }
    if (getUserByEmail(email) != null) {
      throw new BadRequestException("This email is already registered");
    }
    if (!isValidPassword(user.getPassword())) {
      throw new BadRequestException("Password must have at least one digit, one uppercase and one lowercase character");
    }
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail(email);
    userEntity.setUsername(user.getUsername());
    userEntity.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    userEntity.setFirstName(user.getFirstName());
    userEntity.setLastName(user.getLastName());
    return userRepository.save(userEntity);
  }

  public UserEntity updateUser(String id, UserEntity update) {
    validateUserName(update);
    UserEntity userEntity = userRepository.findById(id).orElse(null);
    if (Objects.isNull(userEntity)) {
      throw new RecordNotFoundException("Can't find user with id " + id);
    }
    userEntity.setFirstName(update.getFirstName());
    userEntity.setLastName(update.getLastName());
    return userRepository.save(userEntity);
  }

  private boolean isValidPassword(String password) {
    if (StringUtils.isBlank(password) || password.length() < 8) {
      throw new BadRequestException("Password must be at least 8 characters");
    }
    boolean hasUpper = false, hasLower = false, hasDigit = false;
    for (int i = 0; i < password.length(); i++) {
      char ch = password.charAt(i);
      if (Character.isUpperCase(ch))
        hasUpper = true;
      else if (Character.isLowerCase(ch))
        hasLower = true;
      else if (Character.isDigit(ch))
        hasDigit = true;
    }
    return hasDigit && hasLower && hasUpper;
  }

  private String getEncryptedString(String clientSecret) {
    try {
      return CryptoUtils.hashing(clientSecret);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Can't encrypt client secret", e);
      throw new RuntimeException("Can't create user please try again");
    }
  }

  private void validateUserName(UserEntity userEntity) {
    if (StringUtils.isBlank(userEntity.getFirstName())) {
      throw new BadRequestException("first_name can't be empty");
    }
    if (StringUtils.isBlank(userEntity.getLastName())) {
      throw new BadRequestException("last_name can't be empty");
    }
  }
}
