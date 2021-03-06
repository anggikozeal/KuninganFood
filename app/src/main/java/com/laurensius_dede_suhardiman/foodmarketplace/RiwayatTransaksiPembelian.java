package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.laurensius_dede_suhardiman.foodmarketplace.fragments.FragmentTransaksiPembelian;

public class RiwayatTransaksiPembelian extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static Activity activity;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi_pembelian);

        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        intent = getIntent();
        if(intent.getStringExtra("tab").equals("tagihan")){
            mViewPager.setCurrentItem(0);
        }else
        if(intent.getStringExtra("tab").equals("dibayar")){
            mViewPager.setCurrentItem(1);
        }else
        if(intent.getStringExtra("tab").equals("proses")){
            mViewPager.setCurrentItem(2);
        }else
        if(intent.getStringExtra("tab").equals("selesai")){
            mViewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RiwayatTransaksiPembelian.this,FoodMarketplace.class);
        intent.putExtra("navigasi","riwayat");
        startActivity(intent);
        finish();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentTransaksiPembelian.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
