package ls.co.bytewise.api.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    public void blacklist(String token, Instant expiresAt) {
        BlacklistedToken blacklisted = new BlacklistedToken();
        blacklisted.setToken(token);
        blacklisted.setExpiresAt(expiresAt);
        blacklistedTokenRepository.save(blacklisted);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpiredTokens() {
        blacklistedTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}