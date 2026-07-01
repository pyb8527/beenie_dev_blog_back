package com.beenie.backend.web.feed;

import com.beenie.backend.application.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping(value = "/rss.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> rss() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(feedService.generateRss());
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(feedService.generateSitemap());
    }
}
