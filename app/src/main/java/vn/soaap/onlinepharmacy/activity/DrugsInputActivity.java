package vn.soaap.onlinepharmacy.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.adapter.PrescriptionAdapter;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.entities.ImageHandle;
import vn.soaap.onlinepharmacy.recyclerview.ItemTouchListenerAdapter;
import vn.soaap.onlinepharmacy.recyclerview.SwipeToDismissTouchListener;
import vn.soaap.onlinepharmacy.widget.PullScrollView;

public class DrugsInputActivity extends AppCompatActivity
        implements PullScrollView.OnTurnListener,
        ItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    SwipeToDismissTouchListener swipeToDismissTouchListener;
    private List<Drug> drugs = new ArrayList<Drug>();
    PrescriptionAdapter adapter;
    private View positiveAction;
    private String name;
    private String address;
    private String phoneNum;
    private int action;
    private ImageHandle imageHandle;

    private TextView user_name, user_phone_num, user_addr;
    private EditText etDrugName;
    private EditText etDrugQuantity;
    private Button btnAddDrug;
    private ImageView ivImage;
    private RecyclerView recyclerView;
//    private PullScrollView mScrollView;
//    private ImageView mHeadImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_drugs_input);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_drugs_input);

        getInfo();
        initView();
    }

    private void getInfo() {

        Bundle bundle = getIntent().getBundleExtra("info");
        name = bundle.getString("name");
        address = bundle.getString("address");
        phoneNum = bundle.getString("phoneNum");
        action = bundle.getInt("action", 0);
        if (action == 1) {
            Bitmap bitmap = getIntent().getParcelableExtra("BitmapImage");
            imageHandle = new ImageHandle(bitmap, this);
        }
    }


    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//

        ivImage = (ImageView) findViewById(R.id.ivImage);
        user_name = (TextView) findViewById(R.id.user_name);
        user_phone_num = (TextView) findViewById(R.id.user_phone_num);
        user_addr = (TextView) findViewById(R.id.user_addr);

        btnAddDrug = (Button) findViewById(R.id.btn_add_drug);

        user_name.setText(name);
        user_phone_num.setText(phoneNum);
        user_addr.setText(address);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(DrugsInputActivity.this,
                        SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn có muốn gửi đơn thuốc ?")
                        .setContentText("Đơn thuốc của bạn sẽ được gửi đến quầy thuốc")
//                        .setContentText(getPrescription())
                        .setCancelText("Hủy bỏ")
                        .setConfirmText("Gửi đơn thuốc")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            //  @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog
                                        .setTitleText("Gửi thành công !")
                                        .setContentText("Đơn thuốc đang được sử lý, vui lòng chờ đợi !")
                                        .setConfirmText("Đóng")
                                        .showCancelButton(false)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).show();
                sendPrescription();
            }
        });

//        mScrollView = (PullScrollView) findViewById(R.id.scroll_view);
//        mHeadImg = (ImageView) findViewById(R.id.background_img);
//
//        mScrollView.setHeader(mHeadImg);
//        mScrollView.setOnTurnListener(this);


        if (action == 1) {
            ivImage.setImageBitmap(imageHandle.getBitmap());
            ivImage.setVisibility(View.VISIBLE);
            btnAddDrug.setVisibility(View.INVISIBLE);

        } else {
            btnAddDrug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddDrug();
                }
            });

            recyclerView = (RecyclerView) findViewById(R.id.rvPrescription);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);

            adapter = new PrescriptionAdapter(drugs);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(
                    new ItemTouchListenerAdapter(recyclerView, this));

            swipeToDismissTouchListener =
                    new SwipeToDismissTouchListener(
                            recyclerView, new SwipeToDismissTouchListener.DismissCallbacks() {

                        @Override
                        public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {
                            return SwipeToDismissTouchListener.SwipeDirection.RIGHT;
                        }

                        @Override
                        public void onDismiss(RecyclerView view, List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {
                            for (SwipeToDismissTouchListener.PendingDismissData data : dismissData) {
                                adapter.removeItem(data.position);
                            }
                        }
                    });

            recyclerView.addOnItemTouchListener(swipeToDismissTouchListener);
        }
    }

    private void sendPrescription() {
        try {
            if (action == 1) {
                if (imageHandle != null) {
                    String UPLOAD_URL = "http://192.168.1.17/oppa/test";
                    imageHandle.uploadImage(UPLOAD_URL);
                }

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPrescription() {
        String s = String.format(
                "Họ tên : %s \n" +
                        "Địa chỉ : %s \n" +
                        "Số điện thoại : %s \n" +
                        "Đơn thuốc : \n", name, address, phoneNum);
        int i = 1;
        for (Drug drug : drugs) {
            s += String.valueOf(i) + ". ";
            s += drug.getName() + " ";
            s += " x" + drug.getQuatities() + "\n";
            i++;
        }

        return s;
    }

    @Override
    public void onTurn() {

    }


    private void AddDrug() {
        //  show dialog input drug
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Thêm thuốc mới")
                .customView(R.layout.dialog_add_drug, true)
                .positiveText(R.string.them)
                .negativeText(R.string.huy)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        addDrug();
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        etDrugName = (EditText)
                dialog.getCustomView().findViewById(R.id.etDrugName);
        etDrugQuantity = (EditText)
                dialog.getCustomView().findViewById(R.id.etQuantity);

        etDrugName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etDrugQuantity.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDrugQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false);
    }

    private void editDrug(final int pos) {

        final Drug drug = drugs.get(pos);

        //  show dialog input drug
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Thay đổi thông tin thuốc")
                .customView(R.layout.dialog_add_drug, true)
                .positiveText(R.string.suadoi)
                .negativeText(R.string.huy)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        String drugName = etDrugName.getText().toString().trim();
                        drug.setName(drugName);
                        drug.setQuatities(Integer.parseInt(etDrugQuantity.getText().toString()));
                        drugs.set(pos, drug);

                        adapter.notifyDataSetChanged();
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        etDrugName = (EditText)
                dialog.getCustomView().findViewById(R.id.etDrugName);
        etDrugQuantity = (EditText)
                dialog.getCustomView().findViewById(R.id.etQuantity);

        etDrugName.setText(drug.getName());
        etDrugQuantity.setText(String.valueOf(drug.getQuatities()));

        etDrugQuantity.setEnabled(true);

        etDrugName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etDrugQuantity.setEnabled(s.toString().trim().length() > 0);
                positiveAction.setEnabled(s.toString().trim().length() > 0);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDrugQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false);
    }


    private void addDrug() {
        String drugName = etDrugName.getText().toString().trim();
        Drug drug = new Drug(drugs.size(), drugName);
        drug.setQuatities(Integer.parseInt(etDrugQuantity.getText().toString()));
        drugs.add(drug);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView parent, View clickedView, int position) {
        editDrug(position);
    }

    @Override
    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {

    }
}
