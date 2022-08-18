package com.example.myapplication0817;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button btn_start;
    ImageView[] doArray = new ImageView[9];
    TextView tv_score;
    int score=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        tv_score = findViewById(R.id.tv_score);

        for(int i=0;i<doArray.length;i++){
            //getResource -> R폴더(res)에 있는 데이터를 가져옴
            //getIdentifier -> 어떤 형식으로 가져올지
            int imgId = getResources().getIdentifier("img"+(i+1),"id",getPackageName());
            // 문자열로 해당 Resource (혹은 레이아웃에 포함된 View) 의 ID 값을 가져온다
            // getResources().getIdentifier(파일명, 디렉토리명, 패키지명);

            doArray[i]=findViewById(imgId);
            doArray[i].setTag("off"); //꼬리표달기! -> setTag("내용")

            doArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 내가 클릭한 이미지가 올라온 이미지 라면
                    String Tag = view.getTag().toString();
                    if(Tag.equals("on")){
                        // SCORE 값을 1 증가
                        // 내려가있는 이미지로 바꿈
                        score++;
                        tv_score.setText("SCORE : "+String.valueOf(score));
                        // View 는 모든 뷰들에 대한 최상위 클래스임
                        // TextView, Button, EditText, ImageView
                        ImageView img = (ImageView) view; // 다운캐스팅
                        img.setImageResource(R.drawable.off);
                        img.setTag("off"); // 들어가있는 두더지를 눌러도 스코어가 올라가는거 방지
                    }
                }
            });

        }

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미지가 랜덤하게 올라오게끔 만들어야함
                for(int i=0;i<doArray.length;i++){
                    // i -> i 번째 이미지 뷰에 대해서 새로운 작업 공간을 만들어 줘야함
                    DoThread doThread = new DoThread(i);
                    doThread.start();
                }
            }
        });
    }

    Handler doHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 올라온 이미지 데이터를 가져와서
            // 이미지 뷰에 출력!
            int data = msg.arg1; // 넘겨받은 이미지
            int index = msg.arg2; // 넘겨받은 쓰레드의 위치정보

            String chStatus = (String) msg.obj; // 상태값

            doArray[index].setImageResource(data);

            doArray[index].setTag(chStatus);

        }
    };

    class DoThread extends Thread{ // 하나의 handler와 여러개의 쓰레드를 연동해주기 위해서 생성자(매개변수)를 만들어서 객체인스턴스해줌

        int pos; // 필요한 스레드의 개수 총 9개의 스레드임

        public DoThread(int i){
            this.pos = i;
        } // 생성자 -> 9개의 쓰레드라서 각 쓰레드를 구분해줄 필요가 있음 
        // 그래서 매개변수 i를 같이 전달해줌(위치정보)
        
        public void run() {
            // 랜덤한 시간을 적용시켜서 이미지가 바뀌게끔 !
            // while문 : 두더지 무한 반복
            while (true) {
                Random rd = new Random();
                String status = (String) doArray[pos].getTag();

                int rdTime = rd.nextInt(11); //0~10

                try {
                    Thread.sleep(rdTime * 1000); //alt+enter
                    // 메세지 객체에 올라온 이미지를 전달
                    // 메세지 객체에 이미지 데이터를 담기전에
                    // 현재 두더지 이미지들이 올라와있는 이미지 인지, 내려가 있는 이미지인지 판단!
                    // -----> 이미지 뷰에 태그를 달아주면 됨,---> setTag, getTag
                    Message message = new Message();

                    if (status.equals("off")) {
                        message.arg1 = R.drawable.on; // message.arg1 = R.drawable.on <- 둘다 int 형임
                        message.obj = "on"; // <- 바뀐 상태값 전달
                    } else {
                        message.arg1 = R.drawable.off;
                        message.obj = "off";
                    }

                    message.arg2 = pos; // 위치정보를 같이줌
                    doHandler.sendMessage(message);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}