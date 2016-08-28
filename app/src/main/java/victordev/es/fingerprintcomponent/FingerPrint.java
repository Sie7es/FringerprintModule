package victordev.es.fingerprintcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import victordev.es.fingerprintcomponent.interfaces.FingerprintViewInterface;
import victordev.es.fingerprintcomponent.presenters.FingerprintPresenter;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrint extends RelativeLayout implements FingerprintViewInterface {
    private static final String REX_HEX_COLOR = "^#[0-9a-fA-F]{6}$|^#[0-9a-fA-F]{8}$";

    private CoordinatorLayout mCoordinatorLayout;
    private TextView mFingerPrintText;
    private FingerprintPresenter mFingerprintPresenter;
    private Context mContext;
    private OnFingerprintComponentCallback mFingerprintCallback;

    public void setFingerprintBackgroundColor(String fingerprintBackgroundColor) {
        if (checkHexColor(fingerprintBackgroundColor)) {
            mCoordinatorLayout.setBackgroundColor(Color.parseColor(fingerprintBackgroundColor));
        }
    }


    public interface OnFingerprintComponentCallback {
        void fingerFound();
        void fingerNotFound();
    }

    public void setFingerprintComponentCallback(OnFingerprintComponentCallback callback) {
        this.mFingerprintCallback = callback;
    }

    public FingerPrint(Context context) {
        super(context);
        setup(context);
    }

    private void setup(Context context) {
        mContext = context;

        View view = inflate(mContext, R.layout.finger_print, null);

        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.fp_coordinator_layout);
        mFingerPrintText = (TextView) view.findViewById(R.id.finger_text);

        mFingerprintPresenter = new FingerprintPresenter(this);
    }

    public FingerPrint(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
        init(attrs);
    }

    public FingerPrint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
        init(attrs);
    }

    public FingerPrint(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.FingerprintComponent, 0, 0);

            //TEXT FINGERPRINT
            setFingerprintText(array.getString(R.styleable.FingerprintComponent_text));

            //BACKGROUND COLOR
            setFingerprintBackgroundColor(array.getString(R.styleable.FingerprintComponent_background_color));

            //TEXT SIZE
            setFingerprintTextSize(array.getInt(R.styleable.FingerprintComponent_font_size,
                    (int) mContext.getResources().getDimension(R.dimen.fingerprint_default_text_size)));

            //TEXT FOR FINGERPRINT FOUND
            String correctText = array.getString(R.styleable.FingerprintComponent_finger_print_found).length() > 0 ?
                    array.getString(R.styleable.FingerprintComponent_finger_print_found) :
                    mContext.getString(R.string.text_finger_found);

            //TEXT FOR FINGERPRINT NOT FOUND
            String fingerFrintNotFound = array.getString(R.styleable.FingerprintComponent_fingerprint_not_found).length() > 0 ?
                    array.getString(R.styleable.FingerprintComponent_fingerprint_not_found) :
                    mContext.getString(R.string.text_finger_not_found);

        }
    }

    private void setFingerprintTextSize(int fontSize) {
        mFingerPrintText.setText(fontSize);
    }


    @Override
    public void showFingerprintMessage(CharSequence text) {
        Snackbar.make(mCoordinatorLayout, text.toString(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void authenticationSucceded() {
        Snackbar.make(mCoordinatorLayout, mContext.getString(R.string.text_finger_found), Snackbar.LENGTH_LONG).show();
        navigateToNext();
    }

    @Override
    public void authenticationFailed() {
        Snackbar.make(mCoordinatorLayout, mContext.getString(R.string.text_finger_not_found), Snackbar.LENGTH_LONG).show();
        ((Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE)).vibrate(800);
    }

    @Override
    public void navigateToNext() {

    }

    protected void onPause() {
        mFingerprintPresenter.onPause();
    }

    protected void onResume() {
        mFingerprintPresenter.onResume();

    }


    public void setFingerprintText(String fingerprintText) {
        mFingerPrintText.setText(fingerprintText);
    }

    private boolean checkHexColor(String hexColor) {
        if (hexColor == null) {
            return false;
        }

        Pattern r = Pattern.compile(REX_HEX_COLOR);
        Matcher m = r.matcher(hexColor);

        return m.find();
    }
}
