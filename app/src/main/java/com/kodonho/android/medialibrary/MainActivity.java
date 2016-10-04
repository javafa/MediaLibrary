package com.kodonho.android.medialibrary;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // 결과값을 넘겨받을 때 비교하는 요청 코드
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 안드로이드 버전이 마시멜로우 미만일 경우 데이터를 그냥 세팅
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            initData();
        else
            checkPermissions(); // 마시멜로우 이상일 경우는 런타임 권한을 체크해야 한다
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        // 런타임 권한 체크 (디스크읽기권한)
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            // 요청할 권한 배열생성
            String permissionArray[] = { Manifest.permission.READ_EXTERNAL_STORAGE };
            // 런타임 권한요청을 위한 팝업창 출력
            requestPermissions( permissionArray , REQUEST_CODE );
        }else{
            // 런타임 권한이 이미 있으면 데이터를 세팅한다
            initData();
        }
    }

    // 권한 체크 팝업창 처리후 호출되는 콜백함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE: // 요청코드가 위의 팝업창에 넘겨준 코드와 같으면
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 권한을 체크하고
                    // 권한이 있으면 데이터를 생성한다
                    initData();
                }
                break;
        }
    }

    public void initData(){
        ArrayList<RecyclerData> datas = getMusicInfo();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerCardView);
        RecyclerCardAdapter adapter = new RecyclerCardAdapter(datas, R.layout.activity_recycler_card_item, this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    public ArrayList<RecyclerData> getMusicInfo(){
        ArrayList<RecyclerData> datas = new ArrayList<>();

        // 미디어 스토어에서 가져올 컬럼명 세팅
        String projections[] = {
                MediaStore.Audio.Media._ID,       // 노래아이디
                MediaStore.Audio.Media.ALBUM_ID,  // 앨범아이디
                MediaStore.Audio.Media.TITLE,     // 제목
                MediaStore.Audio.Media.ARTIST     // 가수
        };

        //getContentResolver().query(주소, 검색해올컬럼명들, 조건절, 조건절에매핑되는값, 정렬);
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projections, null, null, null);
        /*
        - uri        : content://스키마 형태로 정해져 있는 곳의 데이터를 가져온다
        - projection : 가져올 컬럼 이름들의 배열. null 을 입력하면 모든값을 가져온다
        - selection : 조건절(where)에 해당하는 내용
        - selectionArgs : 조건절이 preparedstatement 형태일 때 ? 에 매핑되는 값의 배열
        - sort order    : 정렬 조건
         */

        if(cursor != null){
            while(cursor.moveToNext()){
                RecyclerData data = new RecyclerData();
                // 데이터에 가수이름을 입력
                // 1. 가수 이름 컬럼의 순서(index)를 가져온다
                int idx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                // 2. 해당 index를 가진 컬럼의 실제값을 가져온다
                data.artist = cursor.getString(idx);

                idx = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                data.title = cursor.getString(idx);

                idx = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                data.albumId = cursor.getString(idx);

                idx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                data.musicId = cursor.getString(idx);

                datas.add(data);
            }
        }
        cursor.close();
        return datas;
    }
}
