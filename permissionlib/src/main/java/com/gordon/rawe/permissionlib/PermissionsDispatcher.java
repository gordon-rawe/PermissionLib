package com.gordon.rawe.permissionlib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.gordon.rawe.permissionlib.view.PermissionSettingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理工具类
 * <p/>
 * Created by pengjiang on 2015/10/8.
 */
public final class PermissionsDispatcher {
    private static boolean mIsShowDialog = true;
    public static final String TAG_PERMISSION_UNSHOW = "TAG_PERMISSION_UNSHOW";
    private PermissionsDispatcher() {
    }

    public static void checkPermissions(final Activity act, int requestCode, PermissionListener listener, boolean isShowDialog, String... permissions) {
        mIsShowDialog = isShowDialog;
        checkPermissions(act, requestCode, listener, permissions);
    }
    /**
     * check permissions are whether granted or not
     *
     * @param act
     * @param requestCode
     * @param listener
     * @param permissions
     */
    public static void checkPermissions(final Activity act, int requestCode, PermissionListener listener, String... permissions) {

        if (act == null) {
            if (listener != null) {
                listener.onPermissionsError(requestCode, null, "checkPermissions()-->param act :the activity is null", permissions);
            }
            return;
        }
        if (permissions == null || permissions.length < 1) {
            if (listener != null) {
                listener.onPermissionsError(requestCode, null, "checkPermissions()-->param permissions: is null or length is 0", permissions);
            }
            return;
        }

        /*****
         * check permissions are granted ?
         * */
        PermissionUtils.sortGrantedAndDeniedPermissions(act, permissions);

        if (PermissionUtils.getGrantedPermissions().size() > 0) {
            List<String> grantedPermissionsList = PermissionUtils.getGrantedPermissions();
            String[] grantedPermissionsArr = grantedPermissionsList.toArray(new String[grantedPermissionsList.size()]);

            if (listener != null) {
                listener.onPermissionsGranted(requestCode, null, grantedPermissionsArr);
            }
        }

        if (PermissionUtils.getDeniedPermissions().size() > 0) {
            List<String> deniedPermissionsList = PermissionUtils.getDeniedPermissions();
            String[] deniedPermissionsArr = deniedPermissionsList.toArray(new String[deniedPermissionsList.size()]);
            if (deniedPermissionsArr.length > 0) {
                PermissionUtils.sortUnshowPermission(act, deniedPermissionsArr);
            }
        }

        if (PermissionUtils.getUnshowedPermissions().size() > 0) {
            List<String> unShowPermissionsList = PermissionUtils.getUnshowedPermissions();
            String[] unShowPermissionsArr = unShowPermissionsList.toArray(new String[unShowPermissionsList.size()]);
            if (listener != null) {
                if(true == mIsShowDialog) {
                    StringBuilder message = getUnShowPermissionsMessage(unShowPermissionsList);
                    showMessage_GotoSetting(message.toString(), act);
                }
                listener.onShowRequestPermissionRationale(requestCode, false, unShowPermissionsArr);
            }
        }

        mIsShowDialog = true;

        if (PermissionUtils.getNeedRequestPermissions().size() > 0) {//true 表示允许弹申请权限框
            List<String> needRequestPermissionsList = PermissionUtils.getNeedRequestPermissions();
            String[] needRequestPermissionsArr = needRequestPermissionsList.toArray(new String[needRequestPermissionsList.size()]);
            if (listener != null) {
                listener.onShowRequestPermissionRationale(requestCode, true, needRequestPermissionsArr);
            }
        }
    }

    private static StringBuilder getUnShowPermissionsMessage(List<String> list){
        StringBuilder message =  new StringBuilder("您已关闭了");
        String permisson;
        boolean hasCALENDAR = false;
        boolean hasCAMERA = false;
        boolean hasCONTACTS = false;
        boolean hasLOCATION = false;
        boolean hasMICROPHONE = false;
        boolean hasPHONE = false;
        boolean hasSENSORS = false;
        boolean hasSMS = false;
        boolean hasSTORAGE = false;

        if(list.size() == 1) {
            permisson = list.get(0);
            if(permisson.contains("CALENDAR")) {
                message.append("日历 ");
            } else if(permisson.contains("CAMERA")) {
                message.append("相机 ");

            } else if(permisson.contains("CONTACTS") || permisson.equals("android.permission.GET_ACCOUNTS")) {
                message.append("通讯录 ");

            } else if(permisson.contains("LOCATION")) {
                message.append("定位 ");

            } else if(permisson.equals("android.permission.RECORD_AUDIO")) {
                message.append("耳麦 ");

            } else if(permisson.contains("PHONE")
                    || permisson.contains("CALL_LOG")
                    || permisson.contains("ADD_VOICEMAIL")
                    || permisson.contains("USE_SIP")
                    || permisson.contains("PROCESS_OUTGOING_CALLS")) {
                message.append("电话 ");

            } else if(permisson.contains("BODY_SENSORS")) {
                message.append("身体传感 ");

            } else if(permisson.contains("SMS")
                    || permisson.contains("RECEIVE_WAP_PUSH")
                    || permisson.contains("RECEIVE_MMS")
                    || permisson.contains("READ_CELL_BROADCASTS")) {
                message.append("短信 ");

            } else if(permisson.contains("STORAGE")) {
                message.append("手机存储 ");

            }
        } else {
            for(int i = 0; i< list.size(); i++) {
                permisson = list.get(i);
                if(permisson.contains("CALENDAR") && hasCALENDAR == false) {
                    message.append("日历");
                    hasCALENDAR = true;
                } else if(permisson.contains("CAMERA") && hasCAMERA == false) {
                    message.append("相机");
                    hasCAMERA = true;
                } else if(permisson.contains("CONTACTS")
                        || permisson.equals("android.permission.GET_ACCOUNTS")
                        && hasCONTACTS == false) {
                    message.append("通讯录");
                    hasCONTACTS = true;
                } else if(permisson.contains("LOCATION")  && hasLOCATION == false) {
                    message.append("定位");
                    hasLOCATION = true;
                } else if(permisson.equals("android.permission.RECORD_AUDIO")  && hasMICROPHONE == false) {
                    message.append("耳麦");
                    hasMICROPHONE = true;
                } else if(permisson.contains("PHONE")
                        || permisson.contains("CALL_LOG")
                        || permisson.contains("ADD_VOICEMAIL")
                        || permisson.contains("USE_SIP")
                        || permisson.contains("PROCESS_OUTGOING_CALLS") && hasPHONE == false) {
                    message.append("电话");
                    hasPHONE = true;
                } else if(permisson.contains("BODY_SENSORS")  && hasSENSORS == false) {
                    message.append("身体传感");
                    hasSENSORS = true;
                } else if(permisson.contains("SMS")
                        || permisson.contains("RECEIVE_WAP_PUSH")
                        || permisson.contains("RECEIVE_MMS")
                        || permisson.contains("READ_CELL_BROADCASTS")  && hasSMS == false) {
                    message.append("短信");
                    hasSMS = true;
                } else if(permisson.contains("STORAGE")  && hasSTORAGE == false) {
                    message.append("手机存储");
                    hasSTORAGE = true;
                }
                if(i <  list.size() -1) {
                    message.append(",");
                }
            }
        }

        message.append("访问权限，为了保证功能的正常使用，请前往系统设置页面开启");
        return message;
    }

    private static void gotoPermissionSetting(Activity act) {
        Uri packageURI = Uri.parse("package:" + act.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        act.startActivity(intent);
    }

    private static void showMessage_GotoSetting(final String message, final Activity act) {

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PermissionSettingDialog.Builder builder = new PermissionSettingDialog.Builder(act);
                builder.setMessage(message);
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("设置",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                gotoPermissionSetting(act);
                            }
                        });

                builder.create().show();
            }
        });

    }

    /**
     * request permissions to be granted
     *
     * @param act
     * @param requestCode
     * @param permissions
     */
    public static void requestPermissions(Activity act, int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(act, permissions, requestCode);
    }

    public static void checkPermissionsByFragment(Fragment fragment, int requestCode, PermissionListener listener, boolean isShowDialog, String... permissions) {
        mIsShowDialog = isShowDialog;
        checkPermissionsByFragment(fragment, requestCode, listener, permissions);
    }
    /**
     * check permissions are whether granted or not for fragment
     *
     * @param fragment
     * @param requestCode
     * @param listener
     * @param permissions
     */
    public static void checkPermissionsByFragment(Fragment fragment, int requestCode, PermissionListener listener, String... permissions) {

        if (fragment == null) {
            if (listener != null) {
                listener.onPermissionsError(requestCode, null, "checkPermissions()-->param act :the activity is null", permissions);
            }
            return;
        }
        if (permissions == null || permissions.length < 1) {
            if (listener != null) {
                listener.onPermissionsError(requestCode, null, "checkPermissions()-->param permissions: is null or length is 0", permissions);
            }
            return;
        }

        PermissionUtils.sortGrantedAndDeniedPermissions(fragment.getContext(), permissions);

        if (PermissionUtils.getGrantedPermissions().size() > 0) {
            List<String> grantedPermissionsList = PermissionUtils.getGrantedPermissions();
            String[] grantedPermissionsArr = grantedPermissionsList.toArray(new String[grantedPermissionsList.size()]);

            if (listener != null) {
                listener.onPermissionsGranted(requestCode, null, grantedPermissionsArr);
            }
        }

        if (PermissionUtils.getDeniedPermissions().size() > 0) {
            List<String> deniedPermissionsList = PermissionUtils.getDeniedPermissions();
            String[] deniedPermissionsArr = deniedPermissionsList.toArray(new String[deniedPermissionsList.size()]);
            if (deniedPermissionsArr.length > 0) {
                PermissionUtils.sortUnshowPermissionByFragment(fragment, deniedPermissionsArr);
            }
        }

        if (PermissionUtils.getUnshowedPermissions().size() > 0) {
            List<String> unShowPermissionsList = PermissionUtils.getUnshowedPermissions();
            String[] unShowPermissionsArr = unShowPermissionsList.toArray(new String[unShowPermissionsList.size()]);
            if (listener != null) {
                if(true == mIsShowDialog) {
                    StringBuilder message = getUnShowPermissionsMessage(unShowPermissionsList);
                    showMessage_GotoSetting(message.toString(), fragment.getActivity());
                }

                listener.onShowRequestPermissionRationale(requestCode, false, unShowPermissionsArr);
            }
        }

        mIsShowDialog = true;

        if (PermissionUtils.getNeedRequestPermissions().size() > 0) {//true 表示允许弹申请权限框
            List<String> needRequestPermissionsList = PermissionUtils.getNeedRequestPermissions();
            String[] needRequestPermissionsArr = needRequestPermissionsList.toArray(new String[needRequestPermissionsList.size()]);
            if (listener != null) {
                listener.onShowRequestPermissionRationale(requestCode, true, needRequestPermissionsArr);
            }
        }
    }

    /**
     * request permissions to be granted for fragment
     *
     * @param fragment
     * @param requestCode
     * @param permissions
     */
    public static void requestPermissionsByFragment(Fragment fragment, int requestCode, String... permissions) {

        fragment.requestPermissions(permissions, requestCode);
    }


    /**
     * do their permissions results for fragment
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param listener
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, PermissionListener listener) {

        List<String> grantedPermissions = new ArrayList<String>();
        List<String> deniedPermissions = new ArrayList<String>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            } else {
                grantedPermissions.add(permissions[i]);
            }
        }
        if (grantedPermissions.size() > 0) {
            String[] grantedPermissionsArr = grantedPermissions.toArray(new String[grantedPermissions.size()]);

            if (listener != null) {
                listener.onPermissionsGranted(requestCode, null, grantedPermissionsArr);
            }
        }
        if (deniedPermissions.size() > 0) {
            String[] deniedPermissionsArr = deniedPermissions.toArray(new String[deniedPermissions.size()]);

            if (listener != null) {
                listener.onPermissionsDenied(requestCode, null, deniedPermissionsArr);
            }
        }
    }
}

