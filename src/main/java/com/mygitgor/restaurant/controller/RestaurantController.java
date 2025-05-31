package com.mygitgor.restaurant.controller;

import com.mygitgor.restaurant.domain.Restaurant;
import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.dto.RestaurantDto;
import com.mygitgor.restaurant.service.RestaurantService;
import com.mygitgor.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestaurantController управляет операциями, связанными с ресторанами.
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurant(@RequestHeader("Authorization") String jwt,
                                                             @RequestParam String keyword)throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        List<Restaurant> restaurant = restaurantService.searchRestaurant(keyword);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Restaurant>> getAllRestaurant(@RequestHeader("Authorization") String jwt)throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        List<Restaurant> restaurant = restaurantService.getAllRestaurant();
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> findRestaurantById(@RequestHeader("Authorization") String jwt,
                                                               @PathVariable Long id)throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findRestaurantById(id);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @PutMapping("/{id}/add-favorites")
    public ResponseEntity<RestaurantDto> addRestaurantFavorites(@RequestHeader("Authorization") String jwt,
                                                         @PathVariable Long id)throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        RestaurantDto restaurant = restaurantService.addToFavorites(id, user);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @CrossOrigin(origins = {
            "http://localhost:3000",
            "https://restaurant-frontend-9jwn.onrender.com"
    })
    @PutMapping("/{id}/remove-favorites")
    public ResponseEntity<Void> removeRestaurantFromFavorites(@RequestHeader("Authorization") String jwt,
                                                                @PathVariable Long id) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        restaurantService.removeFromFavorites(id, user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
