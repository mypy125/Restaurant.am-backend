package com.mygitgor.restaurant.service.impl;

import com.mygitgor.restaurant.domain.*;
import com.mygitgor.restaurant.repository.AddressRepository;
import com.mygitgor.restaurant.repository.OrderItemRepository;
import com.mygitgor.restaurant.repository.OrderRepository;
import com.mygitgor.restaurant.repository.UserRepository;
import com.mygitgor.restaurant.controller.DTOs.request.OrderRequest;
import com.mygitgor.restaurant.service.CartService;
import com.mygitgor.restaurant.service.OrderService;
import com.mygitgor.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Order Service: Этот сервиз связаны с оформлением заказов.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestaurantService restaurantService;
    private final CartService cartService;


    /**
     * метод обеспечивает создание нового заказа на основе информации, полученной из запроса, и данных пользователя.
     * @param request ордерский запрос
     * @param user пользожатель для оформления заказа
     * @return возжрашает заказ
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Order createOrder(OrderRequest request, User user) throws Exception {
        Address shopAddress = request.getDeliveryAddress();
        Address savedAddress = addressRepository.save(shopAddress);

        if(!user.getAddresses().contains(savedAddress)){
            user.getAddresses().add(savedAddress);
            userRepository.save(user);
        }

        Restaurant restaurant = restaurantService.findRestaurantById(request.getRestaurantId());

        Order createOrder = new Order();
        createOrder.setCustomer(user);
        createOrder.setCreateAt(new Date());
        createOrder.setOrderStatus("PENDING");
        createOrder.setDeliveryAddress(savedAddress);
        createOrder.setRestaurant(restaurant);

        Cart cart = cartService.findCartByUserId(user.getId());

        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cart.getItem()){

            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setIngredients(cartItem.getIngredients());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            OrderItem saveOrder = orderItemRepository.save(orderItem);
            orderItems.add(saveOrder);
        }

        Long totalPrice = cartService.calculateCartTotals(cart);

        createOrder.setItems(orderItems);
        createOrder.setTotalPrice(totalPrice);

        Order saveOrder = orderRepository.save(createOrder);
        restaurant.getOrders().add(saveOrder);

        return createOrder;

    }

    /**
     * метод обеспечивает возможность обновления статуса заказа на основе его идентификатора и переданного статуса.
     * @param orderId идентификатор заказа
     * @param orderStatus статус
     * @return возврошает заказ
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Order updateOrder(Long orderId, String orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        if(orderStatus.equals("OUT_FOR_DELIVERY") || orderStatus.equals("DELIVERED") ||
                orderStatus.equals("COMPLETED") || orderStatus.equals("PENDING")){

            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }
        throw new Exception("select a valid order status");
    }

    /**
     * метод не возвращает результат, так как просто отменяет заказ, удаляя его из базы данных.
     * @param orderId идентификатор заказа
     * @throws Exception бросает исключения Exception
     */
    @Override
    public void cancelOrder(Long orderId) throws Exception {
        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);
    }

    /**
     *  метод предназначен для получения списка заказов пользователя по его идентификатору.
     * @param userId идентификатор пользователя
     * @return возврошает список заказов
     * @throws Exception бросает исключения Exception
     */
    @Override
    public List<Order> getUsersOrder(Long userId) throws Exception {
        return orderRepository.findByCustomerId(userId);
    }

    /**
     * метод предназначен для получения списка заказов ресторана по его идентификатору и статусу заказа.
     * @param restaurantId идентификатор ресторана
     * @param orderStatus статус заказа
     * @return возврошает список зказов
     * @throws Exception бросает исключения Exception
     */
    @Override
    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if(orderStatus != null){
            orders = orders.stream().filter(order ->
                    order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }
        return orders;
    }

    /**
     * метод полезен при поиске заказа по его идентификатору для выполнения различных операций,
     * таких как обновление статуса заказа или получение информации о конкретном заказе.
     * @param orderId идентификатор заказа
     * @return возврошает заказ
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Order findOrderById(Long orderId) throws Exception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()){
            throw new Exception("order not found");
        }
        return optionalOrder.get();
    }
}
