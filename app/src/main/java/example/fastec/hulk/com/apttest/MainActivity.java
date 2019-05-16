package example.fastec.hulk.com.apttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import example.fastec.hulk.com.apt_annotation.bindview.BindView;
import example.fastec.hulk.com.apt_annotation.test.Test;
import example.fastec.hulk.com.apt_library.BindViewTools;

@Test
public class MainActivity extends AppCompatActivity {

    /**
     * 编译时绑定id
     */
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.btn)
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTools.bind(this);
        tv.setText("bind TextView success");
        btn.setText("bind Button success");
    }
}
