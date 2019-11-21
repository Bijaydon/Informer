package com.heartsun.informer;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ItemClickListener{

    StaggeredAdapter staggeredAdapter;
    ProgressDialog progressDialog;
    RecyclerView recyclerfirst;
    public RelativeLayout popUpView;
    List<Message_model> message_models;

    ImageView imagee;
    TextView title,date_createdtxt,messagedesctxt;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerfirst = findViewById(R.id.recyclerfirst);
        popUpView = findViewById(R.id.popUpView);
        imagee = findViewById(R.id.imagee);
        title = findViewById(R.id.title);
        messagedesctxt = findViewById(R.id.messagedesctxt);
        date_createdtxt = findViewById(R.id.date_createdtxt);



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        //StatusBar Color

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statuscolor));


        message_models = new ArrayList<>();
        popUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpView.setVisibility(View.GONE);

            }
        });


        //ProgressDialog

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading ....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getMessageJson();
    }

    public void getMessageJson(){
        String ReceiverMobileNo = "9855059322";
        RetrofitApiInterface retrofitApiInterface = RetrofitClient.getRetrofit().create(RetrofitApiInterface.class);
        Call<List<Message_model>> modalClassCall = retrofitApiInterface.getMessage(ReceiverMobileNo);
        modalClassCall.enqueue(new Callback<List<Message_model>>() {
            @Override
            public void onResponse(Call<List<Message_model>> call, Response<List<Message_model>> response) {
                    if (response.isSuccessful()){
                        //Toast.makeText(MainActivity.this, "Response : "+response.body(), Toast.LENGTH_SHORT).show();
                        if (response.body().size() != 0){
                            message_models = response.body();
                            Toast.makeText(MainActivity.this, "I am here", Toast.LENGTH_SHORT).show();
                            staggeredAdapter = new StaggeredAdapter(getApplicationContext(), response.body());
                            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                            recyclerfirst.setLayoutManager(staggeredGridLayoutManager);
                            recyclerfirst.setAdapter(staggeredAdapter);
                            staggeredAdapter.notifyDataSetChanged();
                            staggeredAdapter.setClickListener(MainActivity.this);
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                        }
                    }else{
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MainActivity.this, "I am null response body", Toast.LENGTH_SHORT).show();
                    }


            }

            @Override
            public void onFailure(Call<List<Message_model>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Message_model message_model = message_models.get(position);
        popUpView.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        popUpView.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        title.setText(message_model.getHeadline());
        date_createdtxt.setText(message_model.getCreatedDate());
        messagedesctxt.setText(message_model.getMsgDesc());
        byte[] imageBytes = Base64.decode(message_model.getPhoto(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imagee.setImageBitmap(decodedImage);




    }
}
