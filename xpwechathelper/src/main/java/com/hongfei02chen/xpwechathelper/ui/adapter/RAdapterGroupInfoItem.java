package com.hongfei02chen.xpwechathelper.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hongfei02chen.xpwechathelper.R;
import com.hongfei02chen.xpwechathelper.bean.ChatRoomBean;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/7
 */
public class RAdapterGroupInfoItem extends RecyclerView.Adapter<RAdapterGroupInfoItem.MyViewHolder> {
    private Context mContext;
    private List<ChatRoomBean> mDataList;
    private LayoutInflater mInflater;

    public RAdapterGroupInfoItem(Context context, List<ChatRoomBean> dataList) {
        mContext = context;
        mDataList = dataList;
        mInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_group_info_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        ChatRoomBean bean = mDataList.get(position);
        holder.tvChatRoom.setText(bean.getChatRoom());
        holder.etRoomName.setText(bean.getRoomName());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.etRoomName.setEnabled(isChecked);
                if (isChecked) {
                    holder.checkBox.setText(R.string._save);
                } else {
                    holder.checkBox.setText(R.string._edit);
                }
            }
        });
        holder.checkBox.setChecked(false);
        holder.etRoomName.setEnabled(false);
        holder.switcher.setChecked(bean.getState() > 0);
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvChatRoom;
        public TextInputEditText etRoomName;
        public AppCompatCheckBox checkBox;
        public SwitchCompat switcher;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvChatRoom = itemView.findViewById(R.id.tv_chat_room);
            etRoomName = itemView.findViewById(R.id.et_room_name);
            checkBox = itemView.findViewById(R.id.check_box);
            switcher = itemView.findViewById(R.id.switcher);
        }

    }
}
