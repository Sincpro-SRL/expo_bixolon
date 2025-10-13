import { NativeEventEmitter } from 'react-native';
import ExpoBixolonModule from './ExpoBixolonModule';

const eventEmitter = new NativeEventEmitter(ExpoBixolonModule as any);

if (!eventEmitter.addListener) {
  (eventEmitter as any).addListener = () => ({ remove: () => {} });
}
if (!eventEmitter.removeAllListeners) {
  (eventEmitter as any).removeAllListeners = () => {};
}

export const BixolonPrinter = ExpoBixolonModule;

export default BixolonPrinter;
