package calegari.murilo.agendaescolar.subjects;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.BaseFragment;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private SubjectLineAdapter mAdapter;
    private SubjectDatabaseHelper subjectDatabase;
    private Group emptyStateGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = getView().findViewById(R.id.floatingActionButton);
        mRecyclerView = getView().findViewById(R.id.recyclerView);
        emptyStateGroup = getView().findViewById(R.id.emptyStateGroup);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSubject();
            }
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

        // Sets the toolbar name and item checked on nav bar
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportActionBar().setTitle(getString(R.string.subjects));
        MainActivity.navigationView.setCheckedItem(R.id.nav_subjects);
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
         The setupRecycler is throwing a silent error when returning from NewSubjectActivity or after unlocking,
         most likely because there's already a RecyclerView set up when it is being called. The error does not
         crash the app, but I should come up with a way of solving this.
         One method is to call setupRecycler at onViewCreated, but I'd need to manually updated the RecyclerView
         when coming from NewSubjectActivity, that's the hole point of an RecyclerView, actually.

         TODO: Put setupRecycler at onViewCreated and setup a new method for when returning from NewSubjectActivity
         */

        setupRecycler();
    }

    private void newSubject() {
        Intent newSubjectIntent = new Intent(getContext(), NewSubjectActivity.class);
        startActivity(newSubjectIntent);
    }

    private void setupRecycler() {
        subjectDatabase = new SubjectDatabaseHelper(getContext());

        // If list is empty, display empty state image

        List<Subject> subjectList = subjectDatabase.getAllSubjects();

        if(subjectList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyStateGroup.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyStateGroup.setVisibility(View.GONE);

            // Configures the layout manager so it becomes a list
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);

            mAdapter = new SubjectLineAdapter(new ArrayList<>(0));
            mRecyclerView.setAdapter(mAdapter);

            // Populates the list:

            mAdapter.setSubjects(subjectList);
        }

        subjectDatabase.close();
    }
}