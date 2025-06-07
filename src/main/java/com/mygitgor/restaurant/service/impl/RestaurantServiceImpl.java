package com.mygitgor.restaurant.service.impl;

import com.mygitgor.restaurant.domain.Address;
import com.mygitgor.restaurant.domain.Restaurant;
import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.dto.RestaurantDto;
import com.mygitgor.restaurant.repository.AddressRepository;
import com.mygitgor.restaurant.repository.RestaurantRepository;
import com.mygitgor.restaurant.repository.UserRepository;
import com.mygitgor.restaurant.controller.DTOs.request.CreateRestaurantRequest;
import com.mygitgor.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RestaurantService: Этот сервиз связаны с ресторанами
 */
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;


    /**
     * метод используется для создания нового ресторана на основе полученного запроса и информации о пользователе.
     * @param request ресторанный запрос
     * @param user пользожатель (админ)
     * @return возврошает созданный ресторан
     */
    @Override
    public Restaurant createRestaurant(CreateRestaurantRequest request, User user) {
        Optional<Restaurant> existingRestaurant = restaurantRepository.findByOwnerId(user.getId());
        if (existingRestaurant.isPresent()) {
            throw new RuntimeException("A restaurant already exists for this owner.");
        }

        Address address = addressRepository.save(request.getAddress());

        Restaurant restaurant = new Restaurant();
        restaurant.setAddress(address);
        restaurant.setContactInformation(request.getContactInformation());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setDescription(request.getDescription());
        restaurant.setImages(request.getImages());
        restaurant.setName(request.getName());
        restaurant.setOpeningHours(request.getOpeningHours());
        restaurant.setRegistrationTime(LocalDateTime.now());
        restaurant.setOwner(user);
        return restaurantRepository.save(restaurant);
    }

    /**
     * метод позволяет администраторам или владельцам ресторанов обновлять информацию о своих заведениях,
     * такую как название, описание и тип кухни.
     * @param restaurantId идентификатор ресторана
     * @param updateRestaurant новый параметры запроса для обновления
     * @return возврошает ресторан обновленным данными
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updateRestaurant)throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if(restaurant.getCuisineType() != null){
            restaurant.setCuisineType(updateRestaurant.getCuisineType());
        }
        if(restaurant.getDescription() != null){
            restaurant.setDescription(updateRestaurant.getDescription());
        }
        if(restaurant.getName() != null){
            restaurant.setName(updateRestaurant.getName());
        }
        return restaurantRepository.save(restaurant);
    }

    /**
     *  метод используется для удаления ресторана из системы.
     * @param restaurantId идентификатор ресторана
     * @throws Exception бросает исключения Exception
     */
    @Override
    public void deleteRestaurant(Long restaurantId)throws Exception {
        Restaurant restaurant = findRestaurantByUserId(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    /**
     *  метод getAllRestaurant используется для получения списка всех ресторанов, которые хранятся в системе.
     *  Он просто извлекает все рестораны из репозитория и возвращает их в виде списка.
     * @return список ресторан
     */
    @Override
    public List<Restaurant> getAllRestaurant() {
        return restaurantRepository.findAll();
    }

    /**
     *  метод searchRestaurant используется для поиска ресторанов по ключевому слову.
     *  Он выполняет поиск в репозитории ресторанов по заданному ключевому слову и возвращает список найденных ресторанов.
     * @param keyword клзчевое слово
     * @return список ресторан
     */
    @Override
    public List<Restaurant> searchRestaurant(String keyword) {
        return restaurantRepository.findBySearchQuery(keyword);
    }

    /**
     * метод используется для поиска ресторана по его идентификатору.
     * Он выполняет запрос к репозиторию ресторанов и возвращает ресторан с указанным идентификатором,
     * @param id идентификатор ресторана
     * @return возврошает ресторан
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Restaurant findRestaurantById(Long id) throws Exception {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new Exception("Restaurant not found with id " + id));
    }

    /**
     *  метод выполняет поиск ресторана по идентификатору его владельца (пользователя).
     *  Он использует restaurantRepository для выполнения запроса к базе данных и возвращает ресторан,
     *  у которого владелец имеет указанный идентификатор.
     * @param id идентификатор владелеца
     * @return возврошает ресторан
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Restaurant findRestaurantByUserId(Long id)throws Exception {
        return restaurantRepository.findByOwnerId(id)
                .orElseThrow(() -> new Exception("Restaurant not found with owner id " + id));
    }

    /**
     * метод addToFavorites предназначен для добавления ресторана в список избранных для определенного пользователя.
     * Он принимает идентификатор ресторана и пользователя. Затем он находит ресторан по его идентификатору с помощью метода findRestaurantById.
     * После этого создается объект RestaurantDto, в который копируются необходимые данные о ресторане. Затем метод проверяет,
     * содержится ли ресторан уже в списке избранных пользователя. Если ресторан уже присутствует в списке избранных, он удаляется из списка, иначе он добавляется.
     * После этого обновляется информация о пользователе в базе данных с помощью userRepository.save(user).
     * Наконец, метод возвращает объект RestaurantDto
     * @param restaurantId идентификатор  ресторана
     * @param user пользователь
     * @return возврошает ресторандто
     * @throws Exception бросает исключения Exception
     */
    @Override
    public RestaurantDto addToFavorites(Long restaurantId, User user)throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);

        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setDescription(restaurant.getDescription());
        restaurantDto.setImages(restaurant.getImages());
        restaurantDto.setTitle(restaurant.getName());
        restaurantDto.setId(restaurantId);

        if(user.getFavorites().contains(restaurantDto)){
            user.getFavorites().remove(restaurantDto);
        }
        else user.getFavorites().add(restaurantDto);

        userRepository.save(user);

        return restaurantDto;
    }

    @Override
    public void removeFromFavorites(Long restaurantId, User user) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);

        if (user.getFavorites().stream().anyMatch(fav -> fav.getId().equals(restaurantId))) {
            user.getFavorites().removeIf(fav -> fav.getId().equals(restaurantId));
            userRepository.save(user);
        } else {
            throw new Exception("Restaurant not found in favorites");
        }
    }


    /**
     * метод предназначен для изменения статуса (открыт/закрыт) ресторана.
     * Он принимает идентификатор ресторана, находит соответствующий ресторан по этому идентификатору с помощью
     * метода findRestaurantById, изменяет его статус на противоположный (открытый ресторан становится закрытым и наоборот),
     * а затем сохраняет обновленную информацию о ресторане в базе данных с помощью restaurantRepository.save(restaurant).
     * Наконец, метод возвращает обновленный объект Restaurant.
     * @param id идентификатор ресторана
     * @return возвращает обновленный ресторан
     * @throws Exception бросает исключения Exception
     */
    @Override
    public Restaurant updateRestaurantStatus(Long id)throws Exception {
        Restaurant restaurant = findRestaurantById(id);
        restaurant.setOpen(!restaurant.isOpen());
        return restaurantRepository.save(restaurant);
    }
}
