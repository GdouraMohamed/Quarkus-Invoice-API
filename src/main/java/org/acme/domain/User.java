package org.acme.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity(name = "app_user")
public class User extends PanacheEntity {
    public String username;
    public String password; // clair en dev, hash plus tard
    public String role;
}
