package com.mygitgor.restaurant.controller;

import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.dto.UserProfileDto;
import com.mygitgor.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController управляет операциями, связанными с пользователями.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @CrossOrigin(origins = {
            "http://localhost:3000",
            "https://restaurant-frontend-9jwn.onrender.com"
    })
    @SneakyThrows
    @GetMapping("/profile")
    public ResponseEntity<User> findUserByJwtToken(@RequestHeader("Authorization") String jwt){

        User user = userService.findUserByJwtToken(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @CrossOrigin(origins = {
            "http://localhost:3000",
            "https://restaurant-frontend-9jwn.onrender.com"
    })
    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(@RequestBody UserProfileDto userProfileDto,
                                                     @RequestHeader("Authorization") String jwt) throws Exception {
        userService.updateUserProfile(userProfileDto, jwt);
        return ResponseEntity.ok().build();
    }
}
