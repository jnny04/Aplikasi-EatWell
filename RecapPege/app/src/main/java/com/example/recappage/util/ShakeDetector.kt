package com.example.recappage.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class ShakeDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var onShakeListener: (() -> Unit)? = null

    private val SHAKE_THRESHOLD_GRAVITY = 2.7F
    private val SHAKE_SLOP_TIME_MS = 500
    private var shakeTimestamp: Long = 0

    fun start(onShake: () -> Unit) {
        this.onShakeListener = onShake
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        Log.d("SENSOR_LIFECYCLE", "🟢 [Accelerometer] STARTED: User masuk Home Page. Siap deteksi guncangan.")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        Log.d("SENSOR_LIFECYCLE", "🔴 [Accelerometer] STOPPED: User keluar Home Page. Sensor non-aktif.")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // Hitung total G-Force
            val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // Cegah guncangan terdeteksi berkali-kali dalam waktu singkat
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }
                shakeTimestamp = now
                onShakeListener?.invoke()
            }
        }
    }
}