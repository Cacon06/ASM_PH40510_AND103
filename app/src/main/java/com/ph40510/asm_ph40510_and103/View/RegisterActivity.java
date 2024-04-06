package com.ph40510.asm_ph40510_and103.View;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.ph40510.asm_ph40510_and103.Model.Response;
import com.ph40510.asm_ph40510_and103.Model.User;
import com.ph40510.asm_ph40510_and103.R;
import com.ph40510.asm_ph40510_and103.Services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private HttpRequest httpRequest;
    private File file;

    Button btnRegister;
    ImageView avatar;
    TextView tvLogin;
    EditText edUsername,edPassword,edEmail,edName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        httpRequest = new HttpRequest();

        avatar = findViewById(R.id.avatar);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        edUsername = findViewById(R.id.ed_username);
        edPassword = findViewById(R.id.ed_password);
        edEmail = findViewById(R.id.ed_email);
        edName = findViewById(R.id.ed_name);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"),edUsername.getText().toString().trim());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"),edPassword.getText().toString().trim());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"),edEmail.getText().toString().trim());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"),edName.getText().toString().trim());
                MultipartBody.Part multipartBody;
                if (file !=null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),file);
                    multipartBody = MultipartBody.Part.createFormData("avartar",file.getName(),requestFile);
                }else {
                    multipartBody = null;
                }
                Log.d("zzzzzz", "onClick: " + _username.toString());
                httpRequest.callAPI().register(_username,_password,_email,_name,multipartBody).enqueue(responseUser);
            }
        });
    }

    private File createFileFormUri (Uri path, String name) {
        File _file = new File(RegisterActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = RegisterActivity.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) >0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Log.d("123123", "createFileFormUri: " +_file);
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void chooseImage() {
        Log.d("123123", "chooseAvatar: " +123123);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImage.launch(intent);
    }
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        Uri imageUri = data.getData();

                        Log.d("RegisterActivity", imageUri.toString());

                        Log.d("123123", "onActivityResult: "+data);

                        file = createFileFormUri(imageUri, "avatar");

                        Glide.with(avatar)
                                .load(imageUri)
                                .centerCrop()
                                .circleCrop()
                                .into(avatar);

                    }
                }
            });

    Callback<Response<User>> responseUser = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if (response.isSuccessful()) {
                Log.d("123123", "onResponse: " + response.body().getStatus());
                if (response.body().getStatus() ==200) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại lỗi" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable t) {
            t.getMessage();
        }
    };
}
