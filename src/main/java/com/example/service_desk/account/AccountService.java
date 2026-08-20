package com.example.service_desk.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount register(String login, String rawPassword) {
        if (userAccountRepository.existsByLogin(login)) {
            throw new DuplicateLoginException("duplicate login");
        }

        String hash = passwordEncoder.encode(rawPassword);
        UserAccount userAccount = new UserAccount(
                login,
                hash,
                UserRole.STUDENT
        );
        return userAccountRepository.save(userAccount);
    }

    @Transactional(readOnly = true)
    public UserAccount getByLogin(String login) {
        return userAccountRepository.findByLogin(login).orElseThrow(
                () -> new IllegalStateException("user account not found")
        );
    }
}
