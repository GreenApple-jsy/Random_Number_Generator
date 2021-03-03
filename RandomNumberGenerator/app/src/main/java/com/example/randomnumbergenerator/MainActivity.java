package com.example.randomnumbergenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public EditText startRange;
    public EditText endRange;
    public EditText number;
    public CheckBox allowOverlapping;
    public TextView result;
    public TextView resultView;

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

        startRange = findViewById(R.id.startRange_editTextNumber);
        endRange = findViewById(R.id.endRange_editTextNumber);
        allowOverlapping = findViewById(R.id.allowOverlapping_checkBox);
        result = findViewById(R.id.pickedNumber_TextView);
        resultView = findViewById(R.id.resultView_TextView);

        //초기화 구문
        startRange.setActivated(true);
        endRange.setActivated(true);
        number.setActivated(true);
        allowOverlapping.setActivated(true);

        result.setText("");
        resultView.setText("");
        allowOverlapping.setChecked(false);

        accumulatedNum = 0;
        hasStarted = false;
    }

    public void pick(View v) {
        //처음 뽑기 누르는 경우, 범위와 개수 값이 유효한지 확인함
        if (hasStarted == false) {

            //시작 범위, 끝 범위를 입력 안 한 경우
            if ((startRange.getText().length() <= 0) || (endRange.getText().length() <= 0))  {
                Toast.makeText(this, "범위 값을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            //올바른 범위 값을 입력하지 않은 경우
            if ((Integer.parseInt(startRange.getText().toString()) < 0)
                    || (Integer.parseInt(endRange.getText().toString()) > 1000)
                    || (Integer.parseInt(startRange.getText().toString()) >= Integer.parseInt(endRange.getText().toString())))  {
                Toast.makeText(this, "0 이상 1000 이하의 범위 값을 입력하세요", Toast.LENGTH_LONG).show();
                return;
            }

            //뽑을 개수를 입력 안 한 경우
            if (number.getText().length() <= 0)  {
                Toast.makeText(this, "뽑을 개수를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            //올바른 개수 값을 입력하지 않은 경우
            if ((Integer.parseInt(number.getText().toString()) < 0) || (Integer.parseInt(number.getText().toString()) > 1000))  {
                Toast.makeText(this, "1 이상 100 이하의 뽑을 개수를 입력하세요", Toast.LENGTH_LONG).show();
                return;
            }

            //개수 값이 범위 크기보다 큰 경우
            if (Integer.parseInt(number.getText().toString()) >
                    Integer.parseInt(endRange.getText().toString()) - Integer.parseInt(startRange.getText().toString()) + 1 )  {
                Toast.makeText(this, "뽑을 개수가 범위보다 큽니다", Toast.LENGTH_SHORT).show();
                return;
            }

            //값이 유효한 경우 값 저장, 텍스트필드 비활성화 후 뽑기 진행 시작 처리(hasStarted 값을 true로)
            startRangeNum = Integer.parseInt(startRange.getText().toString());
            endRangeNum = Integer.parseInt(endRange.getText().toString());
            accumulatedNum = 0;
            allowOverlappingBool = allowOverlapping.isChecked();

            startRange.setActivated(false);
            endRange.setActivated(false);
            number.setActivated(false);
            allowOverlapping.setActivated(false);

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
                result.setText(randomNum);
                if (accumulatedNum == 0)
                    resultView.setText(resultView.getText().toString() + randomNum);
                else
                    resultView.setText(resultView.getText().toString() + ", " + randomNum);
            }
            //중복 허용하지 않은 경우
            else if (allowOverlappingBool == false) {
                //이미 모든 수를 뽑은 경우
                if (accumulatedNum >= endRangeNum - startRangeNum + 1) {
                    Toast.makeText(this, "더 이상 뽑을 수 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                int randomNum = RandomNumberList.get(accumulatedNum);
                result.setText(randomNum);
                if (accumulatedNum == 0)
                    resultView.setText(resultView.getText().toString() + randomNum);
                else
                    resultView.setText(resultView.getText().toString() + ", " + randomNum);
            }
            accumulatedNum++;
        }

    }

    public void reset(View v) {
        //초기화 구문
        startRange.setActivated(true);
        endRange.setActivated(true);
        number.setActivated(true);
        allowOverlapping.setActivated(true);

        result.setText("");
        resultView.setText("");
        allowOverlapping.setChecked(false);

        accumulatedNum = 0;
        hasStarted = false;
    }

    public void copy(View v) {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("결과", resultView.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "결과가 복사되었습니다.",Toast.LENGTH_SHORT).show();
    }

}
