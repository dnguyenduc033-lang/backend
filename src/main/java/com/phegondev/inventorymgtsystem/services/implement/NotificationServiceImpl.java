package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.NotificationDTO;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.exceptions.NotFoundException;
import com.phegondev.inventorymgtsystem.models.Notification;
import com.phegondev.inventorymgtsystem.models.User;
import com.phegondev.inventorymgtsystem.repositories.NotificationRepository;
import com.phegondev.inventorymgtsystem.services.NotificationService;
import com.phegondev.inventorymgtsystem.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    public void createNotification(User recipient, String title, String message, String type, String link) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .link(link)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public Response getMyNotifications() {
        User currentUser = userService.getCurrentLoggedInUser();
        List<Notification> notifications = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUser.getId());
        return Response.builder()
                .status(200)
                .message("success")
                .notifications(notifications.stream().map(this::toDTO).collect(Collectors.toList()))
                .build();
    }

    @Override
    public Response markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông báo"));
        notification.setRead(true);
        notificationRepository.save(notification);
        return Response.builder().status(200).message("Đã đánh dấu đã đọc.").build();
    }

    @Override
    public Response markAllAsRead() {
        User currentUser = userService.getCurrentLoggedInUser();
        List<Notification> unread = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(currentUser.getId());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        return Response.builder().status(200).message("Đã đánh dấu tất cả đã đọc.").build();
    }

    @Override
    public Response getUnreadCount() {
        User currentUser = userService.getCurrentLoggedInUser();
        long count = notificationRepository.countByRecipientIdAndIsReadFalse(currentUser.getId());
        return Response.builder().status(200).message(String.valueOf(count)).build();
    }

    private NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setLink(n.getLink());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}