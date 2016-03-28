package vn.soaap.onlinepharmacy.activity;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class DrugsInputActivity extends AppCompatActivity
        implements ItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    PrescriptionAdapter adapter;
    SwipeToDismissTouchListener swipeToDismissTouchListener;
    List<Drug> drugs = new ArrayList<Drug>();
    View positiveAction;
    EditText etDrugName;
    EditText etDrugQuantity;
    String name;
    String address;
    String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs_input);

        getInfo();
        initView();
        setRecycleView();
        clickAddDrug();
    }

    private void getInfo() {
        Bundle bundle = getIntent().getBundleExtra("info");
        name = bundle.getString("name");
        address = bundle.getString("address");
        phoneNum = bundle.getString("phoneNum");
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(DrugsInputActivity.this,
                        SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn có muốn gửi đơn thuốc ?")
//                        .setContentText("Đơn thuốc của bạn sẽ được gửi đến quầy thuốc")
                        .setContentText(getPrescription())
//                        .setCancelText("Hủy bỏ")
                        .setConfirmText("Gửi đơn thuốc")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog
                                        .setTitleText("Gửi thành công !")
                                        .setContentText("Đơn thuốc đang được sử lý, vui lòng chờ đợi !")
                                        .setConfirmText("Đóng")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        ImageButton btnAddDrug = (ImageButton) findViewById(R.id.btnAddDrug);
        btnAddDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddDrug();
            }
        });
    }

    private String getPrescription() {
        String s = String.format(
                "Họ tên : %s \n" +
                        "Địa chỉ : %s \n" +
                        "Số điện thoại : %s \n" +
                        "Đơn thuốc : \n", name, address, phoneNum);
        int i= 1;
        for (Drug drug : drugs) {
            s += String.valueOf(i) + ". ";
            s += drug.getName() + " ";
            s += " x" + drug.getQuatities() + "\n";
            i++;
        }

        return s;
    }

    private void clickAddDrug() {
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


    private void addDrug() {
        String drugName = etDrugName.getText().toString().trim();
        Drug drug = new Drug(drugs.size(), drugName);
        drug.setQuatities(Integer.parseInt(etDrugQuantity.getText().toString()));
        drugs.add(drug);

        adapter.notifyDataSetChanged();
    }

    private void setRecycleView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvPrescription);
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
//                            adapter.notifyItemRemoved(data.position);
                        }
                    }
                });

        recyclerView.addOnItemTouchListener(swipeToDismissTouchListener);
    }

    @Override
    public void onItemClick(RecyclerView parent, View clickedView, int position) {
        Log.d("", "onItemClick()");
    }

    @Override
    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
        Log.d("", "onItemLongClick()");
    }


}
