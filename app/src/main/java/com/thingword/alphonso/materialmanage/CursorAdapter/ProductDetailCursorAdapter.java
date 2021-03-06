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
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductDetail;


public class ProductDetailCursorAdapter extends BaseAbstractRecycleCursorAdapter<RecyclerView.ViewHolder> {
    private Context context;
    private int cnt;
    public ProductDetailCursorAdapter(Context context) {
        super(context, null);
        this.context = context;
        cnt = 0;
    }
    
    private OnAdpaterItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnAdpaterItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        ProductDetail ld = ProductDetail.fromCursor(cursor);
        if(ld.getChecked().equals("true")){
            ((CusViewHolder) holder).title_a1.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_a2.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_a2.setVisibility(View.VISIBLE);
            ((CusViewHolder) holder).title_b.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_c1.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_c2.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_d1.setTextColor(context.getResources().getColor(R.color.colorGray));
            ((CusViewHolder) holder).title_d2.setTextColor(context.getResources().getColor(R.color.colorGray));
        }else{
            ((CusViewHolder) holder).title_a1.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_a1.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_a2.setVisibility(View.GONE);
            ((CusViewHolder) holder).title_b.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_c1.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_c2.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_d1.setTextColor(context.getResources().getColor(R.color.textblack));
            ((CusViewHolder) holder).title_d2.setTextColor(context.getResources().getColor(R.color.textblack));
        }
        ((CusViewHolder) holder).title_a1.setText("产品:"+ld.getInvname());
        ((CusViewHolder) holder).title_b.setText("规格:"+ld.getInvstd());
        ((CusViewHolder) holder).title_c1.setText("条码:"+ld.getInvcode());
        ((CusViewHolder) holder).title_c2.setText(String.valueOf(cursor.getPosition()));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.cardview_lineinfo_item,parent,false);
        CusViewHolder bvh=new CusViewHolder(v,this);
        cnt = 0;
        return bvh;
    }

    public static class CusViewHolder extends RecyclerView.ViewHolder {
        TextView title_a1;
        TextView title_a2;
        TextView title_b;
        TextView title_c1;
        TextView title_c2;
        TextView title_d1;
        TextView title_d2;
        ProductDetailCursorAdapter mAdapter;
        public CusViewHolder(View itemView, ProductDetailCursorAdapter adapter) {
            super(itemView);
            title_a1 = (TextView) itemView.findViewById(R.id.cv_title_a1);
            title_a2 = (TextView) itemView.findViewById(R.id.cv_title_a2);
            title_b = (TextView) itemView.findViewById(R.id.cv_title_b);
            title_c1 = (TextView) itemView.findViewById(R.id.cv_title_c1);
            title_c2 = (TextView) itemView.findViewById(R.id.cv_title_c2);
            title_d1 = (TextView) itemView.findViewById(R.id.cv_title_d1);
            title_d2 = (TextView) itemView.findViewById(R.id.cv_title_d2);
//            title_c2.setVisibility(View.GONE);
            title_d1.setVisibility(View.GONE);
            title_d2.setVisibility(View.GONE);
            mAdapter = adapter;
            CardView cv = (CardView) itemView.findViewById(R.id.cv_ld);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor = (Cursor) mAdapter.getItem(getAdapterPosition());

                    ProductDetail productDetail = ProductDetail.fromCursor(cursor);
                    if(mAdapter.onItemClickListener!=null){
                        mAdapter.onItemClickListener.onItemClick(productDetail,getAdapterPosition());
                    }
                }
            });
        }
    }
}
