package com.zebrascanner;

import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.zebra.scannercontrol.*;

import java.util.ArrayList;

import static com.zebra.scannercontrol.DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL;

public class ZebraScannerModule extends ReactContextBaseJavaModule implements LifecycleEventListener, IDcsSdkApiDelegate {

    private final String LOG = "[ZEBRA Barcode]";
    private final String BARCODE = "BARCODE";
    private final ReactApplicationContext reactContext;
    private static SDKHandler sdkHandler = null;
    private static DCSScannerInfo scanner = null;
    // Barcode library register listener.
    private static int notifications_mask = 0;

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
        doDisconnect();

        dispose();
    }

    @Override
    public String getName() {
        return "ZebraScanner";
    }

    private void sendEvent(String eventName, WritableMap params) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private void sendEvent(String eventName, String msg) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, msg);
    }

    @ReactMethod
    public void connect(String name, Promise promise) {
        try {
            if (sdkHandler == null) {
                init();
            }

            ArrayList<DCSScannerInfo> list = new ArrayList<>();
            sdkHandler.dcssdkGetAvailableScannersList(list);

            for (DCSScannerInfo item : list) {
                if (item.getScannerName().equals(name)) {
                    scanner = item;
                }
            }

            doConnect();
            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void reconnect() {
        doConnect();
    }

    @ReactMethod
    public void disconnect() {
        doDisconnect();
    }

    @ReactMethod
    public void softTriggerStart() {
        barcodePullTrigger();
    }

    @ReactMethod
    public void softTriggerStop() {
        barcodeReleaseTrigger();
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
        sendEvent(BARCODE, new String(bytes));
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

    private void init() {
        if (sdkHandler == null) {
            sdkHandler = new SDKHandler(this.reactContext);
            sdkHandler.dcssdkSetDelegate(this);

            sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);

            sdkHandler.dcssdkEnableAvailableScannersDetection(true);

            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value
                    | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value);
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value
                    | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value);
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value);
        }
    }

    private void dispose() {
        Log.d(LOG, "dispose");

        if (sdkHandler != null) {
            sdkHandler.dcssdkClose();

            scanner = null;
            sdkHandler = null;
            notifications_mask = 0;
        }
    }

    private void doConnect() {
        if (scanner != null && scanner.isActive()) {
            doDisconnect();
        }

        if (sdkHandler != null && scanner != null && !scanner.isActive()) {
            DCSSDKDefs.DCSSDK_RESULT result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE;
            sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
            result = sdkHandler.dcssdkEstablishCommunicationSession(scanner.getScannerID());

            Log.d(LOG, result.name());
        }
    }

    private void doDisconnect() {
        if (sdkHandler != null && scanner != null && scanner.isActive()) {
            sdkHandler.dcssdkUnsubsribeForEvents(notifications_mask);
            DCSSDKDefs.DCSSDK_RESULT result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE;
            result = sdkHandler.dcssdkTerminateCommunicationSession(scanner.getScannerID());

            Log.d(LOG, result.name());
        }
    }

    private void barcodePullTrigger() {
        if (scanner != null) {
            StringBuilder outXML = null;
            String in_xml = "<inArgs><scannerID>" + scanner.getScannerID() + "</scannerID></inArgs>";
            DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER;
            executeCommand(opcode, in_xml, outXML, scanner.getScannerID());
        }
    }

    private void barcodeReleaseTrigger() {
        if (scanner != null) {
            StringBuilder outXML = null;
            String in_xml = "<inArgs><scannerID>" + scanner.getScannerID() + "</scannerID></inArgs>";
            DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_RELEASE_TRIGGER;
            executeCommand(opcode, in_xml, outXML, scanner.getScannerID());
        }
    }

    private DCSSDKDefs.DCSSDK_RESULT executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE opCode, String inXML,
                                                    StringBuilder outXML,
                                                    int scannerID) {
        DCSSDKDefs.DCSSDK_RESULT result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE;

        if (sdkHandler != null) {
            if (outXML == null) {
                outXML = new StringBuilder();
            }

            result = sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(opCode, inXML,
                    outXML, scannerID);
        }
        return result;
    }
}
