package com.example.main_interface;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity{
    public static boolean judge = true;
    Button log;//登录按钮
    Button forget;//忘记密码按钮
    Button sign;//注册按钮
    EditText et_number;//用户名编辑
    EditText et_password;//密码编辑
    DatabaseHelper myhelper;
    private userdatamanager userdatamanager;//用户数据管理类
    private CheckBox mRememberCheck;//记住密码勾选
    private SharedPreferences login_sp;
    private String usernamevalue,passwordvalue;
    private View activitylogin;//登录界面
    private View mainview;//主界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton btn_back = findViewById(R.id.goback);

        View.OnClickListener mListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_register:                            //登录界面的注册按钮
                        Intent intent_Login_to_Register = new Intent(LoginActivity.this, register.class);    //切换Login Activity注册界面
                        startActivity(intent_Login_to_Register);
                        finish();
                        break;
                    case R.id.btn_login:                              //登录界面的登录按钮
                        login();
                        break;
                    case R.id.tv_forget:                             //登录界面的忘记密码  跳转到修改密码界面
                        Intent intent_Login_to_reset = new Intent(LoginActivity.this, resetpassword.class);    //切换Login Activity至User Activity
                        startActivity(intent_Login_to_reset);
                        finish();
                        break;
                }

            }
        };

        mRememberCheck = (CheckBox) findViewById(R.id.remeber_checkbox);//是否记住密码勾选
        log=(Button)findViewById(R.id.btn_login);//登录按钮
        forget=(Button )findViewById(R.id.tv_forget);//忘记密码按钮
        sign=(Button)findViewById(R.id.tv_register);//注册用户按钮
        et_number = (EditText) findViewById(R.id.et_number);
        et_password = (EditText) findViewById(R.id.et_password);
     //   mainview=findViewById(R.id.MainActivity);
        login_sp=getSharedPreferences("login",0);
        String username=login_sp.getString("username","");
        String password=login_sp.getString("password","");
        boolean choseremember=login_sp.getBoolean("mRememberCheck",false);//要改  数据库里没有这个数据
        boolean autologin=login_sp.getBoolean("mRememberCheck",false);
        if(choseremember){
            et_number.setText(username);
            et_number.setText(password);
            mRememberCheck.setChecked(true);
        }
        sign.setOnClickListener(mListener);
        log.setOnClickListener(mListener);

        if(userdatamanager==null){
            userdatamanager=new userdatamanager(this);
            userdatamanager.openDataBase();
        }


        //点击返回
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (judge) {
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }

            }
        });
    }
    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userName =et_number .getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = et_password.getText().toString().trim();
            SharedPreferences.Editor editor =login_sp.edit();
            int result=userdatamanager.findUserByNameAndPwd(userName, userPwd);
            if(result==1){                                             //返回1说明用户名和密码均正确
                //保存用户名和密码
                editor.putString("USER_NAME", userName);
                editor.putString("PASSWORD", userPwd);

                //是否记住密码
                if(mRememberCheck.isChecked()){
                    editor.putBoolean("mRememberCheck", true);
                }else{
                    editor.putBoolean("mRememberCheck", false);
                }
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class) ;    //切换Login Activity至User Activity
                startActivity(intent);
                finish();
                Toast.makeText(this, getString(R.string.login_success),Toast.LENGTH_SHORT).show();//登录成功提示
            }else if(result==0){
                Toast.makeText(this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();  //登录失败提示
            }
        }
    }

    public boolean isUserNameAndPwdValid() {
        if (et_number.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.username_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (et_password.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onResume() {
        if (userdatamanager == null) {
            userdatamanager = new userdatamanager(this);
            userdatamanager.openDataBase();
        }
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        if (userdatamanager != null) {
            userdatamanager.closeDataBase();
            userdatamanager = null;
        }
        super.onPause();
    }

    }

    class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
            super(context, "login.db", cursorFactory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO 创建登录数据库（账号主键，密码整型，是否保存布尔型）
            db.execSQL("CREATE TABLE login (username INTEGER PRIMARY KEY AUTOINCREMENT, password integer, save boolean);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            // TODO 每次成功打开数据库后首先被执行
        }
    }


}

