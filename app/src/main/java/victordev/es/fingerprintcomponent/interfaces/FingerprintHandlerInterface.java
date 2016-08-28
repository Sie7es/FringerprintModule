package victordev.es.fingerprintcomponent.interfaces;

/**
 * Created by victor on 28/8/16.
 */

public interface FingerprintHandlerInterface {
    void onAuthenticationFailed();
    void onAuthenticationSucceeded();
    void onAuthenticationHelp(CharSequence helpString);
    void onPause();
    void onResume();
}
