package paszkiewicz.myboutlibs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Library item (in nested adapter).
 */
public class LibraryItem {
	final static int ANIM_CONTAINER_COLLAPSE = 350;
	final static int ANIM_BUTTON_COLLAPSE = 150;
	final static int ANIM_BUTTON_CHAIN_DELAY = 25;
	final static int ANIM_TOTAL = ANIM_CONTAINER_COLLAPSE + ANIM_BUTTON_COLLAPSE +
			ANIM_BUTTON_CHAIN_DELAY;

	public String icon;
	public String name;
	public String desc;
	public String github;
	public String store;
	public String www;

	public String license;
	public String licenseNotice;
	public String licenseLongName;
	public String licenseContent;

	public LibraryItem() {

	}

	/**
	 * ViewHolder for library item.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView icon;
		TextView name;
		ViewGroup details;
		TextView desc;
		View buttonContainer;
		View github;
		View store;
		Button www;
		Button license;

		/**
		 * Animates the height of library item.
		 */
		private ValueAnimator expandAnimator;

		public ViewHolder(View itemView) {
			super(itemView);
			Util.ViewFinder finder = new Util.ViewFinder(itemView);
			icon = finder.f(R.id.myboutlibs_recyclerview_item_icon);
			name = finder.f(R.id.myboutlibs_recyclerview_library_name);
			name.setPivotX(0);
			name.setPivotY(getNameHeight() / 2);
			details = finder.f(R.id.myboutlibs_recyclerview_library_details_layout);
			desc = finder.f(R.id.myboutlibs_recyclerview_library_description);
			desc.setPivotY(0);
			buttonContainer = finder.f(R.id.myboutlibs_recyclerview_library_button_container);
			github = finder.f(R.id.myboutlibs_recyclerview_library_github);
			store = finder.f(R.id.myboutlibs_recyclerview_library_store);
			www = finder.f(R.id.myboutlibs_recyclerview_library_www);
			license = finder.f(R.id.myboutlibs_recyclerview_library_license);
		}

		/**
		 * Binds the data of library.
		 *
		 * @param item       LibraryItem
		 * @param isExpanded true if expanded
		 * @param animate    true to run expand/collapse animations
		 */
		void bind(LibraryItem item, boolean isExpanded, boolean animate) {
			name.setText(item.name);
			desc.setText(item.desc);


			if (item.icon != null && !item.icon.isEmpty()) {
				icon.setVisibility(View.VISIBLE);
				icon.setImageResource(Util.getResourceFromString(icon.getContext(), item.icon));
			} else {
				icon.setVisibility(View.GONE);
			}
			bindHttpButton(github, item.github);
			bindHttpButton(www, item.www);
			bindHttpButton(store, item.store);
			bindLicenseButton(license, item);
			setExpanded(isExpanded, animate);
		}

		/**
		 * Expand the libray item.
		 *
		 * @param isExpanded true to expand, otherwise collapse
		 * @param animate    true to run animations
		 */
		void setExpanded(boolean isExpanded, boolean animate) {
			if (!animate) {
				details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
				name.setTextSize(TypedValue.COMPLEX_UNIT_PX, isExpanded ? getNameExpandedSize() :
						getNameCollapsedSize());
				restoreViews(name);
				if (isExpanded) {
					details.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
					details.requestLayout();
					restoreViews(desc, github, www, store, license);
				}
			} else {
				details.setVisibility(View.VISIBLE);
				animateFontHeight(isExpanded);
				if (isExpanded) {
					int delay = ANIM_BUTTON_CHAIN_DELAY;
					int startDelay = ANIM_CONTAINER_COLLAPSE - ANIM_BUTTON_CHAIN_DELAY;
					delay += chainAnimateButton(github, true, delay, startDelay);
					delay += chainAnimateButton(store, true, delay, startDelay);
					delay += chainAnimateButton(www, true, delay, startDelay);
					chainAnimateButton(license, true, delay, startDelay);
					animateContainerHeight(true, 0);
				} else {
					int delay = ANIM_BUTTON_CHAIN_DELAY;
					delay += chainAnimateButton(license, false, delay, 0);
					delay += chainAnimateButton(www, false, delay, 0);
					delay += chainAnimateButton(store, false, delay, 0);
					chainAnimateButton(github, false, delay, 0);
					animateContainerHeight(false, delay + ANIM_BUTTON_COLLAPSE);
				}
			}
		}

		/**
		 * Expand button animations
		 *
		 * @param view       button to animate
		 * @param in         true to expand, false to collapse
		 * @param delay      delay to start animations
		 * @param startDelay delay to start animations
		 * @return delay parameter
		 */
		private int chainAnimateButton(View view, boolean in, int delay, int startDelay) {
			if (view.getVisibility() == View.GONE)
				return 0;
			float fromScale = in ? 0 : 1;
			float toScale = in ? 1 : 0;
			view.setScaleY(fromScale);
			view.setScaleX(fromScale);
			view.setPivotX(25);
			view.setPivotY(50);
			view.animate().scaleX(toScale).scaleY(toScale).setStartDelay(startDelay + delay)
					.setDuration(ANIM_BUTTON_COLLAPSE);
			return delay;
		}

		/**
		 * restore scale of all views to 1
		 *
		 * @param views list of views to rescale.
		 */
		private void restoreViews(View... views) {
			for (View v : views) {
				v.setScaleX(1);
				v.setScaleY(1);
			}
		}

		/**
		 * Animates height of library item.
		 *
		 * @param expand     true to expand, false to collapse
		 * @param startDelay delay before expansion starts
		 */
		private void animateContainerHeight(final boolean expand, final int startDelay) {
			clearAnimator();
			details.setVisibility(View.VISIBLE);
			Resources r = details.getResources();
			final int h = r.getDimensionPixelSize(R.dimen.myboutlibs_lib_bottompadding)
					+ r.getDimensionPixelSize(R.dimen.myboutlibs_lib_buttonheight);
			if (expand) {
				details.getLayoutParams().height = 0;
			}
			desc.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@Override
				public void onLayoutChange(View view2, int i, int i1, int i2, int i3, int i4,
										   int
												   i5, int i6, int i7) {
					view2.removeOnLayoutChangeListener(this);
					animateContainerHeightImpl(expand, h + i3 - i1, startDelay);
				}
			});
			desc.requestLayout();
		}

		/**
		 * Delayed call from {@link #animateContainerHeight(boolean, int)}, called after layout is
		 * set up so we get proper height
		 *
		 * @param expand     true to expand, false to collapse
		 * @param h          height of view
		 * @param startDelay delay before animation starts
		 */
		private void animateContainerHeightImpl(boolean expand, int h, int startDelay) {
			int fromH = expand ? 0 : h;
			int toH = expand ? h : 0;
			details.getLayoutParams().height = fromH;
			details.requestLayout();
			expandAnimator = ValueAnimator.ofInt(fromH, toH);
			expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					details.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
					details.requestLayout();
				}
			});
			if (!expand) {
				expandAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						details.setVisibility(View.GONE);
						animation.removeListener(this);
					}
				});
			}
			expandAnimator.setStartDelay(startDelay);
			expandAnimator.setDuration(ANIM_CONTAINER_COLLAPSE);
			expandAnimator.setInterpolator(new LinearOutSlowInInterpolator());
			expandAnimator.start();
		}

		/**
		 * Smoothly run font animation
		 *
		 * @param expand false to collapse
		 */
		private void animateFontHeight(final boolean expand) {
			name.animate().cancel();
			final float collapsedScale = getNameCollapsedSize() / getNameExpandedSize();
			if (expand) {
				name.setScaleX(collapsedScale);
				name.setScaleY(collapsedScale);
				name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getNameExpandedSize());
			}
			name.animate()
					.scaleX(expand ? 1 : collapsedScale)
					.scaleY(expand ? 1 : collapsedScale)
					.setInterpolator(new LinearOutSlowInInterpolator())
					.setDuration(ANIM_CONTAINER_COLLAPSE)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if (!expand)
								name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getNameCollapsedSize
										());
							name.setScaleX(1);
							name.setScaleY(1);
						}
					});
		}

		/**
		 * clear any animations running currently
		 */
		void clearRunningAnims() {
			www.animate().cancel();
			store.animate().cancel();
			github.animate().cancel();
			details.animate().cancel();
			name.animate().cancel();
			clearAnimator();
		}

		/**
		 * Clean up all animators used for expansion.
		 */
		private void clearAnimator() {
			if (expandAnimator != null) {
				expandAnimator.removeAllUpdateListeners();
				expandAnimator.removeAllListeners();
				expandAnimator.end();
			}
			expandAnimator = null;
		}

		/**
		 * Make button clickable and open a website.
		 *
		 * @param view button
		 * @param url  url to open on click
		 */
		private void bindHttpButton(View view, final String url) {
			if (url != null) {
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Util.openWWW(view.getContext(), url);
					}
				});
				view.setVisibility(View.VISIBLE);
			} else {
				view.setOnClickListener(null);
				view.setVisibility(View.GONE);
			}
		}

		/**
		 * Make button clickable and open a license file.
		 *
		 * @param view button
		 * @param item library item
		 */
		private void bindLicenseButton(Button view, final LibraryItem item) {
			if ((item.license != null && !item.license.isEmpty()) ||
					(item.licenseLongName != null && !item.licenseLongName.isEmpty())) {
				view.setVisibility(View.VISIBLE);
				view.setText(item.license);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (item.license != null) {
							LicenseActivity.openLicense(view.getContext(),
									item.license,
									item.licenseNotice);
						} else {
							LicenseActivity.openLicense(view.getContext(),
									item.licenseNotice,
									item.licenseLongName,
									item.licenseContent);
						}
					}
				});
			} else {
				view.setVisibility(View.GONE);
				view.setOnClickListener(null);
			}
		}

		private float getNameHeight() {
			return name.getResources().getDimension(R.dimen.myboutlibs_lib_titleviewheight);
		}

		private float getNameCollapsedSize() {
			return name.getResources().getDimension(R.dimen.myboutlibs_lib_titlecollapsed);
		}

		private float getNameExpandedSize() {
			return name.getResources().getDimension(R.dimen.myboutlibs_lib_titleexpanded);
		}
	}
}
