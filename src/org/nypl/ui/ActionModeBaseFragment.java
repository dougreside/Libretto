package org.nypl.ui;

import org.nypl.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public abstract class ActionModeBaseFragment extends BaseFragment{
	private	ActionMode			mActionMode		=	null;
	
	protected abstract void deleteItems();
	protected abstract void destroyActionMode();
	protected abstract void refreshItemsAfterDeletion();
	
	protected void startActionMode(){
		 SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
		 mActionMode =  activity.startActionMode(new ModeCallback());
	}
	
	protected void setActionModeText(int selectedItems){
		mActionMode.setTitle(selectedItems+" selected");
	
	}
	
	private final class ModeCallback implements ActionMode.Callback {
		 
       @Override
       public boolean onCreateActionMode(ActionMode mode, Menu menu) {
           SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
           activity.getSupportMenuInflater().inflate(R.menu.action_mode_menu, menu);
           return true;
       }

       @Override
       public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
           // Here, you can checked selected items to adapt available actions
           return false;
       }

       @Override
       public void onDestroyActionMode(ActionMode mode) {
          destroyActionMode();
       }


		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.cab_action_delete:
				deleteItems();
				mode.finish();
	            return true;
			default:
				break;
			}
           
			return false;
		}
   };
   
  
}
