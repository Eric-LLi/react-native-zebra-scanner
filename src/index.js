import { NativeModules, NativeEventEmitter } from 'react-native';

const { ZebraScanner } = NativeModules;


const events = {};

const eventEmitter = new NativeEventEmitter(ZebraScanner);

ZebraScanner.on = (event, handler) => {
	const eventListener = eventEmitter.addListener(event, handler);

	events[event] =  events[event] ? [...events[event], eventListener]: [eventListener];
};

ZebraScanner.off = (event) => {
	if (Object.hasOwnProperty.call(events, event)) {
		const eventListener = events[event].shift();

		if(eventListener) eventListener.remove();
	}
};

export default ZebraScanner;
