package vn.soaap.onlinepharmacy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.entities.ImagePre;
import vn.soaap.onlinepharmacy.entities.ListDrugPre;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.view.Listener.DrugClickListener;
import vn.soaap.onlinepharmacy.view.adapter.PrescriptionAdapter;
import vn.soaap.onlinepharmacy.helper.NetworkHelper;

public class DrugsInputActivity extends AppCompatActivity implements DrugClickListener {

    private static final String TAG = DrugsInputActivity.class.getSimpleName();
    private static int INPUT_IMAGE = 1;
    private final int EDIT_INFO = 2;


    private User user;

    private int action;

    private ImagePre imagePre;

    private List<Drug> drugs = new ArrayList<Drug>();

    private PrescriptionAdapter adapter;

    //    View
    private EditText etDrugName;
    private EditText etDrugQuantity;
    private View positiveAction;

    @Bind(R.id.rvPrescription)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_drugs_input);
        ButterKnife.bind(this);

        //  add card info
        drugs.add(new Drug(0, "info"));

        getInfo();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        user = MyApplication.getInstance().getPrefManager().getUser();

        if (action == INPUT_IMAGE) {
            imagePre.setUser(user);
        }
        initView();
    }

    //  get user infomaton
    private void getInfo() {

        action = getIntent().getIntExtra("action", 0);
        if (action == INPUT_IMAGE) {
            String ima = getIntent().getStringExtra("image");
            Uri uri = Uri.parse(ima);
            imagePre = new ImagePre(getApplicationContext(), user, uri);
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        if (action == INPUT_IMAGE)
            adapter = new PrescriptionAdapter(getApplicationContext(), true, imagePre.getImage());
        else
            adapter = new PrescriptionAdapter(getApplicationContext(), drugs, this);
        recyclerView.setAdapter(adapter);
    }

    public void showDialogAddDrug(View v) {
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

//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    public void editInfo(View v) {
        Intent intent = new Intent(this, InfoInputActivity.class);
        intent.putExtra("action", EDIT_INFO);
        startActivity(intent);
    }

    public void addDrug() {
        String drugName = etDrugName.getText().toString().trim();
        Drug drug = new Drug(drugs.size(), drugName);
        drug.setQuatity(Integer.parseInt(etDrugQuantity.getText().toString()));
        drugs.add(drug);
        adapter.notifyDataSetChanged();
        showToast("Thêm thuốc thành công");
    }

    @Override
    public void OnEditClick(View aView, int position) {
        final int pos = position;

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

    @Override
    public void OnDeleteClick(View aView, int position) {
        final int pos = position;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Bạn có muốn xóa thuốc khỏi toa ?")
                .positiveText(R.string.xoa)
                .negativeText(R.string.huy)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        drugs.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                }).build();
        dialog.show();
    }

    @OnClick(R.id.fab)
    public void sendPrescription() {
        if (!NetworkHelper.isNetworkConnected(getApplicationContext()))
            return;

        try {
            if (action == INPUT_IMAGE)
                imagePre.send(this);
            else {  //  input presciption is list of drug
                if (drugs != null && drugs.size() > 1) {
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

    /*  Util    */

    //  ẩn status bar
    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
