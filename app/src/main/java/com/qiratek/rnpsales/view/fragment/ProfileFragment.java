package com.qiratek.rnpsales.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.qiratek.rnpsales.R;
import com.qiratek.rnpsales.databinding.FragmentProfileBinding;
import com.qiratek.rnpsales.model.datasource.network.ConnectionHandler;
import com.qiratek.rnpsales.model.entity.User;
import com.qiratek.rnpsales.model.helper.SharedPreferenceHelper;
import com.qiratek.rnpsales.view.BaseFragment;
import com.qiratek.rnpsales.view.activity.SplashActivity;
import com.qiratek.rnpsales.viewmodel.BaseViewModel;
import com.qiratek.rnpsales.viewmodel.CustomViewModelFactory;
import com.qiratek.rnpsales.viewmodel.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFragment {

    FragmentProfileBinding binding;
    UserViewModel viewModel;
    AlertDialog.Builder builder;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory(getContext())).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        binding.profNama.setText(isStringNotEmpty(viewModel.getUser().getName()) ? viewModel.getUser().getName() : "-");
        binding.profAlamat.setText(isStringNotEmpty(viewModel.getUser().getAlamat()) ? viewModel.getUser().getAlamat() : "-");
        binding.profNik.setText("NIK. "+ (isStringNotEmpty(viewModel.getUser().getNik()) ? viewModel.getUser().getNik() : "-"));
        binding.profTelp.setText(isStringNotEmpty(viewModel.getUser().getPhone()) ? viewModel.getUser().getPhone() : "-");
        binding.profEmail.setText(isStringNotEmpty(viewModel.getUser().getEmail()) ? viewModel.getUser().getEmail() : "-");
        binding.profTanggal.setText(isStringNotEmpty(viewModel.getUser().getJoin_date()) ? getDate(viewModel.getUser().getJoin_date()) : "-");
        Glide.with(this).load(ConnectionHandler.IMAGE_URL + viewModel.getUser().getFoto()).
                placeholder(R.drawable.user_default).error(R.drawable.user_default).into(binding.imUser);
        binding.containerWa.setOnClickListener(view -> {
            String url = "https://api.whatsapp.com/send?phone=6281275888897";
            try {
                PackageManager pm = view.getContext().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                view.getContext().startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        binding.containerFaq.setOnClickListener(view -> {
            String url = "https://rnpsales.id/faq";
            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin akan keluar dari aplikasi ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferenceHelper.getInstance(getContext()).removePreference(User.table);
                startActivity(new Intent(getContext(), SplashActivity.class));
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Tidak", null);
        binding.containerLogout.setOnClickListener(v -> builder.show());
        return binding.getRoot();
    }

    @Override
    protected BaseViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void showLoading(boolean isLoading) {

    }

    @Override
    protected boolean isValidInput() {
        return false;
    }

    @Override
    protected void initObserver() {

    }
}