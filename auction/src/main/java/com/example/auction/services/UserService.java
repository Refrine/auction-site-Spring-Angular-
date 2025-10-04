package com.example.auction.services;

import com.example.auction.dto.UserDto;
import com.example.auction.models.User;
import com.example.auction.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getActiveUsers() {
        return userRepo.findByEnabledTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDto(user);
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return convertToDto(user);
    }

    public UserDto createUser(UserDto userDto) {

        if (userRepo.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDto.getUsername());
        }


        if (userRepo.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }


        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }


        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(null);

        User savedUser = userRepo.save(user);
        return convertToDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found " + id));


        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (userRepo.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Email : " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }

        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }

        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }

        if (userDto.getAddress() != null) {
            user.setAddress(userDto.getAddress());
        }


        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                throw new RuntimeException("Passwords");
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepo.save(user);
        return convertToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not id: " + id));
        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    public void enableUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not id: " + id));
        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    public boolean validateUserCredentials(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);
        return user.isPresent() &&
                user.get().isEnabled() &&
                passwordEncoder.matches(password, user.get().getPassword());
    }

    public void updateLastLogin(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not  id: " + userId));
        user.setLastLogin(LocalDateTime.now());
        userRepo.save(user);
    }

    public UserDto changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not  id: " + userId));


        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password ");
        }


        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New passwords ");
        }


        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepo.save(user);

        return convertToDto(updatedUser);
    }

    public List<UserDto> searchUsers(String query) {
        return userRepo.findByUsernameContainingOrEmailContaining(query, query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public UserStatistics getUserStatistics(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not  id: " + userId));

        UserStatistics stats = new UserStatistics();
        stats.setTotalProducts((long) user.getProducts().size());
        stats.setActiveAuctions(user.getProducts().stream()
                .filter(p -> p.getAuction() != null && p.getAuction().getActive())
                .count());
        stats.setTotalBids((long) user.getBids().size());
        stats.setWinningBids(user.getBids().stream()
                .filter(bid -> bid.getAuction() != null &&
                        !bid.getAuction().getActive() &&
                        bid.getAuction().getWinner() != null &&
                        bid.getAuction().getWinner().getId().equals(userId))
                .count());

        return stats;
    }


    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Дополнительная статистика
        dto.setTotalProducts((long) user.getProducts().size());
        dto.setTotalBids((long) user.getBids().size());

        return dto;
    }


    public static class UserStatistics {
        private Long totalProducts;
        private Long activeAuctions;
        private Long totalBids;
        private Long winningBids;


        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }

        public Long getActiveAuctions() { return activeAuctions; }
        public void setActiveAuctions(Long activeAuctions) { this.activeAuctions = activeAuctions; }

        public Long getTotalBids() { return totalBids; }
        public void setTotalBids(Long totalBids) { this.totalBids = totalBids; }

        public Long getWinningBids() { return winningBids; }
        public void setWinningBids(Long winningBids) { this.winningBids = winningBids; }
    }
}