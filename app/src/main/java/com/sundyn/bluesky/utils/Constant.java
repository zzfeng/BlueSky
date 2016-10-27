package com.sundyn.bluesky.utils;

/**
 * ============================================================
 * <p>
 * �汾 ��1.0
 * <p>
 * ���� ��
 * <p>
 * �����࣬��װ����Url �޶���ʷ ��
 * <p>
 * ============================================================
 **/
public class Constant {

    // public final static String BASE_URL = "http://123.15.58.210/";
    // public final static String BASE_URL = "http://192.168.0.107:8080/";
    public final static String BASE_URL = "http://192.168.0.108:8080/";
    // public final static String BASE_URL = "http://192.168.0.109:8080/";
    // public final static String BASE_URL = "http://192.168.100.156:8080/";

    public final static String URL_LOGIN = "blueSky/sys/android/common/login";// ��¼

    /* ���� */
    public final static String URL_NEWLIST = "blueSky/sys/android/notice/news?types=1,2";// �����б�
    public final static String URL_NEWCONTENT = "blueSky/sys/android/notice/content?notice_id=";// ��������
    public final static String CACHE_NEWS = "cache_news";// ������������ݶ�Ӧkey

	/* ֪ͨ���� */

    public final static String URL_GETPUSHLIST = "blueSky/sys/notice/getpush/list";
    public final static String URL_PUSHMSG = "blueSky/sys/notice/getPush/push?id=";
    public final static String URL_UPDATEPUSH = "blueSky/sys/notice/updatepush/update";
    public final static String URL_GETPUSHCOUNT = "blueSky/sys/notice/getPush/getPushCount";// ��ȡδ����Ϣ����

    public final static String URL_NOTICELIST = "blueSky/sys/android/notice/news?types=2";// ֪ͨ�����б�
    public final static String URL_NOTICECONTENT = "blueSky/sys/android/notice/content?notice_id=";// ��������

    public final static String URL_PM = "blueSky/sys/pm/mobile/currentBuildSitePmByRegion";// ��ͼ��pm�ӿ�
    public final static String URL_BUILD = "blueSky/sys/report/mb/count_last7jour";// Υ�¹���

    /* ����̨ */
    public final static String URL_GETMODEL = "blueSky/sys/android/common/getModel";// ��ȡ��¼�û�Ȩ��ģ��
    public final static String URL_GETALLAREA = "blueSky/sys/android/common/getAllArea";// ��ȡ���е���
    public final static String URL_AllSite = "blueSky/sys/build/getAllSite";// ����Υ�¹����б�
    public final static String URL_SELECTAREAPRJINFO = "blueSky/sys/cs/selectAreaPrjInfo";// ���й���

    public final static String URL_GETSITEBYAREAID = "blueSky/sys/build/getAllSiteDataByAreaId";// ��������ID��ȡ�����б�
    public final static String URL_GETCAMERALIST = "blueSky/sys/build/getCameraList";// ���ݹ��ر�Ż�ȡ����ͷ
    public final static String URL_GETCAUSES = "blueSky/sys/cs/selectItemsInfo";// ��ȡѲ������
    public final static String URL_REPORT = "blueSky/sys/polling/doCameras";// �ύ����

    /* ��ȡ��������ͷ �ӿ� */
    public final static String URL_GETERRORCAMERA = "blueSky/sys/driver/err";// ��ȡ����ͷ�����б�
    public final static String URL_DELERR = "blueSky/sys/driver/delErr";// �ų�����ͷ����
    // public final static String URL_MYSITE =
    // "blueSky/sys/pm/mobile/selectPmListByUserNo";// �ҵĹ���
    public final static String URL_MYSITE = "blueSky/sys/android/common/getMySite";// �ҵĹ���(��)

    /* �������� */
    public final static String URL_AREAENV = "blueSky/sys/pm/mobile/currentPmAvgDataByRegion";// ������������(��)
    public final static String URL_GETREPORTPCT = "blueSky/sys/report/get/getReportPct/type?pastDayCount=7&type=AREA&queryCount=true";
    public final static String URL_SELECTCOMPLAINTBYWEEKCOUNT = "blueSky/sys/cs/SelectComplaintByWeekCount";// �ٱ�����ͼ

    public final static String URL_GETPM_PLUS = "blueSky/sys/pm/mobile/pmWaringList";// pm10���깤��(��)
    public final static String URL_GETLINECHART = "blueSky/sys/pm/mobile/pastDayReginPmByDate";// ��ȡ����ͼ����(��)
    public final static String URL_GETREPORTCHART = "blueSky/sys/cs/selectComplaintByWeek";// ��ȡ�ٱ�������ͼ����(��)
    /* �������ص��������� */
    public final static String URL_GETSITEDATA = "blueSky/sys/pm/mobile/currentBuildSitePmByRegion";// ��ȡĳ�����ص���������
    public final static String URL_SELECTCOMPLAINTGDBYWEEKCOUNT = "blueSky/sys/cs/selectComplaintgdByWeekCount";// ��ȡĳ�����ؾٱ���
    public final static String URL_GETSITECHARTDATA = "blueSky/sys/pm/pastDayReginPmByDate";// ��ȡ������������ͼ����(��)
    public final static String URL_SELECTCOMPLAINTGDBYWEEK = "blueSky/sys/cs/selectComplaintgdByWeek";// ��ȡĳ�����ؾٱ���

    /* ����֪ͨ */
    public final static String URL_SELECTGOVERNMENT = "blueSky/sys/cs/selectGovernment";// ��ȡ��������ί
    public final static String URL_SELECTMAN = "blueSky/sys/cs/selectMan";// ��ȡѲ��Ա
    public final static String URL_ADDZGTZ = "blueSky/sys/cs/addZgtz";// �ύ����֪ͨ
    public final static String URL_ALLPROJECT = "blueSky/sys/cs/selectProject";// ������Ŀȫ����Ϣ
    public final static String URL_ZGTZONELIST = "blueSky/sys/cs/selectZgtzList ";// ��ȡ����֪ͨ�б�
    public final static String URL_ZGTZONEINFO = "blueSky/sys/cs/selectZgtzOneInfo";// ����֪ͨ����

    /* ����߰� */
    public final static String URL_DBCBLIST = "blueSky/sys/cs/selectDbcbList";// ����߰�
    public final static String URL_DBCBONEINFO = "blueSky/sys/cs/selectDbcbOneInfo";// ����߰�����
    public final static String URL_ADDSUPERVISIONNOTICE = "blueSky/sys/cs/addSupervisionNotice";// ��Ӷ���߰�
    public final static String URL_SELECTTERRITORIAL = "blueSky/sys/cs/selectTerritorial";// ��ȡ����

    /* �ճ�Ѳ�� */
    public final static String URL_ADDCHECKRECORD = "blueSky/sys/cs/addCheckRecord";// ����ճ�Ѳ��
    public final static String URL_SELECTCHECKRECORDLIST = "blueSky/sys/cs/selectCheckRecordList ";// �ճ�Ѳ���б�

    /* �߼����===ָ�� */
    public final static String URL_SELECTOFFICE = "blueSky/sys/cs/selectOffice";// ѡ����´�
    public final static String URL_ASSIGNTOOFFICE = "blueSky/sys/cs/assignToOffice";// ָ�ɰ��´�
    public final static String URL_REPLYZGTZ = "blueSky/sys/cs/replyzgtz";// �ظ�����֪ͨ
    public final static String URL_REPLYRETURNRESULT = "blueSky/sys/cs/replyReturnResult";// �˻�
    public final static String URL_REPLYAUDIT = "blueSky/sys/cs/replyAudit";// ����ͨ��
    public final static String URL_REPLYSUPERVISION = "blueSky/sys/cs/replySupervision ";// ����߰�ظ�
    public final static String URL_GETRETURNRESULT = "blueSky/sys/cs/getReturnResult";// �鿴�˻�ԭ��

    /* Ͷ�߻��� */
    public final static String URL_SELECTCOMPLAINTLIST = "blueSky/sys/cs/selectComplaintList";// Ͷ�߻�����Ŀ�б�
    public final static String URL_SELECTSINGLECOMPLAINT = "blueSky/sys/cs/selectSingleComplaint";// Ͷ�߻�����Ŀ����
    public final static String URL_SELECTCOMPLAINTSTATUS = "blueSky/sys/cs/selectComplaintStatus";// Ͷ��״̬
    public final static String URL_UPDATECOMPLAINTST = "blueSky/sys/cs/updateComplaintSt";// Ͷ��״̬
    public final static String URL_SELECTCOMPLAINTSUMMARY = "blueSky/sys/cs/selectComplaintSummary";// Ͷ�߻�������
    public final static String URL_SELECTPRJAREAAMTSUMMARY = "blueSky/sys/cs/selectPrjAreaAmtSummary";// ��״ͼ

    public final static String URL_SELECTTERRITORIALNOZZ = "blueSky/sys/cs/selectTerritorialNozz";// ������֣���е�����(���ﳾ��)

    // ��ҳ
    public final static String URL_GETPLUSDAY = "blueSky/pmAnalyse/getPlusDay";// ��ҳ��״ͼ
    public final static String URL_GETPLUSPRECENT = "blueSky/pmAnalyse/getPlusPrecent";//
    public final static String URL_INSTALLPCT = "blueSky/sys/pm/installPct";// ֣���и���������ϵͳ����ռ��
    public final static String URL_VALIDAPPVERSION = "blueSky/ycb/validAppVersion";// �汾����
    public final static String URL_GETMYSITESTATE = "blueSky/sys/android/common/getMySiteState";
    public final static String URL_CURRENTPMAVGDATABYNAME = "blueSky/sys/pm/mobile/currentPmAvgDataByUser";// pmʵʱ����

    public final static String URL_SITETBJ = "blueSky/sys/android/common/siteTbj";

    /* ������Ƶ������� */
    public static interface Hik {
        String DEF_SERVER = "http://123.15.58.211/msp";
        String USERNAME = "admin";
        String PASSWORD = "TOPica211";

        /* ��ص�id */
        String CAMERA_ID = "camera_id";
        /* �豸ID */
        String DEVICE_ID = "device_id";
    }

}
