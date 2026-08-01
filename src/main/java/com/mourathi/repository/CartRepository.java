package com.mourathi.repository;

import com.mourathi.entity.Cart;
import com.mourathi.entity.CartStatus;
import com.mourathi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    List<Cart> findByUserId(Long userId);
    List<Cart> findByStatus(CartStatus status);
//    @Query("SELECT c FROM Cart c JOIN FETCH c.user WHERE c.username = :username")
//    Optional<Cart> findByUserName(String userName);
}
