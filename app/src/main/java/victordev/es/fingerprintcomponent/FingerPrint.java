package victordev.es.fingerprintcomponent;

import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import victordev.es.fingerprintcomponent.interfaces.FingerprintViewInterface;
import victordev.es.fingerprintcomponent.presenters.FingerprintPresenter;

public class FingerPrint extends AppCompatActivity implements FingerprintViewInterface{
    private CoordinatorLayout mCoordinatorLayout;
    private FingerprintPresenter mFingerprintPresenter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_print);

        mFingerprintPresenter = new FingerprintPresenter(this);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.fp_coordinator_layout);
    }

    @Override
    public void showFingerprintMessage(CharSequence text) {
        Snackbar.make(mCoordinatorLayout, text.toString(), Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void authenticationSucceded() {
        Snackbar.make(mCoordinatorLayout, getString(R.string.text_finger_not_found), Snackbar.LENGTH_LONG).show();
        navigateToNext();
    }

    @Override
    public void authenticationFailed() {
        Snackbar.make(mCoordinatorLayout, getString(R.string.text_finger_not_found), Snackbar.LENGTH_LONG).show();
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(800);
    }

    @Override
    public void navigateToNext() {

    }
}
