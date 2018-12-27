package calegari.murilo.agendaescolar.subjecthelper;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjecthelper.steps.SubjectAbbreviationStep;
import calegari.murilo.agendaescolar.subjecthelper.steps.SubjectNameStep;
import calegari.murilo.agendaescolar.subjecthelper.steps.SubjectProfessorStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class EditSubjectActivity extends AppCompatActivity implements StepperFormListener {

    private SubjectNameStep newSubjectName;
    private SubjectAbbreviationStep newSubjectAbbreviation;
    private SubjectProfessorStep newSubjectProfessor;
    private VerticalStepperFormView verticalStepperForm;

    String subjectName;
    String subjectProfessor;
    String subjectAbbreviation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Finishes the Activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        Used library: VerticalStepperForm, available at:
        https://github.com/ernestoyaquello/VerticalStepperForm
        */

        // Gets information from which input the user would like to edit
        subjectName = getIntent().getStringExtra("subjectName");
        subjectProfessor = getIntent().getStringExtra("subjectProfessor");
        subjectAbbreviation = getIntent().getStringExtra("subjectAbbreviation");

        // Create the steps
        newSubjectName = new SubjectNameStep(getResources().getString(R.string.name));
        newSubjectAbbreviation = new SubjectAbbreviationStep(getResources().getString(R.string.abbreviation), true, subjectAbbreviation);
        newSubjectProfessor = new SubjectProfessorStep(getResources().getString(R.string.professor));

        // Find the form view, set it up and initialize it.
        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm
                .setup(this, newSubjectName, newSubjectAbbreviation, newSubjectProfessor)
                .lastStepNextButtonText(getString(R.string.subject_confirm_save_button))
                .displayCancelButtonInLastStep(true)
                .lastStepCancelButtonText(getString(R.string.cancel))
                .stepNextButtonText(getString(R.string.next))
                .init();

        reloadSteps(subjectName, subjectProfessor, subjectAbbreviation);
    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.

        // Sends data to database
        SubjectDatabaseHelper subjectDbHelper = new SubjectDatabaseHelper(this);
        Integer result = subjectDbHelper.removeData(subjectAbbreviation); // Deletes entry from database so it will be regenerated
        subjectDbHelper.insertData(
                newSubjectName.getStepDataAsHumanReadableString(),
                newSubjectAbbreviation.getStepDataAsHumanReadableString(),
                newSubjectProfessor.getStepDataAsHumanReadableString());

        finish();
    }

    @Override
    public void onCancelledForm() {
        // This method will be called when the user clicks on the cancel button of the form.
        finish();
    }

    public void reloadSteps(String subjectName, String subjectProfessor, String subjectAbbreviation) {
        newSubjectName.restoreStepData(subjectName);
        newSubjectProfessor.restoreStepData(subjectProfessor);
        newSubjectAbbreviation.restoreStepData(subjectAbbreviation);
    }
}
