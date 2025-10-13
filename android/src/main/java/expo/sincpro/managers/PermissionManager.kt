package expo.sincpro.managers

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {
    private val TAG = "PermissionManager"

    fun requestBluetoothPermissions(currentActivity: Activity?): Boolean {
        try {
            Log.d(TAG, "Requesting Bluetooth permissions")
            
            if (currentActivity == null) {
                Log.w(TAG, "No current activity available for permission request")
                return false
            }
            
            val permissions = mutableListOf<String>()
            
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
            
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
                }
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(android.Manifest.permission.BLUETOOTH_CONNECT)
                }
            }
            
            if (permissions.isEmpty()) {
                Log.d(TAG, "All permissions already granted")
                return true
            }
            
            Log.d(TAG, "Requesting permissions: ${permissions.joinToString()}")
            
            ActivityCompat.requestPermissions(
                currentActivity,
                permissions.toTypedArray(),
                1001
            )
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting Bluetooth permissions: ${e.message}")
            return false
        }
    }

    fun checkBluetoothPermissions(): Map<String, Boolean> {
        try {
            Log.d(TAG, "Checking Bluetooth permissions")
            
            val results = mutableMapOf<String, Boolean>()
            
            results["ACCESS_FINE_LOCATION"] = ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            results["ACCESS_COARSE_LOCATION"] = ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                results["BLUETOOTH_SCAN"] = ContextCompat.checkSelfPermission(
                    context, 
                    android.Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
                
                results["BLUETOOTH_CONNECT"] = ContextCompat.checkSelfPermission(
                    context, 
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            }
            
            return results
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Bluetooth permissions: ${e.message}")
            throw e
        }
    }

    fun hasRequiredPermissions(): Boolean {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, 
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        var hasBluetoothPermissions = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasScanPermission = ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            
            val hasConnectPermission = ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            
            hasBluetoothPermissions = hasScanPermission && hasConnectPermission
        }
        
        return hasLocationPermission && hasBluetoothPermissions
    }
}
