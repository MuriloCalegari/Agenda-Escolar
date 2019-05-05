package calegari.murilo.agendaescolar.grades;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import calegari.murilo.agendaescolar.BaseFragment;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.subjects.NewSubjectActivity;
import calegari.murilo.agendaescolar.subjects.Subject;
import me.saket.inboxrecyclerview.InboxRecyclerView;
import me.saket.inboxrecyclerview.page.ExpandablePageLayout;
import me.saket.inboxrecyclerview.page.InterceptResult;
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks;

public class GradesFragment extends BaseFragment {

	public static InboxRecyclerView inboxRecyclerView;
	private GradesLineAdapter mAdapter;
	private SubjectDatabaseHelper subjectDatabase;
	private Group emptyStateGroup;
	private View view;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//returning our layout file
		return inflater.inflate(R.layout.fragment_grades, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.view = view;

		inboxRecyclerView = view.findViewById(R.id.inbox_recyclerview);
		emptyStateGroup = view.findViewById(R.id.emptyStateGroup);

		// Sets the toolbar name and item checked on nav bar
		AppCompatActivity activity = (AppCompatActivity) getContext();
		activity.getSupportActionBar().setTitle(getString(R.string.grades));
		MainActivity.navigationView.setCheckedItem(R.id.nav_grades);

		setupInboxRecyclerView();
		initInboxRecyclerView();

	}

	@Override
	public void onStart() {
		super.onStart();

		/*
		initInboxRecyclerView() needs to be separated and called alone at onStart()
		since inboxRecyclerView will crash if setExpandablePage is called twice.
		 */
		initInboxRecyclerView();
	}

	private void setupInboxRecyclerView() {
		inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		final ExpandablePageLayout expandablePageLayout = getView().findViewById(R.id.expandablePageLayout);

		// Trigger pull-to-collapse only if the page cannot be scrolled any further in the direction of scroll.
		// Code from https://github.com/saket/InboxRecyclerView/wiki/Pull-to-collapse
		expandablePageLayout.setPullToCollapseInterceptor((downX, downY, upwardPull) -> {
			Integer directionInt = upwardPull ? 1 : -1;
			boolean canScrollFurther = expandablePageLayout.findViewById(R.id.recyclerView).canScrollVertically(directionInt);
			return canScrollFurther ? InterceptResult.INTERCEPTED : InterceptResult.IGNORED;
		});

		inboxRecyclerView.setExpandablePage(expandablePageLayout);

		expandablePageLayout.addStateChangeCallbacks(new SimplePageStateChangeCallbacks() {

			AppCompatActivity activity = (AppCompatActivity) getView().getContext();

			@Override
			public void onPageAboutToCollapse(long collapseAnimDuration) {
				super.onPageAboutToCollapse(collapseAnimDuration);
				MainActivity.anim.reverse();
				activity.getSupportActionBar().setTitle(getString(R.string.grades));

				MainActivity.setDrawerIdleMode();
			}

			@Override
			public void onPageAboutToExpand(long expandAnimDuration) {
				super.onPageAboutToExpand(expandAnimDuration);
				MainActivity.anim.start();
			}

			@Override
			public void onPageCollapsed() {
				super.onPageCollapsed();
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

				/*
				If power saving mode is enabled, the InboxRecyclerView library won't handle collapsing properly
				So we need to reload the entire GradesFragment
				 */

				// Some versions of Android do not break this functionally, for example, Android Pie
				// TODO Check when this starts to break

				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
					PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
					if(activity instanceof MainActivity && powerManager.isPowerSaveMode()) {
						((MainActivity) activity).refreshCurrentFragment();
					}
				}
			}

			@Override
			public void onPageExpanded() {
				super.onPageExpanded();
			}
		});
	}

	private void initInboxRecyclerView() {
		subjectDatabase = new SubjectDatabaseHelper(getContext());

		// If list is empty, display empty state image

		List<Subject> subjectList = subjectDatabase.getAllSubjects();

		if(subjectList.isEmpty()) {
			inboxRecyclerView.setVisibility(View.GONE);
			emptyStateGroup.setVisibility(View.VISIBLE);

			Button newSubjectButton = view.findViewById(R.id.newSubjectButton);
			newSubjectButton.setOnClickListener(v -> {
				Intent newSubjectIntent = new Intent(getContext(), NewSubjectActivity.class);
				startActivity(newSubjectIntent);
			});
		} else {
			inboxRecyclerView.setVisibility(View.VISIBLE);
			emptyStateGroup.setVisibility(View.GONE);

			mAdapter = new GradesLineAdapter(new ArrayList<>(0), getContext(), getView());

			// Needed to avoid "Adapter needs to have stable IDs so that the expanded item can be restored across orientation changes." exception
			mAdapter.setHasStableIds(true);

			inboxRecyclerView.setAdapter(mAdapter);

			mAdapter.setSubjects(subjectList);
		}

		subjectDatabase.close();
	}
}
