package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.NewsRequest;
import com.phegondev.inventorymgtsystem.dtos.Response;

public interface NewsService {
    Response getAllNews();
    Response createNews(NewsRequest newsRequest);
}
