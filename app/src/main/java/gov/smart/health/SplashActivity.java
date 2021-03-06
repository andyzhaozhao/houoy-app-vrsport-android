package gov.smart.health;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fitpolo.support.OrderEnum;
import com.fitpolo.support.bluetooth.BluetoothModule;
import com.fitpolo.support.callback.ConnStateCallback;
import com.fitpolo.support.callback.OrderCallback;
import com.fitpolo.support.entity.BaseResponse;
import com.fitpolo.support.task.InnerVersionTask;

import gov.smart.health.activity.HomeActivity;
import gov.smart.health.activity.login.LoginActivity;
import gov.smart.health.activity.self.DeviceSettingActivity;
import gov.smart.health.fragment.Splash.SplashFragmentPagerAdapter;
import gov.smart.health.utils.SHConstants;
import gov.smart.health.utils.SharedPreferencesHelper;

public class SplashActivity extends AppCompatActivity {

    private View btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(!SharedPreferencesHelper.gettingBoolean(SHConstants.IsShowSplash,false)) {
            setViews();
        }else {
            toHome();
        }
    }

    private void setViews() {
        RadioButton r1 = (RadioButton)findViewById(R.id.splash_page_r1);
        r1.setChecked(true);
        btnStart = findViewById(R.id.btn_start);
        btnStart.setVisibility(View.GONE);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper.settingBoolean(SHConstants.IsShowSplash,true);
                toHome();
            }
        });
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        SplashFragmentPagerAdapter adapter = new SplashFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        RadioButton r1 = (RadioButton)findViewById(R.id.splash_page_r1);
                        r1.setChecked(true);
                        break;
                    case 1:
                        RadioButton r2 = (RadioButton)findViewById(R.id.splash_page_r2);
                        r2.setChecked(true);
                        break;
                    case 2:
                        RadioButton r3 = (RadioButton)findViewById(R.id.splash_page_r3);
                        r3.setChecked(true);
                        break;
                }

                if(position == 2){
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void toHome(){
        BluetoothModule bluetoothModule = BluetoothModule.getInstance();
        if (bluetoothModule.isBluetoothOpen()) {
            String deviceAddress = SharedPreferencesHelper.gettingString(DeviceSettingActivity.AddressKey, null);
            if (deviceAddress != null && !bluetoothModule.isConnDevice(getApplicationContext(), deviceAddress)) {
                bluetoothModule.createBluetoothGatt(getApplicationContext(), deviceAddress, null);
                bluetoothModule.setOpenReConnect(true);
                InnerVersionTask task = new InnerVersionTask(new OrderCallback() {
                    @Override
                    public void onOrderResult(OrderEnum order, BaseResponse response) {

                    }

                    @Override
                    public void onOrderTimeout(OrderEnum order) {

                    }

                    @Override
                    public void onOrderFinish() {

                    }
                });
                BluetoothModule.getInstance().sendOrder(task);
            }
        }

        Intent intent = new Intent();
        String pk = SharedPreferencesHelper.gettingString(SHConstants.LoginUserPkPerson,"");
        if(pk.isEmpty()) {
            intent.setClass(getApplication(), LoginActivity.class);
        } else {
            intent.setClass(getApplication(), HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
