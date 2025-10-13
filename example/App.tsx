import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  SafeAreaView,
  Alert,
  ScrollView,
} from 'react-native';
import { BixolonPrinter, BluetoothDevice } from 'expo-bixolon';
import BluetoothScreen from './src/screens/BluetoothScreen';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<'main' | 'bluetooth'>('main');
  const [isConnected, setIsConnected] = useState(false);
  const [connectedDevice, setConnectedDevice] = useState<BluetoothDevice | null>(null);
  const [isInitialized, setIsInitialized] = useState(false);
  const [permissionsGranted, setPermissionsGranted] = useState(false);

  useEffect(() => {
    initializePrinter();
    checkPermissions();
  }, []);

  const initializePrinter = async () => {
    try {
      const success = await BixolonPrinter.initializePrinter();
      setIsInitialized(success);
      if (success) {
        console.log('Printer initialized successfully');
      }
    } catch (error) {
      console.error('Error initializing printer:', error);
      Alert.alert('Error', 'Failed to initialize printer');
    }
  };

  const checkPermissions = async () => {
    try {
      const permissions = await BixolonPrinter.checkBluetoothPermissions();
      console.log('Current permissions:', permissions);
      
      const locationGranted = permissions.ACCESS_FINE_LOCATION || permissions.ACCESS_COARSE_LOCATION;
      setPermissionsGranted(locationGranted);
      
      if (!locationGranted) {
        console.log('Location permissions are not granted');
      } else {
        console.log('Permissions are sufficient for Bluetooth operations');
      }
    } catch (error) {
      console.error('Error checking permissions:', error);
      setPermissionsGranted(false);
    }
  };

  const requestPermissions = async () => {
    try {
      console.log('Requesting Bluetooth permissions...');
      
      const success = await BixolonPrinter.requestBluetoothPermissions();
      if (success) {
        console.log('Permission request completed');
        
        Alert.alert(
          'Permissions', 
          'Permission dialogs have been shown. Please grant the necessary permissions and then tap "Check Permissions" or try connecting again.',
          [
            { text: 'OK', onPress: () => {
              setTimeout(checkPermissions, 2000);
            }}
          ]
        );
      } else {
        Alert.alert('Error', 'Failed to request permissions');
      }
    } catch (error) {
      console.error('Error requesting permissions:', error);
      Alert.alert(
        'Permission Request Failed', 
        'Could not request permissions. Please enable Bluetooth and Location permissions manually in your device settings.',
        [
          { text: 'OK', onPress: checkPermissions }
        ]
      );
    }
  };

  const handleConnectToDevice = async (device: BluetoothDevice) => {
    try {
      const success = await BixolonPrinter.connectPrinter('bluetooth', device.address, 1);
      if (success) {
        setIsConnected(true);
        setConnectedDevice(device);
        setCurrentScreen('main');
        Alert.alert('Success', `Connected to ${device.name}`);
      } else {
        Alert.alert('Error', 'Failed to connect to device');
      }
    } catch (error) {
      console.error('Error connecting to device:', error);
      Alert.alert('Error', 'Failed to connect to device');
    }
  };

  const handleDisconnect = async () => {
    try {
      const success = await BixolonPrinter.disconnectPrinter();
      if (success) {
        setIsConnected(false);
        setConnectedDevice(null);
        Alert.alert('Success', 'Disconnected from printer');
      }
    } catch (error) {
      console.error('Error disconnecting:', error);
      Alert.alert('Error', 'Failed to disconnect');
    }
  };

  const handleTestPrint = async () => {
    try {
      const testText = `TEST DE IMPRESION
========================
HOLA DESDE TEST PLAIN TEXT
FECHA: ${new Date().toLocaleDateString()}
HORA: ${new Date().toLocaleTimeString()}
========================
Expo Bixolon Module - Test completado`;
      
      const success = await BixolonPrinter.printTextInPages(testText);
      if (success) {
        Alert.alert('Success', 'Test print completed');
      } else {
        Alert.alert('Error', 'Test print failed');
      }
    } catch (error) {
      console.error('Error printing test:', error);
      Alert.alert('Error', 'Failed to print test');
    }
  };

  const handlePrintInvoice = async () => {
    try {
      const currentDate = new Date().toLocaleDateString();
      const currentTime = new Date().toLocaleTimeString();
      const invoiceNumber = Math.floor(Math.random() * 10000);
      
      const items = [
        { description: 'Product 1', quantity: 2, price: 10.50 },
        { description: 'Product 2', quantity: 1, price: 25.00 },
      ];
      
      let subtotal = 0;
      let invoiceText = `EMPRESA EJEMPLO S.A.
RUC: 12345678901
FACTURA ELECTRONICA

No: ${invoiceNumber}
Fecha: ${currentDate}
Cliente: Test Customer

`;

      items.forEach(item => {
        const itemTotal = item.quantity * item.price;
        subtotal += itemTotal;
        invoiceText += `${item.description}
Cant: ${item.quantity} x S/ ${item.price} = S/ ${itemTotal}

`;
      });

      const igv = subtotal * 0.18;
      const totalWithIgv = subtotal + igv;

      invoiceText += `SUBTOTAL: S/ ${subtotal}
IGV (18%): S/ ${igv}
TOTAL: S/ ${totalWithIgv}

GRACIAS POR SU COMPRA!`;

      const success = await BixolonPrinter.printTextInPages(invoiceText);
      if (success) {
        Alert.alert('Success', 'Invoice printed successfully');
      } else {
        Alert.alert('Error', 'Failed to print invoice');
      }
    } catch (error) {
      console.error('Error printing invoice:', error);
      Alert.alert('Error', 'Failed to print invoice');
    }
  };

  const handlePrintQRCode = async () => {
    try {
      const qrContent = 'https://www.example.com';
      const qrSize = 2;
      
      const success = await BixolonPrinter.printQRCode(qrContent, qrSize);
      if (success) {
        Alert.alert('Success', 'QR Code printed successfully');
      } else {
        Alert.alert('Error', 'Failed to print QR Code');
      }
    } catch (error) {
      console.error('Error printing QR Code:', error);
      Alert.alert('Error', 'Failed to print QR Code');
    }
  };

  if (currentScreen === 'bluetooth') {
    return (
      <BluetoothScreen
        onDeviceSelected={handleConnectToDevice}
        onBack={() => setCurrentScreen('main')}
      />
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.scrollView}>
        <View style={styles.header}>
          <Text style={styles.title}>Expo Bixolon Module</Text>
          <Text style={styles.subtitle}>Printer Control Demo</Text>
        </View>

        <View style={styles.statusContainer}>
          <Text style={styles.statusTitle}>Status</Text>
          <View style={styles.statusItem}>
            <Text style={styles.statusLabel}>Initialized:</Text>
            <Text style={[styles.statusValue, { color: isInitialized ? '#4CAF50' : '#F44336' }]}>
              {isInitialized ? '✅ Yes' : '❌ No'}
            </Text>
          </View>
          <View style={styles.statusItem}>
            <Text style={styles.statusLabel}>Permissions:</Text>
            <Text style={[styles.statusValue, { color: permissionsGranted ? '#4CAF50' : '#F44336' }]}>
              {permissionsGranted ? '✅ Granted' : '❌ Not Granted'}
            </Text>
          </View>
          <View style={styles.statusItem}>
            <Text style={styles.statusLabel}>Connected:</Text>
            <Text style={[styles.statusValue, { color: isConnected ? '#4CAF50' : '#F44336' }]}>
              {isConnected ? '✅ Yes' : '❌ No'}
            </Text>
          </View>
          {connectedDevice && (
            <View style={styles.statusItem}>
              <Text style={styles.statusLabel}>Device:</Text>
              <Text style={styles.statusValue}>{connectedDevice.name}</Text>
            </View>
          )}
        </View>

        <View style={styles.actionsContainer}>
          <Text style={styles.sectionTitle}>Actions</Text>
          
          {!permissionsGranted && (
            <>
              <TouchableOpacity
                style={[styles.actionButton, { backgroundColor: '#FF9800' }]}
                onPress={requestPermissions}
              >
                <Text style={styles.actionButtonText}>Request Bluetooth Permissions</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={[styles.actionButton, { backgroundColor: '#9C27B0' }]}
                onPress={checkPermissions}
              >
                <Text style={styles.actionButtonText}>Check Permissions Status</Text>
              </TouchableOpacity>
            </>
          )}
          
          {!isConnected ? (
            <TouchableOpacity
              style={[styles.actionButton, { opacity: permissionsGranted ? 1 : 0.5 }]}
              onPress={() => setCurrentScreen('bluetooth')}
              disabled={!permissionsGranted}
            >
              <Text style={styles.actionButtonText}>Connect to Bluetooth Device</Text>
            </TouchableOpacity>
          ) : (
            <>
              <TouchableOpacity
                style={styles.actionButton}
                onPress={handleTestPrint}
              >
                <Text style={styles.actionButtonText}>Print Test Text</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={styles.actionButton}
                onPress={handlePrintInvoice}
              >
                <Text style={styles.actionButtonText}>Print Sample Invoice</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={[styles.actionButton, { backgroundColor: '#4CAF50' }]}
                onPress={handlePrintQRCode}
              >
                <Text style={styles.actionButtonText}>Print QR Code</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.actionButton, styles.disconnectButton]}
                onPress={handleDisconnect}
              >
                <Text style={styles.actionButtonText}>Disconnect</Text>
              </TouchableOpacity>
            </>
          )}
        </View>

        <View style={styles.infoContainer}>
          <Text style={styles.sectionTitle}>Information</Text>
          <Text style={styles.infoText}>
            This demo shows how to use the Expo Bixolon module to connect to and control Bixolon printers.
          </Text>
          <Text style={styles.infoText}>
            The module supports Bluetooth connectivity and basic printing operations.
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollView: {
    flex: 1,
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 5,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  statusContainer: {
    backgroundColor: '#fff',
    margin: 20,
    padding: 20,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statusTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  statusItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  statusLabel: {
    fontSize: 14,
    color: '#666',
  },
  statusValue: {
    fontSize: 14,
    fontWeight: '600',
  },
  actionsContainer: {
    backgroundColor: '#fff',
    margin: 20,
    padding: 20,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  actionButton: {
    backgroundColor: '#007AFF',
    paddingVertical: 15,
    paddingHorizontal: 20,
    borderRadius: 8,
    marginBottom: 10,
    alignItems: 'center',
  },
  disconnectButton: {
    backgroundColor: '#F44336',
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  infoContainer: {
    backgroundColor: '#fff',
    margin: 20,
    padding: 20,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  infoText: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
    marginBottom: 10,
  },
});
