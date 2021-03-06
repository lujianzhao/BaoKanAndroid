package tv.baokan.baokanandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import tv.baokan.baokanandroid.R;
import tv.baokan.baokanandroid.model.ColumnBean;
import tv.baokan.baokanandroid.ui.fragment.BaseFragment;
import tv.baokan.baokanandroid.ui.fragment.HotFragment;
import tv.baokan.baokanandroid.ui.fragment.NewsFragment;
import tv.baokan.baokanandroid.ui.fragment.NewsListFragment;
import tv.baokan.baokanandroid.ui.fragment.PhotoFragment;
import tv.baokan.baokanandroid.ui.fragment.ProfileFragment;
import tv.baokan.baokanandroid.utils.LogUtils;

import static tv.baokan.baokanandroid.ui.fragment.NewsFragment.REQUEST_CODE_COLUMN;

public class MainActivity extends BaseActivity {

    private RadioGroup mRgTabbar;              // 底部tabbar
    private List<BaseFragment> mBaseFragments; // fragment集合
    private Fragment mPreviousFragment;        // 上一个显示的fragment
    private int position;                      // 当前选中的tabbarItem位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareUI();
        prepareFragments();
        setItemListener();

    }

    // 从其他应用返回回来 会重新添加fragment，这是个bug 得修复
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    /**
     * 准备UI
     */
    private void prepareUI() {
        mRgTabbar = (RadioGroup) findViewById(R.id.rg_tabbar);
    }

    /**
     * 准备fragment
     */
    private void prepareFragments() {
        mBaseFragments = new ArrayList<>();
        mBaseFragments.add(new NewsFragment());
        mBaseFragments.add(new PhotoFragment());
        mBaseFragments.add(new HotFragment());
        mBaseFragments.add(new ProfileFragment());
    }

    /**
     * 监听tabbarItem切换事件
     */
    private void setItemListener() {
        mRgTabbar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_news_item:
                        position = 0;
                        break;
                    case R.id.rb_photo_item:
                        position = 1;
                        break;
                    case R.id.rb_hot_item:
                        position = 2;
                        break;
                    case R.id.rb_profile_item:
                        position = 3;
                        break;
                    default:
                        position = 0;
                        break;
                }

                // 切换fragment
                Fragment currentFragment = mBaseFragments.get(position);
                switchFragment(mPreviousFragment, currentFragment);
            }
        });

        // 默认选中第一个item
        mRgTabbar.check(R.id.rb_news_item);
    }

    /**
     * 切换fragment
     *
     * @param from            需要隐藏的fragment
     * @param currentFragment 当前需要显示的fragment
     */
    private void switchFragment(Fragment from, Fragment currentFragment) {
        // 不是重复点击才切换
        if (mPreviousFragment != currentFragment) {
            mPreviousFragment = currentFragment;

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (from != null) {
                transaction.hide(from);
            }
            if (currentFragment.isAdded()) {
                transaction.show(currentFragment);
            } else {
                transaction.add(R.id.fl_main_content, currentFragment);
            }
            transaction.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return true;
    }

    // 记录两次点击退出时的第一次有效点击时间
    private long time = 0;

    /**
     * 2秒内连续点击返回2次back才退出app
     */
    private void exit() {
        if (System.currentTimeMillis() - time > 2000) {
            time = System.currentTimeMillis();
            showToast("再次点击将退出");
        } else {
            removeAllActivity();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_COLUMN:
                // 传递给资讯模块，更新栏目数据
                NewsFragment newsFragment = (NewsFragment) mBaseFragments.get(0);
                newsFragment.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
