package vn.soaap.onlinepharmacy.activity;

import android.app.ProgressDialog;

import android.content.Intent;
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
import vn.soaap.onlinepharmacy.view.adapter.PrescriptionAdapter;
import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.entities.ImagePre;
import vn.soaap.onlinepharmacy.entities.ListDrugPre;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.view.recyclerview.ItemTouchListenerAdapter;
import vn.soaap.onlinepharmacy.view.recyclerview.SwipeToDismissTouchListener;
import vn.soaap.onlinepharmacy.helper.NetworkHelper;

public class DrugsInputActivity extends AppCompatActivity
        implements ItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    private static int INPUT_IMAGE = 1;
    private static final String TAG = DrugsInputActivity.class.getSimpleName();
    private User user;
    private int action;
    private String token;
    private PrescriptionAdapter adapter;
    private ImagePre imagePre;
    private List<Drug> drugs = new ArrayList<Drug>();

    //    View
    EditText etDrugName;
    EditText etDrugQuantity;
    SwipeToDismissTouchListener swipeToDismissTouchListener;
    private View positiveAction;

    @Bind(R.id.user_name) TextView user_name;

    @Bind(R.id.user_addr) TextView user_addr;

    @Bind(R.id.user_phone_num) TextView user_phone_num;

    @Bind(R.id.btn_add_drug) Button btnAddDrug;

    @Bind(R.id.ivImage) ImageView ivImage;

    @Bind(R.id.rvPrescription) RecyclerView recyclerView;

    @Bind(R.id.fab) FloatingActionButton fab;

    private ProgressDialog statusDialog;
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

    @Override
    protected void onResume() {
        super.onResume();

        user = MyApplication.getInstance().getPrefManager().getUser();
        if (token != null)
            user.setToken(token);
        if (action == INPUT_IMAGE) {
            imagePre.setUser(user);
        }
        initView();
    }

    //  get user infomaton
    private void getInfo() {

        action = getIntent().getIntExtra("action", 0);
        token = getIntent().getStringExtra("token");
        user = MyApplication.getInstance().getPrefManager().getUser();
        user.setToken(token);

        if (action == INPUT_IMAGE) {
            Bitmap bitmap = getIntent().getParcelableExtra("image");
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

        if (action == INPUT_IMAGE) {
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
    public void sendPrescription() {
        if (!NetworkHelper.isNetworkConnected(getApplicationContext()))
            return;

        try {
            if (action == INPUT_IMAGE)
                imagePre.send(this);
            else {  //  input presciption is list of drug
                if (drugs != null && drugs.size() != 0) {
                    ListDrugPre drugPre = new ListDrugPre(user, drugs);
                    drugPre.send(this);
                } else
                    Toast.makeText(this,
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

    private final int EDIT_INFO = 2;
    @OnClick(R.id.btn_edit_info)
    public void editInfo() {
        Intent intent = new Intent(this, InfoInputActivity.class);
        intent.putExtra("action", EDIT_INFO);
        startActivity(intent);
    }
}
