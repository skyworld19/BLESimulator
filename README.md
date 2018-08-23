# BLESimulator

Central (Master) app : ESDA Working Porject-BLE Test

Peripheral (Slave) app : BLESimulator



### BLE 코드



- BLE 지원 가능 장치인지 확인 

```java
// Use this check to determine whether BLE is supported on the device.  Then you can
 // selectively disable BLE-related features.
if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
}
```

- 블루투스 어댑터 연결

```java
// Initializes a Bluetooth adapter. For API level 18 and above, get a reference to
// BluetoothAdapter through BluetoothManager.
private BluetoothAdapter mBluetoothAdapter;

BluetoothManager bluetoothManager = 
    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
mBluetoothAdapter = bluetoothManager.getAdapter();
```

- 블루투스 지원 가능 장치인지 확인

```java
// Checks if Bluetooth is supported on the device.
if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,      Toast.LENGTH_SHORT).show();
            finish();
            return;
}
```

- 블루투스 안 켜놨을 때 켜도록 하기 

```java
// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
// fire an intent to display a dialog asking the user to grant permission to enable it.
if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
    // 결과값은 창이 종료될 때 onActivityResult가 호출되며 안에 담김
}


@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } // if 는 아니오 선택, 예를 누를 경우 계속 onResume 진행
        super.onActivityResult(requestCode, resultCode, data);
}
```

- Location 권한설정

```java
 if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION);        
}
```



