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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.adapter.PrescriptionAdapter;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.entities.ImagePre;
import vn.soaap.onlinepharmacy.entities.ListDrugPre;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.recyclerview.ItemTouchListenerAdapter;
import vn.soaap.onlinepharmacy.recyclerview.SwipeToDismissTouchListener;

public class DrugsInputActivity extends AppCompatActivity
        implements ItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    private User user;
    private int action;
    //    private ImageHandle         imageHandle;
    private PrescriptionAdapter adapter;
    private ImagePre imagePre;
    private List<Drug> drugs = new ArrayList<Drug>();
    private boolean result = false;

    //    View
    EditText etDrugName;
    EditText etDrugQuantity;
    SwipeToDismissTouchListener swipeToDismissTouchListener;
    private View positiveAction;
    @Bind(R.id.user_name)
    TextView user_name;
    @Bind(R.id.user_addr)
    TextView user_addr;
    @Bind(R.id.user_phone_num)
    TextView user_phone_num;
    @Bind(R.id.btn_add_drug)
    Button btnAddDrug;
    @Bind(R.id.ivImage)
    ImageView ivImage;
    @Bind(R.id.rvPrescription)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
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
        ButterKnife.bind(this);

        getInfo();
        initView();
    }

    private void getInfo() {

        Bundle bundle = getIntent().getBundleExtra("info");
        action = bundle.getInt("action", 0);

        String name = bundle.getString("name");
        String address = bundle.getString("address");
        String phone = bundle.getString("phoneNum");
        user = new User(name, phone, address);

        if (action == 1) {
            Bitmap bitmap = getIntent().getParcelableExtra("BitmapImage");
            imagePre = new ImagePre(user);
            imagePre.setImage(bitmap);
        }
    }


    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        recyclerView = (RecyclerView) findViewById(R.id.rvPrescription);
        user_name.setText(user.getName());
        user_phone_num.setText(user.getPhone());
        user_addr.setText(user.getAddress());

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                new SweetAlertDialog(DrugsInputActivity.this,
//                        SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Bạn có muốn gửi đơn thuốc ?")
//                        .setContentText("Đơn thuốc của b ạn sẽ được gửi đến quầy thuốc")
////                        .setContentText(getPrescription())
//                        .setCancelText("Hủy bỏ")
//                        .setConfirmText("Gửi đơn thuốc")
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            //  @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                if (drugs.size() == 0)
//                                    Toast.makeText(DrugsInputActivity.this,"Vui lòng thêm thuốc vào toa !",Toast.LENGTH_LONG).show();
//                                else
//                                sendPrescription();
//
//                                if (result)
//                                    sDialog
//                                            .setTitleText("Gửi thành công !")
//                                            .setContentText("Đơn thuốc đang được sử lý, vui lòng chờ đợi !")
//                                            .setConfirmText("Đóng")
//                                            .showCancelButton(false)
//                                            .setConfirmClickListener(null)
//                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
//                                else
//                                    sDialog
//                                            .setTitleText("Gửi không thành công !")
//                                            .setContentText("Không thể kết nối với hệ thống!")
//                                            .setConfirmText("Đóng")
//                                            .showCancelButton(false)
//                                            .setConfirmClickListener(null)
//                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                            }
//                        }).show();
//            }
//        });

//        mScrollView = (PullScrollView) findViewById(R.id.scroll_view);
//        mHeadImg = (ImageView) findViewById(R.id.background_img);
//
//        mScrollView.setHeader(mHeadImg);
//        mScrollView.setOnTurnListener(this);
        if (action == 1) {
            ivImage.setImageBitmap(imagePre.getImage());
            ivImage.setVisibility(View.VISIBLE);
            btnAddDrug.setVisibility(View.INVISIBLE);

        } else {
//            btnAddDrug.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AddDrug();
//                }
//            });

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);

            adapter = new PrescriptionAdapter(drugs);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(
                    new ItemTouchListenerAdapter(recyclerView, this));

            swipeToDismissTouchListener = new SwipeToDismissTouchListener(
                    recyclerView,
                    new SwipeToDismissTouchListener.DismissCallbacks() {

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

    @OnClick(R.id.fab)
    private void sendPrescription() {
        try {
            if (action == 1)
                imagePre.send(getBaseContext());
            else {
                if (drugs != null && drugs.size() != 0) {
                    ListDrugPre drugPre = new ListDrugPre(user, drugs);
                    result = drugPre.send(getBaseContext());
                } else
                    Toast.makeText(DrugsInputActivity.this,
                            "Vui lòng thêm thuốc vào toa của bạn ! ",
                            Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_add_drug)
    public void AddDrug() {
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
                        drug.setQuatity(Integer.parseInt(etDrugQuantity.getText().toString()));
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
        etDrugQuantity.setText(String.valueOf(drug.getQuatity()));

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
        drug.setQuatity(Integer.parseInt(etDrugQuantity.getText().toString()));
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
