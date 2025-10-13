
export interface BluetoothDevice {
  name: string;
  address: string;
  type: 'CLASSIC' | 'LE' | 'DUAL' | 'UNKNOWN';
  isPrinter: boolean;
}

export interface BluetoothPermissions {
  ACCESS_FINE_LOCATION: boolean;
  ACCESS_COARSE_LOCATION: boolean;
  BLUETOOTH_SCAN?: boolean;
  BLUETOOTH_CONNECT?: boolean;
}

export interface InvoiceItem {
  description: string;
  quantity: number;
  price: number;
}

export interface BixolonPrinterInterface {
  initializePrinter(): Promise<boolean>;
  connectPrinter(interfaceType: string, address: string, port: number): Promise<boolean>;
  disconnectPrinter(): Promise<boolean>;
  executeCommand(command: string): Promise<boolean>;
  
  testPlainText(text: string): Promise<boolean>;
  
  printInvoice(invoiceText: string): Promise<boolean>;
  
  printQRCode(text: string, size?: number): Promise<boolean>;
  printQRCodeAdvanced(
    data: string,
    horizontalPosition: number,
    verticalPosition: number,
    model: string,
    eccLevel: string,
    size: number,
    rotation: string
  ): Promise<boolean>;
  printFormattedText(text: string, fontSize?: number): Promise<boolean>;
  printTextSimple(text: string): Promise<boolean>;
  printTextInPages(text: string): Promise<boolean>;
  
  requestBluetoothPermissions(): Promise<boolean>;
  checkBluetoothPermissions(): Promise<BluetoothPermissions>;
  discoverBluetoothDevices(): Promise<BluetoothDevice[]>;
  startBluetoothDiscovery(): Promise<boolean>;
  stopBluetoothDiscovery(): Promise<boolean>;
  isBluetoothEnabled(): Promise<boolean>;
}

export type ExpoBixolonModuleEvents = {
  onBluetoothDeviceDiscovered: (device: BluetoothDevice) => void;
  onBluetoothDiscoveryStarted: () => void;
  onBluetoothDiscoveryStopped: () => void;
  onPrinterConnected: () => void;
  onPrinterDisconnected: () => void;
  onPrintComplete: (success: boolean) => void;
};

