package cn.teachcourse.slidingitemremove;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/*
 * Created by dazhao@teachcourse.cn on 2019/7/25.
 * 向左滑动，出现删除按钮，点击删除按钮响应事件
 */
public class SlidingItemRemove extends RelativeLayout {
    private static final String TAG = "SlidingItemRemove";
    /**
     * 确定当前屏幕的宽度
     */
    private int screenWidth;

    /**
     * 初始化VelocityTracker获取滑动速度
     */
    private VelocityTracker velocityTracker;

    /**
     * 删除按钮偏离右边的外边距，默认是一个负值，隐藏了删除按钮
     */
    private int rightEdge;

    /**
     * 定义删除按钮View
     */
    private View rightDeleteBtn;

    /**
     * 定义左边itemView
     */
    private View leftItemView;

    /**
     * 获取删除按钮的宽度、高度
     */
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 200;
    private int rightBtnWidth = DEFAULT_WIDTH;
    private int rightBtnHeight = DEFAULT_HEIGHT;

    /**
     * 获取itemView的宽度、高度
     */
    private int itemViewWidth;
    private int itemViewHeight;

    /**
     * 左侧布局的参数，通过此参数来重新确定左侧布局的宽度，以及更改leftMargin的值。
     */
    private MarginLayoutParams leftLayoutParams;

    /**
     * 右侧布局的参数，通过此参数来重新确定右侧布局的宽度。
     */
    private MarginLayoutParams rightLayoutParams;
    /**
     * 记录手指按下时的横坐标。
     */
    private float xDown, yDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float xMove, yMove;

    /**
     * 记录手机抬起时的横坐标。
     */
    private float xUp, yUp;

    /**
     * 记录删除按钮是否显示
     */
    private boolean rightBtnIsVisible = false;


    public SlidingItemRemove(Context context) {
        this(context, null);
    }

    public SlidingItemRemove(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
//        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
//        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //计算内部子控件占据的宽、高
        int width = 0;
        int height = 0;
        LayoutParams lp;

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (i == 0) {
                rightBtnWidth = view.getMeasuredWidth();
                rightBtnHeight = view.getMeasuredHeight();
            }
            lp = (LayoutParams) view.getLayoutParams();
            width = +(view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
            height = view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }

        Log.d(TAG, "onMeasure: width=" + width + ";height=" + height);

        setMeasuredDimension(width, height - rightBtnHeight > 0 ? height : rightBtnHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // 获取右侧布局对象
            rightDeleteBtn = getChildAt(0);
            rightEdge = -rightBtnWidth;
            //设置右边布局外边距为负数
            rightLayoutParams = (MarginLayoutParams) rightDeleteBtn.getLayoutParams();
            rightDeleteBtn.setLayoutParams(rightLayoutParams);


            // 获取左侧布局对象
            leftItemView = getChildAt(1);
            leftLayoutParams = (MarginLayoutParams) leftItemView.getLayoutParams();
            leftLayoutParams.width = screenWidth;
            leftItemView.setLayoutParams(leftLayoutParams);

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean rightBtnVisible = rightBtnIsVisible;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:


                break;
        }
        Log.d(TAG, "onInterceptTouchEvent: " + rightBtnVisible);
        return !rightBtnVisible;
    }


    /**
     * 初始化VelocityTracker实例
     */
    private void createVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * 获取x轴方向滚动速度大小
     */
    private int getXVelocity() {
        //设置1000毫秒移动1000个像素
        velocityTracker.computeCurrentVelocity(1000);
        float speed = velocityTracker.getXVelocity();
        return Math.abs((int) speed);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        createVelocityTracker(event);
        boolean status = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();
                Log.d(TAG, "onTouchEvent: xDown=" + xDown + ";yDown=" + yDown);
                //down必须返回true，才会接收move和up的动作
                status = true;
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                yMove = event.getRawY();
                int distanceX = (int) (xMove - xDown);
                //删除按钮不可见，而且滑动距离识别向左滑动（distanceX为负数）
//                if (!rightBtnIsVisible && distanceX < 0) {
                if (distanceX < 0) {
                    //滑动过程，改变按钮的外边距
                    int margin = -distanceX;
                    //滑动的外边距大于0时，设置外边距为0
                    if (margin > rightBtnWidth) {
                        margin = rightBtnWidth;
                    }
                    setLeftItemMargin(margin);
                }
//                if (rightBtnIsVisible && distanceX > 0) {
                if (distanceX > 0) {
                    int margin = -distanceX;
                    if (margin < 0) {
                        margin = 0;
                    }

                    setLeftItemMargin(margin);
                }
                Log.d(TAG, "onTouchEvent: rightBtnIsVisible=" + rightBtnIsVisible);
                Log.d(TAG, "onTouchEvent: xMove=" + xMove + ";yMove=" + yMove);
                status = true;
                break;
            case MotionEvent.ACTION_UP:

                xUp = event.getRawX();
                yUp = event.getRawY();
                int xUpDistance = (int) (xUp - xDown);
                //识别手势滑动的方向，执行自动回滚的操作

                if (wantToShowRightBtn()) {
                    //滑动的距离大于删除按钮宽度的一半，弹性滑动直到显示全部删除按钮
                    scroll(xUpDistance);
                } else if (wantToShowLeftItem()) {
                    scroll(xUpDistance);
                }
                Log.d(TAG, "onTouchEvent: xUp=" + xUp + ";yUp=" + yUp);
                status = false;
                break;
        }
        return status;
    }

    private void setLeftItemMargin(int margin) {
        leftLayoutParams.rightMargin = margin;
        leftLayoutParams.leftMargin = -margin;
        leftItemView.setLayoutParams(leftLayoutParams);
    }

    /**
     * 向左滑动，移动距离大于删除按钮一半，弹性滑动到最大值
     *
     * @param xUpDistance
     */
    private void scroll(int xUpDistance) {
        if (shouldScrollToShowRightLayout(xUpDistance))
            scrollToShowRightLayout(xUpDistance);
        else
            scrollToShowLeftLayout(xUpDistance);
    }

    /**
     * 识别手势向左滑动，而且右边删除按钮没有显示出来
     *
     * @return
     */
    private boolean wantToShowRightBtn() {
        return xUp - xDown < 0 && !rightBtnIsVisible;
    }

    /**
     * 识别手势向右滑动，而且删除按钮已经显示出来
     *
     * @return
     */
    private boolean wantToShowLeftItem() {
        return xUp - xDown > 0 && rightBtnIsVisible;
    }

    /**
     * 移动的距离大于删除按钮的一半，弹性滑动显示剩下部分
     *
     * @param xUpDistance
     * @return
     */
    private boolean shouldScrollToShowRightLayout(int xUpDistance) {
        return xUp - xDown < 0 && Math.abs(xUpDistance) > rightBtnWidth / 2;
    }


    /**
     * 滑动的距离大于按钮宽度一半，自动滚动到指定宽度
     */
    private void scrollToShowRightLayout(int upDistance) {
        if (Math.abs(upDistance) > rightBtnWidth) {
            upDistance = rightBtnWidth;
            setLeftItemMargin(upDistance);
        } else {
            new ScrollTask().execute(30);
        }
        Log.d(TAG, "scrollToShowRightLayout: " + upDistance);
    }

    /**
     * 滑动距离大于按钮宽度一半，自动滚动到指定宽度
     */
    private void scrollToShowLeftLayout(int upDistance) {
        if (upDistance > rightBtnWidth) {
            upDistance = rightBtnWidth;
            setLeftItemMargin(-upDistance);
        } else {
            new ScrollTask().execute(-30);
        }
        Log.d(TAG, "scrollToShowLeftLayout: " + upDistance);
    }

    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int margin = leftLayoutParams.rightMargin;
            while (true) {
                margin = margin + speed[0];
                if (margin > rightBtnWidth) {
                    margin = rightBtnWidth;
                    break;
                }
                if (margin < 0) {
                    margin = 0;
                    break;
                }
                publishProgress(margin);
                Log.d(TAG, "doInBackground: " + margin);
            }
//            if (speed[0] > 0) {
//                rightBtnIsVisible = true;
//            } else {
//                rightBtnIsVisible = false;
//            }
            return margin;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
//            setLeftItemMargin(progress[0]);
            Log.d(TAG, "onProgressUpdate: " + progress[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            setLeftItemMargin(result);
            Log.d(TAG, "onPostExecute: " + result);
        }
    }
}
