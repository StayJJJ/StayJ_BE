package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// ex) /uploads/2025/08/abc.png 혹은 https://s3.../abc.png
	@Column(nullable = false, length = 1000)
	private String url;
}
