package com.example.androidthings.myproject.motiondetection;

public interface MotionDetectorCallback {
    void onMotionDetected();
    void onTooDark();

    void moveServo();
    void moveServoBack();
}