package com.qiratek.rnpsales.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.qiratek.rnpsales.model.datasource.local.AppDatabase;
import com.qiratek.rnpsales.model.datasource.network.BaseNetwork;
import com.qiratek.rnpsales.model.datasource.network.MultipartFile;
import com.qiratek.rnpsales.model.entity.BillingData;
import com.qiratek.rnpsales.model.entity.Outlet;
import com.qiratek.rnpsales.model.entity.TakeOrderData;
import com.qiratek.rnpsales.model.entity.VisitPlanDb;
import com.qiratek.rnpsales.model.helper.SharedPreferenceHelper;
import com.qiratek.rnpsales.model.repository.BillingRepository;
import com.qiratek.rnpsales.model.repository.OutletRepository;
import com.qiratek.rnpsales.model.repository.TakeOrderRepository;
import com.qiratek.rnpsales.model.repository.VisitPlanRepository;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class SyncViewModel extends BaseViewModel {

    TakeOrderRepository takeOrderRepository;
    VisitPlanRepository visitPlanRepository;
    BillingRepository billingRepository;
    OutletRepository outletRepository;
    LiveData<ArrayList<Outlet>> outletsLiveData;
    LiveData<ArrayList<VisitPlanDb>> visitPlansLiveData;
    LiveData<ArrayList<BillingData>> billingLiveData;
    LiveData<ArrayList<TakeOrderData>> takeOrdersLiveData;
    LiveData<VisitPlanDb> visitPlanDbLiveData;

    public SyncViewModel(Context context) {
        visitPlanRepository = VisitPlanRepository.getInstance(BaseNetwork.getInstance(context), SharedPreferenceHelper.getInstance(context), this,
                AppDatabase.getInstance(context));
        billingRepository = billingRepository.getInstance(BaseNetwork.getInstance(context), SharedPreferenceHelper.getInstance(context), this,
                AppDatabase.getInstance(context));
        takeOrderRepository = TakeOrderRepository.getInstance(BaseNetwork.getInstance(context), SharedPreferenceHelper.getInstance(context), this,
                AppDatabase.getInstance(context));
        outletRepository = OutletRepository.getInstance(BaseNetwork.getInstance(context), SharedPreferenceHelper.getInstance(context), this,
                AppDatabase.getInstance(context));
    }

    public LiveData<ArrayList<Outlet>> getAllOutlet(){
        outletsLiveData = outletRepository.getAllOutlet();
        if(outletsLiveData.getValue() == null || outletsLiveData.getValue().size() == 0) {
            loading.setValue(true);
        }
        return outletsLiveData;
    }

    public LiveData<ArrayList<VisitPlanDb>> getAllVisit(){
        visitPlansLiveData = visitPlanRepository.getSavedVisit();
        if(visitPlansLiveData.getValue() == null || visitPlansLiveData.getValue().size() == 0) {
            loading.setValue(true);
        }
        return visitPlansLiveData;
    }

    public LiveData<ArrayList<BillingData>> getAllBilling(){
        billingLiveData = billingRepository.getSavedBilling();
        if(billingLiveData.getValue() == null || billingLiveData.getValue().size() == 0) {
            loading.setValue(true);
        }
        return billingLiveData;
    }

    public LiveData<ArrayList<TakeOrderData>> getAllTakeOrder(){
        takeOrdersLiveData = takeOrderRepository.getSavedData();
        if(takeOrdersLiveData.getValue() == null || takeOrdersLiveData.getValue().size() == 0) {
            loading.setValue(true);
        }
        return takeOrdersLiveData;
    }

    public LiveData<VisitPlanDb> submitVisit(Map<String, Object> param, Map<String, MultipartFile> paramFile){
        visitPlanDbLiveData = visitPlanRepository.submitDraftVisit(param, paramFile);
        if(visitPlanDbLiveData == null || visitPlanDbLiveData.getValue() == null) {
            loading.setValue(true);
        }
        return visitPlanDbLiveData;
    }

    public LiveData<VisitPlanDb> submitCheckout(JSONObject jsonObject, VisitPlanDb visitPlanDb){
        loading.setValue(true);
        return visitPlanRepository.submitSavedCheckout(jsonObject, visitPlanDb);
    }

    public LiveData<Boolean> setBilling(Map<String, Object> param, Map<String, MultipartFile> paramFile, BillingData billingData){
        loading.setValue(true);
        return billingRepository.submitSavedBilling(param, paramFile, billingData);
    }

    public LiveData<Boolean> submitTakeOrder(JSONObject jsonObject, TakeOrderData takeOrderData){
        loading.setValue(true);
        return takeOrderRepository.submitSavedTakeOrder(jsonObject, takeOrderData);
    }
}
