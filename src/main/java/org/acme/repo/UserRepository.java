package org.acme.repo;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.User;
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByUserName(String username){
        return find("username",username).firstResult();

     }
}
