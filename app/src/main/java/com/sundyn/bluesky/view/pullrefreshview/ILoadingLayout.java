package com.sundyn.bluesky.view.pullrefreshview;


/**
 * ����ˢ�º��������ظ���Ľ���ӿ�
 * 
 * @author Li Hong
 * @since 2013-7-29
 */
public interface ILoadingLayout {
    /**
     * ��ǰ��״̬
     */
    public enum State {
        
        /**
         * Initial state
         */
        NONE,
        
        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET,
        
        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL_TO_REFRESH,
        
        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE_TO_REFRESH,
        
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        REFRESHING,
        
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        @Deprecated
        LOADING,
        
        /**
         * No more data
         */
        NO_MORE_DATA,
    }

    /**
     * ���õ�ǰ״̬��������Ӧ�ø������״̬�ı仯���ı�View�ı仯
     * 
     * @param state ״̬
     */
    public void setState(State state);
    
    /**
     * �õ���ǰ��״̬
     *  
     * @return ״̬
     */
    public State getState();
    
    /**
     * �õ���ǰLayout�����ݴ�С��������Ϊһ��ˢ�µ��ٽ��
     * 
     * @return �߶�
     */
    public int getContentSize();
    
    /**
     * ������ʱ����
     * 
     * @param scale �����ı���
     */
    public void onPull(float scale);
}
