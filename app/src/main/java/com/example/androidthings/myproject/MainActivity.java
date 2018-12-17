/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.myproject;
import com.google.android.things.pio.Pwm;

import android.annotation.SuppressLint;
import android.content.ContentValues.*;
import java.util.*;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Vibrator;
import android.os.Bundle;
import android.os.Handler;
import android.media.ImageReader;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import com.google.android.things.contrib.driver.pwmservo.Servo;
import com.google.android.things.pio.PeripheralManager;

import com.example.androidthings.myproject.motiondetection.MotionDetector;
import com.example.androidthings.myproject.motiondetection.MotionDetectorCallback;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.android.things.contrib.driver.apa102.Apa102;

import com.google.android.things.pio.Gpio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManager. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManager manager = PeripheralManager.getInstance();
 * mLedGpio = manager.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 */


public class MainActivity extends Activity {
    private TextView txtStatus;
    private MotionDetector motionDetector;
    private Servo mServo;
    private Pwm mPwm;
    private static final String PWM_NAME = "PWM1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mPwm = manager.openPwm(PWM_NAME);
            mPwm.setPwmFrequencyHz(120);
            mPwm.setPwmDutyCycle(30);
            mPwm.setEnabled(true);
        } catch (IOException e) {
            System.out.println("mservo failed detected");
        }

        motionDetector = new MotionDetector(this, (SurfaceView) findViewById(R.id.surfaceView));
        motionDetector.setMotionDetectorCallback(new MotionDetectorCallback() {
            @Override
            public void onMotionDetected() {
                txtStatus.setText("Motion detected");
                System.out.println("Motion detected");
                moveServo();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moveServoBack();

            }

            @Override
            public void moveServo() {
                try {
                    System.out.println("Servo moving");
//                    mServo.setAngle(mServo.getMaximumAngle());
                    mPwm.setPwmDutyCycle(8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void moveServoBack() {
                try {
                    System.out.println("Servo moving back");
//                    mServo.setAngle(mServo.getMaximumAngle());
                    mPwm.setPwmDutyCycle(30);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onTooDark() {
                txtStatus.setText("Too dark here");
            }
        });

        ////// Config Options
        motionDetector.setCheckInterval(100);
        //motionDetector.setLeniency(20);
        //motionDetector.setMinLuma(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        motionDetector.onResume();

        if (motionDetector.checkCameraHardware()) {
            txtStatus.setText("Camera found");
        } else {
            txtStatus.setText("No camera available");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        motionDetector.onPause();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onDestroy() {
        if (mPwm != null) {
            try {
                mPwm.close();
                mPwm = null;
            } catch (IOException e) {

            }
        }

    }
}
