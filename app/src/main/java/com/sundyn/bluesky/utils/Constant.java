package com.sundyn.bluesky.utils;

/**
 * ============================================================
 * <p>
 * 版本 ：1.0
 * <p>
 * 描述 ：
 * <p>
 * 常量类，封装请求Url 修订历史 ：
 * <p>
 * ============================================================
 **/
public class Constant {

    // public final static String BASE_URL = "http://123.15.58.210/";
    // public final static String BASE_URL = "http://192.168.0.107:8080/";
    public final static String BASE_URL = "http://192.168.0.108:8080/";
    // public final static String BASE_URL = "http://192.168.0.109:8080/";
    // public final static String BASE_URL = "http://192.168.100.156:8080/";

    public final static String URL_LOGIN = "blueSky/sys/android/common/login";// 登录

    /* 新闻 */
    public final static String URL_NEWLIST = "blueSky/sys/android/notice/news?types=1,2";// 新闻列表
    public final static String URL_NEWCONTENT = "blueSky/sys/android/notice/content?notice_id=";// 新闻内容
    public final static String CACHE_NEWS = "cache_news";// 缓存的新闻内容对应key

	/* 通知公告 */

    public final static String URL_GETPUSHLIST = "blueSky/sys/notice/getpush/list";
    public final static String URL_PUSHMSG = "blueSky/sys/notice/getPush/push?id=";
    public final static String URL_UPDATEPUSH = "blueSky/sys/notice/updatepush/update";
    public final static String URL_GETPUSHCOUNT = "blueSky/sys/notice/getPush/getPushCount";// 获取未读消息个数

    public final static String URL_NOTICELIST = "blueSky/sys/android/notice/news?types=2";// 通知公告列表
    public final static String URL_NOTICECONTENT = "blueSky/sys/android/notice/content?notice_id=";// 公告内容

    public final static String URL_PM = "blueSky/sys/pm/mobile/currentBuildSitePmByRegion";// 地图中pm接口
    public final static String URL_BUILD = "blueSky/sys/report/mb/count_last7jour";// 违章工地

    /* 工作台 */
    public final static String URL_GETMODEL = "blueSky/sys/android/common/getModel";// 获取登录用户权限模块
    public final static String URL_GETALLAREA = "blueSky/sys/android/common/getAllArea";// 获取所有地区
    public final static String URL_AllSite = "blueSky/sys/build/getAllSite";// 所有违章工地列表
    public final static String URL_SELECTAREAPRJINFO = "blueSky/sys/cs/selectAreaPrjInfo";// 所有工地

    public final static String URL_GETSITEBYAREAID = "blueSky/sys/build/getAllSiteDataByAreaId";// 根据区域ID获取工地列表
    public final static String URL_GETCAMERALIST = "blueSky/sys/build/getCameraList";// 根据工地编号获取摄像头
    public final static String URL_GETCAUSES = "blueSky/sys/cs/selectItemsInfo";// 获取巡查问题
    public final static String URL_REPORT = "blueSky/sys/polling/doCameras";// 提交报告

    /* 获取故障摄像头 接口 */
    public final static String URL_GETERRORCAMERA = "blueSky/sys/driver/err";// 获取摄像头故障列表
    public final static String URL_DELERR = "blueSky/sys/driver/delErr";// 排除摄像头故障
    // public final static String URL_MYSITE =
    // "blueSky/sys/pm/mobile/selectPmListByUserNo";// 我的工地
    public final static String URL_MYSITE = "blueSky/sys/android/common/getMySite";// 我的工地(新)

    /* 数据中心 */
    public final static String URL_AREAENV = "blueSky/sys/pm/mobile/currentPmAvgDataByRegion";// 基本环境数据(新)
    public final static String URL_GETREPORTPCT = "blueSky/sys/report/get/getReportPct/type?pastDayCount=7&type=AREA&queryCount=true";
    public final static String URL_SELECTCOMPLAINTBYWEEKCOUNT = "blueSky/sys/cs/SelectComplaintByWeekCount";// 举报环形图

    public final static String URL_GETPM_PLUS = "blueSky/sys/pm/mobile/pmWaringList";// pm10超标工地(新)
    public final static String URL_GETLINECHART = "blueSky/sys/pm/mobile/pastDayReginPmByDate";// 获取折线图数据(新)
    public final static String URL_GETREPORTCHART = "blueSky/sys/cs/selectComplaintByWeek";// 获取举报数折线图数据(新)
    /* 单个工地的数据中心 */
    public final static String URL_GETSITEDATA = "blueSky/sys/pm/mobile/currentBuildSitePmByRegion";// 获取某个工地的数据中心
    public final static String URL_SELECTCOMPLAINTGDBYWEEKCOUNT = "blueSky/sys/cs/selectComplaintgdByWeekCount";// 获取某个工地举报数
    public final static String URL_GETSITECHARTDATA = "blueSky/sys/pm/pastDayReginPmByDate";// 获取当个工地折现图数据(新)
    public final static String URL_SELECTCOMPLAINTGDBYWEEK = "blueSky/sys/cs/selectComplaintgdByWeek";// 获取某个工地举报数

    /* 整改通知 */
    public final static String URL_SELECTGOVERNMENT = "blueSky/sys/cs/selectGovernment";// 获取区政府管委
    public final static String URL_SELECTMAN = "blueSky/sys/cs/selectMan";// 获取巡检员
    public final static String URL_ADDZGTZ = "blueSky/sys/cs/addZgtz";// 提交整改通知
    public final static String URL_ALLPROJECT = "blueSky/sys/cs/selectProject";// 工程项目全部信息
    public final static String URL_ZGTZONELIST = "blueSky/sys/cs/selectZgtzList ";// 获取整改通知列表
    public final static String URL_ZGTZONEINFO = "blueSky/sys/cs/selectZgtzOneInfo";// 整改通知详情

    /* 督办催办 */
    public final static String URL_DBCBLIST = "blueSky/sys/cs/selectDbcbList";// 督办催办
    public final static String URL_DBCBONEINFO = "blueSky/sys/cs/selectDbcbOneInfo";// 督办催办详情
    public final static String URL_ADDSUPERVISIONNOTICE = "blueSky/sys/cs/addSupervisionNotice";// 添加督办催办
    public final static String URL_SELECTTERRITORIAL = "blueSky/sys/cs/selectTerritorial";// 获取区域

    /* 日常巡查 */
    public final static String URL_ADDCHECKRECORD = "blueSky/sys/cs/addCheckRecord";// 添加日常巡查
    public final static String URL_SELECTCHECKRECORDLIST = "blueSky/sys/cs/selectCheckRecordList ";// 日常巡查列表

    /* 逻辑相关===指派 */
    public final static String URL_SELECTOFFICE = "blueSky/sys/cs/selectOffice";// 选择办事处
    public final static String URL_ASSIGNTOOFFICE = "blueSky/sys/cs/assignToOffice";// 指派办事处
    public final static String URL_REPLYZGTZ = "blueSky/sys/cs/replyzgtz";// 回复整改通知
    public final static String URL_REPLYRETURNRESULT = "blueSky/sys/cs/replyReturnResult";// 退回
    public final static String URL_REPLYAUDIT = "blueSky/sys/cs/replyAudit";// 审批通过
    public final static String URL_REPLYSUPERVISION = "blueSky/sys/cs/replySupervision ";// 督办催办回复
    public final static String URL_GETRETURNRESULT = "blueSky/sys/cs/getReturnResult";// 查看退回原因

    /* 投诉汇总 */
    public final static String URL_SELECTCOMPLAINTLIST = "blueSky/sys/cs/selectComplaintList";// 投诉汇总项目列表
    public final static String URL_SELECTSINGLECOMPLAINT = "blueSky/sys/cs/selectSingleComplaint";// 投诉汇总项目详情
    public final static String URL_SELECTCOMPLAINTSTATUS = "blueSky/sys/cs/selectComplaintStatus";// 投诉状态
    public final static String URL_UPDATECOMPLAINTST = "blueSky/sys/cs/updateComplaintSt";// 投诉状态
    public final static String URL_SELECTCOMPLAINTSUMMARY = "blueSky/sys/cs/selectComplaintSummary";// 投诉汇总数据
    public final static String URL_SELECTPRJAREAAMTSUMMARY = "blueSky/sys/cs/selectPrjAreaAmtSummary";// 柱状图

    public final static String URL_SELECTTERRITORIALNOZZ = "blueSky/sys/cs/selectTerritorialNozz";// 不包含郑州市的区域(老扬尘办)

    // 首页
    public final static String URL_GETPLUSDAY = "blueSky/pmAnalyse/getPlusDay";// 首页饼状图
    public final static String URL_GETPLUSPRECENT = "blueSky/pmAnalyse/getPlusPrecent";//
    public final static String URL_INSTALLPCT = "blueSky/sys/pm/installPct";// 郑州市各县区接入系统工地占比
    public final static String URL_VALIDAPPVERSION = "blueSky/ycb/validAppVersion";// 版本升级
    public final static String URL_GETMYSITESTATE = "blueSky/sys/android/common/getMySiteState";
    public final static String URL_CURRENTPMAVGDATABYNAME = "blueSky/sys/pm/mobile/currentPmAvgDataByUser";// pm实时数据

    public final static String URL_SITETBJ = "blueSky/sys/android/common/siteTbj";

    /* 海康视频相关配置 */
    public static interface Hik {
        String DEF_SERVER = "http://123.15.58.211/msp";
        String USERNAME = "admin";
        String PASSWORD = "TOPica211";

        /* 监控点id */
        String CAMERA_ID = "camera_id";
        /* 设备ID */
        String DEVICE_ID = "device_id";
    }

}
