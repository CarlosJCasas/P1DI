package com.example.p1di.core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

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

    private RecyclerView recyclerView;
    private MiAdaptadorRecView mAdapter;
    List<Tarea> listaTareas = new ArrayList<>();
    List<String> valores = new ArrayList<>();
    EditText dateEd, tareaEd;
    Date fechaLimite;
    int posicion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setClickable(true);
        mAdapter = new MiAdaptadorRecView(this,valores);
        mAdapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(mAdapter);



        FloatingActionButton floatActionButtn = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatActionButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            addTask();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.borrar_layout, null);
        builder.setView(customLayout);
        ListView listaDescartes = customLayout.findViewById(R.id.listaDescartados);
        ArrayList<String> itemList = new ArrayList<>();
        ArrayAdapter<String> borrarAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, itemList);
        listaDescartes.setAdapter(borrarAdapter);


        for (Tarea tarea : listaTareas){
            Date fechaHoy = null, fechaTarea = null;
            int position = 0;
            ArrayList<Integer> listaPosiciones = new ArrayList<>();
            try {
                fechaHoy = formatoAFecha(patronFecha(Calendar.getInstance().getTime()));
                fechaTarea = formatoAFecha(patronFecha(tarea.getFechaLimite()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (fechaHoy.compareTo(fechaTarea) > 0 || fechaHoy.compareTo(fechaTarea) == 0 ){
                position = listaTareas.indexOf(tarea);
                listaPosiciones.add(position);
            }
        }
        //Seleccionar varios, ver PMDM 'clear'


        builder.create().show();

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
        builder.setTitle(R.string.addTarea);
        tareaEd = customLayout.findViewById(R.id.tareaEditText);
        dateEd = customLayout.findViewById(R.id.dateEditText);
        dateEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dateEditText:
                        showDatePickerDialog();
                        break;
                }
            }
        });
        builder.setPositiveButton(R.string.positiveButtonTarea, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        builder.setTitle(R.string.modificar);
        tareaEd = customLayout.findViewById(R.id.tareaEditText);
        tareaEd.setHint(listaTareas.get(position).getTitulo());
        dateEd = customLayout.findViewById(R.id.dateEditText);
        dateEd.setHint(patronFecha(listaTareas.get(position).getFechaLimite()));
        dateEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dateEditText:
                        showDatePickerDialog();
                        break;
                }
            }
        });
        builder.setPositiveButton(R.string.positiveButtonModificar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Controlar que no esten vacias o que si está vacía tenga los mismos datos
                Tarea tarea = new Tarea(tareaEd.getText().toString(),fechaLimite);
                listaTareas.remove(position);
                listaTareas.add(position,tarea);

                listaTareas.get(position).setTitulo(tareaEd.getText().toString());
                listaTareas.get(position).setFechaLimite(fechaLimite);

                String resumen = patronFecha(tarea.getFechaLimite())+"  "+tarea.getTitulo();
                valores.remove(position);
                valores.add(position,resumen);
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
    DatePickerDialog datePickDiag = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendario.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            calendario.set(Calendar.MONTH,month);
            calendario.set(Calendar.YEAR,year);

            String selectedDate = dayOfMonth+"/"+month+"/"+year;
            fechaLimite = calendario.getTime();
            dateEd.setText(selectedDate);
        }
    },year,month,day);

    datePickDiag.show();

    }

    public String patronFecha(Date fechaSinPattern){
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String fechaConFormato = format.format(fechaSinPattern);
    return fechaConFormato;
    }
    public Date formatoAFecha(String fechaEnString) throws ParseException {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date fechaEnDate = format.parse(fechaEnString);

        return fechaEnDate;
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
                final ArrayList<Integer> itemsSelected = new ArrayList<>();
                CharSequence[] cs = valores.toArray(new CharSequence[valores.size()]);
                builder.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            itemsSelected.add(which);
                        }else if (itemsSelected.contains(which)){
                            itemsSelected.remove(Integer.valueOf(which));
                        }
                    }
                });
                builder.setPositiveButton(R.string.alert_eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i :itemsSelected){
                            valores.remove(i);
                            listaTareas.remove(i);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                builder.setNegativeButton(R.string.alert_cancelar,null);
                builder.create().show();
                break;

        }
        return true;
    }
}