package com.example.bookstore.repository.role;

import com.example.bookstore.model.Role;
import com.example.bookstore.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByName(RoleName name);
}
