package calegari.murilo.agendaescolar.subjectgrades;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectGradesDatabaseHelper;

public class SubjectGradesFragment extends Fragment {

	private String TAG = "SubjectGradesFragment";

	private RecyclerView mRecyclerView;
	private FloatingActionButton fab;
	private String gradeSubjectAbbreviation;
	private Group emptyStateGroup;
	private View view;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subject_grade, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.view = view;

		fab = view.findViewById(R.id.floatingActionButton);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		emptyStateGroup = view.findViewById(R.id.emptyStateGroup);

		Bundle bundle = this.getArguments();
		gradeSubjectAbbreviation = bundle.getString("subjectAbbreviation");

		fab.setOnClickListener(v -> {
			Intent newSubjectIntent = new Intent(view.getContext(), NewSubjectGradeActivity.class);

			newSubjectIntent.putExtra("subjectAbbreviation", gradeSubjectAbbreviation);
			v.getContext().startActivity(newSubjectIntent);
		});

		// Hides floating action button on scroll down
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0) {
					fab.hide();
				} else {
					fab.show();
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		setupRecycler();
	}



	private void setupRecycler() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(layoutManager);

		SubjectGradesLineAdapter mAdapter = new SubjectGradesLineAdapter(new ArrayList<>(0));

		mRecyclerView.setAdapter(mAdapter);

		// Populates the list
		SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(getContext());

		Cursor cursor = subjectGradesDatabase.getSubjectGradesData(gradeSubjectAbbreviation);

		Integer gradeIdIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_ID);
		Integer gradeDescriptionIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_DESCRIPTION);
		Integer obtainedGradeIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_OBTAINED);
		Integer maximumGradeIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_MAXIMUM);
		Integer isExtraCreditIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT);

		while(cursor.moveToNext()) {
			Integer gradeId = cursor.getInt(gradeIdIndex);
			String gradeDescription = cursor.getString(gradeDescriptionIndex);
			float obtainedGrade = cursor.getFloat(obtainedGradeIndex);
			float maximumGrade = cursor.getFloat(maximumGradeIndex);
			boolean isExtraCredit = (cursor.getInt(isExtraCreditIndex) == 1);

			SubjectGrade subjectGrade = new SubjectGrade(gradeId, gradeDescription, obtainedGrade, maximumGrade, isExtraCredit);
			subjectGrade.setSubjectAbbreviation(gradeSubjectAbbreviation);

			mAdapter.updateList(subjectGrade);
		}

		cursor.close();
		subjectGradesDatabase.close();

		if(mAdapter.getItemCount() == 0) {
			ImageView emptyStateImageView = view.findViewById(R.id.emptyStateImageView);

			// Some devices were throwing a ResourcesNotFoundException, so it's important to catch it
			try {
				emptyStateImageView.setImageResource(R.drawable.img_man_with_pencil_marking_checklist);
			} catch (Resources.NotFoundException e) {
				Log.e(TAG, e.getMessage());
			}

			mRecyclerView.setVisibility(View.GONE);
			emptyStateGroup.setVisibility(View.VISIBLE);
		} else {
			emptyStateGroup.setVisibility(View.GONE);
			mRecyclerView.setVisibility(View.VISIBLE);
		}
	}
}
