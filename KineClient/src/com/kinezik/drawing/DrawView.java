package com.kinezik.drawing;

import com.kinezik.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class DrawView extends View implements OnTouchListener, AnimationListener {
	private static final String TAG = "DrawView";

	enum AnimationState{ VISIBLE, FADING_IN, FADING_OUT, GONE
	}
	AnimationState mState;
	Drawing dessin;
	LinearLayout buttonLayout;
	boolean drawingStarted = false;

	private void init(){
		dessin = new Drawing(this.getHeight(), this.getWidth());

		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);
	}

	public DrawView(Context context) {
		super(context);
		init();

	}

	public DrawView(Context context, AttributeSet a){
		super(context, a);
		init();
	}

	public void reset(){
		dessin = new Drawing(this.getHeight(), this.getWidth());
		invalidate();
	}

	public Drawing getDrawing(){
		return dessin;
	}

	public void setButtonLayout(LinearLayout buttonLayout){
		this.buttonLayout = buttonLayout;
		buttonLayout.setVisibility(GONE);
		mState = AnimationState.GONE;
	}

	@Override
	public void onDraw(Canvas canvas) {
		dessin.setCanvasSize(this.getHeight(), this.getWidth());
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		//String s = new Integer(dessin.getSpeed()).toString();
		if(!drawingStarted){
			canvas.drawText( getContext().getResources().getString(R.string.drawing_invitation), this.getWidth()/8, this.getHeight()/2, paint);
		}
		dessin.draw(canvas);
	}

	public boolean onTouch(View view, MotionEvent event) {
		//On ne prend en compte que les mouvements relatifs � l'�cran tactile
		switch(event.getAction()){
		case(MotionEvent.ACTION_DOWN):
			drawingStarted = true;
			dessin.add(event.getX(), event.getY(), event.getPressure(), event.getEventTime());
		mState = AnimationState.FADING_OUT;
		buttonLayout.startAnimation(fadeOut());
		buttonLayout.setVisibility(GONE);
		break;
		case(MotionEvent.ACTION_MOVE):
			dessin.add(event.getX(), event.getY(), event.getPressure(), event.getEventTime());
		break;
		case(MotionEvent.ACTION_UP):
			dessin.nouveauTrait();
		mState = AnimationState.FADING_IN;
		buttonLayout.setVisibility(VISIBLE);
		buttonLayout.startAnimation(fadeIn());
		break;
		}
		invalidate();
		return true;
	}

	@Override
	public void onAnimationEnd(Animation animation){
		if(mState == AnimationState.FADING_IN){
			buttonLayout.setVisibility(VISIBLE);
		} else {
			buttonLayout.setVisibility(GONE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation){

	}

	@Override
	public void onAnimationStart(Animation animation){

	}

	private Animation fadeIn(){
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setDuration(250);
		fadeIn.setAnimationListener(this);
		return fadeIn;
	}



	private Animation fadeOut(){
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setDuration(250);
		fadeOut.setAnimationListener(this);
		return fadeOut;
	}
}