package com.example.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText idSearch;
    ImageButton button;
    Adapter adapter;
    final String API_KEY = "6163211f41564011abf9c1611450603a";
    List<Articles> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        idSearch=findViewById(R.id.idSearch);
        button=findViewById(R.id.button);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String country=getCountry();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {

                                                        retrieveJson("",country,API_KEY);

                                                    }
                                                });

                retrieveJson("",country, API_KEY);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!idSearch.getText().toString().equals("")) {
                            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {

                                    retrieveJson(idSearch.getText().toString(),country,API_KEY);

                                }
                            });

                            retrieveJson(idSearch.getText().toString(),country, API_KEY);
                        }
                        else{
                            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {

                                    retrieveJson("",country,API_KEY);

                                }
                            });

                            retrieveJson("",country, API_KEY);
                        }

                    }
                });
    }

    public void retrieveJson(String query,String country, String apiKey){

        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        if (!idSearch.getText().toString().equals(""))
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        else {
            call=ApiClient.getInstance().getApi().getHeadlines(country, apiKey);
        }


        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this,articles);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String getCountry(){
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}