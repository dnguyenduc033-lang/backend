package com.phegondev.inventorymgtsystem.dtos;

import lombok.Data;

@Data
public class NewsRequest {
    private String title;
    private String content;
    private String author;
}