import { NativeModule, requireNativeModule } from 'expo';
import { ExpoBixolonModuleEvents, BixolonPrinterInterface } from './ExpoBixolon.types';

declare class ExpoBixolonModule
  extends NativeModule<ExpoBixolonModuleEvents>
  implements BixolonPrinterInterface
{
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
  initializePrinter(): Promise<boolean>;
  connectPrinter(interfaceType: string, address: string, port: number): Promise<boolean>;
  disconnectPrinter(): Promise<boolean>;
  executeCommand(command: string): Promise<boolean>;

  testPlainText(text: string): Promise<boolean>;

  printInvoice(invoiceText: string): Promise<boolean>;

  requestBluetoothPermissions(): Promise<boolean>;
  checkBluetoothPermissions(): Promise<any>;
  discoverBluetoothDevices(): Promise<any[]>;
  startBluetoothDiscovery(): Promise<boolean>;
  stopBluetoothDiscovery(): Promise<boolean>;
  isBluetoothEnabled(): Promise<boolean>;
}

export default requireNativeModule<ExpoBixolonModule>('ExpoBixolon');
