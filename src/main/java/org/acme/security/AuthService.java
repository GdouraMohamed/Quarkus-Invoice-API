package org.acme.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.User;
import org.acme.repo.UserRepository;

@ApplicationScoped
public class AuthService {

    private final UserRepository repo;

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    public User authenticate(String username, String password) {
        User user = repo.findByUserName(username);
        if (user == null) return null;
        if (!user.password.equals(password)) return null;
        return user;
    }
}
