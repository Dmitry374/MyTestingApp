package com.example.dima.mytestingapp.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.dima.mytestingapp.R;

import java.util.Calendar;
import java.util.Date;

public class HelloUserSplashActivity extends AppCompatActivity {

    TextView tvUser;
    String login;
    String userName;
    String email;

    //    Для запоминания вхождения в аккаунт
    SharedPreferences sPref;
    Boolean loginPref;
    final String SAVED_TEXT = "saved_text";

    SharedPreferences sPrefLogin, sPrefUserName, sPrefEmail;

    String sLogin;  // SharedPreferences для login
    String sUserName;  // SharedPreferences для userName
    String sEmail;  // SharedPreferences для email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_user_splash);


        tvUser = (TextView) findViewById(R.id.tvUser1);

//        Приниммаем имя из LoginActivity или RegisterActivity(предыдущее Activity)
        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        userName = intent.getStringExtra("userName");
        email = intent.getStringExtra("email");

//        Toast.makeText(this, login + " " + userName + " " + email, Toast.LENGTH_SHORT).show();



//        Запоминаеи вхождение в аккаунт
//        sPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        ed.putBoolean(SAVED_TEXT, true);
//        ed.commit();
////       -------------------------------------------------------------
//        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
//        SharedPreferences.Editor edPrefLogin = sPrefLogin.edit();
//        edPrefLogin.putString("save_login", login);
//        edPrefLogin.commit();
//
//        sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
//        SharedPreferences.Editor edPrefUserName = sPrefUserName.edit();
//        edPrefUserName.putString("save_user_name", userName);
//        edPrefUserName.commit();
//
//        sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
//        SharedPreferences.Editor edPrefEmail = sPrefEmail.edit();
//        edPrefEmail.putString("save_email", email);
//        edPrefEmail.commit();
//
////         --------------------
//        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
//        sLogin = sPrefLogin.getString("save_login", "");
//
//        sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
//        sUserName = sPrefUserName.getString("save_user_name", "");
//
//        sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
//        sEmail = sPrefEmail.getString("save_email", "");

//        Toast.makeText(this, sLogin + " " + sUserName + " " + sEmail, Toast.LENGTH_SHORT).show();

//       -------------------------------------------------------------------

        //      Определени времени суток и вывод соответствующего сообщения

//        00:00 - 06:00 ночь
//
//        06:00 - 12:00 утро
//
//        12:00 - 18:00 день
//
//        18:00 - 0:00 вечер


//        Текущая дата
        Date curDateTime = new java.util.Date();

//        Задаётся время, дата ост. текущей
        Calendar cal;

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Date n = cal.getTime();  // Ночь началь

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,6);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Date m = cal.getTime();  // Утро начало

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Date d = cal.getTime();  // День началь

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,18);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Date a = cal.getTime();  // Вечер начало

        Log.d("myLogs", "n = " + n.toString());
        Log.d("myLogs", "m = " + m.toString());
        Log.d("myLogs", "d = " + d.toString());
        Log.d("myLogs", "a = " + a.toString());

        if ((curDateTime.after(n)) && curDateTime.before(m)){
            tvUser.setText("Доброй ночи " + userName);
            Log.d("myLogs", "date = " + curDateTime.toString() + " n = " + n.toString());
        } else if ((curDateTime.after(m)) && (curDateTime.before(d))){
            tvUser.setText("Доброе утро " + userName);
            Log.d("myLogs", "date = " + curDateTime.toString() + " m = " + m.toString());
        } else if ((curDateTime.after(d)) && (curDateTime.before(a))){
            tvUser.setText("Добрый день " + userName);
            Log.d("myLogs", "date = " + curDateTime.toString() + " d = " + d.toString());
        } else {
            tvUser.setText("Добрый вечер " + userName);
            Log.d("myLogs", "date = " + curDateTime.toString() + " a = " + a.toString());
        }


//        Создание анимации
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        tvUser.startAnimation(anim);

//        Создание заставки
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          Intent i = new Intent(HelloUserSplashActivity.this, UserActivity.class);
//                Повторно отправляем значение в UserActivity(след. Activity)
                                          i.putExtra("userName", userName);
                                          i.putExtra("login", login);
                                          i.putExtra("email", email);
//                                          i.putExtra("userName", sUserName);
//                                          i.putExtra("login", sLogin);
//                                          i.putExtra("email", sEmail);

//                Текущая дата и время
                                          Date date = new java.util.Date();
                                          Log.d("myLogs", "t1 = " + date.toString());

//                Задаётся время, дата ост. текущей
                                          Calendar cal = Calendar.getInstance();
                                          cal.set(Calendar.HOUR_OF_DAY,17);
                                          cal.set(Calendar.MINUTE,30);
                                          cal.set(Calendar.SECOND,0);
                                          cal.set(Calendar.MILLISECOND,0);

                                          Date d = cal.getTime();
                                          Log.d("myLogs", "t5 = " + d.toString());

                                          Log.d("myLogs", "t6 = " + date.compareTo(d));


                                          startActivity(i);
                                          finish();
                                      }
                                  },
                3*1000);
//                500);
    }
}
