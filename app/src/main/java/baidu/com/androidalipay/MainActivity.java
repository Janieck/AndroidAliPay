package baidu.com.androidalipay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<String> {
    private static final String url = "http://192.168.1.101:8080/HeiMaPay/Pay?goodId=111&count=2&price=0.05";//支付宝本地服务器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void alipay(View view) {
        //1,提交参数到服务器:商品信息(价格,数量,商品id)用户信息(用户id),支付方式(1-微信,2,支付宝,)
        StringRequest request = new StringRequest(url, this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        //2,服务器返回内容,解析获取"支付串码"

    }


    @Override
    public void onResponse(String s) {
        AlipayInfo alipayInfo = JSON.parseObject(s, AlipayInfo.class);
        //3,调用支付宝支付SDK的支付方法,传入参数(支付串码)
        CallAlipay(alipayInfo);
    }

    private void CallAlipay(final AlipayInfo alipayInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //调起支付宝支付
                PayTask task = new PayTask(MainActivity.this);
                //参数,支付需要的参数,参数2,是否显示长得进度条:
                String result = task.pay(alipayInfo.getPayInfo(), true);
                //4,处理支付结果
                Message msg = mHandler.obtainMessage();
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PayResult payResult = new PayResult((String) msg.obj);
            /**
             * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
             * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
             * docType=1) 建议商户依赖异步通知
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息

            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                showToast("支付成功");
            } else {
                // 判断resultStatus 为非"9000"则代表可能支付失败
                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    showToast("支付结果确认中");
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    showToast("支付失败");
                }
            }
        }
    };

    private void showToast(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }
}
