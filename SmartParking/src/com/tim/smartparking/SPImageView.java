package com.tim.smartparking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SPImageView extends ImageView {
	
	private Paint p;
	public static boolean drawRect = true;

	public SPImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	    p = new Paint();
	    p.setDither(true);
	    p.setColor(0xFF00CC00);  // alpha.r.g.b
	    p.setStyle(Paint.Style.STROKE);
	    p.setStrokeJoin(Paint.Join.ROUND);
	    p.setStrokeCap(Paint.Cap.ROUND);
	    p.setStrokeWidth(10);
	    if(drawRect) {
			canvas.drawLine(943, 431, 958, 858, p);
	    }
	}
	
	public static void drawRoute(int carId, double yourX, double yourY) {
		
	}

}
