package com.example.demoapp.userverification;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoapp.R;
import com.example.demoapp.database.AppDatabase;
import com.example.demoapp.nextpage;
import com.example.demoapp.user.model.User;

/**
 * UserVerificationActivity for to login and register a user
 */
public class UserVerificationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText password;
    private TextView navigationLink;
    private Button submit;

    /**
     * To switch between login and registration screens.
     */
    private boolean isLoginScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userverification);

        // Get the fields reference from the xml layout
        title = findViewById(R.id.title);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        navigationLink = findViewById(R.id.navigation_link);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(this);
        navigationLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() ==  R.id.submit) {
            if (isLoginScreen) {
                validateLogin();
            } else {
                validateRegistration();
            }
        } else if (v.getId() == R.id.navigation_link) {
            isLoginScreen = !isLoginScreen;
            updateLayout();
        }
    }

    /**
     * Updates the layout based on the screen Login/Registration
     */
    private void updateLayout() {
        if (isLoginScreen) {
            // Optional
            // Try using from string.xml
            // e.g: Try R.string.login instead for "Login"
            title.setText("Login");
            firstName.setVisibility(View.GONE);
            lastName.setVisibility(View.GONE);
            navigationLink.setText("Create new account");
            submit.setText("Login");
        } else {
            title.setText("Register");
            firstName.setVisibility(View.VISIBLE);
            lastName.setVisibility(View.VISIBLE);
            navigationLink.setText("Already have a account?");
            submit.setText("Register");
        }
    }


    private void validateLogin() {
        if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            Thread thread = new Thread(() -> {
                boolean isAvailable = AppDatabase
                        .getInstance(getApplicationContext())
                        .userDao()
                        .getUser(username.getText().toString(), password.getText().toString()) != null;

                if (!isAvailable) {
                    // When interacting with UI need to post inside the handler with main looper
                    // otherwise application will crash.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // passing "this" for context will take the current child thread
                            // need to specify class name as "UserVerificationActivity.this" to pass the context
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserVerificationActivity.this);
                            alertDialog.setMessage("Authentication failed: Incorrect username or password");
                            alertDialog.setPositiveButton("Ok", null);
                            alertDialog.show();
                        }
                    });
                } else {
                    Intent intent = new Intent(this, nextpage.class);
                    // Intent.FLAG_ACTIVITY_CLEAR_TOP for clearing the backstack
                    // i.e after navigating from the login screen back will open the login screen again
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            thread.start();
        } else {

            //To show a prompt with ok button
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Please fill required fields");
            alertDialog.setPositiveButton("Ok", null);
            alertDialog.show();
        }
    }

    private void validateRegistration() {
        if (!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()
            && !username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

            // Always use separate thread for database operation
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Check if username already exists
                    boolean isUserExist = AppDatabase
                            .getInstance(getApplicationContext())
                            .userDao()
                            .getUser(username.getText().toString()) != null;

                    if (isUserExist) {

                        // When interacting with UI need to post inside the handler with main looper
                        // otherwise application will crash.
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // passing "this" for context will take the current child thread
                                // need to specify class name as "UserVerificationActivity.this" to pass the context
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserVerificationActivity.this);
                                alertDialog.setMessage("Entered username already exits, please choose different username");
                                alertDialog.setPositiveButton("Ok", null);
                                alertDialog.show();
                            }
                        });
                    } else {
                        User user = new User();
                        user.firstName = firstName.getText().toString();
                        user.lastName = lastName.getText().toString();
                        user.username = username.getText().toString();
                        user.password = password.getText().toString();

                        AppDatabase.getInstance(UserVerificationActivity.this).userDao().insert(user);

                        // Clear field once registration completed.
                        clearFields();

                        // Interacting with UI so warp the content with "new Handler(Looper.getMainLooper()).post(new Runnable() {})
                        // Looper.getMainLooper will return Main looper, which used to run the below code in UI thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserVerificationActivity.this, "User registration successful", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
            thread.start();
        } else {

            //Empty field alert dialog
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Please fill required fields");
            alertDialog.setPositiveButton("Ok", null);
            alertDialog.show();
        }
    }

    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        username.setText("");
        password.setText("");
    }
}