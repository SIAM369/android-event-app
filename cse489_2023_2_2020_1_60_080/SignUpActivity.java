package edu.ewubd.cse489_2023_2_2020_1_60_080;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

public class SignUpActivity extends Activity {

    private TextView btnToggle, tvToggleLabel, tvTitle;
    private EditText etName, etEmail, etPhone, etUserID, etPass, etRePass;

    private TableRow rowName, rowEmail, rowPhone, rowRePass;

    private CheckBox rememberUser, rememberPass;

    private boolean isLoginPage = false;

    private Button btnGo, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnToggle = findViewById(R.id.btnToggle);
        tvToggleLabel = findViewById(R.id.tvToggleLabel);
        tvTitle = findViewById(R.id.tvTitle);
        rowName = findViewById(R.id.rowName);
        rowEmail = findViewById(R.id.rowEmail);
        rowPhone = findViewById(R.id.rowPhone);
        rowRePass = findViewById(R.id.rowRePass);
        btnGo = findViewById(R.id.btnGo);
        btnExit = findViewById(R.id.btnExit);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserID = findViewById(R.id.etUserID);
        etPass = findViewById(R.id.etPass);
        etRePass = findViewById(R.id.etRePass);

        rememberPass = findViewById(R.id.rememberPass);
        rememberUser = findViewById(R.id.rememberUser);

        this.changeView();


        if (rememberUser.isChecked()) {
            SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
            String rememberedUserID = sharedPreferences.getString("rememberedUserID", "");
            etUserID.setText(rememberedUserID);
        }


        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoginPage = !isLoginPage;
                changeView();
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoginPage) {
                    // Handle login
                    String userID = etUserID.getText().toString();
                    String pass = etPass.getText().toString();

                    boolean loginSuccessful = validateLogin(userID, pass);

                    if (loginSuccessful) {
                        if (rememberUser.isChecked()) {
                            saveRememberedUserID(userID);
                        }

                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showErrorDialog("Invalid user ID or password. Please try again.");
                    }
                } else {
                    // Handle signup
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String phone = etPhone.getText().toString();
                    String userID = etUserID.getText().toString();
                    String pass = etPass.getText().toString();
                    String rePass = etRePass.getText().toString();

                    String errMsg = validateSignup(name, email, phone, userID, pass, rePass);

                    if (errMsg.isEmpty()) {
                        saveUserData(name, email, phone, userID, pass);
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showErrorDialog(errMsg);
                    }
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void changeView() {
        if (isLoginPage) {
            rowName.setVisibility(View.GONE);
            rowEmail.setVisibility(View.GONE);
            rowPhone.setVisibility(View.GONE);
            rowRePass.setVisibility(View.GONE);
            tvTitle.setText("Login");
            tvToggleLabel.setText("Don't have an account?");
            btnToggle.setText("Signup");
        } else {
            rowName.setVisibility(View.VISIBLE);
            rowEmail.setVisibility(View.VISIBLE);
            rowPhone.setVisibility(View.VISIBLE);
            rowRePass.setVisibility(View.VISIBLE);
            tvTitle.setText("Signup");
            tvToggleLabel.setText("Already have an account?");
            btnToggle.setText("Login");
        }
    }

    private boolean validateLogin(String userID, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        String savedUserID = sharedPreferences.getString("userID", "");
        String savedPassword = sharedPreferences.getString("password", "");

        return userID.equals(savedUserID) && password.equals(savedPassword);
    }

    private String validateSignup(String name, String email, String phone, String userID, String pass, String rePass) {
        String errMsg = "";

        String regex = "^[A-Za-z\\s'-]+$";
        if (name.length() < 6 || name.length() > 30 || !name.matches(regex)) {
            errMsg += "Invalid Name\n";
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            errMsg += "Invalid Email\n";
        }

        if (phone.length() < 8 && phone.length() >= 16) {
            errMsg += "Invalid Phone Number (8 > Phone Number > 17)\n";
        } else if (phone.startsWith("+") && phone.length() != 14) {
            errMsg += "Invalid Phone Number (Add '+88' beginning of the Phone Number\n";
        } else if (phone.startsWith("0") && phone.length() != 11) {
            errMsg += "Invalid Phone (Try again)\n";
        }

        if (userID.length() < 4 || userID.length() > 9 || !userID.matches(regex)) {
            errMsg += "Invalid UserID\n";
        }

        String passRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
        if (pass.isEmpty() || rePass.isEmpty()) {
            errMsg += "Enter Password\n";
        } else if (!pass.equals(rePass)) {
            errMsg += "Passwords do not match\n";
        } else if (pass.length() < 6) {
            errMsg += "Password must be at least 6 characters long\n";
        }

        return errMsg;
    }

    private void saveUserData(String name, String email, String phone, String userID, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("userID", userID);
        editor.putString("password", password);
        editor.apply();
    }

    private void saveRememberedUserID(String rememberedUserID) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("rememberedUserID", rememberedUserID);
        editor.apply();
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
