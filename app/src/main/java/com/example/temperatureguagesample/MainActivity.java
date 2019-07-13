package com.example.temperatureguagesample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.things.contrib.driver.bmx280.Bmx280;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends AppCompatActivity {

    // Board Peripheral management
    Bmx280 sensor = null;
    AlphanumericDisplay segment = null;

    // Temperature Reading (int)
    int temperature = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView temperatureTextView = findViewById(R.id.temperatureTextView);

        // Initialize the project and display the temperature

        try {
            sensor = RainbowHat.openSensor();
            sensor.setTemperatureOversampling(Bmx280.OVERSAMPLING_1X);
            float temperatureFloat = sensor.readTemperature();
            temperature = Math.round(temperatureFloat);

            // Also display the temperature on the Segment display
            segment = RainbowHat.openDisplay();

            segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
            segment.display(temperatureFloat);
            segment.setEnabled(true);

            temperatureTextView.setText(String.valueOf(temperature));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Kill any connections to the board
        if(sensor != null) {
            try {
                sensor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(segment != null) {
            try {
                segment.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
