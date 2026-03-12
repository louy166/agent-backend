package com.example.mobilee_backend.auth.repository;


import com.example.mobilee_backend.auth.model.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, Long> {
    Optional<AgentEntity> findByEmail(String email);
    Optional<AgentEntity> findByTelephone(String telephone);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
}