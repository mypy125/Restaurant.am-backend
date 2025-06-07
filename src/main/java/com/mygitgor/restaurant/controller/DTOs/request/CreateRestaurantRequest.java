package com.mygitgor.restaurant.controller.DTOs.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mygitgor.restaurant.convertor.ImageListDeserializer;
import com.mygitgor.restaurant.domain.Address;
import com.mygitgor.restaurant.domain.ContactInformation;
import lombok.Data;

import java.util.List;

@Data
public class CreateRestaurantRequest {
    private Long id;
    private String name;
    private String description;
    private String cuisineType;
    private Address address;
    private ContactInformation contactInformation;
    private String openingHours;
    @JsonDeserialize(using = ImageListDeserializer.class)
    private List<String> images;
}
