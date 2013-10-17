package com.vuze.android.remote;

import java.util.*;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.aelitis.azureus.util.JSONUtils;

@SuppressWarnings({
	"rawtypes",
	"unchecked"
})
public class AppPreferences
{

	private static final String KEY_REMOTES = "remotes";

	private static final String KEY_CONFIG = "config";

	private static final String KEY_LASTUSED = "lastUsed";

	private SharedPreferences preferences;

	private Context context;

	public AppPreferences(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences("AndroidRemote",
				Activity.MODE_PRIVATE);
	}

	public RemoteProfile getLastUsedRemote() {
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			if (config != null) {
				Map mapConfig = JSONUtils.decodeJSON(config);

				String lastUsed = (String) mapConfig.get(KEY_LASTUSED);
				if (lastUsed != null) {
					Map mapRemotes = (Map) mapConfig.get(KEY_REMOTES);
					if (mapRemotes != null) {
						Map mapRemote = (Map) mapRemotes.get(lastUsed);
						if (mapRemote != null) {
							return new RemoteProfile(mapRemote);
						}
					}
				}
			}
		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}

		return null;
	}

	public String getLastUsedRemoteID() {
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			if (config != null) {
				Map mapConfig = JSONUtils.decodeJSON(config);

				return (String) mapConfig.get(KEY_LASTUSED);
			}
		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}

		return null;
	}

	public RemoteProfile getRemote(String nick) {
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			if (config != null) {
				Map mapConfig = JSONUtils.decodeJSON(config);

				Map mapRemotes = (Map) mapConfig.get(KEY_REMOTES);
				if (mapRemotes != null) {
					Object mapRemote = mapRemotes.get(nick);
					if (mapRemote instanceof Map) {
						return new RemoteProfile((Map) mapRemote);
					}
				}
			}
		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}

		return null;
	}

	public RemoteProfile[] getRemotes() {
		List<RemoteProfile> listRemotes = new ArrayList<RemoteProfile>(1);
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			if (config != null) {
				Map mapConfig = JSONUtils.decodeJSON(config);

				Map mapRemotes = (Map) mapConfig.get(KEY_REMOTES);
				if (mapRemotes != null) {
					for (Object val : mapRemotes.values()) {
						if (val instanceof Map) {
							listRemotes.add(new RemoteProfile((Map) val));
						}
					}
				}
			}
		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}

		return listRemotes.toArray(new RemoteProfile[0]);
	}

	public void addRemoteProfile(RemoteProfile rp) {
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			Map mapConfig = config == null ? new HashMap()
					: JSONUtils.decodeJSON(config);

			if (mapConfig == null) {
				mapConfig = new HashMap();
			}

			Map mapRemotes = (Map) mapConfig.get(KEY_REMOTES);
			if (mapRemotes == null) {
				mapRemotes = new HashMap();
				mapConfig.put(KEY_REMOTES, mapRemotes);
			}

			mapRemotes.put(rp.getID(), rp.getAsMap(true));

			savePrefs(mapConfig);

		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}

	}

	public void setLastRemote(String ac) {
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			Map mapConfig = config == null ? new HashMap()
					: JSONUtils.decodeJSON(config);

			if (mapConfig == null) {
				mapConfig = new HashMap();
			}

			if (ac == null) {
				mapConfig.remove(KEY_LASTUSED);
			} else {
				mapConfig.put(KEY_LASTUSED, ac);
			}

			Map mapRemotes = (Map) mapConfig.get(KEY_REMOTES);
			if (mapRemotes == null) {
				mapRemotes = new HashMap();
				mapConfig.put(KEY_REMOTES, mapRemotes);
			}

			savePrefs(mapConfig);

		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}

	}

	private void savePrefs(Map mapConfig) {
		Editor edit = preferences.edit();
		edit.putString(KEY_CONFIG, JSONUtils.encodeToJSON(mapConfig));
		edit.commit();
		if (AndroidUtils.DEBUG) {
			try {
				System.out.println("Saved Preferences: "
						+ new org.json.JSONObject(mapConfig).toString(2));
			} catch (JSONException t) {
				t.printStackTrace();
			}
		}
	}

	public void removeRemoteProfile(String nick) {
		try {
			String config = preferences.getString(KEY_CONFIG, null);
			Map mapConfig = config == null ? new HashMap()
					: JSONUtils.decodeJSON(config);

			if (mapConfig == null) {
				return;
			}

			Map mapRemotes = (Map) mapConfig.get(KEY_REMOTES);
			if (mapRemotes == null) {
				return;
			}

			mapRemotes.remove(nick);

			savePrefs(mapConfig);

		} catch (Throwable t) {
			if (AndroidUtils.DEBUG) {
				t.printStackTrace();
			}
			VuzeEasyTracker.getInstance(context).logError(context, t);
		}
	}
}
