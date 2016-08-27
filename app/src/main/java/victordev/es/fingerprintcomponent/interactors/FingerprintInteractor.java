package victordev.es.fingerprintcomponent.interactors;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.widget.Toast;

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

import victordev.es.fingerprintcomponent.FingerPrint;
import victordev.es.fingerprintcomponent.R;
import victordev.es.fingerprintcomponent.fingerprint.FingerprintHandler;
import victordev.es.fingerprintcomponent.interfaces.FingerprintInteractorInterface;
import victordev.es.fingerprintcomponent.interfaces.FingerprintPresenterInterface;
import victordev.es.fingerprintcomponent.interfaces.FingerprintViewInterface;
import victordev.es.fingerprintcomponent.presenters.FingerprintPresenter;

/**
 * Created by victor on 27/8/16.
 */

public class FingerprintInteractor implements FingerprintInteractorInterface {
    private static final String KEY_STORE_INSTANCE_NAME = "AndroidKeyStore)";

    private FingerprintPresenter mFingerprintPresenter;
    private Context mContext;
    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private FingerprintManager.CryptoObject mCryptoObject;

    public FingerprintInteractor(FingerprintPresenterInterface presenter, Context context) {
        mFingerprintPresenter = (FingerprintPresenter) presenter;
        mContext = context;
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void checkSecuritySettings() {
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        mFingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.USE_FINGERPRINT) // this might need massaged to 'android.permission.USE_FINGERPRINT'
                != PackageManager.PERMISSION_GRANTED) {
            Log.d ("TEST", "You don't have permission");
        }
        /*if (!mKeyguardManager.isKeyguardSecure()) {
            mFingerprintPresenter.lockScreenSecurityNotEnable();
            return;
        }*/

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
            FingerprintHandler helper = new FingerprintHandler(mContext, this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            mKeyStore = KeyStore.getInstance(KEY_STORE_INSTANCE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mKeyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    KEY_STORE_INSTANCE_NAME);
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    mContext.getString(R.string.text_get_key_store_instance_failed), e);
        }


        try {
            mKeyStore.load(null);
            mKeyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_STORE_INSTANCE_NAME,
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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            mCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_STORE_INSTANCE_NAME,
                    null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(mContext.getString(R.string.text_cipher_error), e);
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
}


