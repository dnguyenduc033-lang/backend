package com.phegondev.inventorymgtsystem.controllers;

import com.phegondev.inventorymgtsystem.dtos.NewsRequest;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.services.NewsService; // Giả định bạn sẽ tạo service này
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Response> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createNews(@RequestBody NewsRequest newsRequest) {
        return ResponseEntity.ok(newsService.createNews(newsRequest));
    }
}