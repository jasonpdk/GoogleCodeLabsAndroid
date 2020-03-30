package id.keane.sensorsurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensorProximity;
    private Sensor mSensorLight;

    private TextView mTextSensorLight;
    private TextView mTextSensorProximity;

    private ImageView tuxImage;
    private int imageWidth;
    private int imageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mTextSensorLight = (TextView) findViewById(R.id.label_light);
        mTextSensorLight.setTextColor(Color.WHITE);
        mTextSensorProximity = (TextView) findViewById(R.id.label_proximity);
        mTextSensorProximity.setTextColor(Color.WHITE);

        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        if (mSensorLight == null) {
            mTextSensorLight.setText(sensor_error);
        }

        if (mSensorProximity == null) {
            mTextSensorProximity.setText(sensor_error);
        }

        tuxImage = findViewById(R.id.tuxImage);
        imageWidth = tuxImage.getWidth();
        imageHeight = tuxImage.getHeight();
        System.out.println("Image height: " + imageHeight + " width: " + imageWidth + " max height: " + tuxImage.getMaxHeight() + " max width: " + tuxImage.getMaxWidth());
        tuxImage.getLayoutParams().width = 1500;
        tuxImage.getLayoutParams().height = 1500;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mSensorProximity != null) {
            mSensorManager.registerListener(this, mSensorProximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // unregister listener to save power when app is not in foreground
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float currentValue = event.values[0];

        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                // handle light sensor
                mTextSensorLight.setText(getResources().getString(
                        R.string.label_light, currentValue));

                // gradually increase the intensity of the red background as the light level goes up
                float colourCoefficient = 255/mSensorLight.getMaximumRange();
                int colourValue = (int)(currentValue * colourCoefficient);

                getWindow().getDecorView().setBackgroundColor(
                        Color.rgb(colourValue, 0, 0));
                break;
            case Sensor.TYPE_PROXIMITY:
                mTextSensorProximity.setText(getResources().getString(
                        R.string.label_proximity, currentValue));

                // 1500 is the max width and height I want. getMaximumRange returns 1 here, but the
                // current value is between 0 and 10, so I multiply it by 10
                float imageSizeCoefficient = 1500/(mSensorProximity.getMaximumRange()*10);
                int imageSizeValue = (int)(currentValue * imageSizeCoefficient);

                tuxImage.getLayoutParams().width = imageSizeValue;
                tuxImage.getLayoutParams().height = imageSizeValue;
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // light and proximity sensors do not report accuracy changes
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
