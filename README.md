# VersionSample
Android Version Helper library


## About
VersionLib是一个逻辑框架，实现对Android App版本进行更新的一个功能，能实现自动更新以及在WiFi下进行自动更新和更新提示灯内容；
在完成整个逻辑的过程中主要是通过接口回调的方式去进行一些相关的操作<br>
<br>
逻辑框架的逻辑：<br>
1.先获取服务器的版本信息<br>
2.解析数据并判断是否需要进行版本更新<br>
3.需要更新,则弹窗提醒用户去更新(设置自动更新不会弹窗)<br>
4.下载新版本APP文件<br>
5.提醒安装新版程序<br>

## How to use



## 权限：
库中会使用到如下权限，需要动态申请：<br>
android.permission.WRITE_EXTERNAL_STORAGE<br>
android.permission.READ_EXTERNAL_STORAGE<br>
android.permission.ACCESS_NETWORK_STATE<br>


## 代码实现：
### 1.新建一个VersionSample(类名去什么都可以)继承VersionHelper，去实现如下接口；<br>
NewVersionInfo onInitNewVersion(String httpResult)<br>
根据请求版本更新接口返回的数据去解析出一个NewVersionInfo(新版本信息并返回)；<br>
<br>
boolean onCheckVersionCode(int newVersionCode, int appVersionCode)<br>
boolean onCheckVersionName(String newVersionName, String appVersionName)<br>
这两个回调分别是按照版本号检查是否需要更新和按照版本名检查是否需要进行更新；<br>
<br>
void onShowUpdateDialog(NewVersionInfo newVersionInfo, VersinInfo versinInfo, UpdateHelper helper)<br>
当检查到需要进行版本更新切未设置自动更新的时候，提醒用户更新的弹窗回调，在该方法中自己弹窗去提醒用户更新新版本；当用户点击更新时，<br>
调用onDialogUpdate()方法去更新；当用户点击取消更新的时候，调用onDialogCancleUpdate()去取消更新即可。<br>
参数说明：<br>
newVersionInfo：新版本信息<br>
versinInfo：app的版本信息<br>
helper：版本更新帮助类<br>
<br>
void onDownloadNewVersionPre(NewVersionInfo newVersion, long fileSize, String downloadPath)<br>
在开始下载新版本文件之前调用，可以在这里创建Notifycation信息，在接下来的下载过程中去显示下载进度。<br>
参数说明：<br>
newVersion：新版本信息<br>
fileSize：新版本文件大小<br>
downloadPath：新版本文件下载到本地的地址<br>
<br>
void onDownloadNewVersion(NewVersionInfo newVersion, long fileSize, long downloadSize, String downloadPath)<br>
正在下载新版本文件会调用，可以显示当前的下载进度信息<br>
newVersion：新版本信息<br>
fileSize：新版本文件大小<br>
downloadSize：已下载文件大小<br>
downloadPath：新版本文件下载地址<br>
<br>
onDownloadFaild(int faildCode)<br>
新版本下载失败会调用，faildCode=-1；<br>
<br>
void onInstallAppPre(NewVersionInfo newVersion, VersinInfo versinInfo, String appPath)<br>
在新版本下载完成后会被调用，可以在该方法内去安装新版App<br>
参数说明：<br>
newVersion：新版本信息<br>
versinInfo：当前app的版本信息<br>
appPath：新版app的文件地址<br>
<br>

### 2.先配置版本更新接口，配置新版更新服务器地址，请求方式，请求参数等信息；
```Java
HttpBuilder httpBuilder=new HttpBuilder()
                    .setMethod(HttpAsyncTask.Method.Get)//请求方法，get/post
                    .setTimeOut(15*1000)//设置请求超时时间
                    .setUrl("url");//新版更新的地址
                    //.setValues();//配置请求需要携带的参数
```                  
                   
                    
### 3.配置版本更新信息
```Java
Builder builder=new Builder()
                    .setHttpBuilder(httpBuilder).setWifiAutoUpdate(true);
                    //设置自动更新，只要有新版本就会更新
                    //.setAutoUpdate(true);
                    //设置新版app下载存放的地址，不设置的话默认为
                    //Context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/apk/apk文件名"
                    //.setNewApkPath("path")
                    //设置wifi下是否自动更新，已开启自动更新，则wifi自动更新也会被开启；当开启自动更新后，
                    // 就不会回调onShowUpdateDialog而是直接去自动下载和安装
                    //.setWifiAutoUpdate(true)
                    //设置检查是否进行版本更新回调，在VersionHelper中已进行设置，就不要重复设置了
                    //.setOnCheckVersion(null)
                    //设置版本更新相关UI操作回调
                    //.setOnVersionUpdate(null);
```                    
                    
### 4.设置配置信息  
```Java
sVersionSample=new VersionSample(app, builder);
```

## 代码调用
```Javas
sVersionSample.checkNewVersion();
```
