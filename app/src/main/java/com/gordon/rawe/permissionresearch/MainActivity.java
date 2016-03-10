package com.gordon.rawe.permissionresearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gordon.rawe.permissionlib.PermissionListener;

public class MainActivity extends AppCompatActivity implements PermissionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onPermissionsGranted(int requestCode, int[] grantResults, String... permissions) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, int[] grantResults, String... permissions) {

    }

    @Override
    public void onShowRequestPermissionRationale(int requestCode, boolean isShowRationale, String... permissions) {

    }

    @Override
    public void onPermissionsError(int requestCode, int[] grantResults, String errorMsg, String... permissions) {

    }
}
