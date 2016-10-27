package com.sundyn.bluesky.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Base64;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtils {

    private static ImageUtils instance = new ImageUtils();

    private ImageUtils() {
    }

    /**
     * ��һʵ��
     */
    public static ImageUtils getImageUtils() {
        return instance;
    }

    public static void setInstance(ImageUtils instance) {
        ImageUtils.instance = instance;
    }

    public static Bitmap decodeBitmap(String path, Context context) {

        // 1.�õ���Ļ�Ŀ����Ϣ
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();

        // 2.�õ�ͼƬ�Ŀ�ߡ�
        Options opts = new Options();// ����λͼ�ĸ�������
        opts.inJustDecodeBounds = true;// ��ȥ������ʵ��λͼ��ֻ�ǻ�ȡ���λͼ��ͷ�ļ���Ϣ
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;

        // 3.�������ű���
        int dx = bitmapWidth / screenWidth;
        int dy = bitmapHeight / screenHeight;
        int scale = 1;
        if (dx > dy && dy > 1) {
            System.out.println("����ˮƽ��������,���ű�����" + dx);
            scale = dx;
        }

        if (dy > dx && dx > 1) {
            System.out.println("���մ�ֱ��������,���ű�����" + dy);
            scale = dy;
        }
        // 4.���ż���ͼƬ���ڴ档
        opts.inSampleSize = scale;// ������ֵ����һ�������ý���������ͼƬ
        opts.inJustDecodeBounds = false;// ������ȥ�������λͼ��
        bitmap = BitmapFactory.decodeFile(path, opts);

        return bitmap;
    }

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    /**
     * ͨ��Base32��Bitmapת����Base64�ַ���
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(CompressFormat.JPEG, 40, bos);// ����100��ʾ��ѹ��
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
