import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class flappybird extends JPanel implements ActionListener, KeyListener {
    int boardwidth = 360;
    int boardheight = 640;

    //imgs
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardwidth/8;
    int birdY = boardheight/2;
    int birdwidth = 34;
    int birdheight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdwidth;
        int height = birdheight;
        Image img;
        
        Bird(Image img) {
         this.img = img;
        }
    }

    //pipes
    int pipeX = boardwidth;
    int pipeY = 0;
    int pipewidth = 64;
    int pipeheight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int height = pipeheight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placepTimer;
    boolean gameOver = false;
    double score = 0;

    flappybird() {
        setPreferredSize(new Dimension(boardwidth, boardheight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load img
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
 
        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placepTimer = new Timer(1500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placepTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    void placePipes() {
        //(0-1)* ppheight/2 -> (0-256)
        //128
        //0-128-(0-256) --> 1/4 ppheight -> 3/4 ppheight
        int randomPipeY = (int) (pipeY - pipeheight/4 - Math.random()*(pipeheight/2));
        int openSpace = boardheight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeheight + openSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //bg
        g.drawImage(backgroundImg,0,0,boardwidth, boardheight,null);

        //bird
        g.drawImage(birdImg,bird.x,bird.y,bird.width,bird.height, null);

        //pipes
        for(int i=0; i< pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if(gameOver){
            g.drawString("Game Over: "+ String.valueOf((int) score), 10, 25);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 25);
        }
    }

    public void move(){
        //bird
        velocityY +=gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);

        //pipes
        for(int i=0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x +pipe.width){
                pipe.passed = true;
                score += 0.5; //2 pipe passed
            }

            if(collision(bird,pipe)){
                gameOver = true;
            }
        }

        //bird fall
        if(bird.y >boardheight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return  a.x < b.x + b.width &&
                a.x +a.width >b.x  &&
                a.y < b.y + b.height &&
                a.y + a.height >b.y;
    } 

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if(gameOver){
            placepTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;

            if(gameOver){
                //reset
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placepTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}