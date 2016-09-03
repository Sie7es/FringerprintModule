package victordev.es.fingerprintmodule.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import victordev.es.fingerprintmodule.R;
import victordev.es.fingerprintmodule.interfaces.FingerprintViewInterface;
import victordev.es.fingerprintmodule.presenters.FingerprintPresenter;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrint extends RelativeLayout implements FingerprintViewInterface {
    private static final String REX_HEX_COLOR = "^#[0-9a-fA-F]{6}$|^#[0-9a-fA-F]{8}$";
    private static final int DEFAULT_VIBRATE_TIME = 800;
    private static final int DEFAULT_TEXT_SIZE = 16;

    private RelativeLayout mMainLayout;
    private TextView mFingerPrintText;
    private FingerprintPresenter mFingerprintPresenter;
    private Context mContext;
    private OnFingerprintModuleCallback mFingerprintCallback;

    private String mFingerprintFoundText;
    private String mFingerprintNotFoundText;
    private int mVibrateTime;
    private int mBackgroundErrorColor;

    public void setFingerprintBackgroundColor(String fingerprintBackgroundColor) {
        if (checkHexColor(fingerprintBackgroundColor)) {
            mBackgroundErrorColor = Color.parseColor(fingerprintBackgroundColor);
        }
    }

    public void setFingerPrintErrorBackgroundColor (String fingerprintErrorBackgroundColor) {
        if (checkHexColor(fingerprintErrorBackgroundColor)) {
            mMainLayout.setBackgroundColor(Color.parseColor(fingerprintErrorBackgroundColor));
        }
    }

    public interface OnFingerprintModuleCallback {
        void fingerFound();
        void fingerNotFound();
    }

    public void setFingerprintModuleCallback(OnFingerprintModuleCallback callback) {
        this.mFingerprintCallback = callback;
    }

    //CONSTRUCTORS
    public FingerPrint(Context context) {
        super(context);

        setup(context);
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


    private void setup(Context context) {
        mContext = context;

        View view = inflate(getContext(), R.layout.finger_print, null);

        mMainLayout = (RelativeLayout) view.findViewById(R.id.finger_print);
        mFingerPrintText = (TextView) view.findViewById(R.id.finger_text);

        mBackgroundErrorColor = getResources().getColor(R.color.default_background_color);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        /*ViewTreeObserver vto = mMainLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/
        addView(view, params);
        mFingerprintPresenter = new FingerprintPresenter(FingerPrint.this);
    }


    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.FingerprintModule, 0, 0);

            //TEXT SIZE
            int fontSize = array.getInteger(R.styleable.FingerprintModule_font_size,
                    DEFAULT_TEXT_SIZE);
            setFingerprintTextSize(fontSize);

            //TEXT FINGERPRINT
            setFingerprintText(array.getString(R.styleable.FingerprintModule_initial_message));

            //BACKGROUND COLOR
            setFingerprintBackgroundColor(array.getString(R.styleable.FingerprintModule_background_color));

            //ERROR BACKGROUND COLOR
            setFingerPrintErrorBackgroundColor(array.getString(R.styleable.FingerprintModule_error_background_color));


            //TEXT FOR FINGERPRINT FOUND
            if (array.getString(R.styleable.FingerprintModule_finger_print_found) != null && array.getString(R.styleable.FingerprintModule_finger_print_found).length() > 0) {
                mFingerprintFoundText = array.getString(R.styleable.FingerprintModule_finger_print_found);
            } else {
                mFingerprintFoundText = mContext.getString(R.string.text_finger_found);
            }


            //TEXT FOR FINGERPRINT NOT FOUND
            if (array.getString(R.styleable.FingerprintModule_fingerprint_not_found) != null && array.getString(R.styleable.FingerprintModule_fingerprint_not_found).length() > 0) {
                mFingerprintNotFoundText = array.getString(R.styleable.FingerprintModule_fingerprint_not_found);
            } else {
                mFingerprintNotFoundText = mContext.getString(R.string.text_finger_not_found);
            }

            //VIBRATE TIME
            mVibrateTime = array.getInteger(R.styleable.FingerprintModule_vibrate_time, DEFAULT_VIBRATE_TIME);
        }
    }

    private void setFingerprintTextSize(int fontSize) {
        mFingerPrintText.setTextSize(fontSize);
    }


    @Override
    public void showFingerprintMessage(CharSequence text) {
        Snackbar.make(mMainLayout, text.toString(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void authenticationSucceded() {
        Snackbar.make(mMainLayout, mFingerprintFoundText, Snackbar.LENGTH_LONG).show();

        if (mFingerprintCallback != null) {
            mFingerprintCallback.fingerFound();
        }
    }

    @Override
    public void authenticationFailed() {
        Snackbar.make(mMainLayout, mFingerprintNotFoundText, Snackbar.LENGTH_LONG).show();
        ((Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE)).vibrate(mVibrateTime);

        if (mFingerprintCallback != null) {
            mFingerprintCallback.fingerNotFound();
        }
    }

    public void setFingerprintText(String fingerprintText) {
        if (fingerprintText != null) {
            mFingerPrintText.setText(fingerprintText);
        }
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
