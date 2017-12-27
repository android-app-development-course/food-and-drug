package com.example.main_interface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import  android.widget.Toast;
public class resetpassword extends AppCompatActivity {

        private EditText mAccount;                        //用户名编辑
        private EditText mPwd_old;                        //密码编辑
        private EditText mPwd_new;                        //密码编辑
        private EditText mPwdCheck;                       //密码编辑
        Button mSureButton;                       //确定按钮
        Button mCancelButton;                     //取消按钮
        private userdatamanager mUserDataManager;         //用户数据管理类

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_resetpwd);
            mAccount = (EditText) findViewById(R.id.username);
            mPwd_old = (EditText) findViewById(R.id.et_oldpwd);
            mPwd_new = (EditText) findViewById(R.id.et_newpwd);
            mPwdCheck = (EditText) findViewById(R.id.et_newpsd_again);//重新确认密码
            mSureButton = (Button) findViewById(R.id.reset_ok);//确认修改
            mCancelButton = (Button) findViewById(R.id.reset_cancel);
            mSureButton.setOnClickListener(m_resetpwd_Listener);      //注册界面两个按钮的监听事件
            mCancelButton.setOnClickListener(m_resetpwd_Listener);
            if (mUserDataManager == null) {
                mUserDataManager = new userdatamanager(this);
                mUserDataManager.openDataBase();                              //建立本地数据库
            }
        }

        View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.reset_ok:                       //确认按钮的监听事件
                        resetpwd_check();
                        break;
                    case R.id.reset_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                        Intent intent_Resetpwd_to_Login = new Intent(resetpassword.this, LoginActivity.class);    //切换Resetpwd Activity至Login Activity
                        startActivity(intent_Resetpwd_to_Login);
                        finish();
                        break;
                }
            }
        };

        public void resetpwd_check() {
            if (isUserNameAndPwdValid()) {
                String userName = mAccount.getText().toString().trim();
                String userPwd_old = mPwd_old.getText().toString().trim();
                String userPwd_new = mPwd_new.getText().toString().trim();
                String userPwdCheck = mPwdCheck.getText().toString().trim();
                int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd_old);
                if (result == 1) {                                             //返回1说明用户名和密码均正确,继续后续操作
                    if (!userPwd_new.equals(userPwdCheck)) {           //两次密码输入不一样
                        Toast.makeText(this, getString(R.string.pwd_not_the_same), Toast.LENGTH_SHORT).show();
                        //return;
                    } else {
                        userdata mUser = new userdata(userName, userPwd_new);
                        mUserDataManager.openDataBase();
                        boolean flag = mUserDataManager.updateUserData(mUser);
                        if (!flag ) {
                            Toast.makeText(this, getString(R.string.resetpwd_fail), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, getString(R.string.resetpwd_success), Toast.LENGTH_SHORT).show();
                            mUser.pwdresetFlag = 1;
                            Intent intent_Register_to_Login = new Intent(resetpassword.this, LoginActivity.class);    //切换User Activity至Login Activity
                            startActivity(intent_Register_to_Login);
                            finish();
                        }
                    }
                } else if (result == 0) {                                       //返回0说明用户名和密码不匹配，重新输入
                    Toast.makeText(this, getString(R.string.pwd_not_fit_user), Toast.LENGTH_SHORT).show();
                   // return;
                }
            }
        }

        public boolean isUserNameAndPwdValid() {
            String userName = mAccount.getText().toString().trim();
            //检查用户是否存在
            int count = mUserDataManager.findUserByName(userName);
            //用户不存在时返回，给出提示文字
            if (count <= 0) {
                Toast.makeText(this, getString(R.string.name_not_exist), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (mAccount.getText().toString().trim().equals("")) {
                Toast.makeText(this, getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mPwd_old.getText().toString().trim().equals("")) {
                Toast.makeText(this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mPwd_new.getText().toString().trim().equals("")) {
                Toast.makeText(this, getString(R.string.pwd_check_empty), Toast.LENGTH_SHORT).show();
                return false;
            } else if (mPwdCheck.getText().toString().trim().equals("")) {
                Toast.makeText(this, getString(R.string.pwd_check_empty), Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

