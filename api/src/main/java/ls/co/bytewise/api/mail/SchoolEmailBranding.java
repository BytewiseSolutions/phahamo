package ls.co.bytewise.api.mail;

public record SchoolEmailBranding(
        String name,
        String contactEmail,
        byte[] logoContents,
        String logoContentType
) {
    public static SchoolEmailBranding generic() {
        return new SchoolEmailBranding("Phahamo", null, null, null);
    }
}
