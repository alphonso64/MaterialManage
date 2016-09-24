package com.thingword.alphonso.materialmanage.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.LoadingInfo;


public class DistriInfoCursorAdapter extends BaseAbstractRecycleCursorAdapter<RecyclerView.ViewHolder> {
    private Context context;

    public DistriInfoCursorAdapter(Context context) {
        super(context, null);
        this.context = context;
    }

    private OnAdpaterItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnAdpaterItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        DistributionInfo ld = DistributionInfo.fromCursor(cursor);
        if(ld.getChecked().equals("true")){
            ((CusViewHolder) holder).title_a.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_b.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_c1.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_c2.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_d1.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_d2.setTextColor(context.getResources().getColor(R.color.colorGray));
        }else{
            ((CusViewHolder) holder).title_a.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_b.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_c1.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_c2.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_d1.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_d2.setTextColor(context.getResources().getColor(R.color.textblack));
        }
        ((CusViewHolder) holder).title_a.setText("任务编码:"+ld.getTasknumber());
//        ((CusViewHolder) holder).title_b.setText("物料名称:"+ld.getcInvName());
//        ((CusViewHolder) holder).title_c1.setText("物料编码:"+ld.getcBatch());
//        ((CusViewHolder) holder).title_c2.setText(ld.getShopnum());
//        ((CusViewHolder) holder).title_d1.setText("数量:"+ld.getiQuantity());
//        ((CusViewHolder) holder).title_d2.setText(ld.getDate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.cardview_loadinfo_item,parent,false);
        CusViewHolder bvh=new CusViewHolder(v,this);
        return bvh;
    }

    public static class CusViewHolder extends RecyclerView.ViewHolder {
        TextView title_a;
        TextView title_b;
        TextView title_c1;
        TextView title_c2;
        TextView title_d1;
        TextView title_d2;
        DistriInfoCursorAdapter mAdapter;
        public CusViewHolder(View itemView, DistriInfoCursorAdapter adapter) {
            super(itemView);
            title_a = (TextView) itemView.findViewById(R.id.cv_title_a);
            title_b = (TextView) itemView.findViewById(R.id.cv_title_b);
            title_c1 = (TextView) itemView.findViewById(R.id.cv_title_c1);
            title_c2 = (TextView) itemView.findViewById(R.id.cv_title_c2);
            title_d1 = (TextView) itemView.findViewById(R.id.cv_title_d1);
            title_d2 = (TextView) itemView.findViewById(R.id.cv_title_d2);
            mAdapter = adapter;
            CardView cv = (CardView) itemView.findViewById(R.id.cv_ld);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor = (Cursor) mAdapter.getItem(getAdapterPosition());

                    LoadingInfo planterRecord = LoadingInfo.fromCursor(cursor);
                    if(mAdapter.onItemClickListener!=null){
                        mAdapter.onItemClickListener.onItemClick(planterRecord,getAdapterPosition());
                    }
                }
            });
        }
    }
}
