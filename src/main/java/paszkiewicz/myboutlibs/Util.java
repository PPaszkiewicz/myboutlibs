package paszkiewicz.myboutlibs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * Static functions.
 */
abstract class Util {

	/**
	 * Launch intent that opens an url
	 *
	 * @param context app context
	 * @param url     website to open
	 */
	static void openWWW(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}

	/**
	 * Fetch resource from string
	 *
	 * @param c        app context
	 * @param resource string pointing to a resource, for example " string:app_name"
	 * @return resource ID
	 */
	static int getResourceFromString(Context c, String resource) {
		String str[] = resource.split(":");
		return c.getResources().getIdentifier(str[1], str[0], c.getPackageName());
	}

	/**
	 * Helper class to quickly cast view types and search from common root view
	 */
	static class ViewFinder {
		private View root;

		ViewFinder(View root) {
			this.root = root;
		}

		/**
		 * find view with given id and cast its type
		 *
		 * @param id  id of view
		 * @param <T> type of view
		 * @return view with required type
		 */
		@SuppressWarnings("unchecked")
		<T extends View> T f(@IdRes int id) {
			return (T) root.findViewById(id);
		}
	}
}