package paszkiewicz.myboutlibs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import paszkiewicz.myboutlibs.util.ClickThroughRecyclerView;

/**
 * Author item, might contain multiple libraries.
 */
public class AuthorItem {
	public boolean lockExpand;
	public String author;
	public ArrayList<LibraryItem> libs;

	public AuthorItem() {

	}

	/**
	 * ViewHolder that can be expanded
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		CardView cardView;
		View background;
		TextView authorName;
		ClickThroughRecyclerView libsRecycler;
		ImageView expandIcon;

		boolean isExpanded;
		/**
		 * Adapter for nested list of libraries
		 */
		private LibraryAdapter libsItemsAdapter;
		/**
		 * Parent adapter to notice when expanding.
		 */
		private AuthorAdapter authorItemAdapter;
		private ValueAnimator expandAnimator;

		/**
		 * ViewHolder for Author
		 *
		 * @param itemView      ViewHolder's itemView
		 * @param parentAdapter adapter containing this viewholder
		 */
		public ViewHolder(View itemView, AuthorAdapter parentAdapter) {
			super(itemView);
			Util.ViewFinder finder = new Util.ViewFinder(itemView);
			cardView = (CardView) itemView;
			background = finder.f(R.id.myboutlibs_recyclerview_item_layout);
			authorName = finder.f(R.id.myboutlibs_recyclerview_item_author);
			libsRecycler = finder.f(R.id.myboutlibs_recyclerview_item_libs_recycler);
			libsRecycler.setLayoutManager(new LinearLayoutManager(libsRecycler.getContext()));
			libsItemsAdapter = new LibraryAdapter();
			libsRecycler.setAdapter(libsItemsAdapter);
			((SimpleItemAnimator) libsRecycler.getItemAnimator()).setSupportsChangeAnimations
					(false);
			expandIcon = finder.f(R.id.myboutlibs_recyclerview_item_expand);
			authorItemAdapter = parentAdapter;
		}

		/**
		 * Bind data to author item.
		 *
		 * @param item Author's data.
		 */
		void bind(AuthorItem item) {
			isExpanded = false;
			expandIcon.setRotation(0);
			authorName.setText(item.author);

			if (item.libs != null && item.libs.size() > 0) {
				libsItemsAdapter.setList(item.libs, item.lockExpand);
			} else {
				libsItemsAdapter.setList(null, item.lockExpand);
			}
			if (!item.lockExpand)
				background.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						setExpanded(!isExpanded, true);
					}
				});
			else
				authorItemAdapter.setItemExpanded(true, getItemId());
		}

		/**
		 * Creates a locked item that cannot be collapsed.<br> Call this once per viewholder after
		 * creating, not while binding.
		 */
		void createLocked() {
			expandIcon.setVisibility(View.GONE);
			background.setClickable(false);
			background.setOnClickListener(null);
			background.setBackground(null);
			setExpanded(true, false);

			cardView.setCardElevation(cardView.getResources().getDimension(R.dimen
					.myboutlibs_author_card_locked_elevation));
			((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).leftMargin = 0;
			((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).rightMargin = 0;
			((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).bottomMargin = cardView
					.getResources().getDimensionPixelSize(R.dimen
							.myboutlibs_author_card_locked_elevation);
		}

		/**
		 * Expand or collapse list of libraries.
		 *
		 * @param expanded true to expand, false to collapse.
		 * @param doAnim   false to skip Z float and arrow animation.
		 */
		void setExpanded(boolean expanded, boolean doAnim) {
			isExpanded = expanded;
			libsItemsAdapter.setExpanded(expanded);
			if (doAnim) {
				clearAnimator();
				animateExpandCard(expanded);
			} else {
				expandIcon.setRotation(expanded ? -180 : 0);
				cardView.setCardElevation(expanded ? cardView.getResources().getDimension(R.dimen
						.myboutlibs_author_card_highlighted_elevation) : 0f);
			}
			authorItemAdapter.setItemExpanded(expanded, getItemId());
		}

		/**
		 * Animate card expansion.
		 *
		 * @param expanded true to expand, false to collapse.
		 */
		void animateExpandCard(final boolean expanded) {
			final float maxElevation = cardView.getResources().getDimension(R.dimen
					.myboutlibs_author_card_highlighted_elevation);
			float startValue = expanded ? 0 : maxElevation;
			float endValue = expanded ? maxElevation : 0;
			cardView.setCardElevation(startValue);
			cardView.requestLayout();

			expandIcon.animate().rotation(expanded ? -180 : 0).setDuration(LibraryItem
					.ANIM_TOTAL).setInterpolator(new
					AnticipateOvershootInterpolator(1.5f));

			expandAnimator = ValueAnimator.ofFloat(startValue, endValue);
			expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float value = (float) valueAnimator.getAnimatedValue();
					cardView.setCardElevation(value);
					cardView.requestLayout();
					if (expanded)
						authorItemAdapter.scrollTo(getItemId());
				}
			});
			expandAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					expandAnimator = null;
				}
			});
			expandAnimator.setDuration(LibraryItem.ANIM_TOTAL);
			expandAnimator.setInterpolator(new
					LinearOutSlowInInterpolator());
			expandAnimator.start();
		}

		/**
		 * Clear running animation.
		 */
		void clearRunningAnimation() {
			expandIcon.animate().cancel();
			clearAnimator();
			libsItemsAdapter.setExpanded(false);
		}

		/**
		 * Clean up all animators used for expansion.
		 */
		void clearAnimator() {
			if (expandAnimator != null) {
				expandAnimator.removeAllUpdateListeners();
				expandAnimator.removeAllListeners();
				expandAnimator.end();
			}
			expandAnimator = null;
		}
	}
}
