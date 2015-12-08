package com.noname.mylearn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class DictDialog extends DialogFragment {
    List<Dictionary> dicts;

    // Интерфейс для передачи словря в активити
    public interface NoticeDialogListener {
        public void selectedDict(Dictionary dict);
    }

    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> data = new ArrayList<>(); // Список пунктов диалога
        data.add("New"); // Отдельный пункт для добавления словаря

        dicts = DBHelper.getInstance(this.getActivity()).loadDicts(100, 0); // Загрузка 100 словарей из бд
        for (Dictionary dict: dicts) {
            data.add(dict.getName());
        }

        // Адаптер для пунктов диалога
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, data);

        // Создание диалога
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setAdapter(adapter, myClickListener);
        return adb.create();
    }

    // Обработчик нажатия на пункт списка диалога
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int which) {
            // Нулевой элемент - добавление словаря
            if (which == 0) {
                Dictionary dict = new Dictionary();
                dict.setId( DBHelper.getInstance(DictDialog.this.getActivity()).insertDict(dict) );
                mListener.selectedDict(dict); // Устанавливаем словарь в активити создавшей диалог
            } else {
                mListener.selectedDict(dicts.get(which - 1)); // Устанавливаем словарь в активити создавшей диалог
            }
        }
    };
}
