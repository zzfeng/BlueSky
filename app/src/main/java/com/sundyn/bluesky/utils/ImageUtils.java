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
     * 单一实例
     */
    public static ImageUtils getImageUtils() {
        return instance;
    }

    public static void setInstance(ImageUtils instance) {
        ImageUtils.instance = instance;
    }

    public static Bitmap decodeBitmap(String path, Context context) {

        // 1.得到屏幕的宽高信息
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();

        // 2.得到图片的宽高。
        Options opts = new Options();// 解析位图的附加条件
        opts.inJustDecodeBounds = true;// 不去解析真实的位图，只是获取这个位图的头文件信息
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;

        // 3.计算缩放比例
        int dx = bitmapWidth / screenWidth;
        int dy = bitmapHeight / screenHeight;
        int scale = 1;
        if (dx > dy && dy > 1) {
            System.out.println("按照水平方法缩放,缩放比例：" + dx);
            scale = dx;
        }

        if (dy > dx && dx > 1) {
            System.out.println("按照垂直方法缩放,缩放比例：" + dy);
            scale = dy;
        }
        // 4.缩放加载图片到内存。
        opts.inSampleSize = scale;// 如果这个值大于一，重新让解析器加在图片
        opts.inJustDecodeBounds = false;// 真正的去解析这个位图。
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
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(CompressFormat.JPEG, 40, bos);// 参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
