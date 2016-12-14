package paszkiewicz.myboutlibs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Activity displaying a license.
 */
public class LicenseActivity extends AppCompatActivity {
	public static final String EXTRA_TITLE_RESOURCE = "paszkiewicz.myboutlibs" +
			".LicenseActivity:EXTRA_TITLE_RESOURCE";
	public static final String EXTRA_TEXT_NOTICE = "paszkiewicz.myboutlibs" +
			".LicenseActivity:EXTRA_TEXT_NOTICE";
	public static final String EXTRA_TEXT_RESOURCE = "paszkiewicz.myboutlibs" +
			".LicenseActivity:EXTRA_TEXT_RESOURCE";

	/**
	 * Open license activity to read it.
	 *
	 * @param context         app context MIT, UNLICENSE<br>
	 * @param licenseNotice   copyright notice to include above license
	 * @param licenseFullName resource name of full name to use as activity title, format as
	 *                        string:resourcename
	 * @param licenseContent  resource name of full license text, format as string:resourcename
	 */
	public static void openLicense(Context context, @Nullable String
			licenseNotice, @Nullable String
										   licenseFullName, @Nullable String licenseContent) {
		Intent intent = new Intent(context, LicenseActivity.class);
		intent.putExtra(EXTRA_TITLE_RESOURCE, Util.getResourceFromString(context,
				licenseFullName));
		intent.putExtra(EXTRA_TEXT_RESOURCE, Util.getResourceFromString(context,
				licenseContent));
		intent.putExtra(EXTRA_TEXT_NOTICE, licenseNotice);
		context.startActivity(intent);
	}

	/**
	 * Open license activity to read it.
	 *
	 * @param context       app context
	 * @param licenseName   name of license. If it is in preset load up the values and ignore other
	 *                      arguments.<br> <i>current available preset values: </i>APACHE 2, MIT,
	 *                      UNLICENSE<br>
	 * @param licenseNotice copyright notice to include above license
	 */
	public static void openLicense(Context context, @Nullable String licenseName, @Nullable String
			licenseNotice) {
		Intent intent = new Intent(context, LicenseActivity.class);
		License license = License.get(licenseName);
		if (license != null) {
			intent.putExtra(EXTRA_TITLE_RESOURCE, license.title);
			intent.putExtra(EXTRA_TEXT_RESOURCE, license.full);
		} else {
			throw new IllegalArgumentException("LicenseActivity - invalid License name");
		}
		intent.putExtra(EXTRA_TEXT_NOTICE, licenseNotice);
		context.startActivity(intent);
	}

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);

		Toolbar toolbar = (Toolbar) findViewById(R.id.myboutlibs_activity_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(getIntent().getIntExtra(EXTRA_TITLE_RESOURCE, R.string
				.myboutlibs_activity_title));

		TextView text = (TextView) findViewById(R.id.myboutlibs_activity_license_text);
		String noticeText = getIntent().getStringExtra(EXTRA_TEXT_NOTICE);
		if (noticeText != null && !noticeText.isEmpty())
			noticeText = noticeText + "\n\n";
		else
			noticeText = "";


		String licenseText = getString(getIntent().getIntExtra(EXTRA_TEXT_RESOURCE, R.string
				.myboutlibs_missing_lib));
		text.setText(noticeText + licenseText);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private enum License {
		APACHE_2(R.string.myboutlibs_license_apache_2_name, R.string
				.myboutlibs_license_apache_2_full),
		MIT(R.string.myboutlibs_license_mit_name, R.string.myboutlibs_license_mit_full),
		UNLICENSE(R.string.myboutlibs_license_unlicense_name, R.string
				.myboutlibs_license_unlicense_full);

		private final int title;
		private final int full;

		License(int title, int full) {
			this.title = title;
			this.full = full;
		}

		private static License get(String name) {
			switch (name) {
				case "APACHE 2":
					return APACHE_2;
				case "MIT":
					return MIT;
				case "UNLICENSE":
					return UNLICENSE;
				default:
					return null;
			}
		}
	}
}
