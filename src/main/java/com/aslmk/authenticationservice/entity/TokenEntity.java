package com.aslmk.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "tokens")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    @Column(unique = true)
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private LocalDateTime expiresAt;
}
