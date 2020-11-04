package com.example.p1di.core;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1di.R;
import com.example.p1di.ui.adapter.MiAdaptadorRecView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MiAdaptadorRecView.ItemClickListener{

    private MiAdaptadorRecView mAdapter;
    List<Tarea> listaTareas = new ArrayList<>();
    List<String> valores = new ArrayList<>();
    EditText dateEd, tareaEd;
    Date fechaLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        RecyclerView recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setClickable(true);
        mAdapter = new MiAdaptadorRecView(this,valores);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);



        FloatingActionButton floatActionButtn = findViewById(R.id.floatingActionButton);
        floatActionButtn.setOnClickListener(v -> addTask());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> itemList = new ArrayList<>();
        ArrayList<Integer> listaPosiciones = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText(R.string.tareasVencidas);
        title.setBackgroundColor(getResources().getColor(R.color.primaryLightColor));
        title.setTextSize(25);
        title.setPadding(25,25,25,25);
        title.setTextColor(getResources().getColor(R.color.primaryTextColor));
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);

        View customLayout = getLayoutInflater().inflate(R.layout.borrar_layout, null);
        builder.setView(customLayout);


        for (Tarea tarea : listaTareas){
            Date fechaHoy = null, fechaTarea = null;
            int position;

            try {
                fechaHoy = formatoAFecha(patronFecha(Calendar.getInstance().getTime()));
                fechaTarea = formatoAFecha(patronFecha(tarea.getFechaLimite()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (fechaHoy.compareTo(fechaTarea) > 0 || fechaHoy.compareTo(fechaTarea) == 0 ){
                position = listaTareas.indexOf(tarea);
                listaPosiciones.add(position);
                itemList.add(valores.get(position));
            }
        }

        ArrayList<Integer> itemsSelected = new ArrayList<>();
        CharSequence[] cs = itemList.toArray(new CharSequence[itemList.size()]);
        builder.setMultiChoiceItems(cs, null, (dialog, which, isChecked) -> {
            if(isChecked){
                itemsSelected.add(which);
            }else if (itemsSelected.contains(which)){
                itemsSelected.remove(Integer.valueOf(which));
            }
        });
        builder.setPositiveButton(R.string.alert_eliminar, (dialog, which) -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage(R.string.confirmacion);
            builder1.setPositiveButton(R.string.alert_eliminar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i :itemsSelected){
                        valores.remove(i);
                        listaTareas.remove(i);
                        itemList.remove(i);
                        mAdapter.notifyDataSetChanged();
                    }
//                        for(int j=0; j < itemList.size() ; j++){
//                            if(valores.contains(itemList.get(j))){
//
//                            }
//                        }
                }
            });
            builder1.setNegativeButton(R.string.alert_cancelar, null);
            builder1.create().show();
        });
        builder.setNegativeButton(R.string.alert_cancelar,null);
        if(!itemList.isEmpty()) {
            builder.create().show();
        }
    }

    // Definir que hacer cuando se hace click en un item
    @Override
    public void onItemClick(View view, int position) {
        modificar(position);
    }

    public void addTask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.add_task, null);
        builder.setView(customLayout);

        TextView title = new TextView(this);
        title.setText(R.string.addTarea);
        title.setBackgroundColor(getResources().getColor(R.color.primaryLightColor));
        title.setTextSize(25);
        title.setPadding(25,25,25,25);
        title.setTextColor(getResources().getColor(R.color.primaryTextColor));
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);

        tareaEd = customLayout.findViewById(R.id.tareaEditText);
        dateEd = customLayout.findViewById(R.id.dateEditText);
        dateEd.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.dateEditText:
                    showDatePickerDialog();
                    break;
            }
        });
        builder.setPositiveButton(R.string.positiveButtonTarea, (dialog, which) -> {
            if(tareaEd.getText().toString().isEmpty() || dateEd.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(MainActivity.this,R.string.toastAñadir, Toast.LENGTH_LONG);
                toast.show();
            }else{
                Tarea tarea = new Tarea(tareaEd.getText().toString(),fechaLimite);
                listaTareas.add(tarea);
                String resumen = patronFecha(tarea.getFechaLimite()) +"  " +tarea.getTitulo();
                valores.add(resumen);
                mAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(R.string.negativeButtonTarea,null);
        builder.create().show();

    }

    public void modificar(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.add_task, null);
        builder.setView(customLayout);

        TextView title = new TextView(this);
        title.setText(R.string.modificar);
        title.setBackgroundColor(getResources().getColor(R.color.primaryLightColor));
        title.setTextSize(25);
        title.setPadding(25,25,25,25);
        title.setTextColor(getResources().getColor(R.color.primaryTextColor));
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);


        tareaEd = customLayout.findViewById(R.id.tareaEditText);
        tareaEd.setHint(listaTareas.get(position).getTitulo());
        dateEd = customLayout.findViewById(R.id.dateEditText);
        dateEd.setHint(patronFecha(listaTareas.get(position).getFechaLimite()));
        dateEd.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.dateEditText:
                    showDatePickerDialog();
                    break;
            }
        });
        builder.setPositiveButton(R.string.positiveButtonModificar, (dialog, which) -> {
            //Controlar que no esten vacias o que si está vacía tenga los mismos datos
            if(tareaEd.getText().toString().isEmpty()){
                if(dateEd.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(MainActivity.this,R.string.sinCambios, Toast.LENGTH_LONG);
                    toast.show();
                }else {
                    listaTareas.get(position).setFechaLimite(fechaLimite);
                    Tarea tarea = listaTareas.get(position);
                    String resumen = patronFecha(tarea.getFechaLimite()) + "  " + tarea.getTitulo();
                    valores.remove(position);
                    valores.add(position, resumen);
                    mAdapter.notifyDataSetChanged();
                }

            }else if(dateEd.getText().toString().isEmpty()){
                    listaTareas.get(position).setTitulo(tareaEd.getText().toString());
                    Tarea tarea = listaTareas.get(position);
                    String resumen = patronFecha(tarea.getFechaLimite()) + "  " + tarea.getTitulo();
                    valores.remove(position);
                    valores.add(position, resumen);
                    mAdapter.notifyDataSetChanged();

            }else{
                Tarea tarea = new Tarea(tareaEd.getText().toString(), fechaLimite);
                listaTareas.remove(position);
                listaTareas.add(position, tarea);

                listaTareas.get(position).setTitulo(tareaEd.getText().toString());
                listaTareas.get(position).setFechaLimite(fechaLimite);

                String resumen = patronFecha(tarea.getFechaLimite()) + "  " + tarea.getTitulo();
                valores.remove(position);
                valores.add(position, resumen);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.negativeButtonModificar,null);
        builder.create().show();
    }

    private void showDatePickerDialog(){
    int day, month, year;
    Calendar calendario = Calendar.getInstance();
    day = calendario.get(Calendar.DAY_OF_MONTH);
    month = calendario.get(Calendar.MONTH);
    year = calendario.get(Calendar.YEAR);
    DatePickerDialog datePickDiag = new DatePickerDialog(MainActivity.this, (view, year1, month1, dayOfMonth) -> {
        calendario.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendario.set(Calendar.MONTH, month1);
        calendario.set(Calendar.YEAR, year1);

        String selectedDate = dayOfMonth+"/"+ month1 +"/"+ year1;
        fechaLimite = calendario.getTime();
        dateEd.setText(selectedDate);
    },year,month,day);

    datePickDiag.show();

    }

    public String patronFecha(Date fechaSinPattern){
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(fechaSinPattern);
    }
    public Date formatoAFecha(String fechaEnString) throws ParseException {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(fechaEnString);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.buttonAddMenu :
                addTask();
                break;

            case R.id.buttonDelMenu :
                AlertDialog.Builder builder = new AlertDialog.Builder(this);


                TextView title = new TextView(this);
                title.setText(R.string.eliminarElementos);
                title.setBackgroundColor(getResources().getColor(R.color.primaryLightColor));
                title.setTextSize(25);
                title.setPadding(25,25,25,25);
                title.setTextColor(getResources().getColor(R.color.primaryTextColor));
                title.setGravity(Gravity.CENTER);
                builder.setCustomTitle(title);

                final ArrayList<Integer> itemsSelected = new ArrayList<>();
                CharSequence[] cs = valores.toArray(new CharSequence[valores.size()]);
                builder.setMultiChoiceItems(cs, null, (dialog, which, isChecked) -> {
                    if(isChecked){
                        itemsSelected.add(which);
                    }else if (itemsSelected.contains(which)){
                        itemsSelected.remove(Integer.valueOf(which));
                    }
                });

                builder.setPositiveButton(R.string.alert_eliminar, (dialog, which) -> {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage(R.string.confirmacion);
                    builder1.setPositiveButton(R.string.alert_eliminar, (dialog1, which1) -> {
                        for (int i :itemsSelected){
                            valores.remove(i);
                            listaTareas.remove(i);
                            mAdapter.notifyDataSetChanged();

                        }
                    });
                    builder1.setNegativeButton(R.string.alert_cancelar, null);
                    builder1.create().show();
                });
                builder.setNegativeButton(R.string.alert_cancelar,null);
                builder.create().show();
                break;

        }
        return true;
    }
}