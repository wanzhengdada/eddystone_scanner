package com.example.wandada.eddystonescanner;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements BeaconConsumer{
    ListView beaconList;
    protected static final String TAG = "MonitoringActivity";
    //这个类用于让Beacon和Activity或者Service进行交互。
    //通常与BeaconConsumer结合在一起使用，也经常通过BeaconManger来绑定服务，设置Beacon格式等。
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //在布局中加入listview控件
        beaconList = (ListView) findViewById(R.id.beacon_list);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        //BeaconParser可以用于告诉库如何通过指定哪些字节偏移与哪些字段匹配来解码来自蓝牙LE广播的信标的字段，以及什么字节序列表示信标

        //public static final String EDDYSTONE_TLM_LAYOUT
        //Constant Value: "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        // Detect the telemetry (TLM) frame:
        //public static final String EDDYSTONE_UID_LAYOUT
        //Constant Value: "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));

        // Detect the URL frame:
        //public static final String EDDYSTONE_URL_LAYOUT
        //Constant Value: "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-21v"
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        //绑定到服务
        //在使用BeaconConsumer Interface实现活动之后，此代码将一个活动绑定到服务，当它收到回调说该服务已准备就绪时，它开始测距。
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑
        beaconManager.unbind(this);
    }


    /**
     * BeaconConsumer类的回调方法，当BeaconService准备完毕时调用该方法
     */

    @Override
    public void onBeaconServiceConnect() {
        //指定每次BeaconService获取测距数据时应调用的类，在检测到信标时名义上每秒一次。
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            //didRangeBeaconsInRegion每秒调用一次，以估计对可见信标的距离
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                if (beacons.size() > 0) {
                    //ArrayList一个其容量能够动态增长的动态数组
                    final ArrayList<BeaconModel> list = new ArrayList<BeaconModel>();
                    //Iteractor对象成为迭代器，用以实现对容器内元素的遍历操作
                    Iterator<Beacon> it = beacons.iterator();
                    //判断游标右边是否有元素
                    while (it.hasNext()) {
                        //返回游标右边的元素
                        Beacon beacon = it.next();
                        //新建 BeaconModel
                        BeaconModel model = new BeaconModel();
                        if (beacon.getServiceUuid() == 0xfeaa) {
                            // This is Eddystone, which uses a service Uuid of 0xfeaa
                            Identifier eddystoneNamespaceId = beacon.getId1();
                            Identifier eddystoneInstanceId = beacon.getId2();
                            if (beacon.getExtraDataFields().size() > 0) {
                                long telemetryVersion = beacon.getExtraDataFields().get(0);
                                long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                                long temperature = beacon.getExtraDataFields().get(2);
                                long pduCount = beacon.getExtraDataFields().get(3);
                                long uptime = beacon.getExtraDataFields().get(4);
                            }
                        }
                        else
                        {

                        }
                        //调用model函数
                        model.setBeaconName("Rssi: " + beacon.getRssi()+" dBm" + "        Power: " + beacon.getTxPower()+" dBm");
                        model.setDistance("Dist: " + beacon.getDistance()+" m");
                        model.setNameSpace("NameSpace: " + beacon.getId1());
                        model.setInsTance("Instance: " + beacon.getId2());
                        model.setTLM("Battery: " +beacon.getExtraDataFields().get(1)+" mv" + "      Temperature: " + beacon.getExtraDataFields().get(2)/256.0f+" ℃");
                        list.add(model);
                    }
                    //更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //借助适配器将数组中数据传给listview
                            CustomBeaconAdapter adapter = new CustomBeaconAdapter(list, MainActivity.this);
                            beaconList.setAdapter(adapter);
                        }
                    });
                }
            }
        });
        //在didEnterRegion覆盖方法内部，因为当我们检测到我们已经进入了信标存在的区域时，
        // 我们使用区域的参数调用startRangingBeaconsinRegion，其中有可能找到信标
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
