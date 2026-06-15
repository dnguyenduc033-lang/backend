package com.phegondev.inventorymgtsystem.services.implement;

import com.phegondev.inventorymgtsystem.dtos.LoginRequest;
import com.phegondev.inventorymgtsystem.dtos.RegisterRequest;
import com.phegondev.inventorymgtsystem.dtos.Response;
import com.phegondev.inventorymgtsystem.dtos.UserDTO;
import com.phegondev.inventorymgtsystem.dtos.UserTreeNodeDTO;
import com.phegondev.inventorymgtsystem.enums.UserRole;
import com.phegondev.inventorymgtsystem.exceptions.BadRequestException;
import com.phegondev.inventorymgtsystem.exceptions.ConflictException;
import com.phegondev.inventorymgtsystem.exceptions.InvalidCredentialsException;
import com.phegondev.inventorymgtsystem.exceptions.NotFoundException;
import com.phegondev.inventorymgtsystem.models.User;
import com.phegondev.inventorymgtsystem.repositories.UserRepository;
import com.phegondev.inventorymgtsystem.security.JwtUtils;
import com.phegondev.inventorymgtsystem.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;


    @Override
    public Response registerUser(RegisterRequest registerRequest) {

        UserRole role = UserRole.MANAGER;

        if (registerRequest.getRole() != null) {
            role = registerRequest.getRole();
        }

        User userToSave = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(role)
                .build();

        if (registerRequest.getManagerId() != null) {
            User manager = userRepository.findById(registerRequest.getManagerId())
                    .orElseThrow(() -> new NotFoundException("Manager Not Found"));
            validateManagerForRole(role, manager);
            userToSave.setManager(manager);
        } else if (role != UserRole.ADMIN) {
            throw new BadRequestException("Manager is required for this role");
        }

        userRepository.save(userToSave);

        return Response.builder()
                .status(200)
                .message("User was successfully registered")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email Not Found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password Does Not Match");
        }
        String token = jwtUtils.generateToken(user.getEmail());

        return Response.builder()
                .status(200)
                .message("User Logged in Successfully")
                .role(user.getRole())
                .token(token)
                .expirationTime("6 months")
                .build();
    }

    @Override
    public Response getAllUsers() {

        List<User> users = userRepository.findAllWithManagerOrderedByName();

        users.forEach(user -> {
            user.setTransactions(null);
            user.setSubordinates(null);
        });

        List<UserDTO> userDTOS = users.stream()
                .map(this::toUserDTO)
                .toList();

        return Response.builder()
                .status(200)
                .message("success")
                .users(userDTOS)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not Found"));

        user.setTransactions(null);

        return user;
    }

    @Override
    public Response getCurrentUserProfile() {
        User user = getCurrentLoggedInUser();

        return Response.builder()
                .status(200)
                .message("success")
                .user(toUserDTO(user))
                .build();
    }

    @Override
    public Response getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        return Response.builder()
                .status(200)
                .message("success")
                .user(toUserDTO(user))
                .build();
    }

    @Override
    public Response updateUser(Long id, UserDTO userDTO) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        User currentUser = getCurrentLoggedInUser(); // Lấy tài khoản đang gửi request

        // Chặn tài khoản cấp thấp cố tình cập nhật hồ sơ người khác
        if (currentUser.getRole() != UserRole.ADMIN && !existingUser.getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa hồ sơ của người khác!");
        }

        // Chặn tài khoản cấp thấp tự nâng quyền (leo thang đặc quyền)
        if (currentUser.getRole() != UserRole.ADMIN) {
            if (userDTO.getRole() != null || userDTO.getManagerId() != null || Boolean.TRUE.equals(userDTO.getClearManager())) {
                throw new BadRequestException("Bạn không có quyền thay đổi vai trò hoặc người quản lý!");
            }
        }

        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getPhoneNumber() != null) existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if (userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());

        if (Boolean.TRUE.equals(userDTO.getClearManager())) {
            existingUser.setManager(null);
        } else if (userDTO.getManagerId() != null) {
            assignManager(existingUser, id, userDTO.getManagerId());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            // Chặn Admin hoặc bất kỳ ai đổi mật khẩu của người khác
            if (!existingUser.getId().equals(currentUser.getId())) {
                throw new BadRequestException("Hành động bị từ chối: Bất kể vai trò nào cũng chỉ được đổi mật khẩu của chính mình!");
            }
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("User successfully updated")
                .build();
    }

    @Override
    public Response deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (userRepository.existsByManagerId(id)) {
            throw new ConflictException("Cannot delete user with direct reports. Reassign subordinates first.");
        }

        userRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("User successfully Deleted")
                .build();

    }

    @Override
    public Response getUserTransactions(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        if (userDTO.getTransactions() != null && !userDTO.getTransactions().isEmpty()) {
            userDTO.getTransactions().forEach(transactionDTO -> {
                transactionDTO.setUser(null);
                transactionDTO.setSupplier(null);
            });
        }

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public Response getOrgTree() {
        List<User> allUsers = userRepository.findAllWithManagerOrderedByName();
        Map<Long, List<User>> childrenByManagerId = groupByManagerId(allUsers);

        List<UserTreeNodeDTO> tree = allUsers.stream()
                .filter(user -> user.getManager() == null)
                .map(root -> toTreeNode(root, childrenByManagerId))
                .toList();

        return Response.builder()
                .status(200)
                .message("success")
                .userTree(tree)
                .build();
    }

    @Override
    public Response getUserChildren(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        List<UserTreeNodeDTO> children = userRepository.findByManagerIdOrderByNameAsc(id).stream()
                .map(user -> toTreeNode(user, Map.of()))
                .toList();

        return Response.builder()
                .status(200)
                .message("success")
                .userTree(children)
                .build();
    }

    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setTransactions(null);

        if (user.getManager() != null) {
            userDTO.setManagerId(user.getManager().getId());
            userDTO.setManagerName(user.getManager().getName());
        }

        return userDTO;
    }

    private Map<Long, List<User>> groupByManagerId(List<User> users) {
        return users.stream()
                .filter(user -> user.getManager() != null)
                .collect(Collectors.groupingBy(user -> user.getManager().getId()));
    }

    private UserTreeNodeDTO toTreeNode(User user, Map<Long, List<User>> childrenByManagerId) {
        List<User> directReports = childrenByManagerId.getOrDefault(user.getId(), List.of());

        return UserTreeNodeDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .managerId(user.getManager() != null ? user.getManager().getId() : null)
                .hasChildren(!directReports.isEmpty())
                .children(directReports.isEmpty()
                        ? new ArrayList<>()
                        : directReports.stream()
                                .map(child -> toTreeNode(child, childrenByManagerId))
                                .toList())
                .build();
    }

    private void validateManagerForRole(UserRole role, User manager) {
        if (role == UserRole.MANAGER && manager.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Manager role must report to an ADMIN");
        }
        if (role == UserRole.STAFF && manager.getRole() == UserRole.STAFF) {
            throw new BadRequestException("Staff must report to a MANAGER or ADMIN");
        }
    }

    private void assignManager(User existingUser, Long userId, Long managerId) {
        if (managerId.equals(userId)) {
            throw new BadRequestException("User cannot be their own manager");
        }

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager Not Found"));

        if (createsManagementCycle(userId, managerId)) {
            throw new BadRequestException("Manager assignment would create a cycle in the org chart");
        }

        existingUser.setManager(manager);
    }

    private boolean createsManagementCycle(Long userId, Long managerId) {
        Long currentManagerId = managerId;
        Set<Long> visited = new HashSet<>();

        while (currentManagerId != null) {
            if (currentManagerId.equals(userId)) {
                return true;
            }
            if (!visited.add(currentManagerId)) {
                return false;
            }
            currentManagerId = userRepository.findById(currentManagerId)
                    .map(user -> user.getManager() != null ? user.getManager().getId() : null)
                    .orElse(null);
        }

        return false;
    }
}
