package calegari.murilo.agendaescolar;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Field;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGradesFragment;
import me.saket.inboxrecyclerview.PullCollapsibleActivity;
import me.saket.inboxrecyclerview.page.ExpandablePageLayout;
import me.saket.inboxrecyclerview.page.InterceptResult;
import me.saket.inboxrecyclerview.page.StandaloneExpandablePageLayout;

public class BaseFragmentActivity extends AppCompatActivity {

	static FragmentManager fragmentManager;
	String fragmentClassName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_fragment);
		fragmentManager = getSupportFragmentManager();

		//androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);

		try {
			fragmentClassName = getIntent().getStringExtra("fragment");
			Log.d(getLocalClassName(), "Received string extra "+ fragmentClassName);

			if (fragmentClassName.equals(SubjectGradesFragment.class.getName())) {
				String subjectAbbreviation = getIntent().getStringExtra("subjectAbbreviation");
				//actionBar.setTitle(subjectAbbreviation);
				Bundle bundle = new Bundle();
				bundle.putString("subjectAbbreviation", subjectAbbreviation);
				bundle.putBoolean("shouldCollapse", false);

				startFragment(Class.forName(fragmentClassName), bundle);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void startFragment(Class fragmentClass) {

		Fragment fragment = null;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		if (fragment != null) {
			fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
		}

	}

	public void startFragment(Class fragmentClass, Bundle bundle) {

		Log.d(getLocalClassName(), "Starting fragment " + fragmentClass.getSimpleName());

		Fragment fragment = null;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		if (fragment != null) {
			fragment.setArguments(bundle);
			fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
		}

	}

}
