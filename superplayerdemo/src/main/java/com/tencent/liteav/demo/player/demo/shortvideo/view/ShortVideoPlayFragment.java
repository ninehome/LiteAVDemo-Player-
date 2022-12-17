package com.tencent.liteav.demo.player.demo.shortvideo.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.liteav.demo.player.R;
import com.tencent.liteav.demo.player.demo.shortvideo.base.AbsBaseFragment;
import com.tencent.liteav.demo.player.expand.model.entity.VideoModel;


import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShortVideoPlayFragment extends AbsBaseFragment implements View.OnClickListener {
    private static final String TAG = "ShortVideoDemo:ShortVideoPlayFragment";
    private static final String SHARE_PREFERENCE_NAME = "tx_short_video_player_guide_setting";
    private static final String KEY_GUIDE_ONE = "is_guide_one_finish";
    private static final String KEY_GUIDE_TWO = "is_guide_two_finish";
    private static final String KEY_GUIDE_THREE = "is_guide_three_finish";
    private static final String KEY_GUIDE_FOUR = "is_guide_four_finish";

    private ImageButton mBack;
    private SuperShortVideoView mSuperShortVideoView;

    @Override
    protected int getLayoutResId() {
        return R.layout.player_fragment_short_video_play;
    }


    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {

        mSuperShortVideoView = getActivity().findViewById(R.id.super_short_video_view_play_fragment);
        mBack = getActivity().findViewById(R.id.ib_back_play);
        mBack.setOnClickListener(this);
        initMask();
    }

    private void initMask() {

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back_play) {
            getActivity().finish();
        }
    }

    @Override
    protected void initData() {


    }



    @Override
    public void onStop() {
        super.onStop();
        mSuperShortVideoView.pause();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSuperShortVideoView.releasePlayer();
    }

    private void putBoolean(String key, boolean value) {
        getContext().getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    private boolean getBoolean(String key) {
        return getContext().getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    /**
     * listAdapter的点击事件
     *
     * @param position
     */
    public void onItemClick(final int position) {
        mSuperShortVideoView.onItemClick(position);
    }

    public void onListPageScrolled() {
        mSuperShortVideoView.onListPageScrolled();
    }

    public void onLoaded(List<VideoModel> shortVideoBeanList) {
        if (mSuperShortVideoView != null) {
            mSuperShortVideoView.setDataSource(shortVideoBeanList);
        }
    }
}
