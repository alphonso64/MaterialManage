package com.thingword.alphonso.materialmanage.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.bean.ProductionInfo;

/**
 * Created by alphonso on 2016/9/22.
 */
public class ProductRecylerViewCusorAdapter extends RecyclerViewCursorAdapter<ProductionInfo, ProductRecylerViewCusorAdapter.ViewHolder> {
    private Context context;
    public ProductRecylerViewCusorAdapter(@NonNull Class<ProductionInfo> klass, @Nullable Cursor cursor,Context ctx) {
        super(klass, cursor);
        context = ctx;
    }

    @Override
    ProductionInfo fromCursorRow(Cursor cursor) {
        ProductionInfo productionInfo= ProductionInfo.fromCursor(cursor);
        return productionInfo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.cardview_loadinfo_item,parent,false);
        ViewHolder bvh=new ViewHolder(v,this);
        return bvh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductionInfo ld = getItem(position);
        holder.title_a.setTextColor(context.getResources().getColor(R.color.textblack));
        holder.title_b.setTextColor(context.getResources().getColor(R.color.textblack));
        holder.title_c1.setTextColor(context.getResources().getColor(R.color.textblack));

        holder.title_a.setText("规格:"+ld.getSpec());
        holder.title_b.setText("任务单号:"+ld.getTasknumber());
        holder.title_c1.setText("编码:"+ld.getProductcode());
    }

    private OnAdpaterItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnAdpaterItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerViewCursorAdapter.ViewHolder{
        TextView title_a;
        TextView title_b;
        TextView title_c1;
        TextView title_c2;
        TextView title_d1;
        TextView title_d2;
        ProductRecylerViewCusorAdapter mAdapter;
        public ViewHolder(View itemView,ProductRecylerViewCusorAdapter adapter) {
            super(itemView);
            title_a = (TextView) itemView.findViewById(R.id.cv_title_a);
            title_b = (TextView) itemView.findViewById(R.id.cv_title_b);
            title_c1 = (TextView) itemView.findViewById(R.id.cv_title_c1);
            title_c2 = (TextView) itemView.findViewById(R.id.cv_title_c2);
            title_d1 = (TextView) itemView.findViewById(R.id.cv_title_d1);
            title_d2 = (TextView) itemView.findViewById(R.id.cv_title_d2);
            title_d1.setVisibility(View.GONE);
            title_d2.setVisibility(View.GONE);
            title_c2.setVisibility(View.GONE);
            mAdapter = adapter;
            CardView cv = (CardView) itemView.findViewById(R.id.cv_ld);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductionInfo productionInfo =  mAdapter.getItem(getAdapterPosition());
                    if(mAdapter.onItemClickListener!=null){
                        mAdapter.onItemClickListener.onItemClick(productionInfo,getAdapterPosition());
                    }
                }
            });
        }
    }
}
