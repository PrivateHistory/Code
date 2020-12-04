package no.nordicsemi.android.nrfthingy.common;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.nrfthingy.MainActivity;
import no.nordicsemi.android.nrfthingy.R;
import no.nordicsemi.android.nrfthingy.common.EnableNFCDialogFragment;
import no.nordicsemi.android.nrfthingy.common.PermissionRationaleDialogFragment;
import no.nordicsemi.android.nrfthingy.common.ProgressDialogFragment;
import no.nordicsemi.android.nrfthingy.common.ScannerFragment;
import no.nordicsemi.android.nrfthingy.common.ScannerFragmentListener;
import no.nordicsemi.android.nrfthingy.common.Utils;
import no.nordicsemi.android.nrfthingy.configuration.CancelInitialConfigurationDialogFragment;
import no.nordicsemi.android.nrfthingy.database.DatabaseContract;
import no.nordicsemi.android.nrfthingy.database.DatabaseHelper;
import no.nordicsemi.android.nrfthingy.dfu.DfuHelper;
import no.nordicsemi.android.nrfthingy.dfu.DfuUpdateAvailableDialogFragment;
import no.nordicsemi.android.nrfthingy.dfu.SecureDfuActivity;
import no.nordicsemi.android.nrfthingy.thingy.Thingy;
import no.nordicsemi.android.nrfthingy.thingy.ThingyService;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import no.nordicsemi.android.thingylib.ThingyListener;
import no.nordicsemi.android.thingylib.ThingyListenerHelper;
import no.nordicsemi.android.thingylib.ThingySdkManager;
import no.nordicsemi.android.thingylib.utils.ThingyUtils;

import static no.nordicsemi.android.nrfthingy.common.Utils.*;
import static no.nordicsemi.android.nrfthingy.common.Utils.REQUEST_ACCESS_COARSE_LOCATION;
import static no.nordicsemi.android.nrfthingy.common.Utils.REQUEST_ACCESS_FINE_LOCATION;
import static no.nordicsemi.android.nrfthingy.common.Utils.TAG;
import static no.nordicsemi.android.nrfthingy.common.Utils.checkIfVersionIsMarshmallowOrAbove;
import static no.nordicsemi.android.nrfthingy.common.Utils.checkIfVersionIsQ;
import static no.nordicsemi.android.nrfthingy.common.Utils.getBluetoothDevice;
import static no.nordicsemi.android.nrfthingy.common.Utils.isConnected;

public class Clouster_Head_Activity extends AppCompatActivity implements ScannerFragmentListener,ThingySdkManager.ServiceConnectionListener{

    private static final int SCAN_DURATION = 15000;


    private BluetoothDevice mDevice;

    private DatabaseHelper mDatabaseHelper;

    private ThingySdkManager mThingySdkManager;

    TextView debbugging;



    private ThingyService.ThingyBinder mBinder;
    private boolean mIsScanning;
    private IntentFilter[] mIntentFiltersArray;


    private BroadcastReceiver mLocationProviderChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final boolean enabled = isLocationEnabled();

        }
    };

    private BroadcastReceiver mNfcAdapterStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {

        }
    };

    private ThingyListener mThingyListener = new ThingyListener() {

        @Override
        public void onDeviceConnected(BluetoothDevice device, int connectionState) {

        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, int connectionState) {

        }

        @Override
        public void onServiceDiscoveryCompleted(BluetoothDevice device) {
        }

        @Override
        public void onBatteryLevelChanged(final BluetoothDevice bluetoothDevice, final int batteryLevel) {

        }

        @Override
        public void onTemperatureValueChangedEvent(BluetoothDevice bluetoothDevice, String temperature) {
        }

        @Override
        public void onPressureValueChangedEvent(BluetoothDevice bluetoothDevice, final String pressure) {
        }

        @Override
        public void onHumidityValueChangedEvent(BluetoothDevice bluetoothDevice, final String humidity) {
        }

        @Override
        public void onAirQualityValueChangedEvent(BluetoothDevice bluetoothDevice, final int eco2, final int tvoc) {
        }

        @Override
        public void onColorIntensityValueChangedEvent(BluetoothDevice bluetoothDevice, final float red, final float green, final float blue, final float alpha) {
        }

        @Override
        public void onButtonStateChangedEvent(BluetoothDevice bluetoothDevice, int buttonState) {

        }

        @Override
        public void onTapValueChangedEvent(BluetoothDevice bluetoothDevice, int direction, int count) {

        }

        @Override
        public void onOrientationValueChangedEvent(BluetoothDevice bluetoothDevice, int orientation) {

        }

        @Override
        public void onQuaternionValueChangedEvent(BluetoothDevice bluetoothDevice, float w, float x, float y, float z) {

        }

        @Override
        public void onPedometerValueChangedEvent(BluetoothDevice bluetoothDevice, int steps, long duration) {

        }

        @Override
        public void onAccelerometerValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onGyroscopeValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onCompassValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onEulerAngleChangedEvent(BluetoothDevice bluetoothDevice, float roll, float pitch, float yaw) {

        }

        @Override
        public void onRotationMatrixValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] matrix) {

        }

        @Override
        public void onHeadingValueChangedEvent(BluetoothDevice bluetoothDevice, float heading) {

        }

        @Override
        public void onGravityVectorChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onSpeakerStatusValueChangedEvent(BluetoothDevice bluetoothDevice, int status) {

        }

        @Override
        public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] data) {

        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clouster__head_);
        debbugging=findViewById(R.id.Check);
        mThingySdkManager = ThingySdkManager.getInstance();
        mDatabaseHelper = new DatabaseHelper(this);

    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isBleEnabled()) {
            enableBle();
        }



        mThingySdkManager.bindService(this, ThingyService.class);
        ThingyListenerHelper.registerThingyListener(this, mThingyListener);
        registerReceiver(mBleStateChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


        startScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfRequiredPermissionsGranted();

    }

    @Override
    protected void onPause() {
        super.onPause();

        final SharedPreferences sp = getSharedPreferences("APP_STATE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("APP_STATE", isFinishing());
        editor.apply();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mIsScanning) {
            if (mBinder != null) {
                mBinder.setScanningState(true);
                final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
                scanner.stopScan(mScanCallback);
                mIsScanning = false;
            }
        }

        mThingySdkManager.unbindService(this);
        ThingyListenerHelper.unregisterThingyListener(this, mThingyListener);
        unregisterReceiver(mBleStateChangedReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    private void handleOnBackPressed() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermission(final String permission, final int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    public void onCancellingPermissionRationale() {
        showToast(this, getString(R.string.requested_permission_not_granted_rationale));
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isBleEnabled()) {
                        enableBle();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.rationale_permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Checks whether the Bluetooth adapter is enabled.
     */
    private boolean isBleEnabled() {
        final BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        return ba != null && ba.isEnabled();
    }

    /**
     * Tries to start Bluetooth adapter.
     */
    private void enableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void onDeviceSelected(final BluetoothDevice device, final String name) {
        if (mThingySdkManager != null) {
            mThingySdkManager.connectToThingy(this, device, ThingyService.class);
        }
        mDevice = device;

    }

    @Override
    public void onNothingSelected() {

    }


    /*
        private void getStarted() {
            if (!isAppInitialisedBefore(this)) {
                final SharedPreferences sp = getSharedPreferences(PREFS_INITIAL_SETUP, MODE_PRIVATE);
                sp.edit().putBoolean(INITIAL_CONFIG_STATE, true).apply();
            }

            final String address = mDevice.getAddress();

            if (!mDatabaseHelper.isExist(address)) {
                if (mDeviceName == null || mDeviceName.isEmpty()) {
                    mDeviceName = mDevice.getName();
                }
                mDatabaseHelper.insertDevice(address, mDeviceName);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_TEMPERATURE);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_PRESSURE);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_HUMIDITY);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_AIR_QUALITY);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_COLOR);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_BUTTON);
                mDatabaseHelper.updateNotificationsState(address, true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_QUATERNION);
                mThingySdkManager.setSelectedDevice(mDevice);
            }
            updateSelectionInDb(new Thingy(mDevice), true);

            if (!mConfig) {
                final Intent intent = new Intent(no.nordicsemi.android.nrfthingy.configuration.InitialConfigurationActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_DEVICE, mDevice);
                startActivity(intent);
            }
            finish();
        }

        private void updateSelectionInDb(final no.nordicsemi.android.nrfthingy.thingy.Thingy thingy, final boolean selected) {
            final ArrayList<no.nordicsemi.android.nrfthingy.thingy.Thingy> thingyList = mDatabaseHelper.getSavedDevices();
            for (int i = 0; i < thingyList.size(); i++) {
                if (thingy.getDeviceAddress().equals(thingyList.get(i).getDeviceAddress())) {
                    mDatabaseHelper.setLastSelected(thingy.getDeviceAddress(), selected);
                } else {
                    mDatabaseHelper.setLastSelected(thingyList.get(0).getDeviceAddress(), !selected);
                }
            }
        }
    */
    @Override
    public void onServiceConnected() {
        //Use this binder to access you own methods declared in the ThingyService
        mBinder = (ThingyService.ThingyBinder) mThingySdkManager.getThingyBinder();
        handleIntent(getIntent());
        if (mThingySdkManager.hasInitialServiceDiscoverCompleted(mDevice)) {
            onServiceDiscoveryCompletion(mDevice);
        }
    }

    @Override
    public void cancelInitialConfiguration() {
        if (mThingySdkManager != null) {
            mThingySdkManager.disconnectFromThingy(mDevice);
        }
        super.onBackPressed();
    }

    final BroadcastReceiver mBleStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        enableBle();
                        break;
                }
            }
        }
    };






    private void checkForFwUpdates() {
        final String currentVersion = mThingySdkManager.getFirmwareVersion(mDevice);
        if (DfuHelper.isFirmwareUpdateAvailable(this, currentVersion)) {
            final String newestVersion = DfuHelper.getCurrentFwVersion(this);
            final DfuUpdateAvailableDialogFragment fragment = DfuUpdateAvailableDialogFragment.newInstance(mDevice, newestVersion);
            fragment.show(getSupportFragmentManager(), null);
        }
    }



    /**
     * Since Marshmallow location services must be enabled in order to scan.
     *
     * @return true on Android 6.0+ if location mode is different than LOCATION_MODE_OFF. It always returns true on Android versions prior to Marshmellow.
     */
    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationMode = Settings.Secure.LOCATION_MODE_OFF;
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (final Settings.SettingNotFoundException e) {
                // do nothing
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        return true;
    }

    private void handleIntent(final Intent intent) {

    }

    private void prepareForScanning(final String address) {
        if (checkIfVersionIsMarshmallowOrAbove()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    if (isBleEnabled()) {
                        handleStartScan(address);
                    } else enableBle();
                } else {
                    showToast(this, getString(R.string.location_services_disabled));
                }
            } else {
                final PermissionRationaleDialogFragment dialog = PermissionRationaleDialogFragment.getInstance(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION, getString(R.string.rationale_message_location));
                dialog.show(getSupportFragmentManager(), null);
            }
        } else {
            if (isBleEnabled()) {

                    mThingySdkManager.disconnectFromThingy(mDevice);

                handleStartScan(address);
                //mScannerFragment.show(getSupportFragmentManager(), null);
            } else enableBle();
        }
    }

    private void handleStartScan(final String address) {
        if (!isConnected(address, mThingySdkManager.getConnectedDevices()) && !mBinder.isScanningState()) {
            mDevice = getBluetoothDevice(this, address);


                mThingySdkManager.disconnectFromThingy(mDevice);



            if (mBinder != null) {
                mBinder.setScanningState(true);
                startScan();
            }
        }
    }

    private void startScan() {
        if (mIsScanning) {
            return;
        }

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0).setUseHardwareBatchingIfSupported(false).setUseHardwareFilteringIfSupported(false).build();
        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(ThingyUtils.THINGY_BASE_UUID)).build());
        scanner.startScan(filters, settings, mScanCallback);
        mIsScanning = true;

        //Handler to stop scan after the duration time out

    }

    /**
     * Stop scan on rotation or on app closing.
     * In case the stopScan is called inside onDestroy we have to check if the app is finishing as the mIsScanning flag becomes false on rotation
     */
    private void stopScan() {
        if (mIsScanning) {
            if (mBinder != null) {
                mBinder.setScanningState(false);
            }
            Log.v(TAG, "Stopping scan");

            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(mScanCallback);
            mIsScanning = false;
        } else if (!isFinishing()) {
            if (mBinder != null) {
                mBinder.setScanningState(false);
            }
            Log.v(TAG, "Stopping scan on rotation");
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(mScanCallback);
            mIsScanning = false;
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // do nothing
            final BluetoothDevice device = result.getDevice();
            if (device.equals(mDevice)) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        stopScan();
                        onDeviceSelected(result.getDevice(), result.getScanRecord().getDeviceName());
                        Log.v(TAG, "Connect?");
                    }
                });
            }
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            Connect_To_Devices(results);

        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };

    private void Connect_To_Devices(List<ScanResult> results) {
        for (final ScanResult result : results) {
            final BluetoothDevice device = result.getDevice();
            if(device!=null){
                debbugging.setText("One device was connected");
            }

        }
    }

    final Runnable mBleScannerTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };







    private boolean checkIfRequiredPermissionsGranted() {
        if (checkIfVersionIsQ()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else if (checkIfVersionIsMarshmallowOrAbove()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    private void onServiceDiscoveryCompletion(final BluetoothDevice device) {
        mThingySdkManager.enableEnvironmentNotifications(device, true);
        checkForFwUpdates();
    }
}

