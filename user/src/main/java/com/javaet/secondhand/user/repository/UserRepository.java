package com.javaet.secondhand.user.repository;

import com.javaet.secondhand.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
