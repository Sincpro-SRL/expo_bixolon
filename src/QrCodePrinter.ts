import ExpoBixolonModule from './ExpoBixolonModule';

export interface QRCodeOptions {
  data: string;
  size?: number;
}

export interface QRCodeConstants {
  QR_CODE_MODEL1: number;
  QR_CODE_MODEL2: number;
  
  ECC_LEVEL_7: number;
  ECC_LEVEL_15: number;
  ECC_LEVEL_25: number;
  ECC_LEVEL_30: number;
  
  ROTATION_NONE: number;
  ROTATION_90_DEGREES: number;
  ROTATION_180_DEGREES: number;
  ROTATION_270_DEGREES: number;
}

export class QrCodePrinter {
  private static instance: QrCodePrinter;
  private isInitialized = false;

  private constructor() {}

  public static getInstance(): QrCodePrinter {
    if (!QrCodePrinter.instance) {
      QrCodePrinter.instance = new QrCodePrinter();
    }
    return QrCodePrinter.instance;
  }

  public async initialize(): Promise<boolean> {
    try {
      if (!this.isInitialized) {
        const result = await ExpoBixolonModule.initializePrinter();
        this.isInitialized = result;
        return result;
      }
      return true;
    } catch (error) {
      console.error('Error initializing QR code printer:', error);
      throw error;
    }
  }

  public async printQRCode(options: QRCodeOptions): Promise<boolean> {
    try {
      if (!this.isInitialized) {
        await this.initialize();
      }

      const {
        data,
        size = 9
      } = options;

      if (!data || data.trim().length === 0) {
        throw new Error('QR code data cannot be empty');
      }

      if (size < 1 || size > 10) {
        throw new Error('QR code size must be between 1 and 10');
      }

      return await ExpoBixolonModule.printQRCodeAdvanced(
        data,
        200,
        100,
        'MODEL2',
        'ECC_LEVEL_7',
        size,
        'NONE'
      );

    } catch (error) {
      console.error('Error printing QR code:', error);
      throw error;
    }
  }

  public async printSimpleQRCode(data: string, size: number = 9): Promise<boolean> {
    return this.printQRCode({
      data,
      size
    });
  }

  public async printURLQRCode(url: string, size: number = 9): Promise<boolean> {
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      url = 'https://' + url;
    }
    return this.printQRCode({
      data: url,
      size
    });
  }

  public async printContactQRCode(contact: {
    name: string;
    phone?: string;
    email?: string;
    company?: string;
    title?: string;
  }, size: number = 9): Promise<boolean> {
    const vCard = this.generateVCard(contact);
    return this.printQRCode({
      data: vCard,
      size
    });
  }

  public async printWiFiQRCode(ssid: string, password: string, encryption: 'WPA' | 'WEP' | 'nopass' = 'WPA', size: number = 9): Promise<boolean> {
    const wifiString = `WIFI:T:${encryption};S:${ssid};P:${password};;`;
    return this.printQRCode({
      data: wifiString,
      size
    });
  }

  public async printPaymentQRCode(paymentData: {
    amount: number;
    currency: string;
    description?: string;
    merchantName?: string;
  }, size: number = 0): Promise<boolean> {
    const paymentString = this.generatePaymentString(paymentData);
    return this.printQRCode({
      data: paymentString,
      size
    });
  }

  private generateVCard(contact: {
    name: string;
    phone?: string;
    email?: string;
    company?: string;
    title?: string;
  }): string {
    let vCard = 'BEGIN:VCARD\nVERSION:3.0\n';
    vCard += `FN:${contact.name}\n`;
    
    if (contact.phone) {
      vCard += `TEL:${contact.phone}\n`;
    }
    
    if (contact.email) {
      vCard += `EMAIL:${contact.email}\n`;
    }
    
    if (contact.company) {
      vCard += `ORG:${contact.company}\n`;
    }
    
    if (contact.title) {
      vCard += `TITLE:${contact.title}\n`;
    }
    
    vCard += 'END:VCARD';
    return vCard;
  }

  private generatePaymentString(paymentData: {
    amount: number;
    currency: string;
    description?: string;
    merchantName?: string;
  }): string {
    return `PAYMENT:${paymentData.amount}${paymentData.currency}${paymentData.description ? ':' + paymentData.description : ''}`;
  }

  public getConstants(): QRCodeConstants {
    return {
      QR_CODE_MODEL1: 1,
      QR_CODE_MODEL2: 2,
      ECC_LEVEL_7: 7,
      ECC_LEVEL_15: 15,
      ECC_LEVEL_25: 25,
      ECC_LEVEL_30: 30,
      ROTATION_NONE: 0,
      ROTATION_90_DEGREES: 90,
      ROTATION_180_DEGREES: 180,
      ROTATION_270_DEGREES: 270
    };
  }
}

export default QrCodePrinter;
