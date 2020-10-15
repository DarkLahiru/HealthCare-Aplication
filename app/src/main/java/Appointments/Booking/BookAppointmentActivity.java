package Appointments.Booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;

import com.example.healthcare.R;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAppointmentActivity extends AppCompatActivity {
    @BindView(R.id.btn_prev_step)
    Button btn_prev_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;
    @BindView(R.id.step_view)
    StepView step_view;
    @BindView(R.id.viewPaper)
    ViewPager viewPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        ButterKnife.bind(BookAppointmentActivity.this);

        setupStepView();
        setColorButton();

        //View
        viewPaper.setAdapter(new MyViewPageAdapter(getSupportFragmentManager()));
        viewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    btn_prev_step.setEnabled(false);
                else
                    btn_prev_step.setEnabled(true);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.color.colorPrimaryDark);
        }
        else{
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_prev_step.isEnabled()){
            btn_prev_step.setBackgroundResource(R.color.colorPrimaryDark);
        }
        else{
            btn_prev_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Doctor");
        stepList.add("Location");
        stepList.add("Time");
        stepList.add("Confirm");
        step_view.setSteps(stepList);

    }
}