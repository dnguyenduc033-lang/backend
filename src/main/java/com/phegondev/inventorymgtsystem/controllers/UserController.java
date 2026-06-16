package com.phegondev.inventorymgtsystem.controllers;

import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.dtos.UserDTO;
import com.phegondev.inventorymgtsystem.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/current")
    public ResponseEntity<Response> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @GetMapping("/org-tree")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getOrgTree() {
        return ResponseEntity.ok(userService.getOrgTree());
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<Response> getUserAndTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserTransactions(userId));
    }

    @GetMapping("/{id}/children")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getUserChildren(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserChildren(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> resetPasswordByAdmin(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.resetPasswordByAdmin(id, userDTO.getPassword()));
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<Response> changeOwnPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        return ResponseEntity.ok(userService.changeOwnPassword(id, oldPassword, newPassword));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
