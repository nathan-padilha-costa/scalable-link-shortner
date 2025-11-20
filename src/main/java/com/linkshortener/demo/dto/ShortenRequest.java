package com.linkshortener.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class ShortenRequest {

    @NotBlank(message = "Long URL is required")
    private String longUrl;
    
}
