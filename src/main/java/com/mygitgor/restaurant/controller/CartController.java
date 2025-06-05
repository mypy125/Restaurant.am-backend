package com.mygitgor.restaurant.controller;

import com.mygitgor.restaurant.domain.Cart;
import com.mygitgor.restaurant.domain.CartItem;
import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.exceptions.userexception.UserNotFoundException;
import com.mygitgor.restaurant.controller.DTOs.request.AddCartItemRequest;
import com.mygitgor.restaurant.controller.DTOs.request.UpdateCartItemRequest;
import com.mygitgor.restaurant.service.CartService;
import com.mygitgor.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CartController управляет операциями, связанными с корзиной пользователя.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @SneakyThrows
    @PutMapping("/cart/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddCartItemRequest request,
                                                  @RequestHeader("Authorization") String jwt
    ){
        CartItem cartItem = cartService.addItemToCart(request, jwt);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @GetMapping("/cart/{cartId}/items")
    public ResponseEntity<List<CartItem>> findAllItemsCart(@PathVariable Long cartId,
                                                            @RequestHeader("Authorization") String jwt)throws Exception
    {
        User user = userService.findUserByJwtToken(jwt);
//        if (!cartService.isCartOwnedByUser(cartId, user.getId())) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
        List<CartItem> allCartItems = cartService.getAllCartItems(cartId);
        return new ResponseEntity<>(allCartItems, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/cart-item/update")
    public ResponseEntity<CartItem> updateCartItemQuantity(@RequestBody UpdateCartItemRequest request,
                                                  @RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwtToken(jwt);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        CartItem cartItem = cartService.updateCartItemQuantity(request.getId(), request.getQuantity());
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/cart-item/{id}/remove")
    public ResponseEntity<Cart> removeCartItem(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String jwt){
        Cart cart = cartService.removeItemFromCart(id, jwt);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/cart/clear")
    public ResponseEntity<Cart> clearCart(@RequestHeader("Authorization") String jwt){

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.clearCart(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/cart")
    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt){

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findCartByUserId(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

}
