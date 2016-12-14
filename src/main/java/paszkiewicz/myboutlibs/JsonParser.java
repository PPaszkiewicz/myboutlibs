package paszkiewicz.myboutlibs;

import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Simple Json deserializer.
 */
class JsonParser {
	private final static String ROOT_TAG = "MyBoutLibs";
	private final Resources resources;
	private final int jsonResId;

	JsonParser(Resources resources, int jsonResId) {
		if (jsonResId == 0)
			throw new IllegalArgumentException("Invalid resource ID for MyBoutLibs json!");

		this.resources = resources;
		this.jsonResId = jsonResId;
	}

	ArrayList<AuthorItem> parse() {
		ArrayList<AuthorItem> returnList = null;
		JSONObject root = readFromResource();
		if (root == null)
			return null;

		try {
			JSONArray authors = root.getJSONArray(ROOT_TAG);
			returnList = new ArrayList<>(authors.length());
			for (int i = 0; i < authors.length(); i++) {
				JSONObject author = authors.getJSONObject(i);
				AuthorItem newAuthor = new AuthorItem();
				newAuthor.author = get(author, "author");
				newAuthor.lockExpand = getBool(author, "lockExpand");

				JSONArray libs = author.getJSONArray("libs");
				newAuthor.libs = new ArrayList<>(libs.length());

				for (int j = 0; j < libs.length(); j++) {
					JSONObject lib = libs.getJSONObject(j);
					LibraryItem newLib = new LibraryItem();
					newLib.name = get(lib, "name");
					newLib.desc = get(lib, "desc");
					newLib.icon = get(lib, "icon");
					newLib.github = get(lib, "github");
					newLib.store = get(lib, "store");
					newLib.www = get(lib, "www");
					newLib.license = get(lib, "license");
					newLib.licenseNotice = get(lib, "licenseNotice");
					newLib.licenseLongName = get(lib, "licenseLongName");
					newLib.licenseContent = get(lib, "licenseContent");
					newAuthor.libs.add(newLib);
				}
				returnList.add(newAuthor);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnList;
	}

	private JSONObject readFromResource() {
		JSONObject jsonObj = null;
		InputStream resourceReader = resources.openRawResource(jsonResId);
		Writer writer = new StringWriter();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader,
					"UTF-8"));
			String line = reader.readLine();
			while (line != null) {
				writer.write(line);
				line = reader.readLine();
			}
			jsonObj = new JSONObject(writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resourceReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObj;
	}

	private String get(JSONObject object, String key) throws JSONException {
		if (object.has(key))
			return object.getString(key);
		return null;
	}

	private int getInt(JSONObject object, String key) throws JSONException {
		if (object.has(key))
			return object.getInt(key);
		return 0;
	}

	private boolean getBool(JSONObject object, String key) throws JSONException {
		return object.has(key) && object.getBoolean(key);
	}
}
