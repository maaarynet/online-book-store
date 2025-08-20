package com.example.bookstore.service;

import com.example.bookstore.dto.user.UserRegistrationRequestDto;
import com.example.bookstore.dto.user.UserResponseDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.RegistrationException;
import com.example.bookstore.mapper.UserMapper;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.RoleName;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.role.RoleRepository;
import com.example.bookstore.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "User with this email already exists: " + requestDto.getEmail());
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role userRole = roleRepository.findRoleByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role for user: "
                        + user.getUsername() + ", role: " + RoleName.ROLE_USER));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        shoppingCartService.createShoppingCart(user);
        return userMapper.toResponseDto(user);
    }
}
