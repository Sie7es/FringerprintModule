package victordev.es.fingerprintcomponent.interfaces;

/**
 * Created by victor on 27/8/16.
 */

public interface FingerprintInteractorInterface {
    void checkSecuritySettings();
    void onAuthenticationFailed();
    void onAuthenticationSucceeded();
    void onAuthenticationHelp(CharSequence helpString);
}
