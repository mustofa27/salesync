package com.qiratek.rnpsales.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qiratek.rnpsales.R;
import com.qiratek.rnpsales.databinding.ActivityMainBinding;
import com.qiratek.rnpsales.model.entity.CheckInTimer;
import com.qiratek.rnpsales.model.entity.User;
import com.qiratek.rnpsales.model.entity.VisitPlanDb;
import com.qiratek.rnpsales.model.helper.SharedPreferenceHelper;
import com.qiratek.rnpsales.view.BaseActivity;
import com.qiratek.rnpsales.viewmodel.BaseViewModel;
import com.qiratek.rnpsales.viewmodel.CustomViewModelFactory;
import com.qiratek.rnpsales.viewmodel.MainActivityViewModel;
import com.qiratek.rnpsales.viewmodel.NewsViewModel;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    FirebaseAnalytics mFirebaseAnalytics;
    NavHostFragment navHostFragment;
    ActivityMainBinding binding;
    AlertDialog.Builder builder;
    String tipe = "Normal";
    MainActivityViewModel viewModel;
    ArrayList<VisitPlanDb> visitPlanDbs;
    boolean isTimeRunning = true;
    CheckInTimer checkInTimer;

    //updater
    AppUpdateManager appUpdateManager;
    private static final int REQ_CODE_VERSION_UPDATE = 530;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory(this)).get(MainActivityViewModel.class);
        visitPlanDbs = new ArrayList<>();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNav,
                navHostFragment.getNavController());
        navHostFragment.getNavController().addOnDestinationChangedListener((controller, destination, arguments) -> {
            //binding.title.setText(destination.getLabel());
        });
        tipe = getIntent().getStringExtra("type");
        if(tipe != null && tipe.equalsIgnoreCase("news")){
            navHostFragment.getNavController().navigate(R.id.history);
        } else if(tipe != null && tipe.equalsIgnoreCase("pricelist")){
            navHostFragment.getNavController().navigate(R.id.catalog);
        }
        binding.visit.setOnClickListener(view -> processVisit());
        checkUpdate();
    }

    private void processVisit(){
        if(viewModel.getVisitPlanLiveData() != null && viewModel.getVisitPlanLiveData().hasActiveObservers()){
            viewModel.getVisitPlanDbLiveData();
        } else{
            viewModel.getVisitPlanDbLiveData().observe(this, visitPlanDbs -> {
                if(this.visitPlanDbs.size() > 0){
                    this.visitPlanDbs.removeAll(this.visitPlanDbs);
                }
                this.visitPlanDbs = visitPlanDbs;
                showLoading(false);
                viewModel.getVisitPlanLiveData().removeObservers(this);
                if (visitPlanDbs.size() > 0) {
                    Intent intent;
                    if (isTimeRunning) {
                        intent = new Intent(this, CountDownTimerActivity.class);
                    } else {
                        intent = new Intent(this, CheckoutActivity.class);
                    }
                    intent.putExtra("data", visitPlanDbs.get(0));
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, SubmitVisit.class));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkInTimer = viewModel.getTimerPreference().getTimerPreference();
        isTimeRunning = checkInTimer.isRunning();
    }

    @Override
    protected BaseViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void showLoading(boolean isLoading) {

    }

    @Override
    protected void initObserver() {

    }

    //updater
    private void checkUpdate(){
        appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startAppUpdateImmediate(appUpdateInfo);
            }
        });
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode != RESULT_OK) {
                showMessageFailed("Update flow failed, Try again later! Result code: " + resultCode);
                finish();
            }
        }
    }
}