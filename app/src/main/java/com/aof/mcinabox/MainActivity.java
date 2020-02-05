package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aof.mcinabox.jsonUtils.AnaliesMinecraftVersionJson;
import com.aof.mcinabox.jsonUtils.AnaliesVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ListVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ModelMinecraftVersionJson;

public class MainActivity extends AppCompatActivity {
Button[] launcherBts;
Button button1,button2,button3,button4,button5,button6,button7,button8;
Button testButton;
LinearLayout[] launcherLins;
LinearLayout layout1,layout2,layout3,layout4,layout5,layout6;
DownloadMinecraft downloadTask = new DownloadMinecraft();
ListVersionManifestJson.Version[] versionList;
ModelMinecraftVersionJson minecraftVersionJson;
Spinner spinnerVersionList;
int targetPos;
private BroadcastReceiver broadcastReceiver1;
private BroadcastReceiver broadcastReceiver2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Activity生命周期开始，执行初始化
        super.onCreate(savedInstanceState);
        //显示activity_main为当前Activity布局
        setContentView(R.layout.activity_main);

        //使用Toolbar作为Actionbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //请求软件所需的权限
        requestPermission();

        //给界面的按键设置按键监听
        button1 = findViewById(R.id.main_linear1_button1);
        button2 = findViewById(R.id.main_linear1_button2);
        button3 = findViewById(R.id.main_linear1_button3);
        button4 = findViewById(R.id.main_linear1_button4);
        button5 = findViewById(R.id.main_linear1_button5);
        button6 = findViewById(R.id.main_linear1_button6);
        button7 = findViewById(R.id.main_linear3_flash1);
        button8 = findViewById(R.id.main_linear3_download1);
        testButton = findViewById(R.id.test);
        launcherBts = new Button[]{button1,button2,button3,button4,button5,button6,button7,button8,testButton};
        for(Button button : launcherBts ){
            button.setOnClickListener(listener);
        }

        //给linearlayout设置对象数组
        layout1 = findViewById(R.id.main_linear2);
        layout2 = findViewById(R.id.main_linear3);
        layout3 = findViewById(R.id.main_linear4);
        layout4 = findViewById(R.id.main_linear5);
        layout5 = findViewById(R.id.main_linear6);
        layout6 = findViewById(R.id.main_linear7);
        launcherLins = new LinearLayout[] {layout1,layout2,layout3,layout4,layout5,layout6};

        //初始化Spinner控件
        spinnerVersionList = findViewById(R.id.main_linear3_spinner);

    }

    //重写boolean onCreatOptionsMenu(Menu menu)方法实现Toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    //重写boolean onOptionsItemSelected(MenuItem item)方法实现Toolbar的菜单的按键监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_action1:
                //ToolBar菜单的按键监听
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void startFloatingService(View view) {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "请授权本地存储权限", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName())), 0);
        }
    }*/

    public void requestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if ( !ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //Button数组launchbts中的按键监听
    private View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch(arg0.getId()){
                case R.id.main_linear1_button1:
                    //具体点击操作的逻辑
                    setVisibleLinearLyout(layout1);
                    break;
                case R.id.main_linear1_button2:
                    setVisibleLinearLyout(layout2);
                    break;
                case R.id.main_linear1_button3:
                    setVisibleLinearLyout(layout3);
                    break;
                case R.id.main_linear1_button4:
                    setVisibleLinearLyout(layout4);
                    break;
                case R.id.main_linear1_button5:
                    setVisibleLinearLyout(layout5);
                    break;
                case R.id.main_linear1_button6:
                    setVisibleLinearLyout(layout6);
                    break;
                case R.id.main_linear3_flash1:
                    //这里使用了多线程
                    Thread syncTask = new Thread() {
                        @Override
                        public void run() {
                            // 执行操作
                            Looper.prepare();
                            long Id = DownloadVersionList();
                            listener1(Id);
                            /*
                            //处理完成后给handler发送消息
                            Message msg = new Message();
                            msg.what = COMPLETED;
                            handler.sendMessage(msg);
                            */
                            Looper.loop();
                        }
                    };
                    syncTask.start();
                    button8.setClickable(true);
                    break;
                case R.id.main_linear3_download1:
                    DownloadVersionFirst();
                    break;
                case R.id.test:
                    break;
                default:
                    break;
            }
        }
    };

    //用于接收子线程传来的ui变化
    /*
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                loadSpinnerVersionList();
            }
        }
    };*/

    //下载Minecraft清单列表
    private long DownloadVersionList(){
        downloadTask.setInformation("https://launchermeta.mojang.com", "/MCinaBox/.minecraft/");
        return (downloadTask.UpdateVersionManifestJson(this));
    }
    private void DownloadVersionFirst(){
        long taskId;
        taskId = downloadTask.DownloadMinecraftVersionJson(versionList[targetPos].getId(),versionList[targetPos].getUrl(),this);
        listener2(taskId);
    }
    private void DownloadVersionSecond(){
        //获取实例化后的versionList
        minecraftVersionJson = new AnaliesMinecraftVersionJson().getModelMinecraftVersionJson(downloadTask.getMINECRAFT_VERSION_DIR()+versionList[targetPos].getId()+"/"+versionList[targetPos].getId()+".json");
        Toast.makeText(getApplicationContext(),minecraftVersionJson.getLibraries().length,Toast.LENGTH_SHORT).show();
    }

    //测试json解析功能
    /*
    private void testJson(){

        try {
            //将version_manifest.json文件加入输入流
            InputStream inputStream = new FileInputStream(new File(downloadTask.getMINECRAFT_TEMP()+"version_manifest.json"));
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            ListVersionManifestJson listVersionManifestJson = gson.fromJson(reader, ListVersionManifestJson.class);

            ListVersionManifestJson.Version[] result = listVersionManifestJson.getVersions();
            String testid = result[0].getId();

            Toast.makeText(getApplicationContext(),testid,Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }*/

    //更新Spinner
    private void loadSpinnerVersionList(){
        //获取实例化后的versionList
        versionList = new AnaliesVersionManifestJson().getVersionList(downloadTask.getMINECRAFT_TEMP()+"version_manifest.json");
        final String[] versions = new String[versionList.length];
        //将versionList中的id值拷贝到一个String数组中作为数据源
        for(int i = 0;i < versionList.length;i++){
            versions[i] = versionList[i].getId();
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, versions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinnerVersionList .setAdapter(adapter);
        spinnerVersionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                targetPos = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //主界面逻辑，显示分界面
    private void setVisibleLinearLyout(LinearLayout layout){

        for(LinearLayout tempLayout : launcherLins){
            tempLayout.setVisibility(View.INVISIBLE);
        }
        layout.setVisibility(View.VISIBLE);
    }

    //DownloadManager下载完成事件的广播监听
    private void listener1(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    loadSpinnerVersionList();
                    Toast.makeText(getApplicationContext(), "版本清单更新完成", Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(broadcastReceiver1, intentFilter);
    }

    private void listener2(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    DownloadVersionSecond();
                }
            }
        };
        registerReceiver(broadcastReceiver2, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
    }

}
