package com.sincpro.printer.domain

interface IBluetooth {
    fun isSupported(): Boolean
    fun isEnabled(): Boolean
    fun getPairedDevices(): Result<List<BluetoothDevice>>
    fun startDiscovery(): Result<Boolean>
    fun stopDiscovery(): Result<Boolean>
    fun isDiscovering(): Boolean
}

data class BluetoothDevice(
    val name: String,
    val address: String,
    val isPrinter: Boolean
)

data class ConnectionConfig(
    val address: String,
    val type: ConnectionType = ConnectionType.BLUETOOTH,
    val port: Int = 9100,
    val timeoutMs: Long = 10000
) {
    companion object {
        fun bluetooth(address: String, timeoutMs: Long = 10000) =
            ConnectionConfig(address, ConnectionType.BLUETOOTH, 0, timeoutMs)

        fun wifi(ip: String, port: Int = 9100, timeoutMs: Long = 10000) =
            ConnectionConfig(ip, ConnectionType.WIFI, port, timeoutMs)

        fun usb() = ConnectionConfig("", ConnectionType.USB, 0, 10000)
    }
}

enum class ConnectionType { BLUETOOTH, WIFI, USB }
