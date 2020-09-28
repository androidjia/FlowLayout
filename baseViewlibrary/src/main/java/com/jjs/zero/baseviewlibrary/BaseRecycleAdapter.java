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
 * @Details: <通用recycleViewAdapter>
 */
public abstract class BaseRecycleAdapter<T,D extends ViewDataBinding> extends RecyclerView.Adapter<BaseRecycleAdapter.ViewHolder<D>>{

    protected List<T> data;
    protected Context mContext;
    protected OnItemClickListener onItemClickListener;

    protected BaseRecycleAdapter(List<T> data) {
        this(null,data);
    }

    protected BaseRecycleAdapter(Context mContext, List<T> data) {
        this.data = data;
        this.mContext = mContext;
    }

    public abstract @LayoutRes
    int layoutResId();

    public abstract void onBindViewHolders(D binding, int position);

    public void addOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder<D> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder<D>(parent,layoutResId());
    }

    @Override
    public void onBindViewHolder(ViewHolder<D> holder, int position) {
        onBindViewHolders(holder.binding,position);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }

    public static class ViewHolder<D extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public D binding;
        public ViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),resId,parent,false).getRoot());
            binding = DataBindingUtil.getBinding(this.itemView);
        }
    }
}
