package com.sincpro.printer.domain

interface IPrinter {
    // Conexión
    suspend fun connect(config: ConnectionConfig): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    fun isConnected(): Boolean

    // Estado
    suspend fun getStatus(): Result<PrinterStatus>
    suspend fun getInfo(): Result<PrinterInfo>
    fun getDpi(): Int

    // Configuración global
    suspend fun configure(config: PrinterConfig): Result<Unit>

    // Impresión (adapter maneja buffer/transaction internamente)
    suspend fun print(elements: List<PrintElement>, media: MediaConfig, copies: Int = 1): Result<Unit>

    // Utilidades
    suspend fun feed(dots: Int): Result<Unit>
    suspend fun cut(): Result<Unit>
}

// ==================== Conexión ====================

data class ConnectionConfig(
    val type: ConnectionType,
    val address: String = "",
    val port: Int = 9100,
    val timeoutMs: Long = 10000
) {
    companion object {
        fun bluetooth(address: String, timeout: Long = 10000) =
            ConnectionConfig(ConnectionType.BLUETOOTH, address, timeoutMs = timeout)

        fun wifi(ip: String, port: Int = 9100, timeout: Long = 10000) =
            ConnectionConfig(ConnectionType.WIFI, ip, port, timeout)

        fun usb() = ConnectionConfig(ConnectionType.USB)
    }
}

enum class ConnectionType { BLUETOOTH, WIFI, USB }

enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

// ==================== Estado ====================

data class PrinterStatus(
    val connectionState: ConnectionState,
    val hasPaper: Boolean,
    val isCoverOpen: Boolean,
    val isOverheated: Boolean,
    val hasError: Boolean,
    val errorMessage: String? = null
) {
    val isConnected: Boolean get() = connectionState == ConnectionState.CONNECTED
    val isReady: Boolean get() = isConnected && hasPaper && !hasError && !isCoverOpen && !isOverheated

    companion object {
        fun disconnected() = PrinterStatus(
            connectionState = ConnectionState.DISCONNECTED,
            hasPaper = false,
            isCoverOpen = false,
            isOverheated = false,
            hasError = false
        )

        fun error(message: String) = PrinterStatus(
            connectionState = ConnectionState.ERROR,
            hasPaper = false,
            isCoverOpen = false,
            isOverheated = false,
            hasError = true,
            errorMessage = message
        )
    }
}

data class PrinterInfo(
    val model: String,
    val firmware: String,
    val serialNumber: String,
    val dpi: Int
)

// ==================== Configuración ====================

data class PrinterConfig(
    val marginLeft: Int = 0,
    val marginTop: Int = 0,
    val density: Density = Density.MEDIUM,
    val speed: Speed = Speed.MEDIUM,
    val orientation: Orientation = Orientation.TOP_TO_BOTTOM,
    val autoCutter: CutterConfig = CutterConfig.DISABLED
) {
    companion object {
        val DEFAULT = PrinterConfig()
    }
}

enum class Density(val level: Int) { LIGHT(5), MEDIUM(10), DARK(15), EXTRA_DARK(20) }

enum class Speed(val ips: Int) { SLOW(25), MEDIUM(50), FAST(70), EXTRA_FAST(80) }

enum class Orientation { TOP_TO_BOTTOM, BOTTOM_TO_TOP }

data class CutterConfig(val enabled: Boolean = false, val fullCut: Boolean = true) {
    companion object {
        val DISABLED = CutterConfig(false)
        val FULL_CUT = CutterConfig(true, true)
        val PARTIAL_CUT = CutterConfig(true, false)
    }
}
