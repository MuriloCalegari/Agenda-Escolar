package calegari.murilo.agendaescolar.utils.verticalstepperform.steps;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import calegari.murilo.agendaescolar.subjects.Subject;
import ernestoyaquello.com.verticalstepperform.Step;

public class SubjectSpinnerStep extends Step<Subject> {

	private Spinner spinner;
	private ArrayAdapter<Subject> dataAdapter;
	private List<Subject> dataset;

	public SubjectSpinnerStep(String title, List<Subject> dataset) {
		super(title);
		this.dataset = dataset;
	}

	@Override
	protected View createStepContentLayout() {
		spinner = new Spinner(getContext());
		dataAdapter = new ArrayAdapter<Subject>(getContext(), android.R.layout.simple_spinner_item, dataset) {
			@Override
			public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				TextView dropDownView = (TextView) super.getDropDownView(position, convertView, parent);
				// Replace text with subject name
				dropDownView.setText(getItem(position).getName());
				return dropDownView;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView view = (TextView) super.getView(position, convertView, parent);
				// Replace text with subject name
				view.setText(getItem(position).getName());
				return view;
			}
		};
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		return spinner;
	}

	@Override
	public Subject getStepData() {
		return (Subject) spinner.getSelectedItem();
	}

	@Override
	public String getStepDataAsHumanReadableString() {
		return ((Subject) spinner.getSelectedItem()).getName();
	}

	@Override
	public void restoreStepData(Subject data) {

	}

	@Override
	protected IsDataValid isStepDataValid(Subject stepData) {
		return null;
	}

	@Override
	protected void onStepOpened(boolean animated) {

	}

	@Override
	protected void onStepClosed(boolean animated) {

	}

	@Override
	protected void onStepMarkedAsCompleted(boolean animated) {

	}

	@Override
	protected void onStepMarkedAsUncompleted(boolean animated) {

	}

	protected class SubjectSpinnerAdapter implements SpinnerAdapter {
		private final List<Subject> mSubjects;

		public SubjectSpinnerAdapter(List<Subject> mSubjects) {
			this.mSubjects = mSubjects;
		}

		@Nullable
		@Override
		public CharSequence[] getAutofillOptions() {
			return new CharSequence[0];
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public int getCount() {
			return mSubjects != null ? mSubjects.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mSubjects.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return null;
		}
	}


}