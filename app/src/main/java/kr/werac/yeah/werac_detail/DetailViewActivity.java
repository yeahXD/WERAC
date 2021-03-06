package kr.werac.yeah.werac_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import kr.werac.yeah.R;
import kr.werac.yeah.data.Result;
import kr.werac.yeah.data.WeracItem;
import kr.werac.yeah.manager.NetworkManager;
import kr.werac.yeah.manager.PropertyManager;
import kr.werac.yeah.werac_modify.ModifyWeracActivity;
import okhttp3.Request;

public class DetailViewActivity extends AppCompatActivity {

    public static final String EXTRA_WERAC_ID = "MId";
    public static final int DONT_KNOW_WHY = 1000;
    int thisMid;
    WeracItem werac;
    MenuItem LikeMenu;
    int like;
    ImageView container_detail_close;
    DetailWeracFragment myDetailWeracFragment;
    Button btn_detail_1, btn_detail_2, btn_detail_360;
    TextView dummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_werac);
        werac = new WeracItem();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        thisMid = getIntent().getIntExtra(EXTRA_WERAC_ID, DONT_KNOW_WHY);

        myDetailWeracFragment = DetailWeracFragment.newInstance(thisMid);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container_detail, myDetailWeracFragment);
        ft.commit();

        btn_detail_1 = (Button) findViewById(R.id.btn_detail_to_modify_werac);
        btn_detail_2 = (Button) findViewById(R.id.btn_detail_status_modify_werac);
        btn_detail_360 = (Button) findViewById(R.id.btn_detail_close_werac);
        btn_detail_1.setVisibility(View.INVISIBLE);
        btn_detail_2.setVisibility(View.INVISIBLE);
        btn_detail_360.setVisibility(View.INVISIBLE);

        container_detail_close = (ImageView)findViewById(R.id.detail_close);
        container_detail_close.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page, menu);
        LikeMenu =  menu.findItem(R.id.btn_like);
        LikeMenu.setIcon(R.drawable.page_heart);
        setData();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }else if(item.getItemId() == R.id.btn_like) {
            if(like == 0) {
                Toast.makeText(DetailViewActivity.this, "좋아요 되었습니다", Toast.LENGTH_SHORT).show();
                LikeMenu.setIcon(R.drawable.page_heart2);
                like = 1;
                likethis();
            }else {
                Toast.makeText(DetailViewActivity.this, "좋아요 취소되었습니다", Toast.LENGTH_SHORT).show();
                LikeMenu.setIcon(R.drawable.page_heart);
                likethis();
                like = 0;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void likethis(){
        NetworkManager.getInstance().getWeracDetailLike(DetailViewActivity.this, thisMid, new NetworkManager.OnResultListener<WeracItem>() {
            @Override
            public void onSuccess(Request request, WeracItem result) {
            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(DetailViewActivity.this, "exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        NetworkManager.getInstance().getWeracDetail(DetailViewActivity.this, thisMid, new NetworkManager.OnResultListener<WeracItem>() {
            @Override
            public void onSuccess(Request request, WeracItem result) {
                werac = result;
                buttonSetting();
                if(werac.getLike() != 0){
                    for (int i = 0; i < werac.getLike(); i++) {
                        if (werac.getLikeList().get(i) == PropertyManager.getInstance().getUser().getUid()) {
                            like = 1;
                            LikeMenu.setIcon(R.drawable.page_heart2);
                        }else
                            LikeMenu.setIcon(R.drawable.page_heart);
                    }
                }else
                    LikeMenu.setIcon(R.drawable.page_heart);
            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(DetailViewActivity.this, "exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeStatus(){
        NetworkManager.getInstance().getWeracChangeStatus(DetailViewActivity.this, thisMid, new NetworkManager.OnResultListener<WeracItem>() {
            @Override
            public void onSuccess(Request request, WeracItem result) {
                werac = result;
            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(DetailViewActivity.this, "exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void joinWerac(){
        NetworkManager.getInstance().getWeracJoin(DetailViewActivity.this, thisMid, new NetworkManager.OnResultListener<Result>() {
            @Override
            public void onSuccess(Request request, Result result) {
                if(result.getSuccess() == 1)
                    myDetailWeracFragment.join();
                else if(result.getSuccess() == 2)
                    Toast.makeText(DetailViewActivity.this, "참여 정원이 꽉 찼습니다", Toast.LENGTH_SHORT).show();
                else if(result.getSuccess() == 3)
                    Toast.makeText(DetailViewActivity.this, "이미 참여하셨습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(DetailViewActivity.this, "exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buttonSetting(){

        if(werac.getCreator().getUid() == PropertyManager.getInstance().getUser().getUid()) {
            if(werac.getStatus() == 1) {
                btn_detail_1.setVisibility(View.VISIBLE);
                btn_detail_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailViewActivity.this, ModifyWeracActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra(EXTRA_WERAC_ID, thisMid);
                        startActivity(intent);
                    }
                });
                btn_detail_2.setVisibility(View.VISIBLE);
                btn_detail_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailStatusChangeDialog f_dialog = new DetailStatusChangeDialog();
                        Bundle args = new Bundle();
                        args.putInt(DetailViewActivity.EXTRA_WERAC_ID, thisMid);
                        f_dialog.setArguments(args);
                        f_dialog.show(getSupportFragmentManager(), "create");
                        changeStatus();
                    }
                });
                btn_detail_360.setVisibility(View.INVISIBLE);
            }else if(werac.getStatus() == 2) {
                btn_detail_1.setVisibility(View.INVISIBLE);
                btn_detail_2.setVisibility(View.INVISIBLE);
                btn_detail_360.setVisibility(View.VISIBLE);
                btn_detail_360.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeStatus();
                        Toast.makeText(DetailViewActivity.this, "마감되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetailViewActivity.this, DetailViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra(EXTRA_WERAC_ID, thisMid);
                        startActivity(intent);
                        finish();
                    }
                });
            }else if(werac.getStatus() == 3) {
                btn_detail_1.setVisibility(View.INVISIBLE);
                btn_detail_2.setVisibility(View.INVISIBLE);
                btn_detail_360.setVisibility(View.INVISIBLE);
                container_detail_close.setVisibility(View.VISIBLE);
            }
        }else{
            if(werac.getStatus() == 2) {
                btn_detail_1.setVisibility(View.INVISIBLE);
                btn_detail_2.setVisibility(View.INVISIBLE);
                btn_detail_360.setVisibility(View.VISIBLE);
                btn_detail_360.setBackground(getResources().getDrawable(R.drawable.page_join_btn));
                btn_detail_360.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinWerac();
                    }
                });
            }else{
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                btn_detail_1.setVisibility(View.INVISIBLE);
                btn_detail_2.setVisibility(View.INVISIBLE);
                btn_detail_360.setVisibility(View.INVISIBLE);
                if (werac.getStatus() == 3) {
                    container_detail_close.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
