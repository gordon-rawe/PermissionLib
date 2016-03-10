package com.gordon.rawe.permissionlib;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/***
 * 权限工具类
 * Created by pengjiang on 2015/10/8.
 */
public class PermissionUtils {
    private static List<String> mGrantedPermissions = new ArrayList<String>();
    private static List<String> mDeniedPermissions = new ArrayList<String>();
    private static List<String> mUnshowedPermissions = new ArrayList<String>();
    private static List<String> mNeedRequestPermissions = new ArrayList<String>();
    private PermissionUtils() {
    }

    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    public static boolean verifyPermissions(int... grantResults) {

        if(grantResults == null || grantResults.length < 1){
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity or Fragment has access to all given permissions.
     *
     * @param context     context
     * @param permissions permission list
     * @return returns true if the Activity or Fragment has access to all given permissions.
     */
    public static boolean hasSelfPermissions(Context context, String... permissions) {
        if(context == null){
            return true;
        }
        if(permissions == null || permissions.length < 1){
            return true;
        }
        for (String permission : permissions) {
            if (PermissionChecker.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void sortGrantedAndDeniedPermissions(Context context, String... permissions){
        if(context == null){
            return ;
        }
        if(permissions == null || permissions.length < 1){
            return ;
        }
        mGrantedPermissions.clear();
        mDeniedPermissions.clear();
        mUnshowedPermissions.clear();
        mNeedRequestPermissions.clear();

        try {
            for (String permission : permissions) {
                if (PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    mGrantedPermissions.add(permission);
                } else {
                    mDeniedPermissions.add(permission);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getGrantedPermissions() {
        return mGrantedPermissions;
    }

    public static List<String> getDeniedPermissions() {
        return mDeniedPermissions;
    }

    public static List<String> getUnshowedPermissions() {
        return mUnshowedPermissions;
    }

    public static List<String> getNeedRequestPermissions() {
        return mNeedRequestPermissions;
    }
    /**
     * Checks given permissions are needed to show rationale.
     *
     * @param activity    activity
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        if(activity == null){
            return false;
        }
        if(permissions == null || permissions.length < 1){
            return false;
        }

        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks given permissions are needed to show rationale for fragment.
     *
     * @param fragment    fragment
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    public static boolean shouldShowRequestPermissionRationaleByFragment(Fragment fragment, String... permissions) {
        if(fragment == null){
            return false;
        }
        return shouldShowRequestPermissionRationale(fragment.getActivity(), permissions);
    }

    public static void sortUnshowPermission(Activity activity, String... permissions) {
        if(activity == null){
            return ;
        }
        mUnshowedPermissions.clear();
        mNeedRequestPermissions.clear();
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                mNeedRequestPermissions.add(permission);
            } else {
                mUnshowedPermissions.add(permission);
            }
        }
    }
    public static void sortUnshowPermissionByFragment(Fragment fragment, String... permissions) {
        if(fragment == null){
            return ;
        }

        mUnshowedPermissions.clear();
        mNeedRequestPermissions.clear();
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                mNeedRequestPermissions.add(permission);
            } else {
                mUnshowedPermissions.add(permission);
            }
        }
    }

}
