package cn.xz.qrmaker.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

/**
 * 自定义border类，美化jpanel <br/>
 * jLabel1.setBorder(new OwnBorder(10,Color.blue)); <br/>
 * http://www.codefans.net/articles/1739.shtml <br/>
 * @author gsx
 */
public class MyBorder implements javax.swing.border.Border {
	private int thickness; // 边界线条的厚度
	private Color color; // 边界的颜色
	/*
	 * 构造方法 传入两个参数：边界的厚度和颜色
	 */

	public MyBorder(int thickness, Color color) {
		this.thickness = thickness;
		this.color = color;
	}

	/*
	 * 实现Border 接口中的第一个方法 该方法用于绘制自定义的边界
	 */
	public void paintBorder(Component c, // 边界所属的组件对象
			Graphics g, // 得到图形上下文引用，用于绘制
			int x, int y, // 边界的坐标
			int width, int height) { // 边界的宽度、长度
		g.setColor(this.color); // 设定颜色
		g.fill3DRect(x, y, width - thickness, thickness, true); // 绘制上边界
		g.fill3DRect(x, y + thickness, thickness, height - thickness, true); // 绘制左边界
		g.fill3DRect(x + thickness, y + height - thickness, width - thickness, thickness, true); // 绘制下边界
		g.fill3DRect(x + width - thickness, y, thickness, height - thickness, true); // 绘制右边界
	}

	/*
	 * 实现Border 接口的第二个方法 返回一个Insets 对象的引用
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(thickness, thickness, thickness, thickness);
	}

	/*
	 * 实现Border 接口的第三个方法 这里返回true,表明按照制定的颜色显示边界
	 */
	public boolean isBorderOpaque() {
		return true;
	}
}
