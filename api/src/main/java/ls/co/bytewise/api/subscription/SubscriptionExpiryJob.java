package ls.co.bytewise.api.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionExpiryJob.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionExpiryJob(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void expireSubscriptions() {
        log.info("Running subscription expiry check");
        subscriptionService.runExpiryCheck();
    }
}