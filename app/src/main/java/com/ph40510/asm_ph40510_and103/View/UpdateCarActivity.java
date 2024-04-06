package com.ph40510.asm_ph40510_and103.View;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ph40510.asm_ph40510_and103.Model.CarModel;
import com.ph40510.asm_ph40510_and103.Model.Response;
import com.ph40510.asm_ph40510_and103.R;
import com.ph40510.asm_ph40510_and103.Services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UpdateCarActivity extends AppCompatActivity {
    private CarModel car;
    private String id ;
    private HttpRequest httpRequest;
    private String id_Distributor;
    private ArrayList<File> ds_image;
    ImageView avatar,btn_back;
    Button btn_register;
    EditText ed_ten,ed_namSX,ed_Hang,ed_Gia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ds_image = new ArrayList<>();
        httpRequest = new HttpRequest();

        httpRequest = new HttpRequest();
        avatar = findViewById(R.id.avatar);
        btn_back = findViewById(R.id.btn_back);
        btn_register = findViewById(R.id.btn_register);
        ed_ten = findViewById(R.id.ed_ten);
        ed_namSX = findViewById(R.id.ed_namSX);
        ed_Hang = findViewById(R.id.ed_hang);
        ed_Gia = findViewById(R.id.ed_gia);
    }

    private void getDataIntent() {
        //get data object intent
        Intent intent = getIntent();
        car = (CarModel) intent.getSerializableExtra("car");
        Log.d("aaaaaa", "getDataIntent: "+car.getImage().get(0));
        id = car.get_id();
        String url = car.getImage().get(0);
        String newUrl = url.replace("localhost", "10.0.2.2");
        Glide.with(this)
                .load(newUrl)
                .thumbnail(Glide.with(this).load(R.drawable.baseline_broken_image_24))
                .into(avatar);
        ed_Gia.setText(car.getGia());
        ed_Hang.setText(car.getHang());
        ed_namSX.setText(car.getNamSX());
        ed_ten.setText(car.getTen());
    }



    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"),value);
    }
    private void userListener() {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, RequestBody> mapRequestBody = new HashMap<>();
                String _ten = ed_ten.getText().toString().trim();
                String _namSX = ed_namSX.getText().toString().trim();
                String _hang = ed_Hang.getText().toString().trim();
                String _gia = ed_Gia.getText().toString().trim();

                mapRequestBody.put("ten", getRequestBody(_ten));
                mapRequestBody.put("namSX", getRequestBody(_namSX));
                mapRequestBody.put("hang", getRequestBody(_hang));
                mapRequestBody.put("gia", getRequestBody(_gia));
                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
                if (ds_image.isEmpty()) {
                    Log.e("aaaaaa", "onClick: Khoon co anh moi" );
                } else {
                    Log.e("aaaaaa", "onClick:  co anh moi" );

                    ds_image.forEach(file1 -> {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
                        _ds_image.add(multipartBodyPart);
                    });
                }

                // Gửi yêu cầu cập nhật lên server
                httpRequest.callAPI().updateCarWithFileImage(mapRequestBody,
                        car.get_id(), _ds_image).enqueue(responseFruit);


            }
        });
    }

    Callback<Response<CarModel>> responseFruit = new Callback<Response<CarModel>>() {
        @Override
        public void onResponse(Call<Response<CarModel>> call, retrofit2.Response<Response<CarModel>> response) {
            if (response.isSuccessful()) {
                Log.d("123123", "onResponse: " + response.body().getStatus());
                if (response.body().getStatus()==200) {
                    Toast.makeText(UpdateCarActivity.this, "Sửa thành công thành công", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<CarModel>> call, Throwable t) {
            Toast.makeText(UpdateCarActivity.this, "Sửa sai rôi thằng ngu ", Toast.LENGTH_SHORT).show();
            onBackPressed();
            Log.e("zzzzzzzzzz", "onFailure: "+t.getMessage());
        }
    };


    private void chooseImage() {
        Log.d("123123", "chooseAvatar: " +123123);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        getImage.launch(intent);
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        ds_image.clear();
                        Intent data = o.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();

                                File file = createFileFormUri(imageUri, "image" + i);
                                ds_image.add(file);
                            }


                        } else if (data.getData() != null) {
                            // Trường hợp chỉ chọn một hình ảnh
                            Uri imageUri = data.getData();
                            // Thực hiện các xử lý với imageUri
                            File file = createFileFormUri(imageUri, "image" );
                            ds_image.add(file);

                        }
                        Glide.with(UpdateCarActivity.this)
                                .load(ds_image.get(0))
                                .thumbnail(Glide.with(UpdateCarActivity.this).load(R.drawable.baseline_broken_image_24))
                                .centerCrop()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(avatar);
                    }
                }
            });

    private File createFileFormUri (Uri path, String name) {
        File _file = new File(UpdateCarActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = UpdateCarActivity.this.getContentResolver().openInputStream(path);
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
}