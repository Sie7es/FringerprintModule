package victordev.es.fingerprintcomponent.presenters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import victordev.es.fingerprintcomponent.FingerPrint;
import victordev.es.fingerprintcomponent.R;
import victordev.es.fingerprintcomponent.interactors.FingerprintInteractor;
import victordev.es.fingerprintcomponent.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintcomponent.interfaces.FingerprintViewInterface;
import victordev.es.fingerprintcomponent.interfaces.OnFingerprintEvents;

/**
 * Created by victor on 27/8/16.
 */

public class FingerprintPresenter implements FingerprintPresenterInterface, OnFingerprintEvents {
    private FingerPrint mFingerpringView;
    private FingerprintInteractor mFingerprintInteractor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintPresenter(FingerprintViewInterface fingerprintViewInterface) {
        mFingerpringView = (FingerPrint) fingerprintViewInterface;
        mFingerprintInteractor = new FingerprintInteractor(this, mFingerpringView);

        mFingerprintInteractor.checkSecuritySettings();
    }


    @Override
    public void lockScreenSecurityNotEnable() {
        mFingerpringView.showFingerprintMessage(mFingerpringView.getString(R.string.text_finger_lock_screen_not_enabled));

    }

    @Override
    public void fingerprintAuthenticationNotEnabled() {
        mFingerpringView.showFingerprintMessage(mFingerpringView.getString(R.string.text_fingerprint_permission_not_enabled));
    }

    @Override
    public void hasNotFingersEnrolled() {
        mFingerpringView.showFingerprintMessage(mFingerpringView.getString(R.string.text_fingerprint_has_not_fingers_enrolled));
    }

    @Override
    public void onAuthenticationFailed() {
        mFingerpringView.authenticationFailed();
    }

    @Override
    public void onAuthenticationSucceeded() {
        mFingerpringView.authenticationSucceded();
    }

    @Override
    public void onAuthenticationHelp(CharSequence helpString) {
        mFingerpringView.showFingerprintMessage(helpString.toString());
    }
}
