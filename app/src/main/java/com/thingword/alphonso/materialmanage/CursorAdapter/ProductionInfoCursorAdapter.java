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
import com.thingword.alphonso.materialmanage.bean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.ProductionInfo;


public class ProductionInfoCursorAdapter extends BaseAbstractRecycleCursorAdapter<RecyclerView.ViewHolder> {
    private Context context;

    public ProductionInfoCursorAdapter(Context context) {
        super(context, null);
        this.context = context;
    }

    private OnAdpaterItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnAdpaterItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        ProductionInfo ld = ProductionInfo.fromCursor(cursor);
        ((CusViewHolder) holder).title_a.setTextColor(context.getResources().getColor(R.color.textblack));
        ((CusViewHolder) holder).title_b.setTextColor(context.getResources().getColor(R.color.textblack));
        ((CusViewHolder) holder).title_c1.setTextColor(context.getResources().getColor(R.color.textblack));

        ((CusViewHolder) holder).title_a.setText("产线:"+ld.getProductline());
        ((CusViewHolder) holder).title_b.setText("规格:"+ld.getSpec());
        ((CusViewHolder) holder).title_c1.setText("编码:"+ld.getProductcode());
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
        ProductionInfoCursorAdapter mAdapter;
        public CusViewHolder(View itemView, ProductionInfoCursorAdapter adapter) {
            super(itemView);
            title_a = (TextView) itemView.findViewById(R.id.cv_title_a);
            title_b = (TextView) itemView.findViewById(R.id.cv_title_b);
            title_c1 = (TextView) itemView.findViewById(R.id.cv_title_c1);
            title_c2 = (TextView) itemView.findViewById(R.id.cv_title_c2);
            title_d1 = (TextView) itemView.findViewById(R.id.cv_title_d1);
            title_d2 = (TextView) itemView.findViewById(R.id.cv_title_d2);
            mAdapter = adapter;
            title_c2.setVisibility(View.INVISIBLE);
            title_d1.setVisibility(View.INVISIBLE);
            title_d2.setVisibility(View.INVISIBLE);
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
