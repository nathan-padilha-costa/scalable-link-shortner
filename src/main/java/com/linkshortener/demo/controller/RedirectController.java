package com.linkshortener.demo.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.linkshortener.demo.service.LinkService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {

        Optional<String> urlOptional = linkService.getOriginalUrl(shortCode);

        if (urlOptional.isPresent()){

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlOptional.get())).build();
        }

        else {
            return ResponseEntity.notFound().build();
        }

    }
    
}
