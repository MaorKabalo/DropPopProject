package com.example.droppopproject.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class Ball extends Spawn {

    public static final int SIZE_MULTIPLIER = 25; //bigger is slower
    public static final int NUMBER_OF_BALLS = 10;
    public Bitmap ballImage;
    private final Matrix rotator;
    private final static Random random = new Random();
    public final static BallSize[] sizes = new BallSize[NUMBER_OF_BALLS];
    private int ballNum;

    public long gameOverTimer;

    public boolean checkGameOver;

    public boolean isCustom;
    public boolean changeBitmap = true;


    /**
     * Constructs a new Ball object with random properties.
     * This constructor sets the position, velocity, mass, and other properties of the ball.
     * The ball's image is randomly selected from available resources based on ballNum.
     *
     * Used when dropping a new ball
     *
     * @param context The context of the application.
     * @param screenWidth The width of the screen.
     */
    public Ball(Context context, int screenWidth, ArrayList<Bitmap> customBalls) {

        super();


        rotator = new Matrix();

        //ball_0 - 50
        //ball_1 - 100
        //ball_2 - 200
        //ball_3 - 300
        //ball_4 - 400
        //ball_5 - 500
        //ball_6 - 600
        //ball_7 - 700
        //ball_8 - 800
        //ball_9 - 900



        Bitmap bitmap;
        if(customBalls == null || customBalls.isEmpty()){
            ballNum = random.nextInt(5);
            @SuppressLint("DiscouragedApi") int resourceId = context.getResources().getIdentifier("ball_" + ballNum , "drawable", context.getPackageName());
            bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        }
        else{
            ballNum = random.nextInt(customBalls.size() / 2);
            bitmap = customBalls.get(ballNum);
        }

        sizes[ballNum] = new BallSize((float) bitmap.getWidth() / 5 , (float) bitmap.getHeight() / 5, bitmap);
        this.ballImage = Bitmap.createScaledBitmap(bitmap, (int) sizes[ballNum].width, (int) sizes[ballNum].height, false);

        if(ballNum == 0){
            super.mass = 50;
        }
        else if (ballNum == 1){
            super.mass = 250;
        }
        else if(ballNum == 6){
            super.mass = 100;
        }
        else{
            super.mass = ballNum * 100;
        }


        //this.ballImage = ballImage;

        // Set the initial position to the middle bottom of the screen
        super.x = (screenWidth - this.ballImage.getWidth()) / 2f;
        super.y = 350;
        super.radius = this.getRadius();
        this.checkGameOver = false;
        this.gameOverTimer = 0;
    }


    /**
     * Constructs a new Ball object with specified properties.
     * This constructor sets the position, velocity, mass, and other properties of the ball.
     * The ball's image is determined by the given type.
     *
     *
     * Used when there is a collision of 2 balls with the same type
     *
     * @param context The context of the application.
     * @param x The x-coordinate of the ball's position.
     * @param y The y-coordinate of the ball's position.
     * @param vx The velocity of the ball along the x-axis.
     * @param vy The velocity of the ball along the y-axis.
     * @param type The type of the ball.
     */
    public Ball(Context context, float x, float y, float vx, float vy, int type, ArrayList<Bitmap> customBalls) {

        super();


        rotator = new Matrix();

        Bitmap bitmap;


        this.ballNum = type;

        if(customBalls == null || customBalls.isEmpty()){
            if(ballNum > 9 || ballNum < 0){
                ballNum = 0;
            }
            @SuppressLint("DiscouragedApi") int resourceId = context.getResources().getIdentifier("ball_" + ballNum , "drawable", context.getPackageName());
            bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        }
        else{
            if(ballNum > customBalls.size() - 1 || ballNum < 0){
                ballNum = 0;
            }
            bitmap = customBalls.get(ballNum);
        }


        sizes[ballNum] = new BallSize((float) bitmap.getWidth() / 5, (float) bitmap.getHeight() / 5, bitmap);
        this.ballImage = Bitmap.createScaledBitmap(bitmap, (int) sizes[ballNum].width/SIZE_MULTIPLIER, (int) sizes[ballNum].height/SIZE_MULTIPLIER, false);


        if(ballNum == 0){
            super.mass = 50;
        }
        else if(ballNum == 6){
            super.mass = 100;
        }
        else if(ballNum == 9){
            super.mass = 208000;
        }
        else{
            super.mass = ballNum * 100;
        }



        super.radius = this.getRadius();
        super.x = x - super.radius;
        super.y = y;
        super.vx = vx;
        super.vy = vy;
        this.checkGameOver = true;
        this.gameOverTimer = 0;
        this.applyPhysics = true;
    }


    /**
     * Constructs a new Ball object with specified properties.
     * This constructor sets the position, velocity, mass, and other properties of the ball.
     * The ball's image is determined by the given type.
     *
     * Used when fetching a ball from the shared preferences
     *
     *
     * @param context The context of the application.
     * @param x The x-coordinate of the ball's position.
     * @param y The y-coordinate of the ball's position.
     * @param vx The velocity of the ball along the x-axis.
     * @param vy The velocity of the ball along the y-axis.
     * @param type The type of the ball.
     * @param applyPhysics Indicates whether physics should be applied to the ball.
     * @param checkGameOver Indicates whether the game over condition should be checked for the ball.
     * @param angle The angle of the ball.
     */
    public Ball(Context context, float x, float y, float vx, float vy, int type, boolean applyPhysics, boolean checkGameOver, float angle, Bitmap customBall){
        super();


        rotator = new Matrix();

        Bitmap bitmap;


        this.ballNum = type;

        if(ballNum > 9 || ballNum < 0){
            ballNum = 0;
        }


        if(customBall == null){
            @SuppressLint("DiscouragedApi") int resourceId = context.getResources().getIdentifier("ball_" + ballNum , "drawable", context.getPackageName());
            bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        }
        else {
            bitmap = customBall;
        }


        if(ballNum == 0){
            super.mass = 50;
        }
        else if(ballNum == 6){
            super.mass = 100;
        }
        else if(ballNum == 9){
            super.mass = 208000;
        }
        else{
            super.mass = ballNum * 100;
        }


        sizes[ballNum] = new BallSize((float) bitmap.getWidth() / 5, (float) bitmap.getHeight() / 5, bitmap);
        this.ballImage = Bitmap.createScaledBitmap(bitmap, (int) sizes[ballNum].width, (int) sizes[ballNum].height, false);

        super.radius = this.getRadius();
        super.x = x;
        super.y = y;
        super.vx = vx;
        super.vy = vy;
        super.angle = angle;
        this.checkGameOver = checkGameOver;
        this.gameOverTimer = 0;
        this.applyPhysics = applyPhysics;
    }

    /**
     * Draws the ball on the canvas.
     * This method adjusts the size of the ball's image based on its original size and the predefined sizes.
     * It also rotates the ball based on its angle and then draws it on the canvas.
     *
     * @param canvas The canvas on which the ball will be drawn.
     */
    public void draw(Canvas canvas) {


        if (ballImage.getWidth() < sizes[ballNum].width) {
            float previousRadius = getRadius();
            ballImage = Bitmap.createScaledBitmap(
                    sizes[ballNum].image,
                    (int) (ballImage.getWidth() + sizes[ballNum].width / SIZE_MULTIPLIER),
                    (int) (ballImage.getHeight() + sizes[ballNum].height / SIZE_MULTIPLIER),
                    false
            );
            this.radius = getRadius();
            this.x -= getRadius() - previousRadius;
        }
        if (ballImage.getWidth() > sizes[ballNum].width) {
            float previousRadius = getRadius();
            ballImage = Bitmap.createScaledBitmap(
                    sizes[ballNum].image,
                    (int) sizes[ballNum].width,
                    (int) sizes[ballNum].height,
                    false
            );
            this.radius = getRadius();
            this.x -= getRadius() - previousRadius;
        }


        rotator.postRotate(angle, this.getRadius(), ballImage.getHeight() / 2.0f);
        rotator.postTranslate(super.x, super.y);
        canvas.drawBitmap(ballImage, rotator, null);
        rotator.reset();

    }

    public float getRadius() {
        return ballImage.getWidth() / 2.0f;
    }
    public boolean sameType(Ball other){
        return this.ballNum == other.ballNum;
    }
    public int getBallNum(){
        return ballNum;
    }

    @NonNull
    @Override
    public String toString() {
        return "Ball{" +
                "ballNum=" + ballNum +
                ", radius=" + getRadius() +
                ", x=" + super.x +
                ", y=" + super.y +
                ", vx=" + super.vx +
                ", vy=" + super.vy +
                ", mass=" + super.mass +
                ", bitmap=" + ballImage.toString() +
                '}';
    }



}
