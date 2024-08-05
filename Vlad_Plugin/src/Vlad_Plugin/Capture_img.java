package Vlad_Plugin;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.geom.AffineTransform;

public class Capture_img {
	//функция получения рандомного числа в диапозоне
	public static int Random(int min,int max) {
		max -= min;
		return (int) (Math.random() * ++max) + min;
	}
	//функция получения рандомного цвета
	private static Color getRandomColor() {
		return new Color(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255));
	}
	public static BufferedImage Create_img_Capture (String text) {
		//создание переменных буфера и двух дополнительных, Canvas для рисования и Graphics2D для отрисовки примитивов
		BufferedImage image = new BufferedImage(128,128, BufferedImage.TYPE_INT_RGB);
		Canvas canvas = new Canvas();
		Graphics2D image_capture_gr = (Graphics2D) image.getGraphics();
		//запуск сглаживания
		image_capture_gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//связывание Canvas для рисования с Graphics2D
		canvas.paint(image_capture_gr);
		//установка цвета квадрата и рисовка его на фоне
		image_capture_gr.setColor(Color.WHITE);
		image_capture_gr.fillRect(0, 0, 128, 128);
		//задаём шрифт
		Font test = new Font("Axial", Font.BOLD + Font.ITALIC, 40);
		image_capture_gr.setFont(test);
		//рисуем 3 буквы
		for(int i=0;i<3;i++) {
			AffineTransform original = image_capture_gr.getTransform();
			String now_char = Character.toString(text.charAt(i));
			image_capture_gr.rotate((Math.random())-0.5, (30 * (i + 1)), 70);
			image_capture_gr.setColor(getRandomColor());
			image_capture_gr.drawString(now_char, (30 * (i + 1)), 70);
			image_capture_gr.setTransform(original);
		}
		//рисуем 10 дополнительных линий
		for(int i=0;i<10;i++) {
			image_capture_gr.setColor(getRandomColor());
			image_capture_gr.drawLine(Random(0,128), Random(0,50), Random(0,128), Random(90,128));
		}
		image_capture_gr.dispose();
		return image;
	}
}
