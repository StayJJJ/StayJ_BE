package com.backend.guesthouses.entity;

import java.util.List;

import com.backend.rooms.entity.Room;
import com.backend.users.entity.User;

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
public class GuestHouse {
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

    @Column(length = 255)
    private String address;

    private Double rating;

    @Column(name = "photos_url", length = 100)
    private String photosUrl;

    @Column(name = "room_count")
    private Integer roomCount;

    // Relationships
    @OneToMany(mappedBy = "guestHouse", cascade = CascadeType.ALL)
    private List<Room> roomList;
}
