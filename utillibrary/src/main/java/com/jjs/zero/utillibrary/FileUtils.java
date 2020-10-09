package com.jjs.zero.utillibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ScrollView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/3/12
 * @Details: <功能描述>
 */
public class FileUtils {

    /**
     * 获取图片保存路径
     * @param activity
     * @return
     */
    public static String getPhotoPath(Activity activity) {
        return  activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + System.currentTimeMillis()+"uuid"+ UUID.randomUUID().toString() +".jpg";
    }
    /**
     * 获取文件路径
     * @param activity
     * @return
     */
    public static String getFilePath(Activity activity) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String fileName = format.format(date);
        return  activity.getFilesDir() + File.separator + fileName+".pdf";
    }

    /**
     * pdf下载
     * @param activity
     * @param inputStreams
     * @param fileLength
     * @return
     */
    public static File downloadFile(Activity activity, InputStream inputStreams , long fileLength) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File file = null;
        Log.i("zero","开始下载================");
        try {
//                       long fileLength = body.contentLength();//文件大小
            String filePath = FileUtils.getFilePath(activity);
            inputStream = inputStreams;

            file = new File(filePath);
            outputStream = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                //获取进度
//                        long currentLength = file.length();
//                        int progress = (int)(currentLength*100/fileLength);

            }
            Log.i("zero","完成下载================");

//            return file;
        }catch (Exception e) {
            Log.i("zero","下载出错================");
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("zero","完成下载 关闭流================");
        }
        return file;
    }

    /**
     * pdf转图片
     * @param activity
     * @param pdfFile
     * @return
     */
    public static ArrayList<String> pdfToBitmap(Activity activity, File pdfFile) {
//        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                final int pageCount = renderer.getPageCount();
                Log.i("zero", "图片de 张数： " +pageCount);
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    int width = activity.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                    int height = activity.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                    //todo 以下三行处理图片存储到本地出现黑屏的问题，这个涉及到背景问题
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    Rect r = new Rect(0, 0, width, height);
                    page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                    bitmaps.add(bitmap);

                    File file = bitmapToFile(activity,bitmap);
                    list.add(file.getAbsolutePath());
                    bitmap.recycle();
                    // close the page
                    page.close();
                }
                // close the renderer
                renderer.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;

    }

    /**
     * bitmap转File
     * @param activity
     * @param bitmap
     * @return
     */
    public static File bitmapToFile(Activity activity, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(getPhotoPath(activity));
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
            is.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //保存资源文件中的图片到本地相册,实时刷新
    public static void saveImageToGallery(Activity context, Bitmap bmp) {
        // 首先保存图片
//        File appDir = new File(Environment.getExternalStorageDirectory(), "imgs");
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
        String fileName = System.currentTimeMillis() + ".jpg";
//        File file = new File(appDir, fileName);
       String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + fileName+".jpg";
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));

    }

    /**
     * bitmap转64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 截取scrollview的屏幕
     **/
    public static Bitmap getScrollViewBitmap(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

}
