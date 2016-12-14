package paszkiewicz.myboutlibs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Activity presenting authors/libraries from JSON file.
 */
public class MyBoutLibsActivity extends AppCompatActivity {
	private final static String ARG_JSON = "paszkiewicz.myboutlibs.MyBoutLibsActivity:ARG_JSON";
	private final static String ARG_TITLE = "paszkiewicz.myboutlibs.MyBoutLibsActivity:ARG_TITLE";
	private AuthorAdapter recyclerViewAdapter;

	/**
	 * Display core library activity.
	 *
	 * @param context   app context
	 * @param jsonResId resource ID of valid json file
	 * @param titleId   resource ID of title
	 */
	public static void start(Context context, @RawRes int jsonResId, @StringRes int titleId) {
		Intent launch = new Intent(context, MyBoutLibsActivity.class);
		launch.putExtra(ARG_JSON, jsonResId);
		launch.putExtra(ARG_TITLE, titleId);
		context.startActivity(launch);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myboutlibs_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.myboutlibs_activity_toolbar);
		setSupportActionBar(toolbar);
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		int titleRes = getIntent().getIntExtra(ARG_TITLE, R.string
				.myboutlibs_default_title);
		setTitle(titleRes);
		getSupportActionBar().setTitle(titleRes);

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id
				.myboutlibs_activity_recyclerview);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		JsonParser parser = new JsonParser(getResources(), getIntent().getIntExtra(ARG_JSON, 0));
		recyclerViewAdapter = new AuthorAdapter(parser.parse(), recyclerView, savedInstanceState);
		recyclerView.setAdapter(recyclerViewAdapter);
		((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations
				(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		recyclerViewAdapter.saveState(outState);
	}
}
