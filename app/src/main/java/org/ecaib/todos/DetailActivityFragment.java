package org.ecaib.todos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ecaib.todos.provider.notes.NotesColumns;
import org.ecaib.todos.provider.notes.NotesContentValues;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private TextView etTitle;
    private TextView etDescription;
    private long itemId = -1;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        etTitle = (TextView) view.findViewById(R.id.etTitle);
        etDescription = (TextView) view.findViewById(R.id.etDescription);

        Intent i = getActivity().getIntent();
        itemId = i.getLongExtra("item_id", -1);
        if (itemId != -1) {
            loadItem();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miOK) {
            saveItem();
            getActivity().finish();
        }

        if (id == R.id.miCancel) {
            if(itemId != -1) {
                deleteItem();
            }
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {

        //NotesContentValues es una clase que te crea especializado para esa base de datos
        NotesContentValues values = new NotesContentValues(); //ContentValues para insertar
        values.putTitle(etTitle.getText().toString());
        values.putDescription(etDescription.getText().toString());

        if (itemId == -1) {   //Si no tiene nada la id del intent es que creareemos una nueva nota
            insertItem(values);
        } else {                //Sino es que el intent lleva algo y es para modificar
            updateItem(values);
        }
    }

    private void loadItem() {
        Cursor cursor = getContext().getContentResolver().query(  //Cursor es para sacar informacion
                NotesColumns.CONTENT_URI,
                null,     //SELECT en este caso seria COMO SELECT *
                NotesColumns._ID + " = ?",  // PARTE DEL WHERE   "Lo del interrogante es por seguridad"
                new String[]{String.valueOf(itemId)},  //Campo donde sustituira los interrogantes
                null
        );

        if (cursor != null) {
            //Ens situem en el primer valor
            cursor.moveToNext();

            String title = cursor.getString(cursor.getColumnIndex(NotesColumns.TITLE)); //Para devolver el valor del indice
            etTitle.setText(title);
            String description = cursor.getString(cursor.getColumnIndex(NotesColumns.DESCRIPTION));
            etDescription.setText(description);
        }
    }

    private void updateItem(NotesContentValues values) {
        getContext().getContentResolver().update(
                NotesColumns.CONTENT_URI,
                values.values(),
                NotesColumns._ID + " = ?",
                new String[]{String.valueOf(itemId)});
    }

    private void insertItem(NotesContentValues values) {
        getContext().getContentResolver().insert(
                NotesColumns.CONTENT_URI,
                values.values());
    }

    private void deleteItem() {
        getContext().getContentResolver().delete(
                NotesColumns.CONTENT_URI,
                NotesColumns._ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );
    }
}
