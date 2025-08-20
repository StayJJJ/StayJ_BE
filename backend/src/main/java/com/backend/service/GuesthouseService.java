package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.entity.Guesthouse;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.RoomRepository;
import com.backend.repository.UserRepository;

@Service
public class GuesthouseService {
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public GuesthouseRepository guesthouseRepository;
	
	@Autowired
	public RoomRepository roomRepository;
	
	
	public void createGuestHouseWithRooms(GuestHouseCreateRequest request) {
	    User host = userRepository.findById(request.getHostId())
	        .orElseThrow(() -> new IllegalArgumentException("Host not found"));

	    Guesthouse guestHouse = Guesthouse.builder()
	        .name(request.getName())
	        .description(request.getDescription())
	        .address(request.getAddress())
	        .photoId(request.getPhotoId())
	        .phoneNumber(request.getPhoneNumber())
	        .rating(request.getRating())
	        .roomCount(request.getRoomCount())
	        .host(host)
	        .roomList(new ArrayList<Room>())
	        .build(); 

	    request.getRooms().forEach(r -> {
	        Room room = Room.builder()
	            .name(r.getName())
	            .capacity(r.getCapacity())
	            .price(r.getPrice())
	            .photoId(r.getPhotoId())
	            .build();

	        guestHouse.addRoom(room);
	    });

	    guesthouseRepository.save(guestHouse);
	}
}
