package com.ph40510.asm_ph40510_and103.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ph40510.asm_ph40510_and103.Adapter.CarAdapter;
import com.ph40510.asm_ph40510_and103.Model.CarModel;
import com.ph40510.asm_ph40510_and103.Model.Page;
import com.ph40510.asm_ph40510_and103.Model.Response;
import com.ph40510.asm_ph40510_and103.R;
import com.ph40510.asm_ph40510_and103.Services.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements CarAdapter.CarClick{
    private HttpRequest httpRequest;
    private SharedPreferences sharedPreferences;
    private String token;
    private CarAdapter adapter;

    private ArrayList<CarModel> ds = new ArrayList<>();
    private int page = 1;
    private int totalPage = 0;
    private String sort="";

    RecyclerView rcv_car;
    FloatingActionButton btn_add;
    EditText edSearchName,edSearchMoney;
    ProgressBar loadmore;

    NestedScrollView nestScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Map<String,String> map = getMapFilter(page, "","0","-1");
        httpRequest.callAPI().getPageCar( map)
                .enqueue(getListCarResponse);

        sharedPreferences = getSharedPreferences("INFO",MODE_PRIVATE);
        token = sharedPreferences.getString("token","");
        httpRequest = new HttpRequest(token);

        rcv_car = findViewById(R.id.rcv_car);
        btn_add = findViewById(R.id.btn_add);
        loadmore = findViewById(R.id.loadmore);
        edSearchName = findViewById(R.id.ed_search_name);
        edSearchMoney = findViewById(R.id.ed_search_money);
        nestScrollView = findViewById(R.id.nestScrollView);

        nestScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    if (totalPage == page) return;
                    if (loadmore.getVisibility() == View.GONE) {
                        loadmore.setVisibility(View.VISIBLE);
                        page++;
                        FilterFruit();
                    }
                }
            }
        });


    }

    private void getData (ArrayList<CarModel> _ds) {
        Log.d("zzzzzzzz", "getData: " + _ds.size());


        if (loadmore.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(ds.size()-1);
                    loadmore.setVisibility(View.GONE);
                    ds.clear();
                    ds.addAll(_ds);
                    adapter.notifyDataSetChanged();
                }
            },1000);
            return;
        } else {
            ds.clear();

            ds.addAll(_ds);
            adapter = new CarAdapter(this, ds,this );
            rcv_car.setAdapter(adapter);
        }

    }

    Callback<Response<CarModel>> responseCarAPI = new Callback<Response<CarModel>>() {
        @Override
        public void onResponse(Call<Response<CarModel>> call, retrofit2.Response<Response<CarModel>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    page = 1;
                    ds.clear();
                    FilterFruit();

                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<CarModel>> call, Throwable t) {
            Log.e("zzzzzzzz", "onFailure: "+t.getMessage() );
        }
    };
    private void FilterFruit(){
        String _name = edSearchName.getText().toString().equals("")? "" : edSearchName.getText().toString();
        String _price = edSearchMoney.getText().toString().equals("")? "0" : edSearchMoney.getText().toString();
        String _sort = sort.equals("") ? "-1": sort;
        Map<String,String> map =getMapFilter(page, _name, _price, _sort);
        httpRequest.callAPI().getPageCar( map).enqueue(getListCarResponse);

    }
    private Map<String, String> getMapFilter(int _page,String _name, String _price, String _sort){
        Map<String,String> map = new HashMap<>();

        map.put("page", String.valueOf(_page));
        map.put("name", String.valueOf(_name));
        map.put("price", String.valueOf(_price));
        map.put("sort", String.valueOf(_sort));


        return map;
    }
    @Override
    public void delete(CarModel car) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteCar(car.get_id())
                    .enqueue(responseCarAPI);
        });
        builder.setNegativeButton("no", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();


    }

    @Override
    public void edit(CarModel car) {
        Intent intent =new Intent(HomeActivity.this, UpdateCarActivity.class);
        intent.putExtra("car", (CharSequence) car);
        startActivity(intent);
    }

    Callback<Response<Page<ArrayList<CarModel>>>> getListCarResponse = new Callback<Response<Page<ArrayList<CarModel>>>>() {
        @Override
        public void onResponse(Call<Response<Page<ArrayList<CarModel>>>> call, retrofit2.Response<Response<Page<ArrayList<CarModel>>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() ==200) {
                    totalPage = response.body().getData().getTotalPage();

                    ArrayList<CarModel> _ds = response.body().getData().getData();
                    getData(_ds);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Page<ArrayList<CarModel>>>> call, Throwable t) {

        }
    };

    @Override
    public void showDetail(CarModel car) {

    }
}