package paszkiewicz.myboutlibs.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Nested recyclerview that doesn't consume touch events.
 */
public class ClickThroughRecyclerView extends RecyclerView {
	public ClickThroughRecyclerView(Context context) {
		super(context);
	}

	public ClickThroughRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ClickThroughRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return false;
	}
}
