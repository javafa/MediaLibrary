package com.kodonho.android.medialibrary;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by HM on 2016-09-28.
 */
public class RecyclerCardAdapter extends RecyclerView.Adapter<RecyclerCardAdapter.ViewHolder>{

    ArrayList<RecyclerData> datas;
    int itemLayout;
    Context context;

    public RecyclerCardAdapter(ArrayList<RecyclerData> datas, int itemLayout, Context context){
        this.datas = datas;
        this.itemLayout = itemLayout;
        this.context = context;
    }

    // view 를 만들어서 홀더에 저장하는 역할
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    // listView getView 를 대체하는 함수
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RecyclerData data = datas.get(position);
        //holder.img.setImageResource(data.image);
        holder.img.setImageBitmap(getAlbumArtImage(context, Integer.parseInt(data.albumId)));
        holder.textTitle.setText(data.title);
        holder.textArtist.setText(data.artist);
        holder.itemView.setTag(data);


        setAnimation(holder.cardView, position);
    }

    int lastPosition = -1;
    public void setAnimation(View view, int position){

        if(position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    // 앨범이미지 가져오기
    public static final Bitmap getAlbumArtImage(Context p_Context, long p_AlbumId){
        Bitmap cover = null;
        ByteArrayOutputStream w_OutBuf = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int w_bFirst = 1;
        int w_nZeroCount = 0;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.valueOf(p_AlbumId));
        ContentResolver res = p_Context.getContentResolver();
        InputStream in;
        try {
            in = res.openInputStream(uri);
            while(true) {
                int count = in.read(buffer);
                if(count == -1){
                    break;
                }
                if(w_bFirst == 1){
                    //. 맨 첫 바이트토막을 쓰는 경우 앞에 붙은 0값들은 제외한다.
                    for(int i = 0; i < count; i++){
                        if(buffer[i] == 0){
                            w_nZeroCount++;
                        }
                        else{
                            break;
                        }
                    }
                    w_OutBuf.write(buffer, w_nZeroCount, count - w_nZeroCount);
                    w_bFirst = 0;
                }
                else {
                    w_OutBuf.write(buffer, 0, count);
                }
            }
            cover = BitmapFactory.decodeByteArray(w_OutBuf.toByteArray(), 0, w_OutBuf.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cover;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textTitle;
        TextView textArtist;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.image);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            textArtist = (TextView) itemView.findViewById(R.id.artist);
            cardView = (CardView) itemView.findViewById(R.id.cardItem);
        }
    }
}
