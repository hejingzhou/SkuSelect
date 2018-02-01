package com.lychee.skuselectdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lychee of on 2018/1/9 15:41.
 * <p>
 * Author: HeJingzhou
 * <p>
 * Email: tcowork@163.com
 */
public class SpecificationSkuAdapter extends BaseAdapter {
    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private List<NormsEntity.DataBean> mSkuData;
    private LayoutInflater inflater;
    List<Map<String, List<NormsEntity.DataBean.OptionsBean>>> mNormsMapList;

    private OnSelectResultListener onSelectResultListener;

    public SpecificationSkuAdapter(Context mContext, List<NormsEntity.DataBean> mSkuData) {
        this.mContext = mContext;
        this.mSkuData = mSkuData;
        inflater = LayoutInflater.from(mContext);
        mNormsMapList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mSkuData.size();
    }

    @Override
    public Object getItem(int position) {
        return mSkuData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // TODO: 2018/1/11 如果进行优化ListView  最后一个未显示出来的属性组  会和第0条属性值一样  没有好的方案不要动
        convertView = inflater.inflate(R.layout.item_sku_layout, null, false);
        viewHolder = new ViewHolder(convertView);

        final NormsEntity.DataBean dataBean = mSkuData.get(position);
        /**
         * 每一组属性都以MapKey为属性名称（颜色，大小）  options中的一组数据为Value (红色 id，黄色 id)
         */
        if (mNormsMapList.size() < mSkuData.size()) {
            Map<String, List<NormsEntity.DataBean.OptionsBean>> optionsBeanMap = new HashMap<>(16);
            optionsBeanMap.put(dataBean.getName(), dataBean.getOptions());
            mNormsMapList.add(optionsBeanMap);
        }
        viewHolder.mTvAttrName.setText(dataBean.getName());
        viewHolder.mTvSelectType.setText(("0".equals(dataBean.getRequired()) ? "（非必选）" : "（必选）"));
        int padding = BaseActivity.dp2px(mContext, 6);
        int textPadding = BaseActivity.dp2px(mContext, 20);
        int topPadding = BaseActivity.dp2px(mContext, 7);
        viewHolder.mSkuViewGroup.setPadding(padding, padding, padding, padding);

        if (viewHolder.mSkuViewGroup.getChildCount() == 0) {
            //设置内边距
            for (NormsEntity.DataBean.OptionsBean optionsBean : dataBean.getOptions()) {
                final TextView attrText = new TextView(mContext);
                attrText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                attrText.setPadding(textPadding, topPadding, textPadding, topPadding);
                attrText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                attrText.setGravity(Gravity.CENTER);
                if (optionsBean.isSelect()) {
                    attrText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sku_select));
                    attrText.setTextColor(Color.parseColor("#ff5100"));
                } else {
                    attrText.setTextColor(Color.parseColor("#333333"));
                    attrText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sku_unselect));
                }
                attrText.setText(optionsBean.getName());
                attrText.setTag(optionsBean);
                if (viewHolder.mSkuViewGroup.getChildCount() < dataBean.getOptions().size()) {
                    viewHolder.mSkuViewGroup.addView(attrText);
                }

                attrText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NormsEntity.DataBean.OptionsBean currSelectOptionsBean = (NormsEntity.DataBean.OptionsBean) v.getTag();
                        for (Map<String, List<NormsEntity.DataBean.OptionsBean>> propertyListMap : mNormsMapList) {
                            for (Map.Entry<String, List<NormsEntity.DataBean.OptionsBean>> detailPropertyListEntry : propertyListMap.entrySet()) {
                                if (detailPropertyListEntry.getKey().equals(currSelectOptionsBean.getPropertyName())) {
                                    for (NormsEntity.DataBean.OptionsBean bean : detailPropertyListEntry.getValue()) {
                                        if (bean.getName().equals(currSelectOptionsBean.getName())) {
                                            if (bean.isSelect()) {
                                                bean.setSelect(false);
                                            } else {
                                                bean.setSelect(true);
                                            }
                                        } else {
                                            bean.setSelect(false);
                                        }
                                    }
                                    viewHolder.mSkuViewGroup = (SkuLayout) v.getParent();
                                    for (int i = 0; i < viewHolder.mSkuViewGroup.getChildCount(); i++) {
                                        TextView currTextView = (TextView) viewHolder.mSkuViewGroup.getChildAt(i);
                                        NormsEntity.DataBean.OptionsBean changeLater = (NormsEntity.DataBean.OptionsBean) currTextView.getTag();
                                        if (changeLater.isSelect()) {
                                            currTextView.setTextColor(Color.parseColor("#ff5100"));
                                            currTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sku_select));
                                        } else {
                                            currTextView.setTextColor(Color.parseColor("#333333"));
                                            currTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sku_unselect));
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (onSelectResultListener != null) {
                            onSelectResultListener.onPropertyResult(mNormsMapList);
                        }
                    }
                });
            }

        }
        return convertView;
    }

    public interface OnSelectResultListener {
        void onPropertyResult(List<Map<String, List<NormsEntity.DataBean.OptionsBean>>> mNormsMapList);
    }

    public void setOnSelectResultListener(OnSelectResultListener onSelectResultListener) {
        this.onSelectResultListener = onSelectResultListener;
    }

    static class ViewHolder {
        @BindView(R.id.mTvAttrName)
        TextView mTvAttrName;
        @BindView(R.id.mTvSelectType)
        TextView mTvSelectType;
        @BindView(R.id.mSkuViewGroup)
        SkuLayout mSkuViewGroup;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
