package com.example.a11322.lifetrace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnGetSuggestionResultListener {
    private AutoCompleteTextView start;
    private AutoCompleteTextView terminal;
    private Button walk;
    private Button ride;
    private Button bus;
    private Button car;
    private Button search;
    private Button AvoidCongestion;
    private Button TimeFirst;
    private Button ShortestDistance;
    private Button FewerExpense;
    private Button NoSubway;
    private Button TimeFirst2;
    private Button TransferFirst;
    private Button WalkFirst;
    private PopupWindow popupWindow;
    private SuggestionSearch mSuggestionSearch = null;
    private ArrayAdapter<String> sugAdapter = null;
    private AutoCompleteTextView editcity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        popupWindow = new PopupWindow(SearchActivity.this);
        start = (AutoCompleteTextView) findViewById(R.id.start);
        terminal = (AutoCompleteTextView) findViewById(R.id.terminal);
        walk = (Button) findViewById(R.id.walk);
        ride = (Button) findViewById(R.id.ride);
        bus =(Button) findViewById(R.id.bus);
        car = (Button) findViewById(R.id.car);
        NoSubway = (Button) findViewById(R.id.EBUS_NO_SUBWAY);
        TimeFirst2 = (Button) findViewById(R.id.EBUS_TIME_FIRST);
        TransferFirst = (Button) findViewById(R.id.EBUS_TRANSFER_FIRST);
        WalkFirst = (Button) findViewById(R.id.EBUS_WALK_FIRST);
        editcity = (AutoCompleteTextView) findViewById(R.id.city);


        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        start.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city(editcity.getText().toString()));
            }
        });

        terminal.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city(editcity.getText().toString()));
            }
        });
        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("way","walk");
                intent.putExtra("city", editcity.getText().toString());
                intent.putExtra("start",start.getText().toString());
                intent.putExtra("terminal",terminal.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("way","bike");
                intent.putExtra("city", editcity.getText().toString());
                intent.putExtra("start",start.getText().toString());
                intent.putExtra("terminal",terminal.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopTransitRouteInfo("transit",editcity.getText().toString(),start.getText().toString(),terminal.getText().toString());
            }
        });
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopDriveRouteInfo("drive",editcity.getText().toString(),start.getText().toString(),terminal.getText().toString());
            }
        });
    }

        private void showPopTransitRouteInfo(String way,String city,String start,String terminal) {

            //设置属性
            View view = LayoutInflater.from(this).inflate(R.layout.poptransitroutestrategy, null);
            NoSubway = (Button) view.findViewById(R.id.EBUS_NO_SUBWAY);
            TimeFirst2 = (Button) view.findViewById(R.id.EBUS_TIME_FIRST);
            TransferFirst = (Button) view.findViewById(R.id.EBUS_TRANSFER_FIRST);
            WalkFirst = (Button) view.findViewById(R.id.EBUS_WALK_FIRST);
            popupWindow.setContentView(view);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            //点击外部消失
            popupWindow.setOutsideTouchable(true);
            //获得当前窗体属性
            View parent = LayoutInflater.from(SearchActivity.this).inflate(R.layout.activity_search, null);
            popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            getWindow().setAttributes(layoutParams);
            //dismiss时恢复原样
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);
                }
            });
            final Intent intent = new Intent();
            intent.putExtra("way","transit");
            intent.putExtra("city", city);
            intent.putExtra("start",start);
            intent.putExtra("terminal",terminal);
            NoSubway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("strategy","NoSubway");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            TimeFirst2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("strategy","TimeFirst2");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            TransferFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("strategy","TransferFirst");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            WalkFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("strategy","WalkFirst");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
    }

    private void showPopDriveRouteInfo(String way,String city,String start,String terminal) {

        //设置属性
        View view = LayoutInflater.from(this).inflate(R.layout.popdriveroutestrategy, null);
        AvoidCongestion = (Button) view.findViewById(R.id.AvoidCongestion);
        TimeFirst = (Button) view.findViewById(R.id.TimeFirst);
        ShortestDistance = (Button) view.findViewById(R.id.ShortestDistance);
        FewerExpense = (Button) view.findViewById(R.id.FewerExpense);
        popupWindow.setContentView(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //获得当前窗体属性
        View parent = LayoutInflater.from(SearchActivity.this).inflate(R.layout.activity_search, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setAttributes(layoutParams);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        final Intent intent = new Intent();
        intent.putExtra("way","drive");
        intent.putExtra("city", city);
        intent.putExtra("start",start);
        intent.putExtra("terminal",terminal);
        AvoidCongestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("strategy","AvoidCongestion");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        TimeFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("strategy","TimeFirst");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ShortestDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("strategy","ShortestDistance");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        FewerExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("strategy","FewerExpense");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        List<String> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }

        sugAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_dropdown_item_1line,
                suggest);
        terminal.setAdapter(sugAdapter);
        start.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        popupWindow.dismiss();
        super.onDestroy();
    }
}
