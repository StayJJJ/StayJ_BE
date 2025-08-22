package com.backend.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.GuesthouseRepository;
import com.backend.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;

import com.backend.dto.request.GuestHouseDetailRequest;
import com.backend.dto.request.GuestHouseRoomRequest;
import com.backend.dto.request.RoomResponseRequest;
import com.backend.dto.response.ReviewResponse;
import com.backend.entity.Guesthouse;
import com.backend.entity.Review;
import com.backend.entity.Room;

@Service
public class reservationInfoService {
    @Autowired
    private GuesthouseRepository guestHouseRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
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
        
        return guestHouseDetail;
    }
    
    public RoomResponseRequest getGuestHouseRooms(Integer guesthouseId, List<Integer> roomAvailableFilter) {
        Guesthouse guesthouse = guestHouseRepository.findById(guesthouseId)
                .orElseThrow(() -> new EntityNotFoundException("Guesthouse not found with id: " + guesthouseId));

        List<Room> roomList = guesthouse.getRoomList();
        
        if (roomAvailableFilter != null && !roomAvailableFilter.isEmpty()) {
            roomList = roomList.stream()
                    .filter(room -> roomAvailableFilter.contains(room.getId()))
                    .collect(Collectors.toList());
        }

        List<GuestHouseRoomRequest> guestHouseRooms = roomList.stream()
        	    .map(room -> GuestHouseRoomRequest.builder()
        	        .id(room.getId())
        	        .name(room.getName())
        	        .capacity(room.getCapacity())
        	        .price(room.getPrice())
        	        .build())
        	    .collect(Collectors.toList());

        	RoomResponseRequest response = RoomResponseRequest.builder()
        	    .rooms(guestHouseRooms)
        	    .build();

        return response;
    }
    
    public List<ReviewResponse> getReview(Integer guesthouseId) {
        if (!guestHouseRepository.existsById(guesthouseId)) {
            throw new EntityNotFoundException("Guesthouse not found with id: " + guesthouseId);
        }
        
        List<Review> reviews = reviewRepository.findByGuesthouseId(guesthouseId);
        
        List<ReviewResponse> responseList = reviews.stream()
        	    .map(review -> {
        	        ReviewResponse response = ReviewResponse.builder()
        	            .id(review.getId())
        	            .reservation_id(review.getReservation().getId())
        	            .rating(review.getRating())
        	            .comment(review.getComment())
        	            .created_at(review.getCreatedAt())
        	            .build();
        	        return response;
        	    })
        	    .collect(Collectors.toList());
        
        return responseList;
    }
}