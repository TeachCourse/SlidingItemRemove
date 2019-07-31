package cn.teachcourse.slidingitemremove;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private View rightBtnView;
    private SlidingItemRemove slidingRemoveView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        rightBtnView=findViewById(R.id.right_btn);
        slidingRemoveView=findViewById(R.id.sliding_remove_btn);
    }
}
