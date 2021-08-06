package com.example.randomnumbergenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public EditText startRange;
    public EditText endRange;
    public CheckBox allowOverlapping;
    public LinearLayout RangeLinearLayout;
    public TextView result;
    public TextView resultView;
    public ScrollView scrollView;
    public AdView adView;

    public int startRangeNum; //시작 범위
    public int endRangeNum; //끝 범위
    public int accumulatedNum; //누적 뽑은 수 개수
    public boolean allowOverlappingBool; // 중복 허용 여부
    public boolean hasStarted; //현재 뽑기 진행중인 경우 true
    public List<Integer> RandomNumberList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //광고 SDK 초기화
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        startRange = findViewById(R.id.startRange_editTextNumber);
        endRange = findViewById(R.id.endRange_editTextNumber);
        allowOverlapping = findViewById(R.id.allowOverlapping_checkBox);
        RangeLinearLayout = findViewById(R.id.RangeLinearLayout);
        result = findViewById(R.id.pickedNumber_TextView);
        resultView = findViewById(R.id.resultView_TextView);
        scrollView = findViewById(R.id.scrollView);

        ActivateEditField();

        result.setText("");
        resultView.setText("");
        allowOverlapping.setChecked(false);

        accumulatedNum = 0;
        hasStarted = false;
    }

    //EditText가 아닌 영역을 터치 시 키보드 숨기기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void pick(View v) {
        //처음 뽑기 누르는 경우, 범위와 개수 값이 유효한지 확인함
        if (hasStarted == false) {

            //시작 범위, 끝 범위를 입력 안 한 경우
            if ((startRange.getText().length() <= 0) || (endRange.getText().length() <= 0))  {
                Toast.makeText(this, "범위 값을 입력하세요", Toast.LENGTH_SHORT).show();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200); // 0.2초 진동
                return;
            }

            //올바른 범위 값을 입력하지 않은 경우
            if ((Integer.parseInt(startRange.getText().toString()) < 0)
                    || (Integer.parseInt(endRange.getText().toString()) > 10000)
                    || (Integer.parseInt(startRange.getText().toString()) > Integer.parseInt(endRange.getText().toString())))  {
                Toast.makeText(this, "0 이상 10000 이하의 범위 값을 입력하세요", Toast.LENGTH_SHORT).show();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200); // 0.2초 진동
                return;
            }

            //값이 유효한 경우 값 저장, 텍스트필드 비활성화 후 뽑기 진행 시작 처리(hasStarted 값을 true로)
            startRangeNum = Integer.parseInt(startRange.getText().toString());
            endRangeNum = Integer.parseInt(endRange.getText().toString());
            accumulatedNum = 0;
            allowOverlappingBool = allowOverlapping.isChecked();
            setNumTextSize(endRangeNum);

            DeactivateEditField();

            hasStarted = true;

            //중복 허용을 하지 않았다면,
            //Shuffle을 이용하여 수들을 미리 랜덤하게 섞어서 정렬함
            if (allowOverlappingBool == false) {
                RandomNumberList = new ArrayList<Integer>();
                for(int i = startRangeNum ; i <= endRangeNum ; i++) {
                    RandomNumberList.add(i);
                }
                Collections.shuffle(RandomNumberList);
            }

        }

        if (hasStarted = true) { //이미 뽑기 진행 중인 경우

            //중복 허용한 경우
            if (allowOverlappingBool == true) {
                Random rand = new Random();
                int randomNum = rand.nextInt(endRangeNum - startRangeNum + 1) + startRangeNum;
                result.setText(Integer.toString(randomNum));

                if (accumulatedNum == 0)
                    resultView.setText(resultView.getText().toString() +  Integer.toString(randomNum));
                else
                    resultView.setText(resultView.getText().toString() + ", " + Integer.toString(randomNum));
            }
            //중복 허용하지 않은 경우
            else if (allowOverlappingBool == false) {
                //이미 모든 수를 뽑은 경우
                if (accumulatedNum >= endRangeNum - startRangeNum + 1) {
                    Toast.makeText(this, "더 이상 뽑을 수 없습니다", Toast.LENGTH_SHORT).show();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200); // 0.2초 진동
                    return;
                }
                int randomNum = RandomNumberList.get(accumulatedNum);
                result.setText(Integer.toString(randomNum));

                if (accumulatedNum == 0)
                    resultView.setText(resultView.getText().toString() + Integer.toString(randomNum));
                else
                    resultView.setText(resultView.getText().toString() + ", " + Integer.toString(randomNum));
            }
            accumulatedNum++;

            //숫자 목록 스크롤뷰 아래쪽으로 자동 스크롤
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    //뽑힌 수의 자릿수에 따라 결과 텍스트 크기 변경
    public void setNumTextSize(int Num) {
        if (Num <= 99)
            result.setTextSize(200);
        else if (Num <= 999)
            result.setTextSize(175);
        else if (Num <= 9999)
            result.setTextSize(125);
        else if (Num == 10000)
            result.setTextSize(110);
    }

    public void reset(View v) {
        startRange.setText("");
        endRange.setText("");
        result.setText("");
        resultView.setText("");
        allowOverlapping.setChecked(false);

        accumulatedNum = 0;
        hasStarted = false;

        ActivateEditField();
    }

    public void copy(View v) {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("결과", resultView.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "결과가 복사되었습니다.",Toast.LENGTH_SHORT).show();
    }

    private void ActivateEditField() {
        RangeLinearLayout.setBackgroundColor(Color.WHITE);

        startRange.setClickable(true);
        startRange.setFocusable(true);
        startRange.setFocusableInTouchMode(true);

        endRange.setClickable(true);
        endRange.setFocusable(true);
        endRange.setFocusableInTouchMode(true);

        allowOverlapping.setClickable(true);
        allowOverlapping.setFocusable(true);
        allowOverlapping.setFocusableInTouchMode(true);
    }

    private void DeactivateEditField() {
        RangeLinearLayout.setBackgroundColor(Color.LTGRAY);

        startRange.setClickable(false);
        startRange.setFocusable(false);
        startRange.setFocusableInTouchMode(false);

        endRange.setClickable(false);
        endRange.setFocusable(false);
        endRange.setFocusableInTouchMode(false);

        allowOverlapping.setClickable(false);
        allowOverlapping.setFocusable(false);
        allowOverlapping.setFocusableInTouchMode(false);
    }

}
