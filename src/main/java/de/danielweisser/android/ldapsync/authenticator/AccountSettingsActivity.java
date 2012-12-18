package de.danielweisser.android.ldapsync.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import de.danielweisser.android.ldapsync.Constants;
import de.danielweisser.android.ldapsync.R;
import de.danielweisser.android.ldapsync.client.LDAPServerInstance;
import de.danielweisser.android.ldapsync.platform.ContactManager;

public class AccountSettingsActivity extends PreferenceActivity {

	private static final String TAG = "AccountSettingsActivity";
	private String accountName;
	private LDAPServerInstance ldapServerInstance;

	// TODO Add title
	// TODO Remove Done if not in create mode
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Account account = (Account) getIntent().getParcelableExtra("account");
		if (account != null) {
			accountName = account.name;
		} else {
			accountName = getIntent().getStringExtra("accountname");
			ldapServerInstance = (LDAPServerInstance) getIntent().getSerializableExtra("ldapServer");
		}
		getPreferenceManager().setSharedPreferencesName(accountName);
		Log.i(TAG, "Get preferences for " + accountName);

		addPreferencesFromResource(R.xml.preference_resources);
		setContentView(R.layout.preference_layout);
		// this.getIntent().getExtras()) and the key "account

		// ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, baseDNs);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// mBaseDNSpinner.setAdapter(adapter);
		// set the preferences file name
	}

	/**
	 * Called when the user touches the done button.
	 * 
	 * @param view
	 *            The Next button for which this method is invoked
	 */
	public void createAccount(View view) {
		Log.i(TAG, "finishLogin()");
		final Account account = new Account(accountName, Constants.ACCOUNT_TYPE);

		Bundle userData = new Bundle();
		userData.putString(Constants.PARAM_USERNAME, ldapServerInstance.bindDN);
		userData.putString(Constants.PARAM_PORT, ldapServerInstance.port + "");
		userData.putString(Constants.PARAM_HOST, ldapServerInstance.host);
		userData.putString(Constants.PARAM_ENCRYPTION, ldapServerInstance.encryption + "");
		AccountManager mAccountManager = AccountManager.get(this);
		mAccountManager.addAccountExplicitly(account, ldapServerInstance.bindPW, userData);

		// Set contacts sync for this account.
		ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
		ContactManager.makeGroupVisible(account.name, getContentResolver());
		setResult(RESULT_OK, new Intent());
		finish();
	}
}