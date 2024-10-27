package com.bjh.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleDTO> scheduleList;
    private ScheduleClickListener listener;

    public ScheduleAdapter(List<ScheduleDTO> scheduleList, ScheduleClickListener listener) {
        this.scheduleList = scheduleList != null ? scheduleList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleDTO schedule = scheduleList.get(position);
        holder.titleTextView.setText(schedule.getScheduleText() != null ? schedule.getScheduleText() : "제목 없음");
        holder.timeTextView.setText(schedule.getStartTime() + " - " + schedule.getEndTime());
        holder.locationTextView.setText(schedule.getLocation() != null ? schedule.getLocation() : "위치 없음");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onScheduleClick(schedule.getScheduleId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void updateSchedules(List<ScheduleDTO> newScheduleList) {
        this.scheduleList = newScheduleList != null ? newScheduleList : new ArrayList<>();
        notifyDataSetChanged(); // 데이터가 변경되었음을 알림
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeTextView, locationTextView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
        }
    }

    public interface ScheduleClickListener {
        void onScheduleClick(int scheduleId);
    }
}
