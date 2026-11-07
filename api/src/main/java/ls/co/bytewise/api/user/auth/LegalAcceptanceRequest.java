package ls.co.bytewise.api.user.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegalAcceptanceRequest {
    private boolean termsAccepted;
    private boolean privacyAccepted;
}
