package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.entity.Guesthouse;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.RoomRepository;
import com.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class GuesthouseService {
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public GuesthouseRepository guesthouseRepository;
	
	@Autowired
	public RoomRepository roomRepository;
	
	
	public Integer createGuestHouseWithRooms(Integer hostId, GuestHouseCreateRequest request) {
	    User host = userRepository.findById(hostId)
	        .orElseThrow(() -> new IllegalArgumentException("Host not found"));

	    Guesthouse guesthouse = Guesthouse.builder()
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
	    
	    if (request.getRooms() != null) {
	    	request.getRooms().forEach(r -> {
		        Room room = Room.builder()
		            .name(r.getName())
		            .capacity(r.getCapacity())
		            .price(r.getPrice())
		            .photoId(r.getPhotoId())
		            .build();
		        guesthouse.addRoom(room);
		    });
        }

	    guesthouseRepository.save(guesthouse);
	    return guesthouse.getId();
	}
	
	public List<GuesthouseRepository.GuesthouseSummary> getMyGuesthouses(Integer hostId) {
		return guesthouseRepository.findMyGuesthouses(hostId);
	}

	@Transactional
	public void deleteGuesthouse(Integer guesthouseId, Integer hostId) {
		Guesthouse guesthouse = guesthouseRepository.findById(guesthouseId)
                .orElseThrow(() -> new IllegalArgumentException("Guesthouse not found"));
		
		if(!guesthouse.getHost().getId().equals(hostId)) {
			throw new IllegalArgumentException("You are not the owner of this guesthouse");
		}
		
		guesthouseRepository.delete(guesthouse);	
	}
	
}
