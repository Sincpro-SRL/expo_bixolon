# Usage Examples - @sincpro/printer-expo

This document provides practical usage examples for the Expo Bixolon printer module.

## Installation

```bash
npm install @sincpro/printer-expo
```

## Basic Usage

### 1. Check Bluetooth and Permissions

```typescript
import { bluetooth, permission } from '@sincpro/printer-expo';

// Check if Bluetooth is supported and enabled
const isSupported = await bluetooth.isSupported();
const isEnabled = await bluetooth.isEnabled();

if (!isSupported) {
  console.log('Bluetooth not supported on this device');
  return;
}

if (!isEnabled) {
  console.log('Please enable Bluetooth');
  return;
}

// Check permissions (important for Android 12+)
const hasPermissions = permission.hasBluetoothPermissions();

if (!hasPermissions) {
  const missing = permission.getMissingPermissions();
  console.log('Missing permissions:', missing);
  
  // Request permissions using Expo's Permissions API
  // ... request permissions
}
```

### 2. Discover and Connect to Printer

```typescript
import { bluetooth, connection, events } from '@sincpro/printer-expo';

// Listen for connection changes
const subscription = events.addConnectionChangedListener((event) => {
  console.log('Connection status:', event.status);
  
  if (event.status === 'connected') {
    console.log('Connected to:', event.address);
  } else if (event.status === 'error') {
    console.log('Connection error:', event.error);
  }
});

// Get paired devices
const devices = await bluetooth.getPairedDevices();
console.log('Paired devices:', devices);

// Filter printers only
const printers = devices.filter(device => device.isPrinter);

// Connect to first printer found
if (printers.length > 0) {
  const printer = printers[0];
  await connection.connect(printer.address, 9100);
}

// Cleanup
subscription.remove();
```

### 3. Print a Simple Receipt

```typescript
import { print } from '@sincpro/printer-expo';
import type { Receipt } from '@sincpro/printer-expo';

const receipt: Receipt = {
  header: [
    {
      type: 'text',
      content: 'MY STORE',
      fontSize: 'large',
      bold: true,
      alignment: 'center',
    },
    {
      type: 'text',
      content: '123 Main Street',
      fontSize: 'small',
      alignment: 'center',
    },
    {
      type: 'separator',
      char: '-',
      length: 48,
    },
  ],
  details: [
    {
      type: 'keyValue',
      key: 'Product A',
      value: '$10.00',
      fontSize: 'medium',
    },
    {
      type: 'keyValue',
      key: 'Product B',
      value: '$15.00',
      fontSize: 'medium',
    },
    {
      type: 'separator',
      char: '-',
      length: 48,
    },
    {
      type: 'keyValue',
      key: 'TOTAL',
      value: '$25.00',
      fontSize: 'medium',
      bold: true,
    },
  ],
  footer: [
    {
      type: 'space',
      lines: 1,
    },
    {
      type: 'text',
      content: 'Thank you for your purchase!',
      fontSize: 'medium',
      alignment: 'center',
    },
    {
      type: 'qrCode',
      data: 'https://mystore.com/receipt/12345',
      size: 5,
      alignment: 'center',
    },
  ],
};

try {
  await print.receipt(receipt);
  console.log('Receipt printed successfully!');
} catch (error) {
  console.error('Print failed:', error);
}
```

### 4. Print with Event Listeners

```typescript
import { print, events } from '@sincpro/printer-expo';

// Listen for print progress
const progressSub = events.addPrintProgressListener((event) => {
  console.log(`Job ${event.jobId}: ${event.progress}%`);
});

// Listen for completion
const completedSub = events.addPrintCompletedListener((event) => {
  console.log('Print job completed:', event.jobId);
});

// Listen for errors
const errorSub = events.addPrintErrorListener((event) => {
  console.error('Print error:', event.error);
});

// Print
await print.receipt(receipt);

// Cleanup
progressSub.remove();
completedSub.remove();
errorSub.remove();
```

### 5. Print QR Code Only

```typescript
import { print } from '@sincpro/printer-expo';

await print.qrCode('https://mywebsite.com', 5);
```

### 6. Print Custom Lines

```typescript
import { print } from '@sincpro/printer-expo';
import type { ReceiptLine } from '@sincpro/printer-expo';

const lines: ReceiptLine[] = [
  {
    type: 'text',
    content: 'Order #12345',
    fontSize: 'large',
    bold: true,
    alignment: 'center',
  },
  {
    type: 'space',
    lines: 1,
  },
  {
    type: 'keyValue',
    key: 'Date',
    value: new Date().toLocaleDateString(),
    fontSize: 'medium',
  },
  {
    type: 'keyValue',
    key: 'Time',
    value: new Date().toLocaleTimeString(),
    fontSize: 'medium',
  },
];

await print.lines(lines);
```

## Advanced Usage

### Custom Media Configuration

```typescript
import { print } from '@sincpro/printer-expo';
import type { MediaConfig } from '@sincpro/printer-expo';

const mediaConfig: MediaConfig = {
  preset: 'continuous80mm',  // or 'label80x50mm', 'label100x60mm'
};

await print.receipt(receipt, mediaConfig);

// Or custom configuration
const customMedia: MediaConfig = {
  widthDots: 640,      // 80mm at 203 DPI
  heightDots: 0,       // Continuous (0 = no fixed height)
  mediaType: 'continuous',
  gapDots: 0,
};

await print.receipt(receipt, customMedia);
```

### Complete Example App

```typescript
import React, { useEffect, useState } from 'react';
import { View, Button, Text } from 'react-native';
import { 
  bluetooth, 
  connection, 
  print, 
  events,
  type BluetoothDevice 
} from '@sincpro/printer-expo';

export default function PrinterExample() {
  const [printers, setPrinters] = useState<BluetoothDevice[]>([]);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Listen for connection changes
    const sub = events.addConnectionChangedListener((event) => {
      setConnected(event.status === 'connected');
    });

    return () => sub.remove();
  }, []);

  const scanPrinters = async () => {
    try {
      const devices = await bluetooth.getPairedDevices();
      const printerDevices = devices.filter(d => d.isPrinter);
      setPrinters(printerDevices);
    } catch (error) {
      console.error('Scan failed:', error);
    }
  };

  const connectToPrinter = async (address: string) => {
    try {
      await connection.connect(address, 9100);
    } catch (error) {
      console.error('Connection failed:', error);
    }
  };

  const printTestReceipt = async () => {
    try {
      await print.receipt({
        header: [
          { type: 'text', content: 'TEST RECEIPT', fontSize: 'large', bold: true, alignment: 'center' }
        ],
        details: [
          { type: 'keyValue', key: 'Date', value: new Date().toLocaleString(), fontSize: 'medium' }
        ],
        footer: [
          { type: 'text', content: 'Thank you!', fontSize: 'medium', alignment: 'center' }
        ],
      });
      console.log('Print successful!');
    } catch (error) {
      console.error('Print failed:', error);
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <Text>Connected: {connected ? 'Yes' : 'No'}</Text>
      
      <Button title="Scan Printers" onPress={scanPrinters} />
      
      {printers.map((printer) => (
        <Button
          key={printer.address}
          title={`Connect: ${printer.name}`}
          onPress={() => connectToPrinter(printer.address)}
        />
      ))}
      
      <Button 
        title="Print Test" 
        onPress={printTestReceipt}
        disabled={!connected}
      />
    </View>
  );
}
```

## TypeScript Types

All types are exported from the main package:

```typescript
import type {
  // Bluetooth
  BluetoothDevice,
  BluetoothDeviceType,
  PermissionStatus,
  
  // Connection
  ConnectionInfo,
  ConnectionType,
  ConnectionStatus,
  
  // Printing
  Receipt,
  ReceiptLine,
  MediaConfig,
  MediaPreset,
  
  // Events
  DeviceDiscoveredEvent,
  ConnectionChangedEvent,
  PrintProgressEvent,
  PrintCompletedEvent,
  PrintErrorEvent,
} from '@sincpro/printer-expo';
```

## Error Handling

All async operations can throw errors. Always use try-catch:

```typescript
try {
  await connection.connect(address, 9100);
} catch (error) {
  if (error.message.includes('timeout')) {
    console.error('Connection timeout - printer may be off or out of range');
  } else if (error.message.includes('permission')) {
    console.error('Bluetooth permissions not granted');
  } else {
    console.error('Connection failed:', error);
  }
}
```

## Best Practices

1. **Always check permissions** before Bluetooth operations
2. **Use event listeners** for observability
3. **Handle connection errors** gracefully
4. **Disconnect when done** to free resources
5. **Test with real hardware** - simulators don't support Bluetooth

## Troubleshooting

### Printer not found in paired devices
- Make sure printer is paired in Android Settings
- Check that Bluetooth permissions are granted

### Connection fails
- Verify printer is turned on and in range
- Check that no other app is connected to the printer
- Try increasing connection timeout (default 30s)

### Print doesn't work
- Verify printer is connected: `connection.isConnected()`
- Check that paper is loaded
- Ensure media configuration matches printer

### Events not received
- Make sure you're listening before the operation starts
- Don't forget to remove listeners to avoid memory leaks

## License

MIT
