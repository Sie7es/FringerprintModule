package victordev.es.fingerprintcomponent.interfaces;

import victordev.es.fingerprintcomponent.fingerprint.FingerprintHandler;

/**
 * Created by victor on 28/8/16.
 */

public interface FingerprintHelperInterface extends FingerprintHandlerInterface {
    void onPause();
    void onResume();
}
