package victordev.es.fingerprintmodule.presenters;

import android.os.Build;
import android.support.annotation.RequiresApi;

import victordev.es.fingerprintmodule.views.FingerPrint;
import victordev.es.fingerprintmodule.R;
import victordev.es.fingerprintmodule.interactors.FingerprintInteractor;
import victordev.es.fingerprintmodule.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintmodule.interfaces.FingerprintViewInterface;
import victordev.es.fingerprintmodule.interfaces.OnFingerprintEvents;

/**
 * Created by victor on 27/8/16.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintPresenter implements FingerprintPresenterInterface, OnFingerprintEvents {
    private FingerPrint mFingerpringView;
    private FingerprintInteractor mFingerprintInteractor;

    public FingerprintPresenter(FingerprintViewInterface fingerprintViewInterface) {
        mFingerpringView = (FingerPrint) fingerprintViewInterface;
        mFingerprintInteractor = new FingerprintInteractor(this, mFingerpringView.getContext());


        mFingerprintInteractor.startAuthentication();
    }


    @Override
    public void lockScreenSecurityNotEnable() {
        mFingerpringView.showFingerprintMessage(mFingerpringView.getContext().getString(R.string.text_finger_lock_screen_not_enabled));
    }

    @Override
    public void fingerprintAuthenticationNotEnabled() {
        mFingerpringView.showFingerprintMessage(mFingerpringView.getContext().getString(R.string.text_fingerprint_permission_not_enabled));
    }

    @Override
    public void hasNotFingersEnrolled() {
        mFingerpringView.showFingerprintMessage(mFingerpringView.getContext().getString(R.string.text_fingerprint_has_not_fingers_enrolled));
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

    @Override
    public void onPause() {
        mFingerprintInteractor.onPause();
    }

    @Override
    public void onResume() {
        mFingerprintInteractor.onResume();
    }
}
