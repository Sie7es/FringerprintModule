package victordev.es.fingerprintcomponent.interactors;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import victordev.es.fingerprintcomponent.R;
import victordev.es.fingerprintcomponent.fingerprint.FingerprintHandler;
import victordev.es.fingerprintcomponent.helpers.FingerprintHelper;
import victordev.es.fingerprintcomponent.interfaces.FingerprintInteractorInterface;
import victordev.es.fingerprintcomponent.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintcomponent.presenters.FingerprintPresenter;

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


