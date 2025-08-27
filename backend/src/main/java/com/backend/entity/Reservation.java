package com.backend.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "people_count", nullable = false)
    private Integer peopleCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public boolean isOverlapping(LocalDate checkIn, LocalDate checkOut) {
        // 체크아웃 날짜와 체크인 날짜가 같은 경우는 겹치지 않음
        // 예: 기존 예약 12/15~12/18, 새 예약 12/18~12/20 → 겹치지 않음
    	
        return !(this.checkOutDate.isBefore(checkIn) || this.checkOutDate.equals(checkIn) || 
                 this.checkInDate.isAfter(checkOut) || this.checkInDate.equals(checkOut));
    }

    // Relationships
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Review review;
    
    public void deleteReview() {
    		this.review = null;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

	public void setReview(Review review) {
		this.review = review;
	}
}