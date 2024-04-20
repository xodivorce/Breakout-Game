import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutGame extends JPanel implements KeyListener, ActionListener {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_DIAMETER = 20;
    private static final int BRICK_WIDTH = 70;
    private static final int BRICK_HEIGHT = 30;
    private static final int PADDLE_SPEED = 10;
    private static final int BALL_SPEED = 5;
    private static final int NUM_BRICKS = 105;
    private static final int BRICK_ROWS = 6;
    private static final int BRICK_COLS = 9;

    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private int ballX = WIDTH / 2 - BALL_DIAMETER / 2;
    private int ballY = HEIGHT - PADDLE_HEIGHT - BALL_DIAMETER;
    private int ballDeltaX = BALL_SPEED;
    private int ballDeltaY = -BALL_SPEED;
    private int score = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private String playerName = "";

    private boolean[] keysPressed = new boolean[KeyEvent.KEY_LAST];

    private Rectangle paddle = new Rectangle(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
    private Rectangle ball = new Rectangle(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);
    private Rectangle[] bricks = new Rectangle[NUM_BRICKS];

    private JButton restartButton;
    private JButton quitButton;
    private JButton startButton;
    private JLabel scoreLabel;
    private JTextField nameTextField;

    public BreakoutGame() {
        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        initializeBricks();

        startButton = new JButton("Start");
        startButton.addActionListener(this);
        add(startButton);

        restartButton = new JButton("Restart");
        restartButton.addActionListener(this);
        add(restartButton);

        quitButton = new JButton("Quit");
        quitButton.addActionListener(this);
        add(quitButton);

        scoreLabel = new JLabel("Your Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel);

        nameTextField = new JTextField("Enter your name");
        nameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameTextField.getText().equals("Enter your name")) {
                    nameTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameTextField.getText().isEmpty()) {
                    nameTextField.setText("Enter your name");
                }
            }
        });
        nameTextField.addActionListener(this);
        add(nameTextField);
    }

    private void initializeBricks() {
        int currentX = 0;
        int currentY = 50;

        for (int i = 0; i < NUM_BRICKS; i++) {
            bricks[i] = new Rectangle(currentX, currentY, BRICK_WIDTH, BRICK_HEIGHT);
            currentX += BRICK_WIDTH;
            if (currentX >= WIDTH) {
                currentX = 0;
                currentY += BRICK_HEIGHT;
            }
        }
    }

    private void update() {
        if (!gameOver && gameStarted) {
            movePaddle();
            moveBall();
            checkCollisions();
            checkGameOver();
        }
    }

    private void movePaddle() {
        if (keysPressed[KeyEvent.VK_LEFT] && paddleX > 0) {
            paddleX -= PADDLE_SPEED;
        }
        if (keysPressed[KeyEvent.VK_RIGHT] && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += PADDLE_SPEED;
        }
        paddle.setLocation(paddleX, HEIGHT - PADDLE_HEIGHT);
    }

    private void moveBall() {
        ball.x += ballDeltaX;
        ball.y += ballDeltaY;

        if (ball.x <= 0 || ball.x >= WIDTH - BALL_DIAMETER) {
            ballDeltaX *= -1;
        }
        if (ball.y <= 0) {
            ballDeltaY *= -1;
        }
    }

    private void checkCollisions() {
        if (paddle.intersects(ball)) {
            ballDeltaY *= -1;
        }
        for (Rectangle brick : bricks) {
            if (brick != null && brick.intersects(ball)) {
                brick.setLocation(0, 0);
                score += 5;
                ballDeltaY *= -1;
            }
        }
        scoreLabel.setText("Your Score: " + score);
    }

    private void checkGameOver() {
        if (ball.y >= HEIGHT - BALL_DIAMETER) {
            gameOver = true;
            restartButton.setEnabled(true);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.YELLOW);
        g.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillOval(ball.x, ball.y, BALL_DIAMETER, BALL_DIAMETER);

        g.setColor(Color.RED);
        for (Rectangle brick : bricks) {
            if (brick != null) {
                g.drawRect(brick.x, brick.y, BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        if (gameOver) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! " + playerName + "'s Score: " + score, WIDTH / 2 - 150, HEIGHT / 2);
        }

        g.setColor(Color.WHITE);
        g.drawString("Player: " + playerName, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == restartButton) {
            restartGame();
        } else if (e.getSource() == quitButton) {
            System.exit(0);
        } else if (e.getSource() == startButton || e.getSource() == nameTextField) {
            startGame();
        }
    }

    public void startGame() {
        playerName = nameTextField.getText();
        gameStarted = true;
        startButton.setEnabled(false);
        nameTextField.setEditable(false);
        nameTextField.setFocusable(false);
        requestFocus();
    }

    public void restartGame() {
        score = 0;
        gameOver = false;
        ball.x = paddleX + PADDLE_WIDTH / 2 - BALL_DIAMETER / 2;
        ball.y = HEIGHT - PADDLE_HEIGHT - BALL_DIAMETER;
        restartButton.setEnabled(false);
        initializeBricks();
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Game");
        BreakoutGame game = new BreakoutGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(1000 / 60, e -> {
            game.update();
            game.repaint();
        });
        timer.start();
    }
}
