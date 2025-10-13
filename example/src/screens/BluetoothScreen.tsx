import React, { useState, useEffect } from 'react';
import {
  View,
  StyleSheet,
  Alert,
  TouchableOpacity,
  Text,
  SafeAreaView,
} from 'react-native';
import BluetoothDeviceList from '../components/BluetoothDeviceList';
import PermissionStatus from '../components/PermissionStatus';
import { BixolonPrinter, BluetoothDevice, BluetoothPermissions } from 'expo-bixolon';

interface BluetoothScreenProps {
  onDeviceSelected: (device: any) => void;
  onBack: () => void;
}

const BluetoothScreen: React.FC<BluetoothScreenProps> = ({
  onDeviceSelected,
  onBack,
}) => {
  const [devices, setDevices] = useState<BluetoothDevice[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isBluetoothEnabled, setIsBluetoothEnabled] = useState(false);
  const [permissions, setPermissions] = useState<BluetoothPermissions | null>(null);

  useEffect(() => {
    checkBluetoothStatus();
  }, []);

  const checkBluetoothStatus = async () => {
    try {
      const enabled = await BixolonPrinter.isBluetoothEnabled();
      setIsBluetoothEnabled(enabled);
      
      if (enabled) {
        // Check permissions before loading devices
        await checkAndRequestPermissions();
      } else {
        Alert.alert(
          'Bluetooth Disabled',
          'Please enable Bluetooth to discover devices.',
          [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Settings', onPress: () => {} }, // Could open Bluetooth settings
          ]
        );
      }
    } catch (error) {
      console.error('Error checking Bluetooth status:', error);
      Alert.alert('Error', 'Failed to check Bluetooth status');
    }
  };

  const checkAndRequestPermissions = async () => {
    try {
      const currentPermissions = await BixolonPrinter.checkBluetoothPermissions();
      console.log('Current permissions:', currentPermissions);
      setPermissions(currentPermissions);
      
      // Check if all required permissions are granted
      const hasAllPermissions = Object.values(currentPermissions).every(granted => granted);
      
      if (hasAllPermissions) {
        loadDevices();
      } else {
        // Request permissions
        const granted = await BixolonPrinter.requestBluetoothPermissions();
        if (granted) {
          // Refresh permissions after request
          const updatedPermissions = await BixolonPrinter.checkBluetoothPermissions();
          setPermissions(updatedPermissions);
          loadDevices();
        } else {
          Alert.alert(
            'Permissions Required',
            'Bluetooth and location permissions are required to discover devices.',
            [
              { text: 'Cancel', style: 'cancel' },
              { text: 'Try Again', onPress: checkAndRequestPermissions },
            ]
          );
        }
      }
    } catch (error) {
      console.error('Error checking/requesting permissions:', error);
      Alert.alert('Error', 'Failed to check permissions');
    }
  };

  const loadDevices = async () => {
    setIsLoading(true);
    try {
      const discoveredDevices = await BixolonPrinter.discoverBluetoothDevices();
      setDevices(discoveredDevices);
      console.log(`Found ${discoveredDevices.length} Bluetooth devices`);
    } catch (error) {
      console.error('Error discovering devices:', error);
      Alert.alert('Error', 'Failed to discover Bluetooth devices');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeviceSelect = async (device: BluetoothDevice) => {
    try {
      console.log(`Selected device: ${device.name} (${device.address})`);
      
      // Show confirmation dialog
      Alert.alert(
        'Connect to Device',
        `Do you want to connect to "${device.name}"?`,
        [
          { text: 'Cancel', style: 'cancel' },
          {
            text: 'Connect',
            onPress: () => {
              onDeviceSelected(device);
            },
          },
        ]
      );
    } catch (error) {
      console.error('Error selecting device:', error);
      Alert.alert('Error', 'Failed to select device');
    }
  };

  const handleRefresh = () => {
    if (isBluetoothEnabled) {
      checkAndRequestPermissions();
    } else {
      checkBluetoothStatus();
    }
  };

  const startDiscovery = async () => {
    try {
      await BixolonPrinter.startBluetoothDiscovery();
      Alert.alert('Discovery Started', 'Searching for new devices...');
      
      // Stop discovery after 10 seconds
      setTimeout(async () => {
        try {
          await BixolonPrinter.stopBluetoothDiscovery();
          loadDevices(); // Reload devices
        } catch (error) {
          console.error('Error stopping discovery:', error);
        }
      }, 10000);
    } catch (error) {
      console.error('Error starting discovery:', error);
      Alert.alert('Error', 'Failed to start device discovery');
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backButton} onPress={onBack}>
          <Text style={styles.backButtonText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Bluetooth Devices</Text>
        <View style={styles.headerActions}>
          <TouchableOpacity style={styles.actionButton} onPress={handleRefresh}>
            <Text style={styles.actionButtonText}>Refresh</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionButton} onPress={startDiscovery}>
            <Text style={styles.actionButtonText}>Scan</Text>
          </TouchableOpacity>
        </View>
      </View>

      {permissions && (
        <PermissionStatus
          permissions={permissions}
          onRequestPermissions={checkAndRequestPermissions}
        />
      )}
      
      <BluetoothDeviceList
        devices={devices}
        onDeviceSelect={handleDeviceSelect}
        isLoading={isLoading}
        onRefresh={handleRefresh}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 20,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  backButton: {
    padding: 10,
  },
  backButtonText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '600',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  headerActions: {
    flexDirection: 'row',
    gap: 10,
  },
  actionButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 6,
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
});

export default BluetoothScreen;
