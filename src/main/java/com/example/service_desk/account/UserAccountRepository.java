package com.example.service_desk.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByLogin(String login);

    boolean existsByLogin(String login);
}
