package io.weichao.callback;

public interface GestureCallback {
	void onFlingUp();

	void onFlingDown();

	void onFlingLeft();

	void onFlingRight();

	void onDown();

	void onLongPress();

	void onSingleTap();

	void onDoubleTap();
}
