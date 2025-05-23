package com.qiratek.rnpsales.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.qiratek.rnpsales.model.datasource.local.AppDatabase;
import com.qiratek.rnpsales.model.datasource.network.BaseNetwork;
import com.qiratek.rnpsales.model.entity.Brand;
import com.qiratek.rnpsales.model.entity.Catalog;
import com.qiratek.rnpsales.model.helper.SharedPreferenceHelper;
import com.qiratek.rnpsales.model.repository.BrandRepository;
import com.qiratek.rnpsales.model.repository.CatalogRepository;

import java.util.ArrayList;


public class CatalogViewModel extends BaseViewModel {

    BrandRepository brandRepository;
    CatalogRepository catalogRepository;
    LiveData<ArrayList<Brand>> brandLiveData;
    LiveData<ArrayList<Catalog>> catalogLiveData;

    public CatalogViewModel(Context context) {
        brandRepository = BrandRepository.getInstance(BaseNetwork.getInstance(context), SharedPreferenceHelper.getInstance(context), this,
                AppDatabase.getInstance(context));
        catalogRepository = CatalogRepository.getInstance(BaseNetwork.getInstance(context), SharedPreferenceHelper.getInstance(context), this,
                AppDatabase.getInstance(context));
    }

    public LiveData<ArrayList<Brand>> getAllBrands(){
        brandLiveData = brandRepository.getAllBrandCatalog();
        if(brandLiveData.getValue() == null || brandLiveData.getValue().size() == 0) {
            loading.setValue(true);
        }
        return brandLiveData;
    }

    public LiveData<ArrayList<Catalog>> getAllCatalog(int brand_id){
        catalogLiveData = catalogRepository.getAllCatalog(brand_id);
        if(catalogLiveData.getValue() == null || catalogLiveData.getValue().size() == 0) {
            loading.setValue(true);
        }
        return catalogLiveData;
    }
}
