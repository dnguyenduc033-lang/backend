package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.NewsRequest;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.models.News; // Bạn cần tạo model này nhé
import com.phegondev.inventorymgtsystem.repositories.NewsRepository; // Bạn cần tạo repo này
import com.phegondev.inventorymgtsystem.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public Response getAllNews() {
        List<News> newsList = newsRepository.findAll();
        return Response.builder()
                .status(200)
                .message("Success")
                .newsList(newsList) // Lưu ý: Bạn cần thêm trường newsList vào class Response
                .build();
    }

    @Override
    public Response createNews(NewsRequest newsRequest) {
        News news = News.builder()
                .title(newsRequest.getTitle())
                .content(newsRequest.getContent())
                .author(newsRequest.getAuthor())
                .build();

        newsRepository.save(news);
        return Response.builder()
                .status(200)
                .message("News created successfully")
                .build();
    }
}