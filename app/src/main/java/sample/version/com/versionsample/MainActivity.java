package sample.version.com.versionsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }else{
            checkNewVersion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            boolean bool=grantResults.length>0;
            for(int grant:grantResults)bool&=grant==PackageManager.PERMISSION_GRANTED;
            if(bool){//权限验证通过
                checkNewVersion();
            }else{
                //权限被拒绝
            }
        }
    }


    private void checkNewVersion(){
        VersionSample.createHelper(getApplication());
        VersionSample sample=VersionSample.getInstance(this);
        sample.checkNewVersion();
    }
}
