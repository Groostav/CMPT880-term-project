package cn.yoho.im.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * һЩ���÷���
 * 
 * @author ice.deng
 * 
 */
public class Utility {
    /**
     * 
     * �����ӻ�ȡ��ǰ��ʱ���
     * 
     * @return ���ص�ǰ��ʱ���
     */
    private static String getTimestamp(Calendar date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        int s = date.get(Calendar.SECOND) / 30;
        return df.format(date.getTime()) + s;
    }

    /**
     * ��ȡ��ǰʱ�䰴���ӻ�ȡ��ʱ���
     * 
     * @return
     */
    public static String getTimestamp() {
        Calendar date = Calendar.getInstance();
        return getTimestamp(date);
    }

    /**
     * ��ȡ���ָ�����Ӻ��ʱ���
     * 
     * @param incr
     *            ��ӵķ�����
     * @return
     */
    public static String getTimestampM(int incr) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, incr);
        return getTimestamp(date);
    }

    /**
     * ��ȡ�û�ID�ķ���ֵ
     * 
     * @param userId
     * @return
     */
    public static int getUserRegion(int userId) {
        return userId / 1000 + 1;
    }
}
