package com.sincpro.printer

import android.content.Context
import com.sincpro.printer.adapter.BixolonPrinterAdapter
import com.sincpro.printer.domain.IBluetooth
import com.sincpro.printer.infrastructure.AndroidBluetoothProvider
import com.sincpro.printer.service.bixolon.BixolonConnectivityService
import com.sincpro.printer.service.bixolon.BixolonPrintService

class SincproPrinterSdk(context: Context) {

    private val bluetoothProvider: IBluetooth = AndroidBluetoothProvider(context)

    val bixolon = Bixolon(context, bluetoothProvider)

    class Bixolon(context: Context, bluetooth: IBluetooth) {
        private val adapter = BixolonPrinterAdapter(context)

        val connectivity = BixolonConnectivityService(adapter, bluetooth)
        val print = BixolonPrintService(adapter)
    }
}
