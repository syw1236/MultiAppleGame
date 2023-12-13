import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DrawPanel extends JPanel {
    Point startPoint;
    Point endPoint;

    public DrawPanel() {
        setOpaque(false);
        setVisible(true);

        requestFocus();

    }

    public void setStartP(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndP(Point endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        if (startPoint != null && endPoint != null) {
            //System.out.println("drawPnael drawing");
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            int width = Math.abs(endPoint.x - startPoint.x);
            int height = Math.abs(endPoint.y - startPoint.y);

            // 테두리 그리기
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(102, 102, 204, 100)); // 채워진 색상 설정 (오렌지, 반투명)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 투명도 설정
            g2d.fillRect(x, y, width, height); // 사각형 채우기
            // 채우기

        }
    }
}
