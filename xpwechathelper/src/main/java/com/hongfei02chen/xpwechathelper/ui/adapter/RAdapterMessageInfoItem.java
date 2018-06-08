package com.hongfei02chen.xpwechathelper.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hongfei02chen.xpwechathelper.R;
import com.hongfei02chen.xpwechathelper.bean.DbChatRoomHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/8
 */
public class RAdapterMessageInfoItem extends RecyclerView.Adapter<RAdapterMessageInfoItem.MyViewHolder> {
    private Context mContext;
    private List<JoinMessageBean> mDataList;
    private LayoutInflater mInflater;

    public RAdapterMessageInfoItem(Context context, List<JoinMessageBean> dataList) {
        mContext = context;
        mDataList = dataList;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_message_info_iten, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        JoinMessageBean messageBean = mDataList.get(position);
        String roomName = DbChatRoomHelper.getRoomName(messageBean.getChatRoom());
        holder.tvMsgId.setText("MsgId: " + messageBean.getMsgId());
        holder.tvRoomName.setText("RoomName: " + roomName);
        holder.tvNickname.setText("Nickname: " + messageBean.getNickname());
        holder.tvState.setText("state: " + messageBean.getState());
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMsgId;
        public TextView tvRoomName;
        public TextView tvNickname;
        public TextView tvState;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMsgId = itemView.findViewById(R.id.tv_msg_id);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvState = itemView.findViewById(R.id.tv_state);
        }

    }
}
