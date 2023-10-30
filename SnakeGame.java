import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.List;

public class SnakeGame extends JPanel implements ActionListener {

   private final int width;
   private final int height;
   private final int cellSize;
   private static final int FRAME_RATE = 20;
   private boolean gameStarted = false;
   private boolean gameOver = false;
   private Direction direction = Direction.RIGHT;
   private Direction newDirection = Direction.RIGHT;
   private final List<GamePoint> snake = new ArrayList<>();

   public SnakeGame(final int width, final int height) {
      super();
      this.width = width;
      this.height = height;
      this.cellSize = width / (FRAME_RATE * 2);
      setPreferredSize(new Dimension(width, height));
      setBackground(Color.BLACK);
   }

   public void startGame() {
      resetGameData();
      setFocusable(true);
      setFocusTraversalKeysEnabled(false);
      requestFocusInWindow();
      addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(final KeyEvent e) {
            handleKeyEvent(e.getKeyCode());
         }
      });
      new Timer(1000 / FRAME_RATE, this).start();
   }

   private void handleKeyEvent(final int keyCode) {
      if (!gameStarted) {
         if (keyCode == KeyEvent.VK_SPACE) {
            gameStarted = true;
         }
      } else if (!gameOver) {
           switch (keyCode) {
              case KeyEvent.VK_UP :
                 if (direction != Direction.DOWN) {
                    newDirection = Direction.UP;
                 }
                 break;
              case KeyEvent.VK_DOWN :
                 if (direction != Direction.UP) {
                    newDirection = Direction.DOWN;
                 }
                 break;
              case KeyEvent.VK_RIGHT :
                 if (direction != Direction.LEFT) {
                    newDirection = Direction.RIGHT;
                 }
                 break;
              case KeyEvent.VK_LEFT :
                 if (direction != Direction.RIGHT) {
                    newDirection = Direction.LEFT;
                 }
                 break;
           }
      }
   }

   private void resetGameData() {
      snake.clear();
      snake.add(new GamePoint(width / 2, height / 2));
   }

   @Override
   protected void paintComponent(final Graphics graphics) {
      super.paintComponent(graphics);

      if (!gameStarted) {
         graphics.setColor(Color.WHITE);
         graphics.setFont(graphics.getFont().deriveFont(30F));
         int currentHeight = height / 3;
         final var graphics2D = (Graphics2D) graphics;
         final var frc = graphics2D.getFontRenderContext();
         String message = "Press Space Bar to Begin Game";
         for (final var line : message.split("\n")) {
            final var layout = new TextLayout(line, graphics.getFont(), frc);
            final var bounds = layout.getBounds();
            final var targetWidth = (float) (width - bounds.getWidth()) / 2;
            layout.draw(graphics2D, targetWidth, currentHeight);
            currentHeight += graphics.getFontMetrics().getHeight();
         }
      } else {
         graphics.setColor(Color.GREEN);
         for (final var point : snake) {
            graphics.fillRect(point.x, point.y, cellSize, cellSize);
         }
      }
   }

   private void move() {
      direction = newDirection;

      final GamePoint head = snake.getFirst();
      final GamePoint newHead = switch (direction) {
         case UP -> new GamePoint(head.x, head.y - cellSize);
         case DOWN -> new GamePoint(head.x, head.y + cellSize);
         case LEFT -> new GamePoint(head.x - cellSize, head.y);
         case RIGHT -> new GamePoint(head.x + cellSize, head.y);
      };
      snake.addFirst(newHead);

      if (isCollision()) {
         gameOver = true;
         snake.removeFirst();
      } else {
         snake.removeLast();
      }

   }

   private boolean isCollision() {
     final GamePoint head = snake.getFirst();
     final var invalidWidth = (head.x < 0) || (head.x >= width);
     final var invalidHeight = (head.y < 0) || (head.y >= height);
     return (invalidWidth || invalidHeight);
   }

   @Override
   public void actionPerformed(final ActionEvent e) {
      if (gameStarted && !gameOver) {
         move();
      }
      repaint();
   }

   private record  GamePoint(int x, int y) {}

   private enum Direction {
      UP, DOWN, RIGHT, LEFT
   }
}
