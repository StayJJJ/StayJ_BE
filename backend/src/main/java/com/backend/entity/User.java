package com.backend.entity;

import java.util.List;

import com.backend.dto.response.UserInfoDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 10)
    private String role = "GUEST"; // 'HOST' or 'GUEST'

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "host")
    private List<Guesthouse> guesthouses;

    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations;
    
    public void updateUserInfo(String username, String phoneNumber, String password) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password; // 암호화는 Service 단에서
    }
    
    public UserInfoDto toDto() {
        return new UserInfoDto(
                this.id,
                this.username,
                this.loginId,
                this.role,
                this.phoneNumber
        );
    }
}
