package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.models.User;

public interface NotificationService {
    void createNotification(User recipient, String title, String message, String type, String link);
    Response getMyNotifications();
    Response markAsRead(Long id);
    Response markAllAsRead();
    Response getUnreadCount();
}