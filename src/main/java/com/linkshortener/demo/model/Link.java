package com.linkshortener.demo.model;


import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "links")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Link {

    @Id
    private String shortCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String longURL;

    @Column(nullable = false)
    private Long clickCount = 0L;

    @Column(nullable = false)
    private Instant createdat = Instant.now();


    
}
