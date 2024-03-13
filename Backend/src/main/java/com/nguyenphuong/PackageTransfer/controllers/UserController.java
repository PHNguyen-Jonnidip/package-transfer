package com.nguyenphuong.PackageTransfer.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.config.exceptions.BadRequestException;
import com.nguyenphuong.PackageTransfer.modals.dto.LoginForm;
import com.nguyenphuong.PackageTransfer.modals.entities.UserEntity;
import com.nguyenphuong.PackageTransfer.modals.share.JwtResponse;
import com.nguyenphuong.PackageTransfer.services.UserService;
import com.nguyenphuong.PackageTransfer.utils.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User", description = "User APIs")
public class UserController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenUtils jwtTokenUtils;

  @GetMapping("/api/users/me")
  @Operation(security = {@SecurityRequirement(name = "user-token")})
  public ResponseEntity<UserModel> getUser() {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    UserEntity user = userService.getUserByUsername(userDetails.getUsername());
    return ResponseEntity.ok(new UserModel(user));
  }

  @PutMapping("/api/users/{id}")
  @Operation(security = {@SecurityRequirement(name = "user-token")})
  public ResponseEntity<UserModel> updateUser(@RequestBody UserEntity userEntity, @PathVariable String id) {
    UserEntity updatedUser = userService.updateUser(id, userEntity);
    return ResponseEntity.ok(new UserModel(updatedUser));
  }

  @PostMapping("/api/auth/sign-in")
  public ResponseEntity<JwtResponse> userSignIn(@RequestBody LoginForm loginForm) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = jwtTokenUtils.generateToken(authentication);

      return ResponseEntity.ok(new JwtResponse(jwt));
    } catch (BadCredentialsException ex) {
      LOGGER.info("Can't find any user with username and password provided", ex);
      throw new BadRequestException("Your username or password is incorrect");
    }
  }

  @PostMapping("/api/auth/sign-up")
  public ResponseEntity<UserModel> registerUser(@RequestBody UserEntity user) {
    UserEntity signedUpUser = userService.createUser(user);
    return new ResponseEntity<>(new UserModel(signedUpUser), HttpStatus.CREATED);
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class UserModel {
    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    public UserModel(UserEntity entity) {
      this.id = entity.getId();
      this.username = entity.getUsername();
      this.firstName = entity.getFirstName();
      this.lastName = entity.getLastName();
      this.email = entity.getEmail();
      this.fullName = entity.getFirstName() + " " + entity.getLastName();
    }
  }
}
