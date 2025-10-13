import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  Alert,
} from 'react-native';
import { BluetoothDevice } from 'expo-bixolon';

interface BluetoothDeviceListProps {
  devices: BluetoothDevice[];
  onDeviceSelect: (device: BluetoothDevice) => void;
  isLoading: boolean;
  onRefresh: () => void;
}

const BluetoothDeviceList: React.FC<BluetoothDeviceListProps> = ({
  devices,
  onDeviceSelect,
  isLoading,
  onRefresh,
}) => {
  const renderDevice = ({ item }: { item: BluetoothDevice }) => (
    <TouchableOpacity
      style={[styles.deviceItem, item.isPrinter && styles.printerDevice]}
      onPress={() => onDeviceSelect(item)}
    >
      <View style={styles.deviceInfo}>
        <Text style={styles.deviceName}>{item.name}</Text>
        <Text style={styles.deviceAddress}>{item.address}</Text>
        <View style={styles.deviceMeta}>
          <Text style={styles.deviceType}>{item.type}</Text>
          {item.isPrinter && (
            <View style={styles.printerBadge}>
              <Text style={styles.printerBadgeText}>PRINTER</Text>
            </View>
          )}
        </View>
      </View>
      <Text style={styles.connectText}>Connect</Text>
    </TouchableOpacity>
  );

  const renderEmpty = () => (
    <View style={styles.emptyContainer}>
      <Text style={styles.emptyText}>
        {isLoading ? 'Searching for devices...' : 'No devices found'}
      </Text>
      {!isLoading && (
        <TouchableOpacity style={styles.refreshButton} onPress={onRefresh}>
          <Text style={styles.refreshButtonText}>Refresh</Text>
        </TouchableOpacity>
      )}
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Bluetooth Devices</Text>
        <Text style={styles.subtitle}>
          {devices.length} device{devices.length !== 1 ? 's' : ''} found
        </Text>
      </View>
      
      {isLoading && (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text style={styles.loadingText}>Discovering devices...</Text>
        </View>
      )}
      
      <FlatList
        data={devices}
        renderItem={renderDevice}
        keyExtractor={(item) => item.address}
        ListEmptyComponent={renderEmpty}
        style={styles.list}
        showsVerticalScrollIndicator={false}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
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
  list: {
    flex: 1,
  },
  deviceItem: {
    backgroundColor: '#fff',
    marginHorizontal: 20,
    marginVertical: 5,
    padding: 15,
    borderRadius: 10,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  printerDevice: {
    borderLeftWidth: 4,
    borderLeftColor: '#007AFF',
  },
  deviceInfo: {
    flex: 1,
  },
  deviceName: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 5,
  },
  deviceAddress: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  deviceMeta: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  deviceType: {
    fontSize: 12,
    color: '#999',
    backgroundColor: '#f0f0f0',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 4,
    marginRight: 10,
  },
  printerBadge: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 4,
  },
  printerBadgeText: {
    fontSize: 10,
    color: '#fff',
    fontWeight: 'bold',
  },
  connectText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '600',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  emptyText: {
    fontSize: 18,
    color: '#666',
    textAlign: 'center',
    marginBottom: 20,
  },
  refreshButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 8,
  },
  refreshButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  loadingText: {
    fontSize: 16,
    color: '#666',
    marginTop: 10,
  },
});

export default BluetoothDeviceList;
