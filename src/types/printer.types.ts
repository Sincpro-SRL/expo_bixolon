/**
 * Printer types for @sincpro/printer-expo
 */

/**
 * Printer status from getStatus()
 */
export interface PrinterStatus {
  connectionState: ConnectionState;
  hasPaper: boolean;
  isCoverOpen: boolean;
  isOverheated: boolean;
  hasError: boolean;
  errorMessage: string | null;
}

/**
 * Connection state
 */
export type ConnectionState = 'DISCONNECTED' | 'CONNECTING' | 'CONNECTED' | 'ERROR';

/**
 * Printer info from getInfo()
 */
export interface PrinterInfo {
  model: string;
  firmware: string;
  serial: string;
  dpi: number;
}

/**
 * Media configuration
 */
export interface MediaConfig {
  /** Use preset instead of manual configuration */
  preset?: MediaPreset;
  
  /** Custom width in millimeters (auto-converts to dots) */
  widthMm?: number;
  /** Custom height in millimeters (for labels) */
  heightMm?: number;
  /** Gap/mark size in millimeters */
  gapMm?: number;
  
  /** Width in dots (use widthMm for easier configuration) */
  widthDots?: number;
  /** Height in dots (use heightMm for easier configuration) */
  heightDots?: number;
  /** Gap in dots */
  gapDots?: number;
  
  /** Media type */
  type?: MediaType;
}

/**
 * Media presets
 */
export type MediaPreset = 'continuous58mm' | 'continuous72mm' | 'continuous80mm';

/**
 * Media type for labels and continuous paper
 */
export type MediaType = 'continuous' | 'gap' | 'label' | 'black_mark';

/**
 * Printer configuration (global settings)
 */
export interface PrinterConfig {
  /** Left margin in dots */
  marginLeft?: number;
  /** Top margin in dots */
  marginTop?: number;
  /** Print density */
  density?: PrintDensity;
  /** Print speed */
  speed?: PrintSpeed;
  /** Print orientation */
  orientation?: PrintOrientation;
  /** Auto cutter configuration */
  autoCutter?: CutterConfig;
}

/**
 * Print density levels
 */
export type PrintDensity = 'light' | 'medium' | 'dark' | 'extra_dark';

/**
 * Print speed levels
 */
export type PrintSpeed = 'slow' | 'medium' | 'fast' | 'extra_fast';

/**
 * Print orientation
 */
export type PrintOrientation = 'top_to_bottom' | 'bottom_to_top';

/**
 * Auto cutter configuration
 */
export interface CutterConfig {
  /** Enable auto cutter */
  enabled: boolean;
  /** Full cut (true) or partial cut (false) */
  fullCut?: boolean;
}

/**
 * Font size options
 */
export type FontSize = 'small' | 'medium' | 'large' | 'xlarge';

/**
 * Alignment options
 */
export type Alignment = 'left' | 'center' | 'right';

/**
 * Barcode types
 */
export type BarcodeType =
  | 'CODE128'
  | 'CODE39'
  | 'EAN13'
  | 'EAN8'
  | 'UPCA'
  | 'UPCE'
  | 'CODE93'
  | 'CODABAR';

/**
 * Print text options
 */
export interface PrintTextOptions {
  fontSize?: FontSize;
  alignment?: Alignment;
  bold?: boolean;
  media?: MediaConfig;
}

/**
 * Print texts options (multiple lines)
 */
export interface PrintTextsOptions {
  fontSize?: FontSize;
  media?: MediaConfig;
}

/**
 * Print QR options
 */
export interface PrintQROptions {
  size?: number;
  alignment?: Alignment;
  media?: MediaConfig;
}

/**
 * Print barcode options
 */
export interface PrintBarcodeOptions {
  type?: BarcodeType;
  height?: number;
  alignment?: Alignment;
  media?: MediaConfig;
}

/**
 * Print image options
 */
export interface PrintImageOptions {
  alignment?: Alignment;
  media?: MediaConfig;
}

/**
 * Print PDF options
 */
export interface PrintPdfOptions {
  page?: number;
  alignment?: Alignment;
  media?: MediaConfig;
}

/**
 * Print key-value options
 */
export interface PrintKeyValueOptions {
  fontSize?: FontSize;
  bold?: boolean;
  media?: MediaConfig;
}

/**
 * Print receipt options
 */
export interface PrintReceiptOptions {
  media?: MediaConfig;
  copies?: number;
}
