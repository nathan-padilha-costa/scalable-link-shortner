package com.linkshortener.demo.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.linkshortener.demo.model.Link;
import com.linkshortener.demo.model.LinkRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final StringRedisTemplate redisTemplate;

    private String generateShortCode(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Link createShortLink(String longUrl) {

        String shortCode = generateShortCode();

        Link newLink = new Link();
        newLink.setShortCode(shortCode);
        newLink.setLongURL(longUrl);

        redisTemplate.opsForValue().set(shortCode, longUrl, 10, TimeUnit.MINUTES);

        return newLink;

    }
    
    @Transactional
    public Optional<String> getOriginalUrl(String shortCode) {

        String cachedUrl =redisTemplate.opsForValue().get(shortCode);

        if (cachedUrl != null){

            redisTemplate.opsForValue().increment("count:" + shortCode);
            redisTemplate.opsForSet().add("dirty_links", shortCode);


            
            return Optional.of(cachedUrl);
        }
        
        Optional<Link> linkOptional = linkRepository.findById(shortCode);

        if (linkOptional.isPresent()){
            Link link = linkOptional.get();

            redisTemplate.opsForValue().set(shortCode, link.getLongURL(), 10, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set("count:" + shortCode, String.valueOf(link.getClickCount()+1));

            linkRepository.save(link);

            return Optional.of(link.getLongURL());
        }

        return Optional.empty();

    }
}
