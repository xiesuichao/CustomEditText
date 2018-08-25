package com.example.admin.customedittext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiesuichao on 2018/6/13.
 */

public class CustomEditText extends View {

    private Paint textPaint;
    private Paint cursorPaint;
    private String textCol = "#FF486A";
    private String cursorCol = "#303F9F";
    private InputMethodManager inputManager;
    private OnInputStrChangedListener textChangeListener;
    private List<String> textList;
    private StringBuffer sb;
    private float mDownX;
    private boolean pressDown = false;
    private List<TextEntity> mTextEntityList = new ArrayList<>();
    private final int CURSOR_POSITION_INIT = -5;
    private int cursorPosition = CURSOR_POSITION_INIT;
    private boolean textDelete = false;
    private boolean textAdd = false;


    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnInputStrChangedListener {
        void textChanged(Editable s);
    }

    public void setOnInputStrChangedListener(OnInputStrChangedListener textListener) {
        this.textChangeListener = textListener;
    }

    private void init() {
        setFocusableInTouchMode(true);
        KeyBoardListener keyListener = new KeyBoardListener();
        setOnKeyListener(keyListener);

        inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        textList = new ArrayList<>();
        sb = new StringBuffer();

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor(textCol));
        textPaint.setTextSize(StrUtil.sp2px(getContext(), 15));

        cursorPaint = new Paint();
        cursorPaint.setAntiAlias(true);
        cursorPaint.setColor(Color.parseColor(cursorCol));
        cursorPaint.setStrokeWidth(StrUtil.dp2px(getContext(), 1.2f));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas, textList);
        drawCursorInput(canvas);
    }

    private void convertToStr(List<String> strList){
        if (strList.size() == 0){
            return;
        }
        sb.setLength(0);
        for (String s : strList) {
            sb.append(s);
        }
        PrintUtil.log("sb.toString", sb.toString());
    }

    private void matchCoordinate(List<String> strList){
        mTextEntityList.clear();
        for (int i = 0; i < strList.size(); i++) {
            TextEntity textEntity = new TextEntity();
            textEntity.setTextStr(strList.get(i));
            textEntity.setWidth(textPaint.measureText(strList.get(i)));
            float leftTextWidth = 0f;
            for (int k = 0; k < i; k++) {
                leftTextWidth += mTextEntityList.get(k).getWidth();
            }
            textEntity.setLeftX(getWidth() / 2 - textPaint.measureText(sb.toString()) / 2 + leftTextWidth);
            textEntity.setRightX(getWidth() / 2 - textPaint.measureText(sb.toString()) / 2
                    + leftTextWidth + textPaint.measureText(strList.get(i)));
            mTextEntityList.add(textEntity);
        }
        PrintUtil.log("mTextEntityList", mTextEntityList.toString());
        PrintUtil.log("mTextEntityList.size", mTextEntityList.size());
    }

    private Editable convertStrEditable(String str){
        return new SpannableStringBuilder(str);
    }

    private void drawText(Canvas canvas, List<String> textList) {
        PrintUtil.log("textList", textList.toString());
        if (textList.size() == 0) {
            return;
        }
        Rect rect = new Rect();
        textPaint.getTextBounds(sb.toString(), 0, sb.toString().length(), rect);
        canvas.drawText(sb.toString(), getWidth() / 2 - rect.width() / 2, getHeight() / 2 + rect.height() / 2, textPaint);
    }

    private void drawCursorInput(Canvas canvas) {
        if (textList.size() == 0) {
            canvas.drawLine(getWidth() / 2, getHeight() / 3, getWidth() / 2, getHeight() * 2 / 3, cursorPaint);
            pressDown = false;
            mDownX = 0f;
            textDelete = false;
            textAdd = false;
            cursorPosition = CURSOR_POSITION_INIT;
            PrintUtil.log("textList.size == 0", textList.size());
        } else if (pressDown && mDownX >= (getWidth() / 2 - textPaint.measureText(sb.toString()))
                && mDownX <= (getWidth() / 2 + textPaint.measureText(sb.toString()))) {
            pressDown = false;
            PrintUtil.log("textList.size > 0", textList.size());
            float cursorX = getWidth() / 2;
            for (int i = 0; i < mTextEntityList.size(); i++) {
                if (i == 0 && mDownX <= mTextEntityList.get(i).getLeftX() + mTextEntityList.get(i).getWidth() / 2) {
                    cursorX = mTextEntityList.get(i).getLeftX();
                    cursorPosition = -1;
                    PrintUtil.log("i == 0");
                    break;
                } else if (i == mTextEntityList.size() - 1
                        && mDownX >= mTextEntityList.get(i).getRightX() - mTextEntityList.get(i).getWidth() / 2) {
                    cursorX = mTextEntityList.get(i).getRightX() + StrUtil.dp2px(getContext(), 0.5f);
                    cursorPosition = i;
                    PrintUtil.log("i == mTextEntityList.size() - 1 ");
                    break;
                } else if (i < mTextEntityList.size() - 1
                        && mDownX >= mTextEntityList.get(i).getLeftX() + mTextEntityList.get(i).getWidth() / 2
                        && mDownX <= mTextEntityList.get(i + 1).getLeftX() + mTextEntityList.get(i + 1).getWidth() / 2) {
                    cursorX = mTextEntityList.get(i).getRightX();
                    cursorPosition = i;
                    PrintUtil.log("i < mTextEntityList.size() - 1 ");
                    break;
                } else {
                    cursorX = getWidth() / 2 + textPaint.measureText(sb.toString()) / 2;
                    cursorPosition = mTextEntityList.size() - 1;
                    PrintUtil.log("else");
                }
            }
            PrintUtil.log("cursorX", cursorX);

            canvas.drawLine(cursorX, getHeight() / 3, cursorX, getHeight() * 2 / 3, cursorPaint);

        } else if (pressDown && mDownX < (getWidth() / 2 - textPaint.measureText(sb.toString()) / 2) && textList.size() > 0) {
            pressDown = false;
            canvas.drawLine(getWidth() / 2 - textPaint.measureText(sb.toString()) / 2 - StrUtil.dp2px(getContext(), 1),
                    getHeight() / 3,
                    getWidth() / 2 - textPaint.measureText(sb.toString()) / 2 - StrUtil.dp2px(getContext(), 1),
                    getHeight() * 2 / 3,
                    cursorPaint);
            cursorPosition = -1;
            PrintUtil.log("press left");

        } else if (pressDown && mDownX > (getWidth() / 2 + textPaint.measureText(sb.toString()) / 2) && textList.size() > 0) {
            pressDown = false;
            canvas.drawLine(getWidth() / 2 + textPaint.measureText(sb.toString()) / 2 + StrUtil.dp2px(getContext(), 1),
                    getHeight() / 3,
                    getWidth() / 2 + textPaint.measureText(sb.toString()) / 2 + StrUtil.dp2px(getContext(), 1),
                    getHeight() * 2 / 3,
                    cursorPaint);
            cursorPosition = textList.size() - 1;
            PrintUtil.log("press right");

        } else if (!pressDown && textDelete) {
            textDelete = false;
            if (cursorPosition >= 0) {
                canvas.drawLine(mTextEntityList.get(cursorPosition).getRightX(),
                        getHeight() / 3,
                        mTextEntityList.get(cursorPosition).getRightX(),
                        getHeight() * 2 / 3,
                        cursorPaint);
                PrintUtil.log("delete position > 0");
            } else if (textList.size() > 0) {
                canvas.drawLine(getWidth() / 2 - textPaint.measureText(sb.toString()) / 2,
                        getHeight() / 3,
                        getWidth() / 2 - textPaint.measureText(sb.toString()) / 2,
                        getHeight() * 2 / 3,
                        cursorPaint);
                PrintUtil.log("delete position == -1 && size() > 0");
            } else {
                canvas.drawLine(getWidth() / 2, getHeight() / 3, getWidth() / 2, getHeight() * 2 / 3, cursorPaint);
                PrintUtil.log("delete position == -1 && size() == 0");
            }

        } else if (!pressDown && textAdd) {
            textAdd = false;
            canvas.drawLine(mTextEntityList.get(cursorPosition).getRightX() + StrUtil.dp2px(getContext(), 0.5f),
                    getHeight() / 3,
                    mTextEntityList.get(cursorPosition).getRightX() + StrUtil.dp2px(getContext(), 0.5f),
                    getHeight() * 2 / 3,
                    cursorPaint);
            PrintUtil.log("textAdd");
        } else if (cursorPosition == -1 && textList.size() > 0) {
            canvas.drawLine(getWidth() / 2 - textPaint.measureText(sb.toString()) / 2,
                    getHeight() / 3,
                    getWidth() / 2 - textPaint.measureText(sb.toString()) / 2,
                    getHeight() * 2 / 3,
                    cursorPaint);
        } else {
            canvas.drawLine(getWidth() / 2 + textPaint.measureText(sb.toString()) / 2 + StrUtil.dp2px(getContext(), 1),
                    getHeight() / 3,
                    getWidth() / 2 + textPaint.measureText(sb.toString()) / 2 + StrUtil.dp2px(getContext(), 1),
                    getHeight() * 2 / 3,
                    cursorPaint);
            PrintUtil.log("out else");
        }
        PrintUtil.log("cursorPosition", cursorPosition);

    }

    class KeyBoardListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (textList.size() > 0) {
                        if (cursorPosition == CURSOR_POSITION_INIT) {
                            textList.remove(textList.size() - 1);
                            PrintUtil.log("remove textList.size - 1, position = ", textList.size() - 1);

                            if (textList.size() == 0) {
                                cursorPosition = CURSOR_POSITION_INIT;
                            }
                        } else if (cursorPosition >= 0 && cursorPosition <= mTextEntityList.size() - 1) {
                            textList.remove(cursorPosition);
                            PrintUtil.log("remove get(cursorPosition), position = ", cursorPosition);
                            textDelete = true;
                            cursorPosition--;
                            if (textList.size() == 0) {
                                cursorPosition = CURSOR_POSITION_INIT;
                            }
                        }
                    }
                    convertToStr(textList);
                    matchCoordinate(textList);
                    invalidate();
                    return true;

                } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    if (cursorPosition == CURSOR_POSITION_INIT || textList.size() == 0) {
                        textList.add(keyCode - 7 + "");
                        cursorPosition = textList.size() - 1;
                    } else if (cursorPosition >= -1 && cursorPosition <= mTextEntityList.size() - 1) {
                        textList.add(cursorPosition + 1, keyCode - 7 + "");
                        textAdd = true;
                        cursorPosition++;
                    }
                    convertToStr(textList);
                    matchCoordinate(textList);
                    invalidate();
                    return true;

                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    PrintUtil.log("enter");
                    inputManager.hideSoftInputFromWindow(CustomEditText.this.getWindowToken(), 0);
                    return true;

                } else if (keyCode == KeyEvent.KEYCODE_PERIOD) {
                    if (!sb.toString().contains(".")) {
                        if (cursorPosition == CURSOR_POSITION_INIT) {
                            textList.add(".");
                            cursorPosition = textList.size() - 1;
                        } else if (cursorPosition >= -1 && cursorPosition <= mTextEntityList.size() - 1) {
                            textList.add(cursorPosition + 1, ".");
                            textAdd = true;
                            cursorPosition++;
                        }
                        convertToStr(textList);
                        matchCoordinate(textList);
                        invalidate();
                    }
                    return true;
                }

            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            requestFocus();
            inputManager.showSoftInput(CustomEditText.this, InputMethodManager.SHOW_FORCED);
            mDownX = event.getX();
            PrintUtil.log("--------------");
            PrintUtil.log("downX", mDownX);
            pressDown = true;
            invalidate();

            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus && inputManager.isActive()) {
            inputManager.hideSoftInputFromWindow(CustomEditText.this.getWindowToken(), 0);
        }
    }

    private static class TextEntity {
        private String textStr;
        private float width;
        private float rightX;
        private float leftX;

        TextEntity() {
        }

        public TextEntity(String textStr, float width, float rightX, float leftX) {
            this.textStr = textStr;
            this.width = width;
            this.rightX = rightX;
            this.leftX = leftX;
        }

        public String getTextStr() {
            return textStr;
        }

        void setTextStr(String textStr) {
            this.textStr = textStr;
        }

        float getWidth() {
            return width;
        }

        void setWidth(float width) {
            this.width = width;
        }

        float getRightX() {
            return rightX;
        }

        void setRightX(float rightX) {
            this.rightX = rightX;
        }

        float getLeftX() {
            return leftX;
        }

        void setLeftX(float leftX) {
            this.leftX = leftX;
        }

        @Override
        public String toString() {
            return "TextEntity{" +
                    "textStr='" + textStr + '\'' +
                    ", width=" + width +
                    ", rightX=" + rightX +
                    ", leftX=" + leftX +
                    '}';
        }
    }


}
