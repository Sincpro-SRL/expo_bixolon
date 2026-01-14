package com.sincpro.printer.domain

interface IBluetooth {
    fun isSupported(): Boolean
    fun isEnabled(): Boolean
    fun getPairedDevices(): Result<List<BluetoothDevice>>
    fun startDiscovery(): Result<Boolean>
    fun stopDiscovery(): Result<Boolean>
    fun isDiscovering(): Boolean
}

enum class BluetoothType { CLASSIC, BLE, DUAL, UNKNOWN }

data class BluetoothDevice(
    val name: String,
    val address: String,
    val type: BluetoothType = BluetoothType.UNKNOWN,
    val isPrinter: Boolean,
    val rssi: Int? = null
) {
    val hasStrongSignal: Boolean
        get() = rssi?.let { it > -60 } ?: false

    val hasWeakSignal: Boolean
        get() = rssi?.let { it < -80 } ?: false
}
