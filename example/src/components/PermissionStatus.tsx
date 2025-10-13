import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import { BluetoothPermissions } from 'expo-bixolon';

interface PermissionStatusProps {
  permissions: BluetoothPermissions;
  onRequestPermissions: () => void;
}

const PermissionStatus: React.FC<PermissionStatusProps> = ({
  permissions,
  onRequestPermissions,
}) => {
  const getPermissionStatus = (granted: boolean) => {
    return granted ? '✅ Granted' : '❌ Denied';
  };

  const getPermissionColor = (granted: boolean) => {
    return granted ? '#4CAF50' : '#F44336';
  };

  const hasAllPermissions = Object.values(permissions).every(granted => granted);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Permission Status</Text>
      
      <View style={styles.permissionList}>
        <View style={styles.permissionItem}>
          <Text style={styles.permissionName}>Location (Fine)</Text>
          <Text style={[styles.permissionStatus, { color: getPermissionColor(permissions.ACCESS_FINE_LOCATION) }]}>
            {getPermissionStatus(permissions.ACCESS_FINE_LOCATION)}
          </Text>
        </View>
        
        <View style={styles.permissionItem}>
          <Text style={styles.permissionName}>Location (Coarse)</Text>
          <Text style={[styles.permissionStatus, { color: getPermissionColor(permissions.ACCESS_COARSE_LOCATION) }]}>
            {getPermissionStatus(permissions.ACCESS_COARSE_LOCATION)}
          </Text>
        </View>
        
        {permissions.BLUETOOTH_SCAN !== undefined && (
          <View style={styles.permissionItem}>
            <Text style={styles.permissionName}>Bluetooth Scan</Text>
            <Text style={[styles.permissionStatus, { color: getPermissionColor(permissions.BLUETOOTH_SCAN) }]}>
              {getPermissionStatus(permissions.BLUETOOTH_SCAN)}
            </Text>
          </View>
        )}
        
        {permissions.BLUETOOTH_CONNECT !== undefined && (
          <View style={styles.permissionItem}>
            <Text style={styles.permissionName}>Bluetooth Connect</Text>
            <Text style={[styles.permissionStatus, { color: getPermissionColor(permissions.BLUETOOTH_CONNECT) }]}>
              {getPermissionStatus(permissions.BLUETOOTH_CONNECT)}
            </Text>
          </View>
        )}
      </View>
      
      {!hasAllPermissions && (
        <TouchableOpacity style={styles.requestButton} onPress={onRequestPermissions}>
          <Text style={styles.requestButtonText}>Request Permissions</Text>
        </TouchableOpacity>
      )}
      
      {hasAllPermissions && (
        <View style={styles.allGrantedContainer}>
          <Text style={styles.allGrantedText}>✅ All permissions granted!</Text>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
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
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
    textAlign: 'center',
  },
  permissionList: {
    marginBottom: 20,
  },
  permissionItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  permissionName: {
    fontSize: 14,
    color: '#666',
    flex: 1,
  },
  permissionStatus: {
    fontSize: 14,
    fontWeight: '600',
  },
  requestButton: {
    backgroundColor: '#007AFF',
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 8,
    alignItems: 'center',
  },
  requestButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  allGrantedContainer: {
    alignItems: 'center',
    paddingVertical: 10,
  },
  allGrantedText: {
    fontSize: 16,
    color: '#4CAF50',
    fontWeight: '600',
  },
});

export default PermissionStatus;
