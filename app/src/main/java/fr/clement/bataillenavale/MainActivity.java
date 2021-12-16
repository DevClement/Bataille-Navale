package fr.clement.bataillenavale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends Activity {
    private int numberHorizontalPixel;
    private int numberVerticalPixel;

    private int blockSize;

    private int gridWidth = 40;
    private int gridHeight;

    private boolean hit = false;

    private int shotTaken;

    private int distanceFromSub;

    private boolean debugging = true;

    private int subHorizontalPosition;
    private int subVerticalPosition;

    private ImageView gameView;
    private Bitmap blankBitmap;
    private Canvas canvas;
    private Paint paint;

    private int horizontalTouch;
    private int verticalTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Permet de supprimer le titre

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);

        numberHorizontalPixel = size.x;
        numberVerticalPixel = size.y;
        blockSize = (int)(numberHorizontalPixel / gridWidth);

        gridHeight = (int)(numberVerticalPixel / blockSize);

        blankBitmap = Bitmap.createBitmap(numberHorizontalPixel, numberVerticalPixel, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(blankBitmap);

        gameView = new ImageView(this);

        paint = new Paint();

        newGame();
        draw();

        setContentView(gameView);
    }

    // Start the game
    void newGame() {
        Random random = new Random();
        subHorizontalPosition = random.nextInt( gridWidth);
        subVerticalPosition = random.nextInt( gridHeight);
        shotTaken = 0;
    }

    // Draw the game
    void draw() {
        gameView.setImageBitmap(blankBitmap);

        canvas.drawColor(Color.argb(255, 255, 255, 255));

        paint.setColor(Color.argb(255, 0, 0, 0));

        for (int i = 0; i < gridWidth; i++) {
            canvas.drawLine(blockSize*i, 0, blockSize*i, numberVerticalPixel-1, paint);
        }

        for (int i = 0; i < gridHeight; i++) {
            canvas.drawLine(0, blockSize*i, numberVerticalPixel-1, blockSize*i, paint);
        }

        paint.setTextSize(blockSize*2);
        paint.setColor(Color.argb(255, 0, 0, 255));

        canvas.drawText("Tir effectué:" + shotTaken + " Distance:" + distanceFromSub, blockSize, blockSize*1.75f, paint);

        canvas.drawRect(horizontalTouch*blockSize, verticalTouch*blockSize, (horizontalTouch*blockSize)+blockSize, (verticalTouch*blockSize)+blockSize, paint);

        printDebuggingText("numberVerticalPixel", String.valueOf(numberVerticalPixel));
        printDebuggingText("numberHorizontalPixel", String.valueOf(numberHorizontalPixel));
        printDebuggingText("blockSize", String.valueOf(blockSize));
        printDebuggingText("gridHeight", String.valueOf(gridHeight));
        printDebuggingText("gridWidth", String.valueOf(gridWidth));
        printDebuggingText("shotTaken", String.valueOf(shotTaken));
        printDebuggingText("subHorizontalPosition", String.valueOf(subHorizontalPosition));
        printDebuggingText("subVerticalPosition", String.valueOf(subVerticalPosition));
    }

    // Called for shot case
    void takeShot(float touchX, float touchY) {
        shotTaken++;

        horizontalTouch = (int) touchX / blockSize;
        verticalTouch = (int) touchY / blockSize;

        hit = horizontalTouch == subHorizontalPosition && verticalTouch == subVerticalPosition;

        int horizontalGap = (int) horizontalTouch - subHorizontalPosition;
        int verticalGap = (int) verticalTouch - subVerticalPosition;

        distanceFromSub = (int) Math.sqrt((horizontalGap*horizontalGap)+(verticalGap*verticalGap));

        if(hit) {
            boom();
        } else {
            draw();
        }
    }

    // Called when user touch boat
    void boom() {
        gameView.setImageBitmap(blankBitmap);
        canvas.drawColor(Color.argb(255, 255, 0, 0));

        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(blockSize*10);

        canvas.drawText("BOOM", blockSize*4, blockSize*14, paint);

        paint.setTextSize(blockSize*2);
        canvas.drawText("Refaire une partie ?", blockSize*8, blockSize*18, paint);
        // newGame();

        // draw();
    }

    // Print debug info
    void printDebuggingText(String ...values) {
        String data = "";
        for(String value: values) {
            data += value + " ";
        }
        Log.d("Debug", data);
    }

    // Event when user touch window
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction()& MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            takeShot(event.getX(), event.getY());
        }
        return false;
    }
}