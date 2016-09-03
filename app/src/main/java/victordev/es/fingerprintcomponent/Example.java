package victordev.es.fingerprintcomponent;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import victordev.es.fingerprintmodule.views.FingerPrint;

/**
 * Created by victor on 29/8/16.
 */

public class Example extends AppCompatActivity implements FingerPrint.OnFingerprintModuleCallback {
    private FingerPrint mFingerPrintView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.example);
        mFingerPrintView = (FingerPrint) findViewById(R.id.finger_print_view);
        mFingerPrintView.setFingerprintModuleCallback(this);
    }

    @Override
    public void fingerFound() {

    }

    @Override
    public void fingerNotFound() {

    }
}
