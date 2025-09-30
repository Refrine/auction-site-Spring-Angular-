package com.example.auction.services;

import com.example.auction.dto.UserDto;
import com.example.auction.models.User;
import com.example.auction.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserDto> getAllUsers(){
        List<User> users = userRepo.findAll();

        return users.stream().map(user -> new UserDto(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id){
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("not found"));
        return new  UserDto(user.getId(),user.getUsername());
    }
}
