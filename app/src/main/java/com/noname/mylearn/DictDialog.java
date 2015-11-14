package com.noname.mylearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class DictDialog extends DialogFragment {
    String data[] = { "one", "two", "three", "four" };

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Title!")
                .setItems(data, myClickListener);
        return adb.create();
    }

    // обработчик нажатия на пункт списка диалога
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            // выводим в лог позицию нажатого элемента
            Log.d("dlg", "which = " + which);
        }
    };
}
