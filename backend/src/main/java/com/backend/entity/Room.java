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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String name;

    @Column(nullable = false)
    private Integer capacity;
    
    @Column(nullable = false)
    private Integer price;
    
    @Column(name = "photo_id", length = 100)
    private Integer photoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guesthouse_id", nullable = false)
    private Guesthouse guestHouse;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
    
    public void setGuestHouse(Guesthouse guesthouse) {
    	this.guestHouse = guesthouse;
    }
}
