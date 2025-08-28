package com.backend.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guesthouse")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Guesthouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(nullable = false)
    private Double rating;
    
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "photo_id", length = 100)
    private Integer photoId;

    @Column(name = "room_count", nullable = false)
    private Integer roomCount;
    
    // Relationships
    @OneToMany(mappedBy = "guesthouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> roomList = new ArrayList<>();
    
    public void addRoom(Room room) {
        room.setGuesthouse(this);
        this.roomList.add(room);
    }
    
    public void updateRating(Double newRating) {
    		this.rating = newRating;
    }
}