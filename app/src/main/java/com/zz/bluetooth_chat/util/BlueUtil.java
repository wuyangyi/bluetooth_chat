package com.zz.bluetooth_chat.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */
public class BlueUtil {

    /**
     * 设置蓝牙永久可见
     */
    public static void setBlueAlwaysCanLook(){
//        //声明一个class类
//        Class serviceManager = null;
//        try {//得到这个class的类
//            serviceManager = Class.forName("android.bluetooth.BluetoothAdapter");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }//声明一个方法
//        Method method = null;
//        try {//得到指定的类中的方法
//            method = serviceManager.getMethod("setDiscoverableTimeout", int.class);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        try {//调用这个方法
//            method.invoke(serviceManager.newInstance(), 30);//根据测试，发现这一函数的参数无论传递什么值，都是永久可见的
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 3000);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭蓝牙可见性
     */
    public static void closeBlueCanLook() {
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置蓝牙可见300s
     */
    public static void setBlueCanLook(Activity activity) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        //使本机蓝牙在300秒内可被搜索
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivity(discoverableIntent);
        }
    }

    /**
     * 获得蓝牙地址
     */
    @SuppressLint("HardwareIds")
    public static String getBtAddressByReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.getAddress();
//        Field field = null;
//        try {
//            field = BluetoothAdapter.class.getDeclaredField("mService");
//            field.setAccessible(true);
//            Object bluetoothManagerService = field.get(bluetoothAdapter);
//            if (bluetoothManagerService == null) {
//                return null;
//            }
//            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
//            if(method != null) {
//                Object obj = method.invoke(bluetoothManagerService);
//                if(obj != null) {
//                    return obj.toString();
//                }
//            }
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    /**
     * 获取蓝牙名称
     */
    public static String getBtNameByReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("蓝牙", bluetoothAdapter.getName());
        return bluetoothAdapter.getName();
//        Field field = null;
//        try {
//            field = BluetoothAdapter.class.getDeclaredField("mService");
//            field.setAccessible(true);
//            Object bluetoothManagerService = field.get(bluetoothAdapter);
//            if (bluetoothManagerService == null) {
//                return null;
//            }
//            Method method = bluetoothManagerService.getClass().getMethod("getName");
//            if(method != null) {
//                Object obj = method.invoke(bluetoothManagerService);
//                if(obj != null) {
//                    return obj.toString();
//                }
//            }
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
    }
}
