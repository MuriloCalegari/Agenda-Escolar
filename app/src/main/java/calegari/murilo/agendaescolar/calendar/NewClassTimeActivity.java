package calegari.murilo.agendaescolar.calendar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.DatabaseHelper;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.DayPickerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.SubjectSpinnerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.TimeStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewClassTimeActivity extends AppCompatActivity implements StepperFormListener {

	private SubjectSpinnerStep spinnerStep;
	private TimeStep endTimeStep;
	private TimeStep startTimeStep;
	private DayPickerStep dayPickerStep;
	private SubjectSpinnerStep subjectSpinnerStep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_class_event);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Finishes the Activity
		toolbar.setNavigationOnClickListener((v) -> finish());

		// Create the steps
		SubjectDatabaseHelper subjectDatabaseHelper = new SubjectDatabaseHelper(this);
		subjectSpinnerStep = new SubjectSpinnerStep(getString(R.string.subject), subjectDatabaseHelper.getAllSubjects());
		subjectDatabaseHelper.close();

		dayPickerStep = new DayPickerStep(getString(R.string.day_of_the_week), true);
		startTimeStep = new TimeStep(getString(R.string.start_time), getString(R.string.which_time_start));
		endTimeStep = new TimeStep(getString(R.string.end_time), getString(R.string.which_time_end));

		// Find the form view, set it up and initialize it.
		VerticalStepperFormView verticalStepperForm = findViewById(R.id.stepper_form);
		verticalStepperForm
				.setup(this, subjectSpinnerStep, dayPickerStep, startTimeStep, endTimeStep)
				.lastStepNextButtonText(getString(R.string.class_event_save_button))
				.displayCancelButtonInLastStep(true)
				.lastStepCancelButtonText(getString(R.string.cancel))
				.stepNextButtonText(getString(R.string.next))
				.init();

	}

	@Override
	public void onCompletedForm() {
		int dayOfTheWeek = 0;

		// loops through the marked days and get the unique day marked
		for(int i = 0; i < dayPickerStep.getStepData().length; i++) {
			if(dayPickerStep.getStepData()[i]) {
				dayOfTheWeek = i + 1; // +1 because the libraries counts day of the week from 1
			}
		}

		ClassTime classTime = new ClassTime(
				subjectSpinnerStep.getStepData().getId(),
				dayOfTheWeek,
				startTimeStep.getStepData().getDateTime(),
				endTimeStep.getStepData().getDateTime()
		);

		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		databaseHelper.insertClassTime(classTime);
		databaseHelper.close();
		finish();
	}

	@Override
	public void onCancelledForm() {
		finish();
	}
}
