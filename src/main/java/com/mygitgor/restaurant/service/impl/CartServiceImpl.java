package com.mygitgor.restaurant.service.impl;

import com.mygitgor.restaurant.domain.Cart;
import com.mygitgor.restaurant.domain.CartItem;
import com.mygitgor.restaurant.domain.Food;
import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.exceptions.cartexception.CartItemNotFoundException;
import com.mygitgor.restaurant.repository.CartItemRepository;
import com.mygitgor.restaurant.repository.CartRepository;
import com.mygitgor.restaurant.controller.DTOs.request.AddCartItemRequest;
import com.mygitgor.restaurant.service.CartService;
import com.mygitgor.restaurant.service.FoodService;
import com.mygitgor.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Cart Service: Этот сервиз связаны с  корзиной покупок.
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodService foodService;
    private final UserService userService;

    /**
     * метод предназначен для добавления товара в корзину пользователя.
     * @param request пренимает foodId quantity ingredients
     * @param jwt  пренимает токен пользователя
     * @return возврошяет корзину товаров пользователя
     * @throws Exception бросает исключения Exception
     */
    @Override
    public CartItem addItemToCart(AddCartItemRequest request, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Food food = foodService.findFoodById(request.getFoodId());
        Cart cart = cartRepository.findByCustomerId(user.getId());

        for(CartItem cartItem : cart.getItem()){
            if(cartItem.getFood().equals(food)){
                int quantity = cartItem.getQuantity() + request.getQuantity();
                return updateCartItemQuantity(cartItem.getId(), quantity);
            }
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setFood(food);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(request.getQuantity());
        newCartItem.setIngredients(request.getIngredients());
        newCartItem.setTotalPrice(request.getQuantity() * food.getPrice());
        CartItem saveCartItem = cartItemRepository.save(newCartItem);

        cart.getItem().add(saveCartItem);
        return saveCartItem;
    }

    @Override
    public List<CartItem> findUserCartItems(String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartRepository.findByCustomerId(user.getId());

        if (cart == null) {
            throw new Exception("Cart not found for user: " + user.getId());
        }
        return cart.getItem();
    }

    /**
     * метод предназначен для обновления количества товара в корзине пользователя
     * @param cartItemId принемает идентификатор карзины
     * @param quantity принемает количество товаров
     * @return возврошает корзину товаров
     * @throws Exception бросает исключения Exception
     */
    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity)throws CartItemNotFoundException{
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getFood().getPrice() * quantity);

        return cartItemRepository.save(cartItem);
    }

    /**
     * метод предназначен для удаления элемента из корзины пользователя.
     * @param id идентификатор карзины
     * @param jwt токен пользователя
     * @return  возврашает карзину
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Cart removeItemFromCart(Long id, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartRepository.findByCustomerId(user.getId());

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if(cartItemOptional.isEmpty()){
            throw new Exception("cart item not found");
        }

        CartItem cartItem = cartItemOptional.get();
        cart.getItem().remove(cartItem);

        return cartRepository.save(cart);
    }

    /**
     * метод предназначен для расчета общей стоимости товаров в корзине.
     * @param cart принимает карзину
     * @return возврошает стоимость товаров
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Long calculateCartTotals(Cart cart) throws Exception {
        Long total = 0L;

        for(CartItem cartItem : cart.getItem()){
            total += cartItem.getFood().getPrice() * cartItem.getQuantity();
        }
        return total;
    }

    /**
     * метод предназначен для поиска корзины по её идентификатору.
     * @param id идентификатор карзины
     * @return возврошает карзину
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Cart findCartById(Long id) throws Exception {
        Optional<Cart> cartOptional = cartRepository.findById(id);

        if(cartOptional.isEmpty()){
            throw new Exception("cart item not found wit id");
        }
        return cartOptional.get();
    }

    /**
     * метод предназначен для поиска корзины по идентификатору пользователя.
     * @param userId идентификатор пользователя
     * @return возврошает карзину пользователя
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Cart findCartByUserId(Long userId) throws Exception {
        Cart cart = cartRepository.findByCustomerId(userId);
        cart.setTotal(calculateCartTotals(cart));
        return cart;
    }

    /**
     * метод clearCart предназначен для очистки корзины пользователя.
     * @param userId идентификатор пользователя
     * @return возбрашает карзину
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Cart clearCart(Long userId) throws Exception {
        Cart cart = findCartByUserId(userId);

        cart.getItem().clear();
        return cartRepository.save(cart);
    }
}
