package victordev.es.fingerprintmodule.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
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

import victordev.es.fingerprintmodule.R;
import victordev.es.fingerprintmodule.fingerprint.FingerprintHandler;
import victordev.es.fingerprintmodule.interfaces.FingerprintHelperInterface;
import victordev.es.fingerprintmodule.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintmodule.presenters.FingerprintPresenter;

/**
 * Created by victor on 28/8/16.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHelper implements FingerprintHelperInterface{
    private static final int FINGERPRINT_PERMISSION_REQUEST_CODE = 1234;
    private static final String KEY_NAME = "key_for_example)";


    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private FingerprintManager.CryptoObject mCryptoObject;
    FingerprintHandler mHelper;

    private FingerprintPresenter mFingerprintPresenter;
    private Context mContext;

    public FingerprintHelper(FingerprintPresenterInterface presenter, Context context) {
        mFingerprintPresenter = (FingerprintPresenter) presenter;
        mContext = context;

    }

    public void start() {
        checkSecuritySettings();
    }

    public void checkSecuritySettings() {
        ((Activity) mContext).requestPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, FINGERPRINT_PERMISSION_REQUEST_CODE);

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
    }

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

    @Override
    public void onPause() {
        if (mHelper != null) {
            mHelper.stopListening();
        }
    }

    @Override
    public void onResume() {
        if (mHelper != null && mFingerprintManager != null && mCryptoObject != null) {
            mHelper.startAuth(mFingerprintManager, mCryptoObject);
        }
    }
}
