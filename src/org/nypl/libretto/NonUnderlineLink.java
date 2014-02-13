package org.nypl.libretto;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

// https://gist.github.com/thanksmister/249970d811d84a529d37	

	 
	public class NonUnderlineLink extends ClickableSpan 
	{
		@Override
	        public void updateDrawState(TextPaint ds) {
	           ds.setColor(ds.linkColor);
	           ds.setUnderlineText(false); // set to false to remove underline
	        }
	 
		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
		}
	}

