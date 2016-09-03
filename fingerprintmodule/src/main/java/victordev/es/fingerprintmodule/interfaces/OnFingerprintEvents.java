package victordev.es.fingerprintmodule.interfaces;

/**
 * Created by victor on 27/8/16.
 */

public interface OnFingerprintEvents {
    void lockScreenSecurityNotEnable();
    void fingerprintAuthenticationNotEnabled();
    void hasNotFingersEnrolled();
    void onAuthenticationFailed();
    void onAuthenticationSucceeded();
    void onAuthenticationHelp(CharSequence helpString);
}
