package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.NavigationAndCommentFragment;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;
import org.ligi.tracedroid.logging.Log;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;

public class GameReviewActivity extends GoActivity  {

	private boolean autoplay_active=false;
	
	class autoPlayRunnable implements Runnable {

		GoGame game;
		
		@Override
		public void run() {
			game=GoGameProvider.getGame();
			Log.i("gobandroid","automove start" + game.getActMove().getNextMoveVariations().size());
			while (autoplay_active &&( game.getActMove().getNextMoveVariations().size()>0)) {
				Log.i("gobandroid","automove move"+game.getActMove().getNextMoveVariationCount());
				game.jump(game.getActMove().getnextMove(0));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_review, menu);
    	
    	menu.findItem(R.id.menu_autoplay).setTitle(autoplay_active?R.string.autoplay_off:R.string.autoplay_on);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId()) {
		case R.id.menu_bookmark:
			BookmarkDialog.show(this);
			return true;

		case R.id.menu_autoplay:
			Log.i("gobandroid","automove init");
	
			if (autoplay_active) {
				autoplay_active=false;
			} else {
				autoplay_active=true;
				new Thread(new autoPlayRunnable()).start();;
			}
			this.invalidateOptionsMenu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onStop() {
		autoplay_active=false;
		super.onStop();
	}


	public Fragment getGameExtraFragment() {
		return new NavigationAndCommentFragment();		
	}
	
	
	@Override
	public byte doMoveWithUIFeedback(byte x,byte y) {
		// we want the user not to be able to edit in review mode
		return GoGame.MOVE_VALID;
	}	 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBoard().setOnKeyListener(this);
		getBoard().do_mark_act=false;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
	    	
		if (event.getAction()==KeyEvent.ACTION_DOWN)
	    	switch (keyCode) {
	    	case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
	    	case KeyEvent.KEYCODE_DPAD_LEFT:
	    		if (!game.canUndo())
	    			return true;
	    		game.undo();
	    		return true;
	    		
	    	case KeyEvent.KEYCODE_DPAD_RIGHT:
	    	case KeyEvent.KEYCODE_MEDIA_NEXT:
	    		GameForwardAlert.show(this, game);
	    		return true;
	    		
	    	case KeyEvent.KEYCODE_DPAD_UP:
	    	case KeyEvent.KEYCODE_DPAD_DOWN:
	    		return false;

	    	}
	    	return super.onKey(v,keyCode, event);
	 }

	@Override
	public boolean isAsk4QuitEnabled() {
		return false;
	}
}
