package com.phegondev.inventorymgtsystem.services;

import com.phegondev.inventorymgtsystem.dtos.LoginRequest;
import com.phegondev.inventorymgtsystem.dtos.RegisterRequest;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.dtos.UserDTO;
import com.phegondev.inventorymgtsystem.models.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();

    Response getCurrentUserProfile();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response resetPasswordByAdmin(Long id, String newPassword);

    Response changeOwnPassword(Long id, String oldPassword, String newPassword);

    Response getUserTransactions(Long id);

    Response getOrgTree();

    Response getUserChildren(Long id);

}