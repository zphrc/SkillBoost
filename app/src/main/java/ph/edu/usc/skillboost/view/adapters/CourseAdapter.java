package ph.edu.usc.skillboost.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ph.edu.usc.skillboost.R;
import ph.edu.usc.skillboost.model.Course;
import ph.edu.usc.skillboost.utils.Utilities;
import ph.edu.usc.skillboost.view.CourseDetailsActivity;

public class CourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum CardSize {
        SMALL, MEDIUM, LARGE
    }
    private List<Course> courseList;
    private List<Course> allCourses;
    private CardSize cardSize;
    private Context context;
    private String source;

    public CourseAdapter(Context context, List<Course> courseList, CardSize cardSize, String source) {
        this.courseList = courseList;
        this.allCourses = new ArrayList<>(courseList);
        this.cardSize = cardSize;
        this.context = context;
        this.source = source;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // Set card size dynamically
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        switch (cardSize) {
            case SMALL:
                width = dpToPx(parent.getContext(), 200);
                height = dpToPx(parent.getContext(), 180);
                break;
            case MEDIUM:
                width = dpToPx(parent.getContext(), 230);
                height = dpToPx(parent.getContext(), 300);
                break;
            case LARGE:
                width = dpToPx(parent.getContext(), 340);
                height = dpToPx(parent.getContext(), 280);
                break;
        }

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(width, height);
        layoutParams.setMargins(dpToPx(context, 0), dpToPx(context, 0), dpToPx(context, 30), dpToPx(context, 30));

        if (viewType == 1) {  // Completed course
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_completed, parent, false);
            view.setLayoutParams(layoutParams);
            return new CompletedCourseViewHolder(view);
        } else {  // Normal course
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
            view.setLayoutParams(layoutParams);
            return new CourseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Course course = courseList.get(position);

        if (holder instanceof CourseViewHolder) {
            // Set course details (title, description, etc.)
            CourseViewHolder courseHolder = (CourseViewHolder) holder;
            courseHolder.title.setText(course.getTitle());
            courseHolder.subtitle.setText(course.getSubtitle());
            courseHolder.image.setImageResource(Utilities.getDrawableFromRes(context, course.getImageRes()));
        } else if (holder instanceof CompletedCourseViewHolder) {
            CompletedCourseViewHolder courseHolder = (CompletedCourseViewHolder) holder;
            courseHolder.image.setImageResource(Utilities.getDrawableFromRes(context, course.getImageRes()));
        }

        // Set onClickListener to route to CourseDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseDetailsActivity.class);
            intent.putExtra("courseId", course.getCourseId());
            intent.putExtra("source", source); // Pass the origin
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void filter(String query) {
        courseList.clear();
        if (query.isEmpty()) {
            courseList.addAll(allCourses); // If query is empty, restore the original list
        } else {
            for (Course course : allCourses) {
                if (course.getTitle().toLowerCase().startsWith(query.toLowerCase())) {
                    courseList.add(course);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        category = category.toLowerCase();

        courseList.clear();
        if (category.isEmpty() || category.equals("all")) {
            courseList.addAll(allCourses); // If query is empty, restore the original list
        } else {
            for (Course course : allCourses) {
                List<String> categories = course.getCategories();
                if (categories != null && categories.stream().map(String::toLowerCase).anyMatch(category::equals)) {
                    courseList.add(course);
                }
            }
        }

        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView image;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_course_title);
            subtitle = itemView.findViewById(R.id.text_course_subtitle);
            image = itemView.findViewById(R.id.image_course);
        }
    }

    static class CompletedCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public CompletedCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_course);
        }
    }

    private int dpToPx(android.content.Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void updateCourseList(List<Course> newCourses) {
        courseList.clear();
        courseList.addAll(newCourses);

        if (allCourses.isEmpty()){
            allCourses.addAll(newCourses); // Add all courses at the start
        }

        notifyDataSetChanged();
    }
}
