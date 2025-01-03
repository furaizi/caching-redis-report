package org.example.restproject.services;

import org.example.restproject.entities.User;
import org.example.restproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class UserService {

    private static final Duration DELAY = Duration.ofSeconds(10);

    @Autowired
    private UserRepository userRepository;

    @Cacheable(cacheNames = "users", key = "#root.methodName")
    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .delayElements(DELAY);
    }

    @Cacheable(cacheNames = "users", key = "#id")
    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id)
                .delayElement(DELAY);
    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public Mono<User> createUser(User user) {
        return userRepository.save(user)
                .delayElement(DELAY);
    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public Mono<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setPassword(updatedUser.getPassword());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setRole(updatedUser.getRole());
                    existingUser.setBanned(updatedUser.isBanned());
                    return userRepository.save(existingUser);
                })
                .delayElement(DELAY);
    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id)
                .delayElement(DELAY);
    }
}
