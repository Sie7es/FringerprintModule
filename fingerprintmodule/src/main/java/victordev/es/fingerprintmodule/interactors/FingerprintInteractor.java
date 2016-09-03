package victordev.es.fingerprintmodule.interactors;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import victordev.es.fingerprintmodule.helpers.FingerprintHelper;
import victordev.es.fingerprintmodule.interfaces.FingerprintInteractorInterface;
import victordev.es.fingerprintmodule.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintmodule.presenters.FingerprintPresenter;

/**
 * Created by victor on 27/8/16.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintInteractor implements FingerprintInteractorInterface {
    private FingerprintHelper mFingerprintHelper;
    private FingerprintPresenter mFingerprintPresenter;
    private Context mContext;


    public FingerprintInteractor(FingerprintPresenterInterface presenter, Context context) {
        mFingerprintPresenter = (FingerprintPresenter) presenter;
        mContext = context;
    }

    @Override
    public void startAuthentication() {
        mFingerprintHelper = new FingerprintHelper(mFingerprintPresenter, mContext);
        mFingerprintHelper.start();
    }


    @Override
    public void onAuthenticationFailed() {
        mFingerprintPresenter.onAuthenticationFailed();
    }

    @Override
    public void onAuthenticationSucceeded() {
        mFingerprintPresenter.onAuthenticationSucceeded();
    }

    @Override
    public void onAuthenticationHelp(CharSequence helpString) {
        mFingerprintPresenter.onAuthenticationHelp(helpString);
    }

    @Override
    public void onPause() {
        if (mFingerprintHelper != null) {
            mFingerprintHelper.onPause();
        }
    }

    @Override
    public void onResume() {
        if (mFingerprintHelper != null) {
            mFingerprintHelper.onResume();
        }
    }
}


