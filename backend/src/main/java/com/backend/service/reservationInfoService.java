package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.GuesthouseRepository;

import jakarta.persistence.EntityNotFoundException;

import com.backend.dto.request.GuestHouseDetailRequest;
import com.backend.entity.Guesthouse;

@Service
public class reservationInfoService {
    
    @Autowired
    private GuesthouseRepository guestHouseRepository;
    
    public GuestHouseDetailRequest getGuestHouseDetail(Integer guesthouseId) {
    	Guesthouse guestHouse = guestHouseRepository.findById(guesthouseId)
    		    .orElseThrow(() -> new IllegalArgumentException("Guesthouse not found with id: " + guesthouseId));
    	
    	GuestHouseDetailRequest guestHouseDetail = GuestHouseDetailRequest.builder()
    		    .id(guestHouse.getId())
    		    .name(guestHouse.getName())
    		    .description(guestHouse.getDescription())
    		    .address(guestHouse.getAddress())
    		    .rating(guestHouse.getRating())
    		    .photoId(guestHouse.getPhotoId())
    		    .room_count(guestHouse.getRoomCount())
    		    .build();
        
    	System.out.println("guest house = > " + guestHouseDetail.getAddress());

        
        return guestHouseDetail;
    }
}