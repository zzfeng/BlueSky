package com.sundyn.bluesky.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.bean.OfficeBean;
import com.sundyn.bluesky.bean.User;
import com.sundyn.bluesky.utils.CommonRq;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.view.AlertDialog;
import com.sundyn.bluesky.view.ImageGridView;
import com.sundyn.bluesky.view.RebackDialogFra;
import com.sundyn.bluesky.view.floatingactionmenu.FloatingActionButton;
import com.sundyn.bluesky.view.floatingactionmenu.FloatingActionsMenu;
import com.sundyn.bluesky.view.floatingactionmenu.ObservableScrollView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import okhttp3.Call;

public class BaseRoleActivity extends BaseActivity {
    public int id;
    public CommonRq mConRq;// 共同请求体
    public static final String NOTICE = "notice";
    public static final String ZGTZ = "zgtz";
    public final String ROLE_YCB = "ROLE_YCB";
    public final String ROLE_SITE_AREA = "ROLE_SITE_AREA";
    public final String ROLE_STREET = "ROLE_STREET";
    public final String[] roles = {ROLE_YCB, ROLE_SITE_AREA, ROLE_STREET};
    public final static String NOTICEID = "noticeId";
    public static final String REPLYRETURNRESULT = "REPLYRETURNRESULT";// 退回
    public static final String REBACK = "REBACK";
    public String type;//整改通知或者督办

    @ViewInject(R.id.id_tv_back)
    public TextView tv_back;// 市/区政府还未回复
    @ViewInject(R.id.id_ll_back)
    public LinearLayout ll_back;
    @ViewInject(R.id.id_wv_back)
    public WebView mWebView;
    public WebSettings settings;
    @ViewInject(R.id.id_ll_control)
    public LinearLayout ll_control;
    @ViewInject(R.id.id_button_no1)
    public Button button_no1;
    @ViewInject(R.id.id_button_no2)
    public Button button_no2;
    @ViewInject(R.id.id_ll_returnresult)
    public LinearLayout ll_returnresult;
    @ViewInject(R.id.id_wv_returnresult)
    public WebView mWebViewResult;
    public WebSettings settingsResult;
    @ViewInject(R.id.imageGridView)
    public ImageGridView imageGridView;
    @ViewInject(R.id.id_ll_img)
    public LinearLayout ll_img;
    @ViewInject(R.id.multiple_actions)
    protected FloatingActionsMenu mMenuMultipleActions;
    @ViewInject(R.id.action_a)
    protected FloatingActionButton mFloatingActionButtonA;
    @ViewInject(R.id.action_b)
    protected FloatingActionButton mFloatingActionButtonB;
    @ViewInject(R.id.scroll_view)
    protected ObservableScrollView mScrollView;

    public int statusNo;// 状态
    public boolean isReturn; // 是否为退回的消息
    public String returnResult;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void initView() {
        settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settings.setDefaultTextEncodingName("UTF-8");//
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setTextSize(TextSize.LARGEST);

        settingsResult = mWebViewResult.getSettings();
        settingsResult.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settingsResult.setDefaultTextEncodingName("UTF-8");//
        settingsResult.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settingsResult.setTextSize(TextSize.LARGEST);

        mMenuMultipleActions.attachToScrollView(mScrollView);
    }

    public String getStatusName() {
        User user = mApplication.getUser();
        if (user != null) {
            List<String> rolesList = user.getRoles();
            for (String role : rolesList) {
                for (String statu : roles) {
                    if (role.equalsIgnoreCase(statu)) {
                        return role;
                    }
                }
            }
        }
        return "";
    }

    /* 指派办事处 */
    public void assignToOffice(String replayBy, String id, String type) {
        showDialog("正在指派...");
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_ASSIGNTOOFFICE)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("replayBy", replayBy).addParams("id", id)
                .addParams("type", type).build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        showToast("指派成功！");
                        BaseRoleActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                disDIalog();
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("指派失败!");
                disDIalog();
            }
        });

    }

    /* 退回 */
    public void replyReturnResult(String content, String type) {
        OkHttpUtils.post()
                .url(Constant.BASE_URL + Constant.URL_REPLYRETURNRESULT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("returnResult", content).addParams("id", id + "")//
                .addParams("type", type)//
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        BaseRoleActivity.this.finish();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("退回失败!");
            }
        });
    }

    /* 审批通过 */
    public void replyAudit(String type) {
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_REPLYAUDIT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("userNo", mApplication.getUser().getUserNo())//
                .addParams("id", id + "")//
                .addParams("type", type)//
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        BaseRoleActivity.this.finish();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("审批失败!");
            }
        });
    }

    /* 回复 */
    protected void reply(String content, String type) {
        String replyUrl = "";
        if (ZGTZ.equals(type)) {
            replyUrl = Constant.URL_REPLYZGTZ;
        } else if (NOTICE.equals(type)) {
            replyUrl = Constant.URL_REPLYSUPERVISION;
        }
        if (TextUtils.isEmpty(replyUrl))
            return;
        showDialog("正在回复...");
        OkHttpUtils.post().url(Constant.BASE_URL + replyUrl)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("userNo", mApplication.getUser().getUserNo())
                .addParams("replyContent", content).addParams("id", id + "")//
                .addParams("btnType", 1 + "")//
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        BaseRoleActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                disDIalog();
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("回复失败!");
                disDIalog();
            }
        });
    }

    protected void initStautusButton(int no) {
        String statusName = getStatusName();
        if (ROLE_YCB.equals(statusName)) {
            if (no == 3) {
                tv_back.setVisibility(View.GONE);
                mMenuMultipleActions.setVisibility(View.VISIBLE);
                mMenuMultipleActions.setVisible(true);
                mFloatingActionButtonB.setVisibility(View.GONE);
                mFloatingActionButtonA.setTitle("退回");
                mFloatingActionButtonA.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RebackDialogFra rFra = new RebackDialogFra();
                        rFra.show(getSupportFragmentManager(),
                                REPLYRETURNRESULT);
                        mMenuMultipleActions.collapse();
                    }
                });
                /*ll_control.setVisibility(View.VISIBLE);
				tv_back.setVisibility(View.GONE);
				button_no2.setVisibility(View.GONE);
				button_no1.setVisibility(View.VISIBLE);
				button_no1.setText("退回");
				button_no1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						RebackDialogFra rFra = new RebackDialogFra();
						rFra.show(getSupportFragmentManager(),
								REPLYRETURNRESULT);
					}
				});*/
            } else if (no == 2) {
                ll_back.setVisibility(View.GONE);
                tv_back.setVisibility(View.VISIBLE);
                tv_back.setText("市/区政府还未回复");
                mMenuMultipleActions.setVisible(false);
				/*ll_back.setVisibility(View.GONE);
				ll_control.setVisibility(View.GONE);
				tv_back.setVisibility(View.VISIBLE);
				tv_back.setText("市/区政府还未回复");*/
            } else {
				/*ll_control.setVisibility(View.GONE);
				tv_back.setVisibility(View.VISIBLE);
				tv_back.setText("市/区政府还未回复");*/
                tv_back.setVisibility(View.VISIBLE);
                tv_back.setText("市/区政府还未回复");
                mMenuMultipleActions.setVisible(false);

            }

        } else if (ROLE_SITE_AREA.equals(statusName)) {// 区政府
            if (no == 0) {// 未指派
                mMenuMultipleActions.setVisibility(View.VISIBLE);
                mMenuMultipleActions.setVisible(true);
                tv_back.setVisibility(View.GONE);
                mFloatingActionButtonB.setVisibility(View.GONE);
                mFloatingActionButtonA.setTitle("指派到办事处");
                mFloatingActionButtonA.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mConRq == null) {
                            mConRq = new CommonRq(mContext, mApplication);
                        }
                        mConRq.selectOffice(new CommonRq.RqCallBack<OfficeBean.Office>() {

                            @Override
                            public void initSelectData(
                                    final OfficeBean.Office data) {
                                new AlertDialog(mContext)
                                        .builder()
                                        .setTitle("指派办事处")
                                        .setMsg("指派到:" + data.UserName)
                                        .setPositiveButton("确认",
                                                new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        assignToOffice(
                                                                data.UserNo, id
                                                                        + "",
                                                                type);
                                                    }
                                                })
                                        .setNegativeButton("取消",
                                                new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                }).show();
                            }

                        });
                        mMenuMultipleActions.collapse();
                    }
                });
				
				/*ll_control.setVisibility(View.VISIBLE);
				tv_back.setVisibility(View.GONE);
				button_no2.setVisibility(View.GONE);
				button_no1.setVisibility(View.VISIBLE);
				button_no1.setText("指派到办事处");
				button_no1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (mConRq == null) {
							mConRq = new CommonRq(mContext, mApplication);
						}
						mConRq.selectOffice(new CommonRq.RqCallBack<OfficeBean.Office>() {

							@Override
							public void initSelectData(
									final OfficeBean.Office data) {
								new AlertDialog(mContext)
										.builder()
										.setTitle("指派办事处")
										.setMsg("指派到:" + data.UserName)
										.setPositiveButton("确认",
												new OnClickListener() {
													@Override
													public void onClick(View v) {
														assignToOffice(
																data.UserNo, id
																		+ "",
																ZGTZ);
													}
												})
										.setNegativeButton("取消",
												new OnClickListener() {
													@Override
													public void onClick(View v) {

													}
												}).show();
							}

						});

					}
				});*/
            } else if (no == 2) {
                mMenuMultipleActions.setVisibility(View.VISIBLE);
                mMenuMultipleActions.setVisible(true);
                tv_back.setVisibility(View.GONE);
                mFloatingActionButtonA.setTitle("审核通过");
                mFloatingActionButtonA.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyAudit(type);
                        mMenuMultipleActions.collapse();
                    }
                });
                mFloatingActionButtonB.setTitle("退回");
                mFloatingActionButtonB.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RebackDialogFra rFra = new RebackDialogFra();
                        rFra.show(getSupportFragmentManager(),
                                REPLYRETURNRESULT);
                        mMenuMultipleActions.collapse();
                    }
                });
				
			/*	ll_control.setVisibility(View.VISIBLE);
				tv_back.setVisibility(View.GONE);
				button_no1.setVisibility(View.VISIBLE);
				button_no1.setText("审核通过");
				button_no1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						replyAudit(ZGTZ);
					}
				});
				button_no2.setVisibility(View.VISIBLE);
				button_no2.setText("退回");
				button_no2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						RebackDialogFra rFra = new RebackDialogFra();
						rFra.show(getSupportFragmentManager(),
								REPLYRETURNRESULT);
					}
				});*/
                if (isReturn) {
                    ll_returnresult.setVisibility(View.VISIBLE);
                    mWebViewResult.loadData(returnResult,
                            "text/html; charset=UTF-8", null);
                }
            } else if (no == 1) {
                mMenuMultipleActions.setVisible(false);
                tv_back.setVisibility(View.VISIBLE);
                tv_back.setText("办事处还未回复");
				
				/*ll_control.setVisibility(View.GONE);
				tv_back.setVisibility(View.VISIBLE);
				tv_back.setText("办事处还未回复");*/
            } else {
                mMenuMultipleActions.setVisible(false);
//				ll_control.setVisibility(View.GONE);
                tv_back.setVisibility(View.GONE);
            }

        } else if (ROLE_STREET.equals(statusName)) {
            if (no == 1) {
                mMenuMultipleActions.setVisibility(View.VISIBLE);
                mMenuMultipleActions.setVisible(true);
                tv_back.setVisibility(View.GONE);
                mFloatingActionButtonB.setVisibility(View.GONE);
                mFloatingActionButtonA.setTitle("回复");
                mFloatingActionButtonA.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RebackDialogFra rFra = new RebackDialogFra();
                        rFra.show(getSupportFragmentManager(), REBACK);
                        mMenuMultipleActions.collapse();
                    }
                });
				
				/*ll_control.setVisibility(View.VISIBLE);
				tv_back.setVisibility(View.GONE);
				button_no2.setVisibility(View.GONE);
				button_no1.setVisibility(View.VISIBLE);
				button_no1.setText("回复");
				button_no1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						RebackDialogFra rFra = new RebackDialogFra();
						rFra.show(getSupportFragmentManager(), REBACK);
					}
				});*/
                if (isReturn) {
                    ll_returnresult.setVisibility(View.VISIBLE);
                    mWebViewResult.loadData(returnResult,
                            "text/html; charset=UTF-8", null);
                }
            } else {
                mMenuMultipleActions.setVisible(false);
//				ll_control.setVisibility(View.GONE);
                tv_back.setVisibility(View.GONE);
            }
        }
    }


}
