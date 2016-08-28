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
import victordev.es.fingerprintcomponent.interfaces.FingerprintInteractorInterface;
import victordev.es.fingerprintcomponent.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintcomponent.presenters.FingerprintPresenter;

/**
 * Created by victor on 27/8/16.
 */

public class FingerprintInteractor implements FingerprintInteractorInterface {
    private static final String KEY_NAME = "key_for_example)";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";

    private FingerprintPresenter mFingerprintPresenter;
    private Context mContext;
    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private FingerprintManager.CryptoObject mCryptoObject;
    FingerprintHandler mHelper;

    public FingerprintInteractor(FingerprintPresenterInterface presenter, Context context) {
        mFingerprintPresenter = (FingerprintPresenter) presenter;
        mContext = context;
    }



    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void checkSecuritySettings() {
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(KeyguardManager.class);
        mFingerprintManager = (FingerprintManager) mContext.getSystemService(FingerprintManager.class);

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.USE_FINGERPRINT) // this might need massaged to 'android.permission.USE_FINGERPRINT'
                != PackageManager.PERMISSION_GRANTED) {
            Log.d ("DEBUG", "No tienes permisos");
        }
        if (!mKeyguardManager.isKeyguardSecure()) {
            mFingerprintPresenter.lockScreenSecurityNotEnable();
            return;
        }

        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            mFingerprintPresenter.fingerprintAuthenticationNotEnabled();

            return;
        }

        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            mFingerprintPresenter.hasNotFingersEnrolled();
            return;
        }

        generateKey();

        if (cipherInit()) {
            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);
            mHelper = new FingerprintHandler(mContext, this);
            mHelper.startAuth(mFingerprintManager, mCryptoObject);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    mContext.getString(R.string.text_get_key_store_instance_failed), e);
        }

        try {
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        KeyguardManager keyguardManager = mContext.getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = mContext.getSystemService(FingerprintManager.class);
        createKey(KEY_NAME, true);
        //createKey(KEY_NAME_NOT_INVALIDATED, false);
        /*try {
            mKeyStore.load(null);
            mKeyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        //Las keys se usan para saber si se ha dado de alta alguna huella nueva
        try {
            mKeyStore.load(null);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPause() {
        if (mHelper != null) {
            mHelper.stopListening();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResume() {
        if (mHelper != null && mFingerprintManager != null && mCryptoObject != null) {
            mHelper.startAuth(mFingerprintManager, mCryptoObject);
        }
    }
}


