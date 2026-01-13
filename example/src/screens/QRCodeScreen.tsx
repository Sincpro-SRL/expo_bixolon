import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Alert,
} from 'react-native';
import { ExpoBixolonModule } from 'expo-bixolon';
import QrCodePrinter from 'expo-bixolon/src/QrCodePrinter';

export default function QRCodeScreen() {
  const [qrData, setQrData] = useState('https://www.bixolon.com');
  const [qrSize, setQrSize] = useState('9');
  const [isConnected, setIsConnected] = useState(false);

  const printSimpleQR = async () => {
    try {
      const qrPrinter = QrCodePrinter.getInstance();
      const result = await qrPrinter.printSimpleQRCode(qrData, parseInt(qrSize));

      if (result) {
        Alert.alert('Success', 'QR code printed successfully!');
      } else {
        Alert.alert('Error', 'Failed to print QR code');
      }
    } catch (error) {
      Alert.alert('Error', `Failed to print QR code: ${error}`);
    }
  };

  const printURLQR = async () => {
    try {
      const qrPrinter = QrCodePrinter.getInstance();
      const result = await qrPrinter.printURLQRCode(qrData, parseInt(qrSize));

      if (result) {
        Alert.alert('Success', 'URL QR code printed successfully!');
      } else {
        Alert.alert('Error', 'Failed to print URL QR code');
      }
    } catch (error) {
      Alert.alert('Error', `Failed to print URL QR code: ${error}`);
    }
  };

  const printContactQR = async () => {
    try {
      const qrPrinter = QrCodePrinter.getInstance();
      const contact = {
        name: 'John Doe',
        phone: '+1234567890',
        email: 'john.doe@example.com',
        company: 'Example Corp',
        title: 'Software Engineer',
      };

      const result = await qrPrinter.printContactQRCode(contact, parseInt(qrSize));

      if (result) {
        Alert.alert('Success', 'Contact QR code printed successfully!');
      } else {
        Alert.alert('Error', 'Failed to print contact QR code');
      }
    } catch (error) {
      Alert.alert('Error', `Failed to print contact QR code: ${error}`);
    }
  };

  const printWiFiQR = async () => {
    try {
      const qrPrinter = QrCodePrinter.getInstance();
      const result = await qrPrinter.printWiFiQRCode(
        'MyWiFiNetwork',
        'password123',
        'WPA',
        parseInt(qrSize)
      );

      if (result) {
        Alert.alert('Success', 'WiFi QR code printed successfully!');
      } else {
        Alert.alert('Error', 'Failed to print WiFi QR code');
      }
    } catch (error) {
      Alert.alert('Error', `Failed to print WiFi QR code: ${error}`);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>QR Code Printing</Text>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>QR Code Data</Text>
        <TextInput
          style={styles.input}
          placeholder="Enter QR code data"
          value={qrData}
          onChangeText={setQrData}
        />

        <Text style={styles.label}>Size (1-10):</Text>
        <TextInput
          style={styles.numberInput}
          placeholder="9"
          value={qrSize}
          onChangeText={setQrSize}
          keyboardType="numeric"
        />
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Print Options</Text>

        <TouchableOpacity style={styles.button} onPress={printSimpleQR}>
          <Text style={styles.buttonText}>Print Simple QR Code</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={printURLQR}>
          <Text style={styles.buttonText}>Print URL QR Code</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={printContactQR}>
          <Text style={styles.buttonText}>Print Contact QR Code</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={printWiFiQR}>
          <Text style={styles.buttonText}>Print WiFi QR Code</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Instructions</Text>
        <Text style={styles.instruction}>1. Make sure the printer is connected first</Text>
        <Text style={styles.instruction}>2. Enter the data you want to encode in the QR code</Text>
        <Text style={styles.instruction}>3. Set the size (1-10, recommended: 7-9)</Text>
        <Text style={styles.instruction}>4. Choose the type of QR code to print</Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
    color: '#333',
  },
  section: {
    backgroundColor: 'white',
    padding: 16,
    marginBottom: 16,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#333',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 12,
    fontSize: 16,
    marginBottom: 12,
  },
  numberInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 12,
    fontSize: 16,
    width: 100,
  },
  label: {
    fontSize: 16,
    fontWeight: '500',
    marginBottom: 8,
    color: '#555',
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 12,
    borderRadius: 6,
    alignItems: 'center',
    marginBottom: 8,
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  instruction: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
    lineHeight: 20,
  },
});
