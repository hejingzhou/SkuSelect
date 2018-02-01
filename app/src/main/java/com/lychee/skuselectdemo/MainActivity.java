package com.lychee.skuselectdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SpecificationSkuAdapter.OnSelectResultListener {


    @BindView(R.id.mSkuListView)
    ListView mSkuListView;

    private SpecificationSkuAdapter mSkuAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        NormsEntity data = getData();
        for (NormsEntity.DataBean dataBean : data.getData()) {
            for (NormsEntity.DataBean.OptionsBean optionsBean : dataBean.getOptions()) {
                optionsBean.setPropertyName(dataBean.getName());
                optionsBean.setPropertyId(dataBean.getId());
                optionsBean.setPropertyRequired(dataBean.getRequired());
            }
        }
        List<NormsEntity.DataBean> listData = new ArrayList<>();
        listData.addAll(data.getData());
        mSkuAdapter = new SpecificationSkuAdapter(this, listData);
        mSkuListView.setAdapter(mSkuAdapter);
        mSkuAdapter.setOnSelectResultListener(this);
    }

    /**
     * 获取示例数据
     *
     * @return
     */
    private NormsEntity getData() {
        InputStream is;
        try {
            is = mContext.getAssets().open("skudata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer, "utf-8");
            NormsEntity normsEntity = new Gson().fromJson(text, NormsEntity.class);
            return normsEntity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPropertyResult(List<Map<String, List<NormsEntity.DataBean.OptionsBean>>> mNormsMapList) {
        // TODO: 2018/2/1 处理最终选择结果
    }
}
