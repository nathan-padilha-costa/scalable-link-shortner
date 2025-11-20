package com.linkshortener.demo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.linkshortener.demo.model.Link;
import com.linkshortener.demo.model.LinkRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    private String generateShortCode(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Link createShortLink(String longUrl) {

        String shortCode = generateShortCode();

        Link newLink = new Link();
        newLink.setShortCode(shortCode);
        newLink.setLongURL(longUrl);

        return linkRepository.save(newLink);

    }
    
    @Transactional
    public Optional<String> getOriginalUrl(String shortCode) {

        Optional<Link> linkOptional = linkRepository.findById(shortCode);

        if (linkOptional.isPresent()){
            Link link = linkOptional.get();

            link.setClickCount(link.getClickCount() + 1);

            linkRepository.save(link);

            return Optional.of(link.getLongURL());
        }

        return Optional.empty();

    }
}
