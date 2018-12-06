package com.laurensius_dede_suhardiman.foodmarketplace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.laurensius_dede_suhardiman.foodmarketplace.fragments.FragmentTransaksiPenjualan;

public class RiwayatTransaksiPenjualan extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static Activity activity;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi_penjualan);

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
        if(intent.getStringExtra("tab").equals("order")){
            mViewPager.setCurrentItem(0);
        }else
        if(intent.getStringExtra("tab").equals("proses")){
            mViewPager.setCurrentItem(1);
        }else
        if(intent.getStringExtra("tab").equals("kirim")){
            mViewPager.setCurrentItem(2);
        }else
        if(intent.getStringExtra("tab").equals("selesai")){
            mViewPager.setCurrentItem(3);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RiwayatTransaksiPenjualan.this,FoodMarketplace.class);
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
//            return PlaceholderFragment.newInstance(position + 1);
            return FragmentTransaksiPenjualan.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
