package paszkiewicz.myboutlibs;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Adapter for top level recyclerview.<br> Use {@link #saveState(Bundle)} to save expanded state of
 * items.
 */
class AuthorAdapter extends RecyclerView.Adapter<AuthorItem.ViewHolder> {
	private final static String SAVE_KEY = "paszkiewicz.myboutlibs:AuthorAdapter:SAVE_KEY";
	ArrayList<AuthorItem> items;
	RecyclerView recyclerView;
	Set<Long> expandedItems = new HashSet<>();
	private long currentlyExpandedItem = 0;

	/**
	 * Creates adapter
	 *
	 * @param items              list of authors and their libraries libraries
	 * @param recyclerView       host recyclerView
	 * @param savedInstanceState pass saved instance to restore expanded state
	 */
	AuthorAdapter(ArrayList<AuthorItem> items, RecyclerView recyclerView, Bundle
			savedInstanceState) {
		this.items = items;
		this.recyclerView = recyclerView;
		setHasStableIds(true);
		restoreState(savedInstanceState);
	}

	/**
	 * Creates author viewholder.
	 *
	 * @param parent   ViewHolder's ViewGroup
	 * @param viewType item view type, refer to {@link #getItemViewType(int)}
	 * @return new AuthorItem.ViewHolder
	 */
	@Override
	public AuthorItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View holderLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout
				.myboutlibs_recyclerview_item, parent, false);
		AuthorItem.ViewHolder holder = new AuthorItem.ViewHolder(holderLayout, this);
		if (viewType == 1)
			holder.createLocked();
		return holder;
	}

	/**
	 * Binds authors data to the viewholder and expands without animation if needed
	 *
	 * @param holder   ViewHolder
	 * @param position position of AuthorItem
	 */
	@Override
	public void onBindViewHolder(AuthorItem.ViewHolder holder, int position) {
		holder.bind(items.get(position));
		if (expandedItems.contains(getItemId(position))) {
			holder.setExpanded(true, false);
		} else {
			holder.setExpanded(false, false);
		}
	}

	/**
	 * Get item view type
	 *
	 * @param position position of item
	 * @return 0 - normal author item<br>1 - locked author item
	 */
	@Override
	public int getItemViewType(int position) {
		return items.get(position).lockExpand ? 1 : 0;
	}

	@Override
	public int getItemCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public void onViewRecycled(AuthorItem.ViewHolder holder) {
		holder.clearRunningAnimation();
	}

	/**
	 * Add or remove item from expanded list
	 *
	 * @param expanded state of expansion
	 * @param item     ID of item
	 * @return true if the set of expanded items changed
	 */
	boolean setItemExpanded(boolean expanded, Long item) {
		if (expanded) {
			currentlyExpandedItem = item;
			return expandedItems.add(item);
		}
		return expandedItems.remove(item);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	/**
	 * Save expanded state of items
	 *
	 * @param outState Bundle to inflate with added array of expanded items
	 */
	void saveState(Bundle outState) {
		long list[] = new long[expandedItems.size()];
		Iterator<Long> iter = expandedItems.iterator();
		int i = 0;
		while (iter.hasNext()) {
			list[i] = iter.next();
			i++;
		}
		outState.putLongArray(SAVE_KEY, list);
	}

	/**
	 * Restore expanded state
	 *
	 * @param savedInstanceState bundle with save state from {@link #saveState(Bundle)}
	 */
	void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState == null)
			return;

		long list[] = savedInstanceState.getLongArray(SAVE_KEY);
		if (list == null)
			return;
		for (long l : list)
			expandedItems.add(l);
	}

	/**
	 * Scroll to currently expanding item
	 *
	 * @param itemID ID of item currently expanding, only the last item expanded by {@link
	 *               #setItemExpanded(boolean, Long)} can get scrolled to
	 * @return true if scroll was accepted. false if not
	 */
	boolean scrollTo(Long itemID) {
		if (itemID == currentlyExpandedItem) {
			recyclerView.smoothScrollToPosition(itemID.intValue());
			return true;
		}
		return false;
	}
}
