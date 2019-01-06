package com.example.sanket.mvcdemo.model.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanket.mvcdemo.R;
import com.example.sanket.mvcdemo.model.pojo.Photo;
import com.example.sanket.mvcdemo.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class AdapterPhotos extends RecyclerView.Adapter<AdapterPhotos.MyViewHolder> implements Filterable {

    private List<Photo> mList;
    private List<Photo> mListFiltered;
    private Context mContext;
    private OnListItemClickListener onListItemClickListener;

    public AdapterPhotos(Context mContext, List<Photo> mList) {
        this.mList = mList;
        this.mListFiltered = mList;
        this.mContext = mContext;
    }

    public void setPhotosList(List<Photo> mList){
        this.mListFiltered = mList;
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Photo photo = mListFiltered.get(i);
        Glide.with(mContext)
                .load(photo.getThumbnailUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageViewPhoto);
        holder.textViewAlbumId.setText(mContext.getResources().getString(R.string.P_Album,String.valueOf(photo.getAlbumId())));
        holder.textViewTitle.setText(photo.getTitle());
        holder.textViewId.setText(mContext.getResources().getString(R.string.P_Id,String.valueOf(photo.getId())));

    }

    @Override
    public int getItemCount() {
        return mListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mListFiltered = mList;
                } else {
                    List<Photo> filteredList = new ArrayList<>();
                    for (Photo row : mList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListFiltered = (List<Photo>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.imageViewPhoto) ImageView imageViewPhoto;
        @BindView(R.id.textViewId) TextView textViewId;
        @BindView(R.id.textViewAlbumId)TextView textViewAlbumId;
        @BindView(R.id.textViewTitle)TextView textViewTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onListItemClickListener==null){
                        Functions.showMessage(mContext,"OnListItemClickListener not set");
                        return;
                    }

                    int pos = getLayoutPosition();
                    Photo photo = mListFiltered.get(pos);
                    onListItemClickListener.onItemClick(pos, photo);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (onListItemClickListener==null){
                        Functions.showMessage(mContext,"OnListItemClickListener not set");
                        return false;
                    }

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext)
                            .setTitle("Delete Item")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int pos = getLayoutPosition();
                                    Photo photo = mListFiltered.get(pos);
                                    mListFiltered.remove(pos);
                                    List<Photo> copyList = mList;
                                    for(Photo p : copyList){
                                        if(p.getId()==photo.getId()){
                                            mList.remove(p);
                                            break;
                                        }
                                    }
                                    notifyDataSetChanged();
                                    onListItemClickListener.onItemLongClick(pos, photo, mList);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dialog.show();

                    return false;
                }
            });
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener){
        this.onListItemClickListener = onListItemClickListener;
    }

    public interface OnListItemClickListener{
        void onItemClick(int pos, Photo photo);
        void onItemLongClick(int pos, Photo photo, List<Photo> newList);
    }

}
