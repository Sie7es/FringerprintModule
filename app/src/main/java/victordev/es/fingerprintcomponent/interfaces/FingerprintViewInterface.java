package victordev.es.fingerprintcomponent.interfaces;

/**
 * Created by victor on 27/8/16.
 */

public interface FingerprintViewInterface {
    void showFingerprintMessage(CharSequence text);
    void authenticationSucceded();
    void authenticationFailed();
    void navigateToNext();
}
