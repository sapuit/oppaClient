package vn.soaap.onlinepharmacy.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.adapter.PrescriptionAdapter;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.recyclerview.ItemTouchListenerAdapter;
import vn.soaap.onlinepharmacy.recyclerview.SwipeToDismissTouchListener;
import vn.soaap.onlinepharmacy.widget.PullScrollView;

public class DrugsInputActivity extends AppCompatActivity implements PullScrollView.OnTurnListener{


    List<Drug> drugs = new ArrayList<Drug>();
    View positiveAction;
    String name;
    String address;
    String phoneNum;

    private PullScrollView mScrollView;
    private ImageView mHeadImg;
    private TableLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_drugs_input);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.act_pull_down);

        getInfo();
        initView();
        showTable();
    }

    private void getInfo() {
        Bundle bundle = getIntent().getBundleExtra("info");
        name = bundle.getString("name");
        address = bundle.getString("address");
        phoneNum = bundle.getString("phoneNum");
    }


    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SweetAlertDialog(DrugsInputActivity.this,
//                        SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Bạn có muốn gửi đơn thuốc ?")
////                        .setContentText("Đơn thuốc của bạn sẽ được gửi đến quầy thuốc")
//                        .setContentText(getPrescription())
////                        .setCancelText("Hủy bỏ")
//                        .setConfirmText("Gửi đơn thuốc")
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog
//                                        .setTitleText("Gửi thành công !")
//                                        .setContentText("Đơn thuốc đang được sử lý, vui lòng chờ đợi !")
//                                        .setConfirmText("Đóng")
//                                        .setConfirmClickListener(null)
//                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                            }
//                        })
//                        .show();
//            }
//        });


        mScrollView = (PullScrollView) findViewById(R.id.scroll_view);
        mHeadImg = (ImageView) findViewById(R.id.background_img);

        mMainLayout = (TableLayout) findViewById(R.id.table_layout);

        mScrollView.setHeader(mHeadImg);
        mScrollView.setOnTurnListener(this);
    }

    public void showTable() {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.leftMargin = 30;
        layoutParams.bottomMargin = 10;
        layoutParams.topMargin = 10;

        for (int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(this);
            TextView textView = new TextView(this);
            textView.setText("Test pull down scroll view " + i);
            textView.setTextSize(20);
            textView.setPadding(15, 15, 15, 15);

            tableRow.addView(textView, layoutParams);
            if (i % 2 != 0) {
                tableRow.setBackgroundColor(Color.LTGRAY);
            } else {
                tableRow.setBackgroundColor(Color.WHITE);
            }

            final int n = i;
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DrugsInputActivity.this, "Click item " + n, Toast.LENGTH_SHORT).show();
                }
            });

            mMainLayout.addView(tableRow);
        }
    }

    @Override
    public void onTurn() {

    }
}
