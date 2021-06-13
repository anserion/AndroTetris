package com.example.androtetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    RecordDataHelper recordDBconnector;

    final int[] score_rules={0,100,300,700,1500};

    final static Point[][] tetramino_base = {
            {new Point(-1, 0), new Point(-2, 0), new Point(0, 0), new Point(1, 0)},
            {new Point(0, -1), new Point(-1, -1), new Point(-1, 0), new Point(0, 0)},
            {new Point(-1, 0), new Point(-1, 1), new Point(0, 0), new Point(0, -1)},
            {new Point(0, 0), new Point(-1, 0), new Point(0, 1), new Point(-1, -1)},
            {new Point(0, 0), new Point(0, -1), new Point(0, 1), new Point(-1, -1)},
            {new Point(0, 0), new Point(0, -1), new Point(0, 1), new Point(-1, 1)},
            {new Point(0, 0), new Point(0, -1), new Point(0, 1), new Point(-1, 0)}
    };

    final static int box_width = 10, box_height = 16;
    static int[][] tetrisBox = new int[box_height][box_width];
    static Point[] tetramino = new Point[4];
    static Point[] tetramino_tmp = new Point[4];

    static int current_color;
    static int next_color;

    static int current_tetramino;
    static int next_tetramino;

    static int brick_width, brick_height;

    static int speed_cnt;
    static int speed_cnt_inc;
    static int speed_cnt_limit;

    static int canvasHeight;
    static int canvasWidth;

    static boolean touch_now;
    static float touch_x;
    static float touch_y;

    static int lines_total;
    static int score;
    static int record_score;

    static int bg_idx;

    static String tetris_lbl;
    static String score_lbl;
    static String record_lbl;
    static String lines_lbl;
    static String speed_lbl;

    static Bitmap[] bg_bitmap=new Bitmap[10];

    //==================================================================================
    public int RndColor() {
        return Color.argb(
                240,
                (int) (Math.random() * 150 + 100),
                (int) (Math.random() * 150 + 100),
                (int) (Math.random() * 150 + 100)
        );
    }

    //check single tetramino brick for side-touch or another bricks inside tetris box
    public boolean checkBrick(int brickX, int brickY) {
        if ((brickX<0)||(brickX>box_width-1)) return false;
        if ((brickY>box_height-1)||(tetrisBox[brickY][brickX] != -1)) return false;
        return true;
    }

    public boolean tetraminoShiftDown() {
        for(int i=0;i<4;i++) {tetramino_tmp[i].x=tetramino[i].x; tetramino_tmp[i].y=tetramino[i].y;}
        for(int i=0;i<4;i++){
            tetramino[i].y=tetramino[i].y+1;
            if (!checkBrick(tetramino[i].x,tetramino[i].y)) {
                for (int k = 0; k < 4; k++) {
                    tetramino[k].x = tetramino_tmp[k].x;
                    tetramino[k].y = tetramino_tmp[k].y;
                    tetrisBox[tetramino_tmp[k].y][tetramino_tmp[k].x] = current_color;
                }
                return false;
            }
        }
        return true;
    }

    public void tetraminoShiftRight() {
        for(int i=0;i<4;i++) {tetramino_tmp[i].x=tetramino[i].x; tetramino_tmp[i].y=tetramino[i].y;}
        for(int i=0;i<4;i++){
            tetramino[i].x=tetramino[i].x+1;
            if (!checkBrick(tetramino[i].x,tetramino[i].y)) {
                for (int k = 0; k < 4; k++) {
                    tetramino[k].x = tetramino_tmp[k].x;
                    tetramino[k].y = tetramino_tmp[k].y;
                }
                return;
            }
        }
    }

    public void tetraminoShiftLeft() {
        for(int i=0;i<4;i++) {tetramino_tmp[i].x=tetramino[i].x; tetramino_tmp[i].y=tetramino[i].y;}
        for(int i=0;i<4;i++){
            tetramino[i].x=tetramino[i].x-1;
            if (!checkBrick(tetramino[i].x,tetramino[i].y)) {
                for (int k = 0; k < 4; k++) {
                    tetramino[k].x = tetramino_tmp[k].x;
                    tetramino[k].y = tetramino_tmp[k].y;
                }
                return;
            }
        }
    }

    //rotate single tetramino with some checking
    public void tetraminoRotate() {
        for(int i=0;i<4;i++) {tetramino_tmp[i].x=tetramino[i].x; tetramino_tmp[i].y=tetramino[i].y;}
        int center_x=tetramino[0].x;
        int center_y=tetramino[0].y;
        for(int i=0;i<4;i++){
            int x=tetramino[i].y-center_y;
            int y=tetramino[i].x-center_x;
            tetramino[i].x=center_x-x;
            tetramino[i].y=center_y+y;
            if (!checkBrick(tetramino[i].x,tetramino[i].y)) {
                for (int k = 0; k < 4; k++) {
                    tetramino[k].x = tetramino_tmp[k].x;
                    tetramino[k].y = tetramino_tmp[k].y;
                }
                return;
            }
        }
    }

    public int filledLinesSearch() {
        int lines_fit=0;
        for(int i=box_height-1;i>=0;i--) {
            int tmp_cnt=0;
            for(int j=0;j<box_width;j++){
                if (tetrisBox[i][j] != -1) tmp_cnt=tmp_cnt+1;
                tetrisBox[i+lines_fit][j]=tetrisBox[i][j];
            }
            if (tmp_cnt==box_width) lines_fit=lines_fit+1;
        }
        return lines_fit;
    }

    //Draw single flying tetramino
    public void drawTetramino(Canvas canvas, int sx, int sy){
        RectF brick=new RectF();
        Paint paint=new Paint();
        paint.setColor(current_color);
        for(int i=0;i<4;i++){
            brick.left=tetramino[i].x*brick_width+sx+1;
            brick.top=tetramino[i].y*brick_height+sy+1;
            brick.right=brick.left+brick_width-2;
            brick.bottom=brick.top+brick_height-2;
            canvas.drawRect(brick, paint);
        }
    }

    //Draw tetramino box
    public void drawBox(Canvas canvas, int sx, int sy) {
        RectF gridBrick=new RectF();
        RectF tetraBrick=new RectF();

        Paint gridPaint=new Paint();
        gridPaint.setColor(Color.rgb(100,100,100));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        Paint tetraPaint=new Paint();
        tetraPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tetraPaint.setStrokeWidth(1);

        for(int i=0;i<box_height;i++)
            for (int j = 0; j < box_width; j++) {
                gridBrick.left = j * brick_width + sx+1;
                gridBrick.top = i * brick_height + sy+1;
                gridBrick.right = gridBrick.left + brick_width-2;
                gridBrick.bottom = gridBrick.top + brick_height-2;
                canvas.drawRect(gridBrick, gridPaint);

                int tetraColor = tetrisBox[i][j];
                if (tetraColor != -1) {
                    tetraBrick.left = gridBrick.left+2;
                    tetraBrick.top = gridBrick.top+2;
                    tetraBrick.right = tetraBrick.left + brick_width - 3;
                    tetraBrick.bottom = tetraBrick.top + brick_height - 3;
                    tetraPaint.setColor(tetraColor);
                    canvas.drawRect(tetraBrick, tetraPaint);
                }
            }
    }

    //draw some amazing control widgets
    public void drawControls(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawLine(0,canvasHeight*9/10,canvasWidth,canvasHeight*9/10,paint);
        canvas.drawLine(canvasWidth/3,canvasHeight/10,canvasWidth/3,canvasHeight*9/10,paint);
        canvas.drawLine(canvasWidth*2/3,canvasHeight/10,canvasWidth*2/3,canvasHeight*9/10,paint);
    }

    //draw some text information
    public void drawLabels(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(Color.WHITE);
        int sy=canvasHeight/20;
        paint.setTextSize(sy);
        canvas.drawText(tetris_lbl,(canvasWidth-paint.measureText(tetris_lbl))/2,sy,paint);
        paint.setTextSize(canvasHeight/20);

        int sx=canvasWidth/50;
        int dy=canvasHeight/30;
        paint.setTextSize(dy);
        canvas.drawText(score_lbl+" "+score,sx,3*sy+2*dy,paint);
        canvas.drawText(lines_lbl+" "+lines_total,sx,3*sy+4*dy,paint);
        canvas.drawText(speed_lbl+" "+speed_cnt_inc,sx,3*sy+6*dy,paint);
        canvas.drawText(record_lbl+" "+record_score,sx,3*sy+8*dy,paint);
    }
    //==================================================================================
    public GameSurfaceView(Context context) {
        super(context);

        recordDBconnector = new RecordDataHelper(context);
        record_score=recordDBconnector.select();

        score=0;
        lines_total=0;
        bg_idx=(int) (Math.random()*10);

        for(int i=0;i<box_height;i++)
            for(int j=0;j<box_width;j++)
                tetrisBox[i][j] = -1;

        current_tetramino=(int)(Math.random()*7);
        next_tetramino=(int)(Math.random()*7);
        current_color=RndColor();
        next_color=RndColor();

        for(int i=0;i<4;i++) {
            tetramino_tmp[i]=new Point();
            tetramino[i]=new Point();
            tetramino[i].x=tetramino_base[current_tetramino][i].x+box_width/2;
            tetramino[i].y=tetramino_base[current_tetramino][i].y+1;
        }

        tetris_lbl =  getResources().getString(R.string.tetris_lbl);
        score_lbl =  getResources().getString(R.string.score_lbl);
        speed_lbl =  getResources().getString(R.string.speed_lbl);
        lines_lbl =  getResources().getString(R.string.lines_lbl);
        record_lbl =  getResources().getString(R.string.record_lbl);

        bg_bitmap[0]= BitmapFactory.decodeResource(getResources(), R.drawable.bg0);
        bg_bitmap[1]= BitmapFactory.decodeResource(getResources(), R.drawable.bg1);
        bg_bitmap[2]= BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
        bg_bitmap[3]= BitmapFactory.decodeResource(getResources(), R.drawable.bg3);
        bg_bitmap[4]= BitmapFactory.decodeResource(getResources(), R.drawable.bg4);
        bg_bitmap[5]= BitmapFactory.decodeResource(getResources(), R.drawable.bg5);
        bg_bitmap[6]= BitmapFactory.decodeResource(getResources(), R.drawable.bg6);
        bg_bitmap[7]= BitmapFactory.decodeResource(getResources(), R.drawable.bg7);
        bg_bitmap[8]= BitmapFactory.decodeResource(getResources(), R.drawable.bg8);
        bg_bitmap[9]= BitmapFactory.decodeResource(getResources(), R.drawable.bg9);

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();
            getHolder().unlockCanvasAndPost(canvas);
        }
        new Thread((Runnable) this).start(); // <--- multithreading for ITSchool pleasure
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    //===============================================
    // multithreading routine for ITSchool pleasure
    //===============================================
    @Override
    public void run() {
        speed_cnt=0;
        speed_cnt_inc=2000;
        speed_cnt_limit=100000;

        while(true) {
            if (touch_now) {
                if (touch_y < canvasHeight * 9.0 / 10.0) {
                    if (touch_x < canvasWidth / 3.0) tetraminoShiftLeft();
                    if ((touch_x > canvasWidth / 3.0) && (touch_x < canvasWidth * 2.0/ 3.0))
                        tetraminoRotate();
                    if (touch_x > canvasWidth * 2.0 / 3.0) tetraminoShiftRight();
                } else speed_cnt_limit=10000;
            }
            touch_now = false;

            speed_cnt=speed_cnt+speed_cnt_inc;
            if (speed_cnt>speed_cnt_limit) {
                speed_cnt_inc=speed_cnt_inc+5;
                speed_cnt=0;
                if (!tetraminoShiftDown()) {
                    current_tetramino=next_tetramino;
                    current_color=next_color;
                    next_tetramino=(int)(Math.random()*7);
                    next_color=RndColor();

                    for(int i=0;i<4;i++) {
                        tetramino[i]=new Point();
                        tetramino[i].x=tetramino_base[current_tetramino][i].x+box_width/2;
                        tetramino[i].y=tetramino_base[current_tetramino][i].y+1;
                    }
                    speed_cnt_limit=100000;

                    int lines_fit=filledLinesSearch();
                    lines_total=lines_total+lines_fit;
                    score=score+score_rules[lines_fit];
                    if (score>record_score) {
                        record_score=score;
                        recordDBconnector.update(record_score);
                    }
                }

                Paint bitmap_paint=new Paint();
                bitmap_paint.setStyle(Paint.Style.FILL);
                Canvas canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    canvasWidth=canvas.getWidth();
                    canvasHeight=canvas.getHeight();
                    brick_width = canvasWidth / box_width;
                    brick_height = canvasHeight / box_height;
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(bg_bitmap[bg_idx],0,0,bitmap_paint);
                    drawBox(canvas, 1, 1);
                    for(int i=0;i<4;i++) {tetramino_tmp[i].x=tetramino[i].x; tetramino_tmp[i].y=tetramino[i].y;}
                    int tmp=current_color;
                    for(int i=0;i<4;i++) {
                        tetramino[i].x=tetramino_base[next_tetramino][i].x+2;
                        tetramino[i].y=tetramino_base[next_tetramino][i].y+1;
                    }
                    current_color=next_color;
                    drawTetramino(canvas, 1, 1);
                    for(int i=0;i<4;i++) {tetramino[i].x=tetramino_tmp[i].x; tetramino[i].y=tetramino_tmp[i].y;}
                    current_color=tmp;
                    drawTetramino(canvas, 1, 1);
                    drawLabels(canvas);
                    drawControls(canvas);

                    for(int i=0;i<box_width;i++)
                        if (tetrisBox[0][i] != -1) {
                            for(int j=0;j<box_height;j++)
                                for(int k=0;k<box_width;k++)
                                    tetrisBox[j][k] = -1;
                            score=0;
                            lines_total=0;
                            bg_idx=(int) (Math.random()*10);
                            break;
                        }

                    getHolder().unlockCanvasAndPost(canvas);
                }

            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //===============================================
    // end of multithreading routine
    //===============================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touch_now=true;
        touch_x=event.getX();
        touch_y=event.getY();
        return super.onTouchEvent(event);
    }
}
