package com.squadrant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;

import com.squadrant.postboxnotification.R;
import com.squadrant.util.PackageNameUtils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void getAppName_NotNull() {
        Context c = ApplicationProvider.getApplicationContext();
        // Try with garbage inputs
        Assert.assertNotNull(PackageNameUtils.getAppName(c, "blah"));
        Assert.assertNotNull(PackageNameUtils.getAppName(c, ""));
        Assert.assertNotNull(PackageNameUtils.getAppName(c, null));

        // Try with real inputs
        Assert.assertNotNull(PackageNameUtils.getAppName(c, "com.android.chrome"));
        Assert.assertNotNull(PackageNameUtils.getAppName(c, "com.squadrant.postboxnotification"));
        Assert.assertNotNull(PackageNameUtils.getAppName(c, "com.android.apps.messaging"));
    }

    @Test
    public void getAppName_Correct() {
        Context c = ApplicationProvider.getApplicationContext();
        // Try with garbage inputs
        Assert.assertEquals("(unknown)", PackageNameUtils.getAppName(c, "blah"));
        Assert.assertEquals("(unknown)", PackageNameUtils.getAppName(c, ""));
        Assert.assertEquals("(unknown)", PackageNameUtils.getAppName(c, null));

        // Try with real inputs
        Assert.assertEquals("Chrome", PackageNameUtils.getAppName(c, "com.android.chrome"));
        Assert.assertEquals("Notification Postbox", PackageNameUtils.getAppName(c, "com.squadrant.postboxnotification"));
        Assert.assertEquals("Messages", PackageNameUtils.getAppName(c, "com.google.android.apps.messaging"));
    }

    @Test
    public void getTimeSent_NotNull() {
        Context c = ApplicationProvider.getApplicationContext();
        Assert.assertNotNull(PackageNameUtils.getTimeSent(c, 123456789));
        Assert.assertNotNull(PackageNameUtils.getTimeSent(c, 1241));
        Assert.assertNotNull(PackageNameUtils.getTimeSent(c, 76576453));
        Assert.assertNotNull(PackageNameUtils.getTimeSent(c, Long.MIN_VALUE));
        Assert.assertNotNull(PackageNameUtils.getTimeSent(c, 0));
        Assert.assertNotNull(PackageNameUtils.getTimeSent(c, Long.MAX_VALUE));
    }

    @Test
    public void getTimeSent_LengthWithinBounds() {
        Context c = ApplicationProvider.getApplicationContext();

        int maxLength = 10;
        int minLength = 2;

        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 123456789).length() <= maxLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 1241).length() <= maxLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 76576453).length() <= maxLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, Long.MIN_VALUE).length() <= maxLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 0).length() <= maxLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, Long.MAX_VALUE).length() <= maxLength);

        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 123456789).length() >= minLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 1241).length() >= minLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 76576453).length() >= minLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, Long.MIN_VALUE).length() >= minLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, 0).length() >= minLength);
        Assert.assertTrue(PackageNameUtils.getTimeSent(c, Long.MAX_VALUE).length() >= minLength);
    }

    @Test
    public void getAppIcon_NotNull() {
        Context c = ApplicationProvider.getApplicationContext();

        Assert.assertNotNull(PackageNameUtils.getAppIcon(c, "blah"));
        Assert.assertNotNull(PackageNameUtils.getAppIcon(c, ""));
        Assert.assertNotNull(PackageNameUtils.getAppIcon(c, null));

        Assert.assertNotNull(PackageNameUtils.getAppIcon(c, "com.android.chrome"));
        Assert.assertNotNull(PackageNameUtils.getAppIcon(c, "com.squadrant.postboxnotification"));
        Assert.assertNotNull(PackageNameUtils.getAppIcon(c, "com.android.apps.messaging"));
    }

    @Test
    public void getAppIcon_ErrorCaseCorrect() {
        Context c = ApplicationProvider.getApplicationContext();

        Assert.assertTrue(areDrawablesIdentical(c.getDrawable(R.drawable.exclamation_mark), PackageNameUtils.getAppIcon(c, "blah")));
        Assert.assertTrue(areDrawablesIdentical(c.getDrawable(R.drawable.exclamation_mark), PackageNameUtils.getAppIcon(c, "")));
        Assert.assertTrue(areDrawablesIdentical(c.getDrawable(R.drawable.exclamation_mark), PackageNameUtils.getAppIcon(c, null)));
    }

    private static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    private static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }
}
