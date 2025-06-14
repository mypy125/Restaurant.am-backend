package com.mygitgor.restaurant.controller;

import com.mygitgor.restaurant.domain.Order;
import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.service.OrderService;
import com.mygitgor.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * AdminOrderController определяется конечные точки для управления заказами в панели администратора.
 */
@Slf4j
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;
    private final UserService userService;

    @SneakyThrows
    @GetMapping("/order/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getOrderHistory(@PathVariable Long restaurantId,
                                                       @RequestParam(required = false) String order_status,
                                                       @RequestHeader("Authorization") String jwt){

        User user = userService.findUserByJwtToken(jwt);
        List<Order> order = orderService.getRestaurantsOrder(restaurantId, order_status);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/order/{id}/{orderStatus}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
                                                       @PathVariable String orderStatus,
                                                       @RequestHeader("Authorization") String jwt){

        User user = userService.findUserByJwtToken(jwt);

        Order order = orderService.updateOrder(id, orderStatus);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

}
