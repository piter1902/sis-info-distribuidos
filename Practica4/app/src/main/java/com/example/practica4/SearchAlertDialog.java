package com.example.practica4;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class SearchAlertDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;
    private final Activity activity;
    private EditText editText2Search;

    public SearchAlertDialog(Activity activity) {
        this.activity = activity;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate dialog
        View view = inflater.inflate(R.layout.search_dialog, null);
        this.editText2Search = view.findViewById(R.id.text2seach);

        // Create dialog
        builder.setView(view)
                .setMessage("Search photos by title")
                .setPositiveButton("Search!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // link to positive listener
                        String text = SearchAlertDialog.this.editText2Search.getText().toString();
                        if (!text.isEmpty()) {
                            listener.onDialogPositiveClick(SearchAlertDialog.this, text);
                        } else {
                            Toast.makeText(activity, "Please enter a string to search",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // link to negative listener
                        listener.onDialogNegativeClick(SearchAlertDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String text2Search);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
