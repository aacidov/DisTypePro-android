package ru.ibakaidov.distypepro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aacidov on 29.05.16.
 */
public class CategoryController implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private final String delete;
    public int currentCategory = 0;
    private ListView categoriesLV;
    private Context cxt;
    private DB db;
    private String addCategory;
    private WordsController wc;
    private String editCategory;

    public CategoryController(ListView categoriesLV, Context cxt, DB db, String addCategory, String editCategory, String delete) {
        this.categoriesLV = categoriesLV;
        this.cxt = cxt;
        this.db = db;
        this.addCategory = addCategory;
        this.editCategory = editCategory;
        this.delete = delete;
    }

    public void setWC(WordsController wc) {
        this.wc = wc;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (categoriesLV.getAdapter().getCount() - 1 == position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
            builder.setTitle(R.string.add_category);
            final EditText input = new EditText(cxt);

            builder.setView(input);


            builder.setPositiveButton(R.string.add_category, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.createCategory(input.getText().toString());
                    loadCategories();

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        currentCategory = position;
        wc.loadStatements();
    }

    public void loadCategories() {

        ArrayList<String> categoriesList = db.getCategories();
        categoriesList.add(addCategory);
        String[] categories = categoriesList.toArray(new String[categoriesList.size()]);

        this.categoriesLV.setAdapter(new ArrayAdapter<String>(cxt, R.layout.support_simple_spinner_dropdown_item, categories));

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == parent.getAdapter().getCount() - 1 || position == 0) return true;
        final String selected = ((TextView) view).getText().toString();
        final AlertDialog.Builder builder = new AlertDialog.Builder(cxt);


        final LinearLayout ll = new LinearLayout(cxt);
        ll.setOrientation(LinearLayout.VERTICAL);


        String[] managerStrings = new String[]{editCategory, delete};
        final ListView managerLV = new ListView(cxt);

        managerLV.setAdapter(new ArrayAdapter<String>(cxt, R.layout.support_simple_spinner_dropdown_item, managerStrings));


        ll.addView(managerLV);


        builder.setView(ll);


        final AlertDialog mainDialog = builder.create();
        mainDialog.setTitle(R.string.edit_category);
        mainDialog.show();

        managerLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        //edit category
                        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
                        builder.setTitle(R.string.edit_category);
                        final EditText input = new EditText(cxt);
                        input.setText(selected);
                        builder.setView(input);


                        builder.setPositiveButton(R.string.edit_category, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                db.editCategory(selected, input.getText().toString());
                                mainDialog.hide();
                                loadCategories();

                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        final AlertDialog editDialog = builder.create();
                        editDialog.show();

                        break;
                    case 1:
                        //delete statement

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dDialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:

                                        db.deleteCategory(selected);
                                        mainDialog.hide();
                                        loadCategories();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        };

                        builder = new AlertDialog.Builder(cxt);
                        builder.setMessage(R.string.delete).setPositiveButton(R.string.yes, dialogClickListener)
                                .setNegativeButton(R.string.no, dialogClickListener).show();
                        break;
                }
            }
        });
        return true;
    }

}
