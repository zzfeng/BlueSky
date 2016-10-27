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
    public CommonRq mConRq;// ��ͬ������
    public static final String NOTICE = "notice";
    public static final String ZGTZ = "zgtz";
    public final String ROLE_YCB = "ROLE_YCB";
    public final String ROLE_SITE_AREA = "ROLE_SITE_AREA";
    public final String ROLE_STREET = "ROLE_STREET";
    public final String[] roles = {ROLE_YCB, ROLE_SITE_AREA, ROLE_STREET};
    public final static String NOTICEID = "noticeId";
    public static final String REPLYRETURNRESULT = "REPLYRETURNRESULT";// �˻�
    public static final String REBACK = "REBACK";
    public String type;//����֪ͨ���߶���

    @ViewInject(R.id.id_tv_back)
    public TextView tv_back;// ��/��������δ�ظ�
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

    public int statusNo;// ״̬
    public boolean isReturn; // �Ƿ�Ϊ�˻ص���Ϣ
    public String returnResult;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void initView() {
        settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);// ���ô����ԣ��������������
        settings.setDefaultTextEncodingName("UTF-8");//
        settings.setLoadWithOverviewMode(true); // ��������Ļ�Ĵ�С
        settings.setTextSize(TextSize.LARGEST);

        settingsResult = mWebViewResult.getSettings();
        settingsResult.setUseWideViewPort(true);// ���ô����ԣ��������������
        settingsResult.setDefaultTextEncodingName("UTF-8");//
        settingsResult.setLoadWithOverviewMode(true); // ��������Ļ�Ĵ�С
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

    /* ָ�ɰ��´� */
    public void assignToOffice(String replayBy, String id, String type) {
        showDialog("����ָ��...");
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
                        showToast("ָ�ɳɹ���");
                        BaseRoleActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                disDIalog();
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("ָ��ʧ��!");
                disDIalog();
            }
        });

    }

    /* �˻� */
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
                showToast("�˻�ʧ��!");
            }
        });
    }

    /* ����ͨ�� */
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
                showToast("����ʧ��!");
            }
        });
    }

    /* �ظ� */
    protected void reply(String content, String type) {
        String replyUrl = "";
        if (ZGTZ.equals(type)) {
            replyUrl = Constant.URL_REPLYZGTZ;
        } else if (NOTICE.equals(type)) {
            replyUrl = Constant.URL_REPLYSUPERVISION;
        }
        if (TextUtils.isEmpty(replyUrl))
            return;
        showDialog("���ڻظ�...");
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
                showToast("�ظ�ʧ��!");
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
                mFloatingActionButtonA.setTitle("�˻�");
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
				button_no1.setText("�˻�");
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
                tv_back.setText("��/��������δ�ظ�");
                mMenuMultipleActions.setVisible(false);
				/*ll_back.setVisibility(View.GONE);
				ll_control.setVisibility(View.GONE);
				tv_back.setVisibility(View.VISIBLE);
				tv_back.setText("��/��������δ�ظ�");*/
            } else {
				/*ll_control.setVisibility(View.GONE);
				tv_back.setVisibility(View.VISIBLE);
				tv_back.setText("��/��������δ�ظ�");*/
                tv_back.setVisibility(View.VISIBLE);
                tv_back.setText("��/��������δ�ظ�");
                mMenuMultipleActions.setVisible(false);

            }

        } else if (ROLE_SITE_AREA.equals(statusName)) {// ������
            if (no == 0) {// δָ��
                mMenuMultipleActions.setVisibility(View.VISIBLE);
                mMenuMultipleActions.setVisible(true);
                tv_back.setVisibility(View.GONE);
                mFloatingActionButtonB.setVisibility(View.GONE);
                mFloatingActionButtonA.setTitle("ָ�ɵ����´�");
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
                                        .setTitle("ָ�ɰ��´�")
                                        .setMsg("ָ�ɵ�:" + data.UserName)
                                        .setPositiveButton("ȷ��",
                                                new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        assignToOffice(
                                                                data.UserNo, id
                                                                        + "",
                                                                type);
                                                    }
                                                })
                                        .setNegativeButton("ȡ��",
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
				button_no1.setText("ָ�ɵ����´�");
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
										.setTitle("ָ�ɰ��´�")
										.setMsg("ָ�ɵ�:" + data.UserName)
										.setPositiveButton("ȷ��",
												new OnClickListener() {
													@Override
													public void onClick(View v) {
														assignToOffice(
																data.UserNo, id
																		+ "",
																ZGTZ);
													}
												})
										.setNegativeButton("ȡ��",
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
                mFloatingActionButtonA.setTitle("���ͨ��");
                mFloatingActionButtonA.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyAudit(type);
                        mMenuMultipleActions.collapse();
                    }
                });
                mFloatingActionButtonB.setTitle("�˻�");
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
				button_no1.setText("���ͨ��");
				button_no1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						replyAudit(ZGTZ);
					}
				});
				button_no2.setVisibility(View.VISIBLE);
				button_no2.setText("�˻�");
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
                tv_back.setText("���´���δ�ظ�");
				
				/*ll_control.setVisibility(View.GONE);
				tv_back.setVisibility(View.VISIBLE);
				tv_back.setText("���´���δ�ظ�");*/
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
                mFloatingActionButtonA.setTitle("�ظ�");
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
				button_no1.setText("�ظ�");
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
