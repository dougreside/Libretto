/**
 * 
 */
package org.nypl.libretto.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;




/**
 * Extension of a relative layout to provide a checkable behaviour
 * 
 * @author marvinlabs
 */
public class CheckableLinearLayout extends LinearLayout implements
		Checkable {

	private boolean isChecked;

	public CheckableLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialise(attrs);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise(attrs);
	}

	public CheckableLinearLayout(Context context, int checkableId) {
		super(context);
		initialise(null);
	}

	/*
	 * @see android.widget.Checkable#isChecked()
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/*
	 * @see android.widget.Checkable#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		// change background according to view state
		if(isChecked){
			setBackgroundColor(Color.parseColor("#5c0005"));
		}else {
			setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
		}
	}

	/*
	 * @see android.widget.Checkable#toggle()
	 */
	public void toggle() {
		this.isChecked = !this.isChecked;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	/**
	 * Read the custom XML attributes
	 */
	private void initialise(AttributeSet attrs) {
		this.isChecked = false;
	}

}
