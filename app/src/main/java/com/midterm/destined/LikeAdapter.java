//package com.midterm.destined;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
//
//    private final Fragment fragment;
//
//    public LikeAdapter(Fragment fragment) {
//        this.fragment = fragment;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_collection_object, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.bind(position + 1); // Bind the position as an object
//    }
//
//    @Override
//    public int getItemCount() {
//        return 100;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView textView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(android.R.id.text1);
//        }
//
//        public void bind(int object) {
//            textView.setText(Integer.toString(object));
//        }
//    }
//}
//
