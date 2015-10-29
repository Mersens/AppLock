/*
 * Copyright 2015-2016 TakWolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mersens.applicationlock.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import java.util.ArrayList;
import java.util.List;
import com.mersens.applicationlock.main.R;

public class Lock9View extends ViewGroup {

    /**
     * 节点相关定义
     */
    private List<Pair<NodeView, NodeView>> lineList = new ArrayList<Pair<NodeView, NodeView>>(); // 已经连线的节点链表
    private NodeView currentNode; // 最近一个点亮的节点，null表示还没有点亮任何节点
    private float x; // 当前手指坐标x
    private float y; // 当前手指坐标y
    /**
     * 自定义属性列表
     */
    private Drawable nodeSrc;
    private Drawable nodeOnSrc;
    private float nodeSize; // 节点大小，如果不为0，则忽略内边距和间距属性
    private float nodeAreaExpand; // 对节点的触摸区域进行扩展
    private int nodeOnAnim; // 节点点亮时的动画
    private int lineColor;
    private float lineWidth;
    private float padding; // 内边距
    private float spacing; // 节点间隔距离

    /**
     * 震动管理器
     */
    private Vibrator vibrator;
    private boolean enableVibrate;
    private int vibrateTime;

    /**
     * 画线用的画笔
     */
    private Paint paint;

    /**
     * 密码构建器
     */
    private StringBuilder passwordBuilder = new StringBuilder();

    /**
     * 结果回调监听器接口
     */
    private CallBack callBack;

    public interface CallBack {

        void onFinish(String password);

    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 构造函数
     */

    public Lock9View(Context context) {
        super(context);
        initFromAttributes(context, null, 0);
    }

    public Lock9View(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs, 0);
    }

    public Lock9View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs, defStyleAttr);
    }



    /**
     * 初始化
     */
    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        // 获取定义的属性
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock9View, defStyleAttr, 0);

        nodeSrc = a.getDrawable(R.styleable.Lock9View_lock9_nodeSrc);
        nodeOnSrc = a.getDrawable(R.styleable.Lock9View_lock9_nodeOnSrc);
        nodeSize = a.getDimension(R.styleable.Lock9View_lock9_nodeSize, 0);
        nodeAreaExpand = a.getDimension(R.styleable.Lock9View_lock9_nodeAreaExpand, 0);
        nodeOnAnim = a.getResourceId(R.styleable.Lock9View_lock9_nodeOnAnim, 0);
        lineColor = a.getColor(R.styleable.Lock9View_lock9_lineColor, Color.argb(0, 0, 0, 0));
        lineWidth = a.getDimension(R.styleable.Lock9View_lock9_lineWidth, 0);
        padding = a.getDimension(R.styleable.Lock9View_lock9_padding, 0);
        spacing = a.getDimension(R.styleable.Lock9View_lock9_spacing, 0);

        enableVibrate = a.getBoolean(R.styleable.Lock9View_lock9_enableVibrate, false);
        vibrateTime = a.getInt(R.styleable.Lock9View_lock9_vibrateTime, 20);

        a.recycle();

        // 初始化振动器
        if (enableVibrate) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        // 初始化画笔
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(lineColor);
        paint.setAntiAlias(true); // 抗锯齿

        // 构建node
        for (int n = 0; n < 9; n++) {
            NodeView node = new NodeView(getContext(), n + 1);
            addView(node);
        }

        // 清除FLAG，否则 onDraw() 不会调用，原因是 ViewGroup 默认透明背景不需要调用 onDraw()
        setWillNotDraw(false);
    }

    /**
     * TODO 我们让高度等于宽度 - 方法有待验证
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = measureSize(widthMeasureSpec); // 测量宽度
        setMeasuredDimension(size, size);
    }

    /**
     * TODO 测量长度
     */
    private int measureSize(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec); // 得到模式
        int specSize = MeasureSpec.getSize(measureSpec); // 得到尺寸
        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                return specSize;
            default:
                return 0;
        }
    }

    /**
     * 在这里进行node的布局
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            if (nodeSize > 0) { // 如果设置nodeSize值，则将节点绘制在九等分区域中心
                float areaWidth = (right - left) / 3;
                for (int n = 0; n < 9; n++) {
                    NodeView node = (NodeView) getChildAt(n);
                    // 获取3*3宫格内坐标
                    int row = n / 3;
                    int col = n % 3;
                    // 计算实际的坐标
                    int l = (int) (col * areaWidth + (areaWidth - nodeSize) / 2);
                    int t = (int) (row * areaWidth + (areaWidth - nodeSize) / 2);
                    int r = (int) (l + nodeSize);
                    int b = (int) (t + nodeSize);
                    node.layout(l, t, r, b);
                }
            } else { // 否则按照分割边距布局，手动计算节点大小
                float nodeSize = (right - left - padding * 2 - spacing * 2) / 3;
                for (int n = 0; n < 9; n++) {
                    NodeView node = (NodeView) getChildAt(n);
                    // 获取3*3宫格内坐标
                    int row = n / 3;
                    int col = n % 3;
                    // 计算实际的坐标，要包括内边距和分割边距
                    int l = (int) (padding + col * (nodeSize + spacing));
                    int t = (int) (padding + row * (nodeSize + spacing));
                    int r = (int) (l + nodeSize);
                    int b = (int) (t + nodeSize);
                    node.layout(l, t, r, b);
                }
            }
        }
    }

    /**
     * 在这里处理手势
     */
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                x = event.getX(); // 这里要实时记录手指的坐标
                y = event.getY();
                NodeView nodeAt = getNodeAt(x, y);
                if (currentNode == null) { // 之前没有点
                    if (nodeAt != null) { // 第一个点
                        currentNode = nodeAt;
                        currentNode.setHighLighted(true);
                        passwordBuilder.append(currentNode.getNum());
                        invalidate();  // 通知重绘
                    }
                } else { // 之前有点-所以怎么样都要重绘
                    if (nodeAt != null && !nodeAt.isHighLighted()) { // 当前碰触了新点
                        nodeAt.setHighLighted(true);
                        Pair<NodeView, NodeView> pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                        lineList.add(pair);
                        // 赋值当前的node
                        currentNode = nodeAt;
                        passwordBuilder.append(currentNode.getNum());
                    }
                    invalidate(); // 通知重绘
                }
                break;
            case MotionEvent.ACTION_UP:
                if (passwordBuilder.length() > 0) { // 有触摸点
                    // 回调结果
                    if (callBack != null) {
                        callBack.onFinish(passwordBuilder.toString());
                    }
                    // 清空状态
                    lineList.clear();
                    currentNode = null;
                    passwordBuilder.setLength(0);
                    // 清除高亮
                    for (int n = 0; n < getChildCount(); n++) {
                        NodeView node = (NodeView) getChildAt(n);
                        node.setHighLighted(false);
                    }
                    // 通知重绘
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * 系统绘制回调-主要绘制连线
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 先绘制已有的连线
        for (Pair<NodeView, NodeView> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(), pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
        // 如果已经有点亮的点，则在点亮点和手指位置之间绘制连线
        if (currentNode != null) {
            canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), x, y, paint);
        }
    }

    /**
     * 获取给定坐标点的Node，返回null表示当前手指在两个Node之间
     */
    private NodeView getNodeAt(float x, float y) {
        for (int n = 0; n < getChildCount(); n++) {
            NodeView node = (NodeView) getChildAt(n);
            if (!(x >= node.getLeft() - nodeAreaExpand && x < node.getRight() + nodeAreaExpand)) {
                continue;
            }
            if (!(y >= node.getTop() - nodeAreaExpand && y < node.getBottom() + nodeAreaExpand)) {
                continue;
            }
            return node;
        }
        return null;
    }

    /**
     * 节点描述类
     */
    private class NodeView extends View {

        private int num;
        private boolean highLighted = false;

        @SuppressWarnings("deprecation")
        public NodeView(Context context, int num) {
            super(context);
            this.num = num;
            setBackgroundDrawable(nodeSrc);
        }

        public boolean isHighLighted() {
            return highLighted;
        }

        @SuppressWarnings("deprecation")
        public void setHighLighted(boolean highLighted) {
            if (this.highLighted != highLighted) {
                this.highLighted = highLighted;
                if (nodeOnSrc != null) { // 没有设置高亮图片则不变化
                    setBackgroundDrawable(highLighted ? nodeOnSrc : nodeSrc);
                }
                if (nodeOnAnim != 0) { // 播放动画
                    if (highLighted) {
                        startAnimation(AnimationUtils.loadAnimation(getContext(), nodeOnAnim));
                    } else {
                        clearAnimation();
                    }
                }
                if (enableVibrate) { // 震动
                    if (highLighted) {
                        vibrator.vibrate(vibrateTime);
                    }
                }
            }
        }

        public int getCenterX() {
            return (getLeft() + getRight()) / 2;
        }

        public int getCenterY() {
            return (getTop() + getBottom()) / 2;
        }

        public int getNum() {
            return num;
        }

    }

}
