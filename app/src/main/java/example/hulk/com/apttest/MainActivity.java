package example.hulk.com.apttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import example.hulk.com.apt_annotation.bindview.BindView;
import example.hulk.com.apt_annotation.test.Test;
import example.hulk.com.apt_library.BindViewTools;

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
//        MainActivity_ViewBinding binding = new MainActivity_ViewBinding();
//        binding.bind(this);
        tv.setText("bind TextView success");
        btn.setText("bind Button success");
    }
}
