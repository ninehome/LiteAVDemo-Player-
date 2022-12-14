package com.tencent.liteav.demo.superplayer.ui.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.liteav.demo.superplayer.R;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.model.entity.PlayImageSpriteInfo;
import com.tencent.liteav.demo.superplayer.model.entity.PlayKeyFrameDescInfo;
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality;
import com.tencent.liteav.demo.superplayer.model.net.LogReport;
import com.tencent.liteav.demo.superplayer.model.utils.VideoGestureDetector;
import com.tencent.liteav.demo.superplayer.model.utils.VideoQualityUtils;
import com.tencent.liteav.demo.superplayer.ui.view.PointSeekBar;
import com.tencent.liteav.demo.superplayer.ui.view.VideoProgressLayout;
import com.tencent.liteav.demo.superplayer.ui.view.VipWatchView;
import com.tencent.liteav.demo.superplayer.ui.view.VodMoreView;
import com.tencent.liteav.demo.superplayer.ui.view.VodQualityView;
import com.tencent.liteav.demo.superplayer.ui.view.VodSoundTrackView;
import com.tencent.liteav.demo.superplayer.ui.view.VodSubtitlesSettingView;
import com.tencent.liteav.demo.superplayer.ui.view.VodSubtitlesView;
import com.tencent.liteav.demo.superplayer.ui.view.VolumeBrightnessProgressLayout;
import com.tencent.liteav.demo.superplayer.ui.view.download.DownloadMenuListView;
import com.tencent.rtmp.TXImageSprite;
import com.tencent.rtmp.TXTrackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ????????????????????????
 * <p>
 * ???{@link WindowPlayer}???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 * ?????????????????????????????????????????????????????????????????????
 * <p>
 * 1?????????????????????{@link #onClick(View)}
 * <p>
 * 2?????????????????????{@link #onTouchEvent(MotionEvent)}
 * <p>
 * 3??????????????????????????????{@link #onProgressChanged(PointSeekBar, int, boolean)}
 * {@link #onStartTrackingTouch(PointSeekBar)}{@link #onStopTrackingTouch(PointSeekBar)}
 * <p>
 * 4????????????????????????????????????{@link #onSeekBarPointClick(View, int)}
 * <p>
 * 5?????????????????????{@link #onQualitySelect(VideoQuality)}
 * <p>
 * 6?????????????????????{@link #onSpeedChange(float)}
 * <p>
 * 7?????????????????????{@link #onMirrorChange(boolean)}
 * <p>
 * 8?????????????????????{@link #onHWAcceleration(boolean)}
 */
public class FullScreenPlayer extends AbsPlayer implements View.OnClickListener,
        VodMoreView.Callback, VodQualityView.Callback
        , PointSeekBar.OnSeekBarChangeListener, PointSeekBar.OnSeekBarPointClickListener
        , VipWatchView.VipWatchViewClickListener, VodSoundTrackView.OnClickSoundTrackItemListener,
        VodSubtitlesView.OnClickSubtitlesItemListener, VodSubtitlesView.OnClickSettingListener,
        VodSubtitlesSettingView.OnClickBackButtonListener {

    // UI??????
    private RelativeLayout                 mLayoutTop;                             // ?????????????????????
    private LinearLayout                   mLayoutBottom;                          // ???????????????????????????
    private ImageView                      mIvPause;                               // ??????????????????
    private TextView                       mTvTitle;                               // ??????????????????
    private LinearLayout                   mLlTitle;                               // ???????????? ??? ???????????????
    private TextView                       mTvBackToLive;                          // ??????????????????
    private ImageView                      mIvWatermark;                           // ??????
    private TextView                       mTvCurrent;                             // ??????????????????
    private TextView                       mTvDuration;                            // ???????????????
    private ImageView                      mIvPlayNext;                            // ?????????????????????
    private ImageView                      mIvSoundTrack;
    private ImageView                      mIvSubtitle;
    private PointSeekBar                   mSeekBarProgress;                       // ???????????????
    private LinearLayout                   mLayoutReplay;                          // ????????????????????????
    private ProgressBar                    mPbLiveLoading;                         // ?????????
    private VolumeBrightnessProgressLayout mGestureVolumeBrightnessProgressLayout; // ????????????????????????
    private VideoProgressLayout            mGestureVideoProgressLayout;            // ????????????????????????
    private TextView                       mTvQuality;                             // ??????????????????
    private ImageView                      mIvBack;                                // ?????????????????????????????????
    private ImageView                      mIvDanmu;                               // ????????????
    private ImageView                      mIvSnapshot;                            // ????????????
    private ImageView                      mIvLock;                                // ????????????
    private ImageView                      mIvDownload;                            // ????????????
    private ImageView                      mIvMore;                                // ????????????????????????
    private ImageView                      mImageStartAndResume;                   // ?????????????????????
    private ImageView                      mImageCover;                            // ?????????
    private VodQualityView                 mVodQualityView;                        // ??????????????????
    private VodMoreView                    mVodMoreView;                           // ??????????????????
    private TextView                       mTvVttText;                             // ???????????????????????????
    private DownloadMenuListView           mDownloadMenuView;                         // ??????????????????
    private HideLockViewRunnable           mHideLockViewRunnable;                  // ???????????????????????????
    private GestureDetector                mGestureDetector;                       // ?????????????????????
    private VideoGestureDetector           mVideoGestureDetector;                      // ??????????????????
    private boolean                        isShowing;                              // ??????????????????
    private boolean                        mIsChangingSeekBarProgress;             // ????????????????????????????????????SeekBar?????????????????????update?????????
    private SuperPlayerDef.PlayerType      mPlayType;                              // ????????????????????????
    private SuperPlayerDef.PlayerState     mCurrentPlayState = SuperPlayerDef.PlayerState.END;                 // ??????????????????
    private long                           mDuration;                              // ???????????????
    private long                           mLivePushDuration;                      // ?????????????????????
    private long                           mProgress;                              // ??????????????????
    private Bitmap                         mBackgroundBmp;                         // ?????????
    private Bitmap                         mWaterMarkBmp;                          // ?????????
    private float                          mWaterMarkBmpX;                         // ??????x??????
    private float                          mWaterMarkBmpY;                         // ??????y??????
    private boolean                        mBarrageOn;                             // ??????????????????
    private boolean                        mLockScreen;                            // ????????????
    private TXImageSprite                  mTXImageSprite;                         // ???????????????
    private List<PlayKeyFrameDescInfo>     mTXPlayKeyFrameDescInfoList;            // ???????????????
    private int                            mSelectedPos      = -1;                      // ???????????????????????????
    private VideoQuality                   mDefaultVideoQuality;                   // ????????????
    private List<VideoQuality>             mVideoQualityList;                      // ????????????
    private boolean                        mFirstShowQuality;                      // ?????????????????????????????????
    private boolean                        mIsOpenGesture    = true;                  // ??????????????????
    private boolean                        isDestroy         = false;              // Activity???????????????
    private VodSoundTrackView mVodSoundTrackView;
    private VodSubtitlesView  mVodSubtitlesView;
    private VodSubtitlesSettingView mVodSubtitlesSettingView;

    public FullScreenPlayer(Context context) {
        super(context);
        initialize(context);
    }

    public FullScreenPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public FullScreenPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * ????????????????????????????????????????????????/??????/?????????????????????
     */
    private void initialize(Context context) {
        initView(context);
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isShowingVipView()) {   //?????????????????????????????????????????????????????????
                    return true;
                }
                if (mLockScreen) return false;
                togglePlayState();
                show();
                if (mHideViewRunnable != null) {
                    removeCallbacks(mHideViewRunnable);
                    postDelayed(mHideViewRunnable, 7000);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggle();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY) {
                if (mLockScreen) return false;
                if (downEvent == null || moveEvent == null) {
                    return false;
                }
                if (mVideoGestureDetector != null && mGestureVolumeBrightnessProgressLayout != null) {
                    mVideoGestureDetector.check(mGestureVolumeBrightnessProgressLayout.getHeight(), downEvent, moveEvent, distanceX, distanceY);
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (mLockScreen) return true;
                if (mVideoGestureDetector != null) {
                    mVideoGestureDetector.reset(getWidth(), mSeekBarProgress.getProgress());
                }
                return true;
            }

        });
        mGestureDetector.setIsLongpressEnabled(false);

        mVideoGestureDetector = new VideoGestureDetector(getContext());
        mVideoGestureDetector.setVideoGestureListener(new VideoGestureDetector.VideoGestureListener() {
            @Override
            public void onBrightnessGesture(float newBrightness) {
                if (mGestureVolumeBrightnessProgressLayout != null) {
                    mGestureVolumeBrightnessProgressLayout.setProgress((int) (newBrightness * 100));
                    mVodMoreView.setBrightProgress((int) (newBrightness * 100));
                    mGestureVolumeBrightnessProgressLayout.setImageResource(R.drawable.superplayer_ic_light_max);
                    mGestureVolumeBrightnessProgressLayout.show();
                }
            }

            @Override
            public void onVolumeGesture(float volumeProgress) {
                if (mGestureVolumeBrightnessProgressLayout != null) {
                    mGestureVolumeBrightnessProgressLayout.setImageResource(R.drawable.superplayer_ic_volume_max);
                    mGestureVolumeBrightnessProgressLayout.setProgress((int) volumeProgress);
                    mGestureVolumeBrightnessProgressLayout.show();
                }
            }

            @Override
            public void onSeekGesture(int progress) {
                mIsChangingSeekBarProgress = true;
                if (mGestureVideoProgressLayout != null) {

                    if (progress > mSeekBarProgress.getMax()) {
                        progress = mSeekBarProgress.getMax();
                    }
                    if (progress < 0) {
                        progress = 0;
                    }
                    mGestureVideoProgressLayout.setProgress(progress);
                    mGestureVideoProgressLayout.show();

                    float percentage = ((float) progress) / mSeekBarProgress.getMax();
                    float currentTime = (mDuration * percentage);
                    if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
                        if (mLivePushDuration > MAX_SHIFT_TIME) {
                            currentTime = (int) (mLivePushDuration - MAX_SHIFT_TIME * (1 - percentage));
                        } else {
                            currentTime = mLivePushDuration * percentage;
                        }
                        mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime));
                    } else {
                        mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime) + " / " + formattedTime((long) mDuration));
                    }
                    setThumbnail(progress);
                }
                if (mSeekBarProgress != null)
                    mSeekBarProgress.setProgress(progress);
            }
        });
    }

    /**
     * ?????????view
     */
    private void initView(Context context) {
        mHideLockViewRunnable = new HideLockViewRunnable(this);
        LayoutInflater.from(context).inflate(R.layout.superplayer_vod_player_fullscreen, this);
        mLlTitle = (LinearLayout) findViewById(R.id.superplayer_ll_title);
        mLayoutTop = (RelativeLayout) findViewById(R.id.superplayer_rl_top);
        mLayoutTop.setOnClickListener(this);
        mLayoutBottom = (LinearLayout) findViewById(R.id.superplayer_ll_bottom);
        mLayoutBottom.setOnClickListener(this);
        mLayoutReplay = (LinearLayout) findViewById(R.id.superplayer_ll_replay);

        mIvBack = (ImageView) findViewById(R.id.superplayer_iv_back);
        mIvLock = (ImageView) findViewById(R.id.superplayer_iv_lock);
        mTvTitle = (TextView) findViewById(R.id.superplayer_tv_title_full_screen);
        mIvPause = (ImageView) findViewById(R.id.superplayer_iv_pause);
        mIvDanmu = (ImageView) findViewById(R.id.superplayer_iv_danmuku);
        mIvMore = (ImageView) findViewById(R.id.superplayer_iv_more);
        mIvDownload = (ImageView) findViewById(R.id.superplayer_iv_download);
        mIvSnapshot = (ImageView) findViewById(R.id.superplayer_iv_snapshot);
        mTvCurrent = (TextView) findViewById(R.id.superplayer_tv_current);
        mTvDuration = (TextView) findViewById(R.id.superplayer_tv_duration);
        mImageCover = (ImageView) findViewById(R.id.superplayer_cover_view);
        mImageStartAndResume = (ImageView) findViewById(R.id.superplayer_resume);
        mIvPlayNext = (ImageView) findViewById(R.id.superplayer_iv_play_next);
        mIvSoundTrack = (ImageView) findViewById(R.id.superplayer_iv_sound_track);
        mIvSubtitle = (ImageView) findViewById(R.id.superplayer_iv_subtitle);
        mVodSoundTrackView = (VodSoundTrackView) findViewById(R.id.superplayer_vod_selection_sound_track);
        mVodSoundTrackView.setOnClickSoundTrackItemListener(this);
        mVodSubtitlesView = (VodSubtitlesView) findViewById(R.id.superplayer_vod_selection_subtitle);
        mVodSubtitlesView.setOnClickSubtitlesItemListener(this);
        mVodSubtitlesView.setOnClickSettingListener(this);
        mVodSubtitlesSettingView = (VodSubtitlesSettingView)
                findViewById(R.id.superplayer_vod_selection_subtitle_setting);
        mVodSubtitlesSettingView.setOnClickBackButtonListener(this);
        mDownloadMenuView = findViewById(R.id.superplayer_cml_cache_menu);

        mSeekBarProgress = (PointSeekBar) findViewById(R.id.superplayer_seekbar_progress);
        mSeekBarProgress.setProgress(0);
        mSeekBarProgress.setOnPointClickListener(this);
        mSeekBarProgress.setOnSeekBarChangeListener(this);



        mTvQuality = (TextView) findViewById(R.id.superplayer_tv_quality);
        mTvBackToLive = (TextView) findViewById(R.id.superplayer_tv_back_to_live);
        mPbLiveLoading = (ProgressBar) findViewById(R.id.superplayer_pb_live);

        mVodQualityView = (VodQualityView) findViewById(R.id.superplayer_vod_quality);
        mVodQualityView.setCallback(this);
        mVodMoreView = (VodMoreView) findViewById(R.id.superplayer_vod_more);
        mVodMoreView.setCallback(this);

        mImageStartAndResume.setOnClickListener(this);
        mIvPlayNext.setOnClickListener(this);
        mTvBackToLive.setOnClickListener(this);
        mLayoutReplay.setOnClickListener(this);
        mIvLock.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mIvSoundTrack.setOnClickListener(this);
        mIvSubtitle.setOnClickListener(this);
        mIvPause.setOnClickListener(this);
        mIvDanmu.setOnClickListener(this);
        mIvDownload.setOnClickListener(this);
        mIvSnapshot.setOnClickListener(this);
        mIvMore.setOnClickListener(this);
        mTvQuality.setOnClickListener(this);
        mTvVttText = (TextView) findViewById(R.id.superplayer_large_tv_vtt_text);
        mTvVttText.setOnClickListener(this);
        if (mDefaultVideoQuality != null) {
            mTvQuality.setText(mDefaultVideoQuality.title);
        }
        mGestureVolumeBrightnessProgressLayout = (VolumeBrightnessProgressLayout) findViewById(R.id.superplayer_gesture_progress);
        mGestureVideoProgressLayout = (VideoProgressLayout) findViewById(R.id.superplayer_video_progress_layout);
        mIvWatermark = (ImageView) findViewById(R.id.superplayer_large_iv_water_mark);
        mVipWatchView = findViewById(R.id.superplayer_vip_watch_view);
        mVipWatchView.setVipWatchViewClickListener(this);
    }

    public void setPlayNextButtonVisibility(boolean isShowing) {
        toggleView(mIvPlayNext, isShowing);
    }

    /**
     * ??????????????????
     * <p>
     * ?????????????????????/??????????????????????????????
     */
    private void togglePlayState() {
        switch (mCurrentPlayState) {
            case INIT:
            case PAUSE:
            case END:
                if (mLockScreen) {
                    return;
                }
                if (mControllerCallback != null) {
                    mControllerCallback.onResume();
                }
                break;
            case PLAYING:
            case LOADING:
                if (mControllerCallback != null) {
                    mControllerCallback.onPause();
                }
                mLayoutReplay.setVisibility(View.GONE);
                break;
        }
        show();
    }


    /**
     * ????????????????????????
     */
    private void toggle() {
        if (!mLockScreen) {
            if (isShowing) {
                hide();
            } else {
                show();
                if (mHideViewRunnable != null) {
                    removeCallbacks(mHideViewRunnable);
                    postDelayed(mHideViewRunnable, 7000);
                }
            }
        } else {
            mIvLock.setVisibility(VISIBLE);
            if (mHideLockViewRunnable != null) {
                removeCallbacks(mHideLockViewRunnable);
                postDelayed(mHideLockViewRunnable, 7000);
            }
        }
        if (mVodMoreView.getVisibility() == VISIBLE) {
            mVodMoreView.setVisibility(GONE);
        }
        mVodSoundTrackView.setVisibility(GONE);
        mVodSubtitlesView.setVisibility(GONE);
        mVodSubtitlesSettingView.setVisibility(GONE);
    }

    private void updateStartUI(boolean isAutoPlay) {
        if (isAutoPlay) {
            toggleView(mImageStartAndResume, false);
            toggleView(mPbLiveLoading, true);
        } else {
            toggleView(mImageStartAndResume, true);
            toggleView(mPbLiveLoading, false);
        }
        toggleView(mLayoutReplay, false);
    }

    public void preparePlayVideo(SuperPlayerModel superPlayerModel) {
        updateTitle(superPlayerModel.title);
        if (!isDestroy) {
            if (superPlayerModel.coverPictureUrl != null) {
                Glide.with(getContext()).load(superPlayerModel.coverPictureUrl)
                        .placeholder(R.drawable.superplayer_default).into(mImageCover);
            } else {
                Glide.with(getContext()).load(superPlayerModel.placeholderImage)
                        .placeholder(R.drawable.superplayer_default).into(mImageCover);
            }
        }
        mLivePushDuration = 0;
        toggleView(mImageCover, true);
        mIvPause.setImageResource(R.drawable.superplayer_ic_vod_play_normal);
        updateVideoProgress(0, superPlayerModel.duration);
        mSeekBarProgress.setEnabled(superPlayerModel.playAction != SuperPlayerModel.PLAY_ACTION_MANUAL_PLAY);
        updateStartUI(superPlayerModel.playAction == SuperPlayerModel.PLAY_ACTION_AUTO_PLAY);
    }

    /**
     * ????????????
     *
     * @param bmp ?????????
     * @param x   ?????????x??????
     * @param y   ?????????y??????
     */
    @Override
    public void setWatermark(Bitmap bmp, float x, float y) {
        mWaterMarkBmp = bmp;
        mWaterMarkBmpY = y;
        mWaterMarkBmpX = x;
    }

    /**
     * ????????????
     */
    @Override
    public void show() {
        isShowing = true;
        mLayoutTop.setVisibility(View.VISIBLE);
        mLayoutBottom.setVisibility(View.VISIBLE);
        mLlTitle.setVisibility(View.VISIBLE);
        if (mHideLockViewRunnable != null) {
            removeCallbacks(mHideLockViewRunnable);
        }
        mIvLock.setVisibility(VISIBLE);
        if (mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            if (mLayoutBottom.getVisibility() == VISIBLE)
                mTvBackToLive.setVisibility(View.VISIBLE);
        }
        List<PointSeekBar.PointParams> pointParams = new ArrayList<>();
        if (mTXPlayKeyFrameDescInfoList != null)
            for (PlayKeyFrameDescInfo info : mTXPlayKeyFrameDescInfoList) {
                int progress = (int) (info.time / mDuration * mSeekBarProgress.getMax());
                pointParams.add(new PointSeekBar.PointParams(progress, Color.WHITE));
            }
        mSeekBarProgress.setPointList(pointParams);
    }

    /**
     * ????????????
     */
    @Override
    public void hide() {
        isShowing = false;
        mLlTitle.setVisibility(View.GONE);
        mLayoutTop.setVisibility(View.GONE);
        mLayoutBottom.setVisibility(View.GONE);
        mVodQualityView.setVisibility(View.GONE);
        mTvVttText.setVisibility(GONE);
        mIvLock.setVisibility(GONE);
        if (mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????
     */
    @Override
    public void release() {
        isDestroy = true;
        releaseTXImageSprite();
    }


    public void toggleCoverView(boolean isVisible) {
        toggleView(mImageCover, isVisible);
    }

    public void prepareLoading() {
        toggleView(mPbLiveLoading, true);
        toggleView(mImageStartAndResume, false);
    }

    @Override
    public void updatePlayState(SuperPlayerDef.PlayerState playState) {
        switch (playState) {
            case INIT:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_play_normal);
                break;
            case PLAYING:
                mSeekBarProgress.setEnabled(true);
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_pause_normal);
                toggleView(mImageStartAndResume, false);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, false);
                break;
            case LOADING:
                mSeekBarProgress.setEnabled(true);
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_pause_normal);
                toggleView(mPbLiveLoading, true);
                toggleView(mLayoutReplay, false);
                break;
            case PAUSE:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_play_normal);
                toggleView(mLayoutReplay, false);
                toggleView(mImageStartAndResume, true);
                break;
            case END:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_play_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, true);
                break;
        }
        mCurrentPlayState = playState;
    }

    /**
     * ????????????????????????
     *
     * @param list ????????????
     */
    @Override
    public void setVideoQualityList(List<VideoQuality> list) {
        mVideoQualityList = list;
        mFirstShowQuality = false;
    }

    /**
     * ??????????????????
     *
     * @param title ????????????
     */
    @Override
    public void updateTitle(String title) {
        if (title != null) {
            mTvTitle.setText(title);
        }
    }

    /**
     * ????????????????????????
     *
     * @param current  ????????????(???)
     * @param duration ???????????????(???)
     */
    @Override
    public void updateVideoProgress(long current, long duration) {
        mProgress = current < 0 ? 0 : current;
        mDuration = duration < 0 ? 0 : duration;
        mTvCurrent.setText(formattedTime(mProgress));

        float percentage = mDuration > 0 ? ((float) mProgress / (float) mDuration) : 1.0f;
        if (mProgress == 0) {
            percentage = 0;
        }
        if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            mLivePushDuration = mLivePushDuration > mProgress ? mLivePushDuration : mProgress;
            long leftTime = mDuration - mProgress;
            mDuration = mDuration > MAX_SHIFT_TIME ? MAX_SHIFT_TIME : mDuration;
            percentage = 1 - (float) leftTime / (float) mDuration;
        } else {
            mVipWatchView.setCurrentTime(current);
        }

        if (percentage >= 0 && percentage <= 1) {
            int progress = Math.round(percentage * mSeekBarProgress.getMax());
            if (!mIsChangingSeekBarProgress)
                mSeekBarProgress.setProgress(progress);
            mTvDuration.setText(formattedTime(mDuration));
        }
    }

    @Override
    public void updatePlayType(SuperPlayerDef.PlayerType type) {
        mPlayType = type;
        switch (type) {
            case VOD:
                mTvBackToLive.setVisibility(View.GONE);
                mVodMoreView.updatePlayType(SuperPlayerDef.PlayerType.VOD);
                mTvDuration.setVisibility(View.VISIBLE);
                break;
            case LIVE:
                mTvBackToLive.setVisibility(View.GONE);
                mTvDuration.setVisibility(View.GONE);
                mVodMoreView.updatePlayType(SuperPlayerDef.PlayerType.LIVE);
                mSeekBarProgress.setProgress(100);
                break;
            case LIVE_SHIFT:
                if (mLayoutBottom.getVisibility() == VISIBLE) {
                    mTvBackToLive.setVisibility(View.VISIBLE);
                }
                mTvDuration.setVisibility(View.GONE);
                mVodMoreView.updatePlayType(SuperPlayerDef.PlayerType.LIVE_SHIFT);
                break;
        }
    }

    /**
     * ????????????????????????
     *
     * @param videoQuality ??????
     */
    @Override
    public void updateVideoQuality(VideoQuality videoQuality) {
        if (videoQuality == null) {
            mTvQuality.setText("");
            return;
        }
        mDefaultVideoQuality = videoQuality;
        if (mTvQuality != null && videoQuality.title != null) {
            mTvQuality.setText(VideoQualityUtils.transformToQualityName(videoQuality.title));
        }
        if (mVideoQualityList != null && mVideoQualityList.size() != 0) {
            for (int i = 0; i < mVideoQualityList.size(); i++) {
                VideoQuality quality = mVideoQualityList.get(i);
                if (quality != null && quality.title != null && quality.title.equals(mDefaultVideoQuality.title)) {
                    mVodQualityView.setDefaultSelectedQuality(i);
                    break;
                }
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param info ???????????????
     */
    @Override
    public void updateImageSpriteInfo(PlayImageSpriteInfo info) {
        if (mTXImageSprite != null) {
            releaseTXImageSprite();
        }
        // ????????????????????????????????????
        mGestureVideoProgressLayout.setProgressVisibility(info == null || info.imageUrls == null || info.imageUrls.size() == 0);
        if (mPlayType == SuperPlayerDef.PlayerType.VOD) {
            mTXImageSprite = new TXImageSprite(getContext());
            if (info != null) {
                // ?????????ELK??????
                LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_IMAGE_SPRITE, 0, 0);
                mTXImageSprite.setVTTUrlAndImageUrls(info.webVttUrl, info.imageUrls);
            } else {
                mTXImageSprite.setVTTUrlAndImageUrls(null, null);
            }
        }
    }

    private void releaseTXImageSprite() {
        if (mTXImageSprite != null) {
            mTXImageSprite.release();
            mTXImageSprite = null;
        }
    }

    /**
     * ?????????????????????
     *
     * @param list ?????????????????????
     */
    @Override
    public void updateKeyFrameDescInfo(List<PlayKeyFrameDescInfo> list) {
        mTXPlayKeyFrameDescInfoList = list;
    }

    @Override
    public void setVideoQualityVisible(boolean isShow) {
        mTvQuality.setVisibility(isShow ? VISIBLE : GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsOpenGesture && mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mLockScreen) {
            if (event.getAction() == MotionEvent.ACTION_UP && mVideoGestureDetector != null && mVideoGestureDetector.isVideoProgressModel()) {
                int progress = mVideoGestureDetector.getVideoProgress();
                if (progress > mSeekBarProgress.getMax()) {
                    progress = mSeekBarProgress.getMax();
                }
                if (progress < 0) {
                    progress = 0;
                }
                mSeekBarProgress.setProgress(progress);

                int seekTime = 0;
                float percentage = progress * 1.0f / mSeekBarProgress.getMax();
                if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
                    if (mLivePushDuration > MAX_SHIFT_TIME) {
                        seekTime = (int) (mLivePushDuration - MAX_SHIFT_TIME * (1 - percentage));
                    } else {
                        seekTime = (int) (mLivePushDuration * percentage);
                    }
                } else {
                    seekTime = (int) (percentage * mDuration);
                }
                if (mControllerCallback != null) {
                    mControllerCallback.onSeekTo(seekTime);
                }
                mIsChangingSeekBarProgress = false;
                if (mPlayType == SuperPlayerDef.PlayerType.VOD) {
                    mVipWatchView.setCurrentTime(seekTime);
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(mHideViewRunnable);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            postDelayed(mHideViewRunnable, 7000);
        }
        return true;
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.superplayer_iv_back || i == R.id.superplayer_tv_title_full_screen) { //???????????????
            if (mControllerCallback != null) {
                mControllerCallback.onBackPressed(SuperPlayerDef.PlayerMode.FULLSCREEN);
            }
        } else if (i == R.id.superplayer_iv_pause || i == R.id.superplayer_resume) {            //??????\????????????
            togglePlayState();
        } else if (i == R.id.superplayer_iv_danmuku) {          //????????????
            toggleBarrage();
        } else if (i == R.id.superplayer_iv_snapshot) {         //????????????
            if (mControllerCallback != null) {
                mControllerCallback.onSnapshot();
            }
        } else if (i == R.id.superplayer_iv_more) {             //??????????????????
            showMoreView();
        } else if (i == R.id.superplayer_tv_quality) {          //????????????
            showQualityView();
        } else if (i == R.id.superplayer_iv_lock) {             //????????????
            toggleLockState();
        } else if (i == R.id.superplayer_ll_replay) {           //????????????
            replay();
        } else if (i == R.id.superplayer_tv_back_to_live) {     //??????????????????
            if (mControllerCallback != null) {
                mControllerCallback.onResumeLive();
            }
        } else if (i == R.id.superplayer_large_tv_vtt_text) {   //???????????????????????????
            seekToKeyFramePos();
        } else if (i == R.id.superplayer_iv_play_next) {
            if (mControllerCallback != null) {
                mControllerCallback.playNext();
            }
        } else if (i == R.id.superplayer_iv_download) {  // ????????????
            showCacheList();
        } else if (i == R.id.superplayer_iv_sound_track) {
            showSoundTrackView();
        } else if (i == R.id.superplayer_iv_subtitle) {
            showSubTitleView();
        }
    }

    private void showSoundTrackView() {
        hide();
        mVodSoundTrackView.setVisibility(VISIBLE);
    }

    private void showSubTitleView() {
        hide();
        mVodSubtitlesView.setVisibility(VISIBLE);
    }

    private void showCacheList() {
        List<SuperPlayerModel> superPlayerModelList = new ArrayList<>();
        if (mControllerCallback != null) {
            superPlayerModelList = mControllerCallback.getPlayList();
        }
        mDownloadMenuView.initDownloadData(superPlayerModelList, mVideoQualityList, mDefaultVideoQuality, "default");
        mDownloadMenuView.setCurrentPlayVideo(mControllerCallback.getPlayingVideoModel());
        mDownloadMenuView.setOnCacheListClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mControllerCallback) {
                    mControllerCallback.onShowDownloadList();
                }
            }
        });
        mDownloadMenuView.show();
    }

    /**
     * ????????????
     */
    private void toggleBarrage() {
        mBarrageOn = !mBarrageOn;
        if (mBarrageOn) {
            mIvDanmu.setImageResource(R.drawable.superplayer_ic_danmuku_on);
        } else {
            mIvDanmu.setImageResource(R.drawable.superplayer_ic_danmuku_off);
        }
        if (mControllerCallback != null) {
            mControllerCallback.onDanmuToggle(mBarrageOn);
        }
    }

    /**
     * ????????????????????????
     * ??????????????????
     * ????????????
     * ??????????????????UI
     */
    public void revertUI() {
        //????????????
        if (mBarrageOn) {
            mBarrageOn = false;
            mIvDanmu.setImageResource(R.drawable.superplayer_ic_danmuku_off);
        }
        if (mVodMoreView != null) {
            mVodMoreView.revertUI();
        }
    }


    /**
     * ????????????????????????
     */
    private void showMoreView() {
        hide();
        mVodMoreView.setVisibility(View.VISIBLE);
    }

    /**
     * ????????????????????????
     */
    private void showQualityView() {
        if (mVideoQualityList == null || mVideoQualityList.size() == 0) {
            return;
        }
        if (mVideoQualityList.size() == 1 && (mVideoQualityList.get(0) == null || TextUtils.isEmpty(mVideoQualityList.get(0).title))) {
            return;
        }
        // ?????????????????????????????????
        mVodQualityView.setVisibility(View.VISIBLE);
        if (!mFirstShowQuality && mDefaultVideoQuality != null) {
            for (int i = 0; i < mVideoQualityList.size(); i++) {
                VideoQuality quality = mVideoQualityList.get(i);
                if (quality != null && quality.title != null && quality.title.equals(mDefaultVideoQuality.title)) {
                    mVodQualityView.setDefaultSelectedQuality(i);
                    break;
                }
            }
            mFirstShowQuality = true;
        }
        mVodQualityView.setVideoQualityList(mVideoQualityList);
    }

    /**
     * ??????????????????
     */
    private void toggleLockState() {
        mLockScreen = !mLockScreen;
        mIvLock.setVisibility(VISIBLE);
        if (mHideLockViewRunnable != null) {
            removeCallbacks(mHideLockViewRunnable);
            postDelayed(mHideLockViewRunnable, 7000);
        }
        if (mLockScreen) {
            mIvLock.setImageResource(R.drawable.superplayer_ic_player_lock);
            hide();
            mIvLock.setVisibility(VISIBLE);
        } else {
            mIvLock.setImageResource(R.drawable.superplayer_ic_player_unlock);
            show();
        }
    }

    /**
     * ??????
     */
    private void replay() {
        toggleView(mLayoutReplay, false);
        if (mControllerCallback != null) {
            mControllerCallback.onResume();
        }
    }

    /**
     * ???????????????????????????
     */
    private void seekToKeyFramePos() {
        float time = mTXPlayKeyFrameDescInfoList != null ? mTXPlayKeyFrameDescInfoList.get(mSelectedPos).time : 0;
        if (mControllerCallback != null) {
            mControllerCallback.onSeekTo((int) time);
            mControllerCallback.onResume();
        }
        mTvVttText.setVisibility(GONE);
        toggleView(mLayoutReplay, false);
    }

    @Override
    public void onProgressChanged(PointSeekBar seekBar, int progress, boolean isFromUser) {
        if (mGestureVideoProgressLayout != null && isFromUser) {
            mGestureVideoProgressLayout.show();
            float percentage = ((float) progress) / seekBar.getMax();
            float currentTime = (mDuration * percentage);
            if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
                if (mLivePushDuration > MAX_SHIFT_TIME) {
                    currentTime = (int) (mLivePushDuration - MAX_SHIFT_TIME * (1 - percentage));
                } else {
                    currentTime = mLivePushDuration * percentage;
                }
                mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime));
            } else {
                mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime) + " / " + formattedTime((long) mDuration));
            }
            mGestureVideoProgressLayout.setProgress(progress);
        }
        // ?????????????????????
        if (isFromUser && mPlayType == SuperPlayerDef.PlayerType.VOD) {
            setThumbnail(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(PointSeekBar seekBar) {
        removeCallbacks(mHideViewRunnable);
    }

    @Override
    public void onStopTrackingTouch(PointSeekBar seekBar) {
        int curProgress = seekBar.getProgress();
        int maxProgress = seekBar.getMax();

        switch (mPlayType) {
            case VOD:
                if (curProgress >= 0 && curProgress <= maxProgress) {
                    // ??????????????????
                    toggleView(mLayoutReplay, false);
                    float percentage = ((float) curProgress) / maxProgress;
                    int position = (int) (mDuration * percentage);
                    boolean showResult = mVipWatchView.canShowVipWatchView(position);
                    if (mControllerCallback != null) {
                        mControllerCallback.onSeekTo(position);
                    }
                    if (showResult) {
                        mVipWatchView.setCurrentTime(position);
                    }
                }
                break;
            case LIVE:
            case LIVE_SHIFT:
                toggleView(mPbLiveLoading, true);
                int seekTime = (int) (mLivePushDuration * curProgress * 1.0f / maxProgress);
                if (mLivePushDuration > MAX_SHIFT_TIME) {
                    seekTime = (int) (mLivePushDuration - MAX_SHIFT_TIME * (maxProgress - curProgress) * 1.0f / maxProgress);
                }
                if (mControllerCallback != null) {
                    mControllerCallback.onSeekTo(seekTime);
                }
                break;
        }
        postDelayed(mHideViewRunnable, 7000);
    }

    @Override
    public void onSeekBarPointClick(final View view, final int pos) {
        if (mHideLockViewRunnable != null) {
            removeCallbacks(mHideViewRunnable);
            postDelayed(mHideViewRunnable, 7000);
        }
        if (mTXPlayKeyFrameDescInfoList != null) {
            //ELK????????????
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_PLAYER_POINT, 0, 0);
            mSelectedPos = pos;
            view.post(new Runnable() {
                @Override
                public void run() {
                    int[] location = new int[2];
                    view.getLocationInWindow(location);

                    int viewX = location[0];
                    PlayKeyFrameDescInfo info = mTXPlayKeyFrameDescInfoList.get(pos);
                    String content = info.content;

                    mTvVttText.setText(formattedTime((long) info.time) + " " + content);
                    mTvVttText.setVisibility(VISIBLE);
                    adjustVttTextViewPos(viewX);
                }
            });
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param progress ????????????
     */
    private void setThumbnail(int progress) {
        float percentage = ((float) progress) / mSeekBarProgress.getMax();
        float seekTime = (mDuration * percentage);
        if (mVipWatchView.canShowVipWatchView(seekTime)) {
            mGestureVideoProgressLayout.hideThumbnail();
        } else {
            if (mTXImageSprite != null) {
                Bitmap bitmap = mTXImageSprite.getThumbnail(seekTime);
                if (bitmap != null) {
                    mGestureVideoProgressLayout.setThumbnail(bitmap);
                }
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param viewX ???????????????view
     */
    private void adjustVttTextViewPos(final int viewX) {
        mTvVttText.post(new Runnable() {
            @Override
            public void run() {
                int width = mTvVttText.getWidth();

                int marginLeft = viewX - width / 2;

                LayoutParams params = (LayoutParams) mTvVttText.getLayoutParams();
                params.leftMargin = marginLeft;

                if (marginLeft < 0) {
                    params.leftMargin = 0;
                }

                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                if (marginLeft + width > screenWidth) {
                    params.leftMargin = screenWidth - width;
                }

                mTvVttText.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onSpeedChange(float speedLevel) {
        if (mControllerCallback != null) {
            mControllerCallback.onSpeedChange(speedLevel);
        }
    }

    @Override
    public void onMirrorChange(boolean isMirror) {
        if (mControllerCallback != null) {
            mControllerCallback.onMirrorToggle(isMirror);
        }
    }

    @Override
    public void onHWAcceleration(boolean isAccelerate) {
        if (mControllerCallback != null) {
            mControllerCallback.onHWAccelerationToggle(isAccelerate);
        }
    }

    @Override
    public void onQualitySelect(VideoQuality quality) {
        if (mControllerCallback != null) {
            mControllerCallback.onQualityChange(quality);
        }
        mVodQualityView.setVisibility(View.GONE);
    }


    public void disableGesture(boolean flag) {
        this.mIsOpenGesture = !flag;
    }

    @Override
    public void onClickVipTitleBack() {
        if (mControllerCallback != null) {
            mControllerCallback.onBackPressed(SuperPlayerDef.PlayerMode.FULLSCREEN);
            mControllerCallback.onClickVipTitleBack(SuperPlayerDef.PlayerMode.FULLSCREEN);
        }
    }

    @Override
    public void onClickVipRetry() {
        if (mControllerCallback != null) {
            mControllerCallback.onClickVipRetry();
        }
    }

    @Override
    public void onShowVipView() {
        if (mControllerCallback != null) {
            mControllerCallback.onPause();
        }
    }

    @Override
    public void onClickVipBtn() {
        if (mControllerCallback != null) {
            mControllerCallback.onClickHandleVip();
        }
    }

    @Override
    public void onCloseVipTip() {
        if (mControllerCallback != null) {
            mControllerCallback.onCloseVipTip();
        }
    }

    public void updateDownloadViewShow(boolean isShow) {
        if (isShow) {
            mIvDownload.setVisibility(VISIBLE);
        } else {
            mIvDownload.setVisibility(GONE);
        }
        mDownloadMenuView.dismiss();
    }

    public void setVodSelectionViewPositionAndData(List<TXTrackInfo> models) {
        mVodSoundTrackView.setModelList(models);
        mIvSoundTrack.setVisibility(models.size() == 0 ? GONE : VISIBLE);
    }

    public void setVodSubtitlesViewPositionAndData(List<TXTrackInfo> models) {
        mVodSubtitlesView.setModelList(models);
        mIvSubtitle.setVisibility(models.size() == 0 ? GONE : VISIBLE);
    }

    /**
     * ???????????????????????????????????????
     */
    public void checkIsNeedRefreshCacheMenu() {
        if (mDownloadMenuView.isShowing()) {
            mDownloadMenuView.notifyRefreshCacheState();
        }
    }

    @Override
    public void onClickSoundTrackItem(TXTrackInfo clickInfo) {
        mVodSoundTrackView.setVisibility(GONE);
        mControllerCallback.onClickSoundTrackItem(clickInfo);
        hide();
    }

    @Override
    public void onClickSubtitlesItem(TXTrackInfo clickInfo) {
        mVodSubtitlesView.setVisibility(GONE);
        mControllerCallback.onClickSubtitleItem(clickInfo);
        hide();
    }

    /**
     * ?????????????????????runnable
     */
    private static class HideLockViewRunnable implements Runnable {
        private WeakReference<FullScreenPlayer> mWefControllerFullScreen;

        public HideLockViewRunnable(FullScreenPlayer controller) {
            mWefControllerFullScreen = new WeakReference<>(controller);
        }

        @Override
        public void run() {
            if (mWefControllerFullScreen != null && mWefControllerFullScreen.get() != null) {
                mWefControllerFullScreen.get().mIvLock.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onClickSetting() {
        mVodSubtitlesView.setVisibility(GONE);
        mVodSubtitlesSettingView.setVisibility(VISIBLE);
    }

    @Override
    public void onClickBackButton() {
        mVodSubtitlesView.setVisibility(VISIBLE);
        mVodSubtitlesSettingView.setVisibility(GONE);
    }

    @Override
    public void onCLickDoneButton(Map map) {
        mControllerCallback.onClickSubtitleViewDoneButton(map);
        onClickBackButton();
    }
}
