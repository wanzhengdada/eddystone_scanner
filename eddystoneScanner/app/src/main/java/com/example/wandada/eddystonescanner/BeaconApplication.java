package com.example.wandada.eddystonescanner;


import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

//创建一个类继承Application并在AndroidManifest.xml文件中的application标签中进行注册
//启动Application时，系统会创建一个PID，即进程ID，所有的Activity都会在此进程上运行。
//那么我们在Application创建的时候初始化全局变量，同一个应用的所有Activity都可以取到这些全局变量的值
//换句话说，我们在某一个Activity中改变了这些全局变量的值，那么在同一个应用的其他Activity中值就会改变。
public class BeaconApplication extends Application implements BootstrapNotifier, BeaconConsumer {

    BeaconManager beaconManager;
    public static String TAG = "BeaconApplication";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initiateBeaconService() {
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());

        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));

        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));

        //beaconManager.setDebug(true);

        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundBetweenScanPeriod(30000l);

        beaconManager.bind(this);
    }

    @Override
    //当区域中至少有一个信标可见时调用
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Got a didEnterRegion call");
    }

    @Override
    //当区域中没有信标可见时调用。
    public void didExitRegion(Region region) {
        Log.d(TAG, "Got a didExitRegion call");
    }

    @Override
    //当区域中至少有一个信标可见时，使用状态值MonitorNotifier.INSIDE调用。
    public void didDetermineStateForRegion(int i, Region region) {

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("com.example.wandada.eddystonescanner", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}