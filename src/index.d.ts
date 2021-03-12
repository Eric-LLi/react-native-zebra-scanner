export enum READER_EVENTS {
	BARCODE = 'BARCODE',
}

type onBarcodeResult = (barcode: string) => void;

export type Callbacks = onBarcodeResult;

export declare function on(event: READER_EVENTS, callback: Callbacks): void;

export declare function off(event: READER_EVENTS): void;

export declare function connect(name: string): Promise<boolean>;

export declare function reconnect(): void;

export declare function disconnect(): Promise<void>;
