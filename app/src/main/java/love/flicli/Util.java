package love.flicli;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.R.attr.bitmap;
import static android.R.attr.x;
import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by tommaso on 07/07/17.
 */

public class Util {

    public static File getImageUri(Context contex, Bitmap image) {
        File imagePath = new File(contex.getFilesDir(), "images");

        if (!imagePath.exists())
            imagePath.mkdir();

        File newFile = new File(imagePath, "default_image.jpg");

        try {
            Bitmap bmp = image;
            if (bmp != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                new FileOutputStream(newFile).write(stream.toByteArray());
            }
        } catch (IOException e) {
            e.getMessage();
        }

        return newFile;
    }
}
