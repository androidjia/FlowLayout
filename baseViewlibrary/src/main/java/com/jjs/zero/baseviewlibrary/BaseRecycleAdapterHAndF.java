package com.jjs.zero.baseviewlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020-01-07
 * @Details: <带头部和底部的基础适配器>
 */
public abstract class BaseRecycleAdapterHAndF<T,D extends ViewDataBinding,H extends ViewDataBinding,F extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    protected List<T> data;
    protected Context mContext;
    protected OnItemClickListener onItemClickListener;

    protected static final int TYPE_HEADER_VIEW = 1;
    protected static final int TYPE_FOOTER_VIEW = 2;

    private boolean isHeader;
    private boolean isFooter;

    protected BaseRecycleAdapterHAndF(List<T> data, boolean header, boolean footer) {
        this(null,data);
        isHeader = header;
        isFooter = footer;
    }

    protected BaseRecycleAdapterHAndF(Context mContext, List<T> data) {
        this.data = data;
        this.mContext = mContext;
    }

    public abstract @LayoutRes
    int layoutResId();
    public abstract @LayoutRes int layoutHeaderesId();
    public abstract @LayoutRes int layoutFooterResId();

    public abstract void onBindViewHolders(D binding, int position);
    public abstract void onBindHeaderViewHolders(H binding);
    public abstract void onBindFooterViewHolders(F binding);

    public void addOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER_VIEW) {
            return new HeaderViewHolder<H>(parent,layoutHeaderesId());
        } else if (viewType == TYPE_FOOTER_VIEW) {
            return new FooterViewHolder<F>(parent,layoutFooterResId());
        }
        return new ViewHolder<D>(parent,layoutResId());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
             if (holder instanceof HeaderViewHolder) {
                onBindHeaderViewHolders((H)((HeaderViewHolder) holder).binding);
                ((HeaderViewHolder) holder).binding.executePendingBindings();
            } else if (holder instanceof FooterViewHolder) {
                 onBindFooterViewHolders((F)((FooterViewHolder) holder).binding);
                 ((FooterViewHolder) holder).binding.executePendingBindings();
             } else{
                 if (isHeader){
                     onBindViewHolders((D)((ViewHolder) holder).binding, position-1);
                 } else {
                     onBindViewHolders((D)((ViewHolder) holder).binding, position);
                 }
                 ((ViewHolder) holder).binding.executePendingBindings();
            }
        }
    }

    @Override
    public int getItemCount() {
        int item = 0;
        if (isHeader) ++item;
        if (isFooter) ++item;
        return data!=null?data.size()+item:0;
    }

    @Override
    public int getItemViewType(int position) {
        int head = 0;
        if (isHeader) ++head;
        if (isFooter) ++head;
        if (position == 0) {
            if (isHeader) return TYPE_HEADER_VIEW;
        }
        if (position+1 == data.size()+head) {
            if (isFooter ) return TYPE_FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    public static class ViewHolder<D extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public D binding;
        public ViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),resId,parent,false).getRoot());
            binding = DataBindingUtil.getBinding(this.itemView);
        }
    }


    public static class FooterViewHolder<F extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public F binding;
        public FooterViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),resId,parent,false).getRoot());
            binding = DataBindingUtil.getBinding(this.itemView);
        }
    }

    public static class HeaderViewHolder<H extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public H binding;
        public HeaderViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),resId,parent,false).getRoot());
            binding = DataBindingUtil.getBinding(this.itemView);
        }
    }
}
