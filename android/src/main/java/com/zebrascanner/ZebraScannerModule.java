package com.zebrascanner;

import android.view.View;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.zebra.scannercontrol.*;

import java.util.ArrayList;

import static com.zebra.scannercontrol.DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL;

public class ZebraScannerModule extends ReactContextBaseJavaModule implements LifecycleEventListener, IDcsSdkApiDelegate {

	private final ReactApplicationContext reactContext;
	private SDKHandler sdkHandler;
	private ArrayList<DCSScannerInfo> scannerInfoList;
	private int scannerId;

	public ZebraScannerModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		this.reactContext.addLifecycleEventListener(this);
	}

	@Override
	public void onHostResume() {

	}

	@Override
	public void onHostPause() {

	}

	@Override
	public void onHostDestroy() {

	}

	@Override
	public String getName() {
		return "ZebraScanner";
	}

	@ReactMethod
	public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
		// TODO: Implement some actually useful functionality
		callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
	}

	@ReactMethod
	public void init(Promise promise) {
		try {
			sdkHandler = new SDKHandler(this.reactContext);

			sdkHandler.dcssdkEnableAvailableScannersDetection(true);
			sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
			sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
			sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_LE);

			promise.resolve(true);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@ReactMethod
	public void connect(View view, Promise promise) {
		try {
			sdkHandler.dcssdkGetAvailableScannersList(scannerInfoList);

			scannerId = scannerInfoList.get(0).getScannerID();

			sdkHandler.dcssdkEstablishCommunicationSession(scannerId);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@Override
	public void dcssdkEventScannerAppeared(DCSScannerInfo dcsScannerInfo) {

	}

	@Override
	public void dcssdkEventScannerDisappeared(int i) {

	}

	@Override
	public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo dcsScannerInfo) {

	}

	@Override
	public void dcssdkEventCommunicationSessionTerminated(int i) {

	}

	@Override
	public void dcssdkEventBarcode(byte[] bytes, int i, int i1) {

	}

	@Override
	public void dcssdkEventImage(byte[] bytes, int i) {

	}

	@Override
	public void dcssdkEventVideo(byte[] bytes, int i) {

	}

	@Override
	public void dcssdkEventBinaryData(byte[] bytes, int i) {

	}

	@Override
	public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {

	}

	@Override
	public void dcssdkEventAuxScannerAppeared(DCSScannerInfo dcsScannerInfo, DCSScannerInfo dcsScannerInfo1) {

	}
}
