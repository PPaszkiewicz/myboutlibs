package paszkiewicz.myboutlibs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for nested library items.
 */
class LibraryAdapter extends RecyclerView.Adapter<LibraryItem.ViewHolder> {
	ArrayList<LibraryItem> items;
	boolean isExpanded;

	/**
	 * Set the list of libraries.
	 *
	 * @param items      list of libraries
	 * @param isExpanded true if Author's card is already expanded
	 */
	public void setList(ArrayList<LibraryItem> items, boolean isExpanded) {
		this.items = items;
		this.isExpanded = isExpanded;
		notifyDataSetChanged();
	}

	/**
	 * Expand every library item in the list
	 *
	 * @param expanded true to expand, false to collapse
	 * @return this
	 */
	public LibraryAdapter setExpanded(boolean expanded) {
		isExpanded = expanded;
		notifyItemRangeChanged(0, getItemCount(), expanded);
		return this;
	}

	@Override
	public LibraryItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View holderLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout
				.myboutlibs_recyclerview_item_lib, parent, false);
		return new LibraryItem.ViewHolder(holderLayout);
	}

	@Override
	public void onBindViewHolder(LibraryItem.ViewHolder holder, int position) {
		holder.bind(items.get(position), isExpanded, false);
	}

	@Override
	public void onBindViewHolder(LibraryItem.ViewHolder holder, int position, List<Object>
			payloads) {
		if (payloads == null || payloads.size() < 1)
			onBindViewHolder(holder, position);
		else
			holder.setExpanded((boolean) payloads.get(0), true);
	}

	@Override
	public void onViewRecycled(LibraryItem.ViewHolder holder) {
		holder.clearRunningAnims();
	}

	@Override
	public int getItemCount() {
		return items == null ? 0 : items.size();
	}
}
