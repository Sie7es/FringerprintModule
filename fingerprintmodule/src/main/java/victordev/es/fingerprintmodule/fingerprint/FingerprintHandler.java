package victordev.es.fingerprintmodule.fingerprint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import victordev.es.fingerprintmodule.helpers.FingerprintHelper;

/**
 * Created by victor on 27/8/16.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private FingerprintHelper mFingerprintHelper;

    private CancellationSignal mCancellationSignal;
    private FingerprintManager mFingerprintManager;
    private FingerprintManager.CryptoObject mCryptoObject;

    private Context mAppContext;


    public FingerprintHandler(Context context, FingerprintHelper helper) {
        mAppContext = context;
        mFingerprintHelper = helper;

    }

    /*
        Este método es el primero que se cuando se inicia el proceso de autentificación.
        Se le debe pasar las intancias del FingerprintManager
        y del CryptoObject
     */
    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {

        mFingerprintManager = manager;
        mCryptoObject = cryptoObject;

        mCancellationSignal = new CancellationSignal();

        boolean hasFingerprintPermissions = ActivityCompat.checkSelfPermission(mAppContext,
                Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;

        if (!hasFingerprintPermissions) {
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, this, null);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);

        mFingerprintHelper.onAuthenticationHelp(helpString);
        startAuth(mFingerprintManager, mCryptoObject);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        mFingerprintHelper.onAuthenticationSucceeded();
        startAuth(mFingerprintManager, mCryptoObject);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();

        mFingerprintHelper.onAuthenticationFailed();

        startAuth(mFingerprintManager, mCryptoObject);
    }
}
