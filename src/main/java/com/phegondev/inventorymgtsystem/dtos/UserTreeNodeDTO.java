package com.phegondev.inventorymgtsystem.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.inventorymgtsystem.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTreeNodeDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Long managerId;
    private boolean hasChildren;
    @Builder.Default
    private List<UserTreeNodeDTO> children = new ArrayList<>();
}
