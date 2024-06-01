package com.example.droppopproject.game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.FirebaseControl;
import com.example.droppopproject.MusicControl;
import com.example.droppopproject.activities.CreateNewBallsActivity;
import com.example.droppopproject.activities.GameActivity;
import com.example.droppopproject.activities.HomeActivity;
import com.example.droppopproject.activities.LoginActivity;
import com.example.droppopproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;


/**
 * Custom view representing a simulation of bouncing balls.
 * This view allows users to interact with the balls by adding them to the screen and moving them horizontally on touch.
 * The physics of the balls are simulated with basic acceleration and collision handling.
 */
public class GameView extends SurfaceView implements Runnable
{

    //TODO: Scoring system and leaderboard of user to today, week, and all time

    public static final double ELASTICITY = 0.8;
    public static final int SPAWN_NEW_BALL_MILLIS = 200;
    private static final float GRAVITY = 3000;
    private static final float DRAG = 0.2F;

    /**
     * Indexes in BOUNCE_PER_BALL are numbered .png balls files
     * - Index 0 represents the elasticity value for ping-pong balls. (ball_0.png)
     * - Index 1 represents the elasticity value for golf balls. (ball_1.png)
     * - Index 2 represents the elasticity value for tennis balls. (ball_2.png)
     * - Index 3 represents the elasticity value for baseballs. (ball_3.png)
     * - Index 4 represents the elasticity value for billiard (pool) balls. (ball_4.png)
     * - Index 5 represents the elasticity value for soccer balls. (ball_5.png)
     * - Index 6 represents the elasticity value for beach balls. (ball_6.png)
     * - Index 7 represents the elasticity value for volleyballs. (ball_7.png)
     * - Index 8 represents the elasticity value for basketballs. (ball_8.png)
     * - Index 9 represents the elasticity value for bowling balls. (ball_9.png)
     */
    private static final float[] BOUNCE_PER_BALL = {0.7F, 0.2F, 0.65F, 0.2F, 0.2F, 0.6F, 0.3F, 0.5F, 0.9F, 0.0F};
    public static final double RESOLVE_OVERLAP_CONSTANT = 0.5;
    public static final float ANGLE_CONSTANT = 0.95F;

    public static final long GAME_OVER_TIMEOUT = 3000; // 3 seconds to game over

    private static final float GAME_OVER_Y = 800;

    private static float FLOOR_Y;

    //TODO: STOP VIBRATING IF BALLS ARE IN STATIC MODE
    //TODO: CHANGE SIZES OF BALLS PNG
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private Thread mGameThread;
    private boolean mRunning;

    private long curTime = 0;
    private static double timeFraction = 0.0;

    public static ArrayList<Ball> mBalls;
    private ArrayList<Bitmap> mCustomBalls;

    private Handler mHandler;

    public TextView mScoreView;
    public FloatingActionButton mHomeButton;
    public FloatingActionButton mRestartButton;

    public int mScore;

    private Canvas mCanvas;
    private Paint mGameOverPainter;
    private Paint mFollowLinePainter;
    private Paint mGroundPainter;
    private int width;
    private int height;
    boolean mIsGameOver;

    private FirebaseAuth firebaseAuth;
    private FirebaseControl firebaseControl;


    private BallsSharedPreferences mBallsSharedPreferences;
    private boolean loadedBallsFromSP;

    private Bitmap mBallOrderBitmap;
    private Bitmap mBackgroundBitmap;

    boolean bitmap1Drawn = false, bitmap2Drawn = false;


    private MusicControl musicControl;

    private void initBallOrderBitmap() {
        mBallOrderBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ball_order), 700, 350, false);
        mBackgroundBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.game_bg), getWidth(), getHeight(), false);
    }



    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }


    /**
     * Called when the size of the view changes.
     * Adds a ball to the top of the screen when the size changes.
     *
     * @param w     Current width of the view
     * @param h     Current height of the view
     * @param oldW  Old width of the view
     * @param oldH  Old height of the view
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        height = h;
        width = w;
        FLOOR_Y = (float) (h * 8) / 10 + 50; //80% of screen


        initBallOrderBitmap();


        if(mBallsSharedPreferences.getEnableCustomBalls()){
            mCustomBalls = mBallsSharedPreferences.getCustomBallsFromSharedPreferences();
        }
        loadedBallsFromSP = mBallsSharedPreferences.getBallsFromSharedPreferences(mBalls, mCustomBalls);



        if(!loadedBallsFromSP){
            addBallToTopOfScreen();
        }
        //addBallToTopOfScreen();

    }

    /**
     * Initializes the view.
     *
     * @param context The context in which the view is created.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        mContext = context;
        mSurfaceHolder = getHolder();

        //mScoreView = findViewById(R.id.ScoreText);

        mBallsSharedPreferences = BallsSharedPreferences.getInstance(mContext);

        mScore = mBallsSharedPreferences.getScore();
        if(mScore == -1){
            mScore = 0;
        }
        //mScoreView.setText(String.valueOf(mScore));
        mBallsSharedPreferences.setScore(mScore);

        mBalls = new ArrayList<>();
        mCustomBalls = CreateNewBallsActivity.getCreatedCustomBalls();


        mHandler = new Handler(Looper.getMainLooper());

        mCanvas = new Canvas();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseControl = FirebaseControl.getInstance();


        mGameOverPainter = new Paint();
        mGameOverPainter.setColor(Color.RED);
        mGameOverPainter.setStrokeWidth(20);

        mFollowLinePainter = new Paint();
        mFollowLinePainter.setColor(ContextCompat.getColor(mContext, R.color.blue));
        mFollowLinePainter.setStrokeWidth(10);

        mGroundPainter = new Paint();
        mGroundPainter.setColor(ContextCompat.getColor(mContext, R.color.brown));
        mGroundPainter.setStrokeWidth(10);

        loadedBallsFromSP = false;

        mIsGameOver = false;






        this.resume();

    }



    /**
     * Main simulation loop for updating and rendering the balls.
     */
    @Override
    public void run() {



        while (mRunning) {

            updateTime();
            applyConstForces();
            moveEnt();


            if (!mSurfaceHolder.getSurface().isValid()) {
                continue;
            }

            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.save();



            mCanvas.drawColor(Color.WHITE);


            drawScreenInLoop();



            mCanvas.restore();

            // Unlock and post the canvas
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    /**
     * Draws the screen including balls, game over line, and ground.
     */
    private void drawScreenInLoop() {

        // Load the bitmap from the drawable resource

        mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, null);


        Ball b;
        // Iterate through the list of balls
        for (int i = 0; i < mBalls.size(); i++) {
            b = mBalls.get(i);
            // Draw the ball on the canvas
            b.draw(mCanvas);

            // Check if the ball has reached the game over line
            if (!b.checkGameOver && b.getCenter().y + b.radius >= GAME_OVER_Y) {
                b.checkGameOver = true;
            }

            // If physics are not applied to the ball, draw a line from the ball's center to the floor
            if (!b.applyPhysics) {
                mCanvas.drawLine(b.getCenter().x, b.getCenter().y + b.radius, b.getCenter().x, FLOOR_Y, mFollowLinePainter);
            }
        }


        // Draw the game over line
        mCanvas.drawLine(0, GAME_OVER_Y, width, GAME_OVER_Y, mGameOverPainter);
        // Draw the ground
        mCanvas.drawRect(0, FLOOR_Y, width, height, mGroundPainter);
        mCanvas.drawBitmap(mBallOrderBitmap, (float) width / 2 - 350, FLOOR_Y + 30, null);




    }



    /**
     * Handles touch events on the view.
     *
     * @param event The motion event
     * @return True if the event was handled, false otherwise
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();

        Ball lastBall = mBalls.stream()
                .filter(b -> !b.applyPhysics)
                .findFirst()
                .orElse(null);

        if(lastBall == null){
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float minX = 0;
                float maxX = width - lastBall.getDiameter();
                lastBall.x =  Math.max(minX, Math.min(touchX, maxX));
                break;
            case MotionEvent.ACTION_UP:
                lastBall.applyPhysics = true;
                lastBall.vy = 0.001f;  //close to 0 but not 0
                mHandler.postDelayed(this::addBallToTopOfScreen, SPAWN_NEW_BALL_MILLIS);
                break;
        }
        return true;
    }




    /**
     * Adds a ball to the top of the screen without applying physics.
     */
    private void addBallToTopOfScreen() {
        Ball ball;
        ball = new Ball(mContext, width, mCustomBalls);
        ball.applyPhysics = false;
        mBalls.add(ball);
        mBallsSharedPreferences.saveBallsToSharedPreferences(mBalls);
        mBallsSharedPreferences.setScore(mScore);
    }


    /**
     * Handles collision between two balls of the same type.
     * The two balls are removed from the list, and a new ball is added at the collision point.
     * The new ball inherits the velocity of the faster moving of the two collided balls.
     *
     * @param s           The first ball involved in the collision.
     * @param t           The second ball involved in the collision.
     * @param sCenter     The center point of the first ball.
     * @param tCenter     The center point of the second ball.
     * @param distBetween The distance between the centers of the balls.
     */
    private void sameBallsCollision(Ball s, Ball t, PointF sCenter, PointF tCenter, double distBetween) {



        // Determine the type of the colliding balls
        int typeOfBalls = s.getBallNum();

        // Calculate the direction of the collision
        float dx = tCenter.x - sCenter.x;
        float dy = tCenter.y - sCenter.y;
        float unitX = (float) (dx / distBetween);
        float unitY = (float) (dy / distBetween);

        // Calculate the collision point
        float x = sCenter.x + s.getRadius() * unitX;
        float y = sCenter.y + s.getRadius() * unitY;

        // Remove the collided balls from the list
        mBalls.remove(s);
        mBalls.remove(t);

        // Increase the score
        mScore += (typeOfBalls + 2) * 2;
        mScoreView.setText(String.valueOf(mScore));

        // Determine the faster moving ball
        float vx = Math.max(s.vx, t.vx);
        float vy = vx == s.vx ? s.vy : t.vy;

        // Create a new ball at the collision point with inherited velocity
        Ball ball = new Ball(mContext, x, y, vx, vy, ++typeOfBalls, mCustomBalls);
        ball.applyPhysics = true;
        mBalls.add(ball); // Add the new ball to the list of balls

        mBallsSharedPreferences.saveBallsToSharedPreferences(mBalls);
        mBallsSharedPreferences.setScore(mScore);



    }


    //Physics----------------------------------------------------------------

    /**
     * Updates the time variables for the simulation.
     * Calculates the time passed since the last update and updates the time fraction accordingly.
     */
    private void updateTime()
    {
        long lastTime = curTime;
        curTime = System.currentTimeMillis();
        long timePassed = (curTime - lastTime);
        timeFraction = (float) (timePassed / 1000.0); //calculating delta t
    }


    /**
     * Calculates the sum of forces acting on the balls and updates their velocities.
     * Also applies drag to simulate friction.
     */
    private void applyConstForces()
    {

        for (int i = 0; i < mBalls.size(); i++)  {
            Ball b = mBalls.get(i);

            if(b.applyPhysics && b.vy != 0){ //don't activate physics for the last ball if he's in drag mode
                float vy = (float) (b.vy + (GRAVITY * timeFraction)); // v = v0 + at
                b.updateVelocity(b.vx, vy);
                b.applyDrag((float) (1.0 - (timeFraction * (DRAG /2.5))));//RESPONSIBLE FOR SPEED AND FRICTION
            }
        }

    }


    /**
     * Moves the entities (balls) based on their velocities and elapsed time.
     * Checks for wall collisions and updates ball positions accordingly.
     * Also checks for collisions between balls and resolves them.
     */
    private void moveEnt()
    {

        for (int i = 0; i < mBalls.size(); i++) {

            Ball b = mBalls.get(i);

            if(b.applyPhysics){ //don't activate physics for the last ball if he's in drag mode
                float oldX = b.x, oldY = b.y;
                float newX = (float) (oldX + (b.vx * timeFraction)); // x = x0 + vt
                float newY = (float) (oldY + (b.vy * timeFraction));
                b.updatePos(newX, newY);

                b.angle += (float) (b.vx * ANGLE_CONSTANT * timeFraction);

            }

            checkBoundaries(b);
        }

//        for (int i = 0; i < mBalls.size(); i++) {
//            Ball b = mBalls.get(i);
//
//            b.vy = Math.abs(b.vy) < (mBalls.size() - 5) * 5 ? 0 : b.vy;
//            b.vx = Math.abs(b.vx) < (mBalls.size() - 5) * 5 ? 0 : b.vx;
//
//            b.angle += b.vx * ANGLE_CONSTANT * timeFraction;
//        }



        checkCollisions();

    }







    /**
     * Checks for collisions of balls with the walls of the view.
     * Adjusts ball positions and velocities accordingly upon collision with the walls.
     *
     * @param s The ball to check for wall collisions.
     */
    private void checkBoundaries(Ball s) {


        float maxY = FLOOR_Y - s.getDiameter();
        float maxX = width - s.getDiameter();

        if (s.y > maxY) { // Ground
            s.updatePos(s.x, maxY);
            float a = lowerRound(s.vy * -BOUNCE_PER_BALL[s.getBallNum()]);
            s.updateVelocity(s.vx, a);
            Log.d("SSS", String.valueOf(s.vy));
        }


        if (s.x > maxX) { // Right Wall
            s.updatePos(maxX, s.y);
            float a = lowerRound(s.vx * -DRAG);
            s.updateVelocity(a, s.vy); // Adjust the bounce factor as needed
        }


        if (s.x < 0) { // Left Wall
            s.updatePos(0, s.y);
            float a = lowerRound(s.vx * -DRAG);
            s.updateVelocity(a, s.vy); // Adjust the bounce factor as needed
        }


        checkIfGameOver(s);


    }

    /**
     * Checks if the game is over.
     * It is game over if a ball is crossing GAME_OVER_Y for at least GAME_OVER_TIMEOUT Milliseconds
     *
     * @param s The ball object to check for game over condition.
     */
    private void checkIfGameOver(Ball s) {
        //Checking if game over
        if (!mIsGameOver && s.checkGameOver && Math.abs(s.getCenter().y - s.radius) <= GAME_OVER_Y) {

            if (s.gameOverTimer == 0) {
                mBallsSharedPreferences.saveBallsToSharedPreferences(mBalls);
                mBallsSharedPreferences.setScore(mScore);

                s.gameOverTimer = System.currentTimeMillis();
            }
            long currentTime = System.currentTimeMillis();

            // Check if the condition has been true for GAME_OVER_TIMEOUT milliseconds
            if (currentTime - s.gameOverTimer >= GAME_OVER_TIMEOUT) {
                mIsGameOver = true;
                showGameOverDialog();
            }

        }
        else {
            s.gameOverTimer = 0;
        }
    }



    /**
     * Update scores of user after game over at firestore
     */
    private void updateUsersNewScore() {

        firebaseControl.getCurrentUser(currentUser -> {
            if (currentUser != null) {
                currentUser.addNewScore(mScore);
                firebaseControl.saveUser(currentUser);
            }
        });
        mBallsSharedPreferences.resetScore();
    }




    /**
     * Displays the game over dialog.
     * This method should be called on the main UI thread.
     */
    public void showGameOverDialog() {

        MusicControl.playMainMusic(R.raw.game_over_sound_effect, getContext(), false);

        updateUsersNewScore();



        mHandler.post(() -> {

            mBallsSharedPreferences.resetSharedPreferences(false);

            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.game_over_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            dialog.findViewById(R.id.restart_button).setOnClickListener(v -> {
                mBallsSharedPreferences.resetSharedPreferences(false);
                Intent intent = new Intent(mContext, GameActivity.class);
                getContext().startActivity(intent);
            });

            dialog.findViewById(R.id.exit_to_menu_button).setOnClickListener(v -> {
                //SettingsFragment.enableSwitch = true;
                mBallsSharedPreferences.resetScore();
                Intent intent = new Intent(mContext, HomeActivity.class);
                getContext().startActivity(intent);
            });


            dialog.findViewById(R.id.log_out_button).setOnClickListener(v -> {
                mBallsSharedPreferences.resetScore();
                firebaseAuth.signOut();
                Intent intent = new Intent(mContext, LoginActivity.class);
                getContext().startActivity(intent);
            });

            dialog.show();
            this.pause();
        });


    }


    /**
     * Checks for collisions between balls and resolves them.
     */
    private void checkCollisions() {


        for (int i = 0; i < mBalls.size(); i++) {
            Ball s = mBalls.get(i);

            PointF sCenter = s.getCenter();

            for (int j = i + 1; j < mBalls.size(); j++) {
                Ball t = mBalls.get(j);

                PointF tCenter = t.getCenter();
                double distBetween = Math.hypot(tCenter.x - sCenter.x, tCenter.y - sCenter.y);
                double sumOfRadii = s.getRadius() + t.getRadius();

                if (distBetween < sumOfRadii) {

                    if(s.sameType(t) && s.applyPhysics && t.applyPhysics){
                        MusicControl.playBackgroundSound(R.raw.pop_sound_effect, getContext(), false);
                        sameBallsCollision(s,t,sCenter,tCenter, distBetween);
                    }
                    else {
                        resolveOverlap(s, t, distBetween, sumOfRadii);
                    }

                }

            }
        }
    }


    /**
     * Resolves overlap between two colliding balls by moving them apart along the collision normal.
     * If the balls are on top of each other, they are moved randomly.
     *
     * @param s            The first ball involved in the collision.
     * @param t            The second ball involved in the collision.
     * @param distBetween  The distance between the centers of the balls.
     * @param sumOfRadii   The sum of the radii of the two balls.
     */
    private void resolveOverlap(Ball s, Ball t, double distBetween, double sumOfRadii) {
        // Calculate the overlap distance
        double overlap = sumOfRadii - distBetween;

        // Move the balls away from each other along the collision normal
        if (distBetween > 0) {
            double dx = (t.getCenter().x - s.getCenter().x) / distBetween;
            double dy = (t.getCenter().y - s.getCenter().y) / distBetween;

            double moveX = overlap * RESOLVE_OVERLAP_CONSTANT * dx; // Slightly move more to separate the balls
            double moveY = overlap * RESOLVE_OVERLAP_CONSTANT * dy; // Slightly move more to separate the balls

            // Move ball s
            s.x -= moveX;
            s.y -= moveY;

            // Move ball t
            t.x += moveX;
            t.y += moveY;
        } else {
            // If balls are on top of each other, move them randomly
            s.x += overlap * RESOLVE_OVERLAP_CONSTANT; // Slightly move more to separate the balls
            s.y += overlap * RESOLVE_OVERLAP_CONSTANT; // Slightly move more to separate the balls

            t.x -= overlap * RESOLVE_OVERLAP_CONSTANT; // Slightly move more to separate the balls
            t.y -= overlap * RESOLVE_OVERLAP_CONSTANT; // Slightly move more to separate the balls
        }

        collide(s, t);


    }

    /**
     * Handles collision between two balls by updating their velocities according to the laws of physics.
     *
     * @param ball1 The first ball involved in the collision.
     * @param ball2 The second ball involved in the collision.
     */
    private void collide(Ball ball1, Ball ball2) {

        // Relative velocity
        double dvx = ball2.vx - ball1.vx;
        double dvy = ball2.vy - ball1.vy;

        // Relative position
        double dx = ball2.getCenter().x - ball1.getCenter().x;
        double dy = ball2.getCenter().y - ball1.getCenter().y;

        // Distance between the centers of the balls
        double distance = Math.hypot(dx,dy);

        // Normalized collision vector
        double collisionNormalX = dx / distance;
        double collisionNormalY = dy / distance;

        // Relative velocity in the direction of the collision
        double relativeVelocity = dvx * collisionNormalX + dvy * collisionNormalY;

        // If the balls are moving away from each other, do nothing
        if (relativeVelocity > 0) {
            return;
        }

        // Coefficient of restitution (elasticity)

        // Impulse calculation
        double impulse = (2.0 * relativeVelocity) / (ball1.mass + ball2.mass);

        double boostMultiplier = 1.2; // Adjust as needed

        // Update velocities with boost
        ball1.vx += lowerRound((float) (impulse * ball2.mass * collisionNormalX * ELASTICITY * boostMultiplier));

        ball1.vy += lowerRound((float) (impulse * ball2.mass * collisionNormalY * ELASTICITY * boostMultiplier));

        ball2.vx -= lowerRound((float) (impulse * ball1.mass * collisionNormalX * ELASTICITY * boostMultiplier));
        ball2.vy -= lowerRound((float) (impulse * ball1.mass * collisionNormalY * ELASTICITY * boostMultiplier));



        //Rotating section ----------------------------------------------------------------------------------------------------
//        if(ball1.y == ball2.y) {return;}
//
//        if((ball1.vx <= 1.5 || ball1.vy <= 1.5) || (ball2.vx <= 1.5 || ball1.vy <= 1.5)) {return;}
//
//        // Perpendicular collision vector (tangent)
//        double collisionTangentX = -dy / distance;
//        double collisionTangentY = dx / distance;
//
//        // Relative velocity in the direction of the collision tangent
//        double relativeTangentVelocity = dvx * collisionTangentX + dvy * collisionTangentY;
//
//        // Angular momentum
//        double angularMomentum = (dx * dvy - dy * dvx) * ball1.mass;
//
//        // Moment of inertia
//        double momentOfInertia = 0.5 * ball1.mass * ball1.radius * ball1.radius;
//
//        // Angular acceleration
//        double angularAcceleration = angularMomentum / momentOfInertia;
//
//        // Update angular velocities
//        ball1.angle += angularAcceleration * Math.signum(relativeTangentVelocity);
//        ball2.angle -= angularAcceleration * Math.signum(relativeTangentVelocity);
//
//        // Calculate the damping factor based on the current angular velocity
//        double dampingFactor = Math.pow(1.5, timeFraction);
//
//        // Apply rotational damping
//        ball1.angle *= dampingFactor;
//        ball2.angle *= dampingFactor;


    }

    //Threading-------------------------------------------------------------------------------------------------

    /**
     * Pauses the game loop by setting the running flag to false and waiting for the game thread to finish.
     * This method is typically called when the game is paused or stopped.
     */
    public void pause() {
        mRunning = false;
        try {
            mGameThread.join();
        } catch (InterruptedException ignored) {}
    }

    /**
     * Resumes the game loop by setting the running flag to true and starting a new game thread.
     * This method is typically called when the game is resumed after being paused.
     */
    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }




    //    /**
//     * Applies constant forces to the balls in the simulation.
//     * Calculates the total acceleration due to constant forces and applies it to the balls.
//     */
//    private void applyConstForces()
//    {
//        float xAccel = 0, yAccel = 0;
//        // Find the total acceleration of all const forces.
//        for (int i = 0; i < constForces.size(); i++) {
//            xAccel += constForces.get(i).ax();
//            yAccel += constForces.get(i).ay();
//        }
//
//
//
////        for (int i = 0; i < mBalls.size(); i++)  {
////            Ball b = mBalls.get(i);
////            if(b.applyPhysics) //If the ball is in drag mode, don't activate physics (the last ball)
////            {
////                b.addAccel(new Acceleration(xAccel, yAccel));
////            }
////        }
//
//    }



    public float lowerRound(float val) {
        if (val < 0) {
            // If the value is negative, round up using Math.ceil
            float roundedUp = (float) Math.ceil(val);

            // If the rounded value is -1, set it to 0
            if (roundedUp <= -1 && roundedUp >= -5) {
                return 0;
            } else {
                return roundedUp;
            }
        } else {
            // If the value is non-negative, round down using Math.floor
            return (float) Math.floor(val);
        }
    }


}

