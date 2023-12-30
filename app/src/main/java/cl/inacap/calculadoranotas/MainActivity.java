package cl.inacap.calculadoranotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;

public class MainActivity extends AppCompatActivity {
    private int porcentajeActual = 0;
    private EditText notaTxt;
    private EditText porcentajeTxt;
    private Button agregarBtn;
    private Button limpiarBtn;
    private ListView notasLv;
    private List<Nota> notas = new ArrayList<>();
    private ArrayAdapter<Nota> adapter;
    private TextView promedioTxt;
    private LinearLayout promedioLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.promedioLl = findViewById(R.id.promedioLl);
        this.promedioTxt = findViewById(R.id.promedioTxt);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.porcentajeTxt = findViewById(R.id.porcentajeTxt);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notasLv = findViewById(R.id.notasLv);
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notas); //El primer parámetro indica el activity donde se va a ejecutar el ListView, el segundo parámetro indica que la lista es simple, osea solo soporta texto y el tercer parámetro indica cada contenido del ListView.
        this.notasLv.setAdapter(adapter);
        this.agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> errores = new ArrayList<>();
                String notaStr = notaTxt.getText().toString().trim();
                String porcentajeStr = porcentajeTxt.getText().toString().trim();
                int porcentaje = 0;
                double nota = 0;

                try {
                    nota = Double.parseDouble(notaStr);
                    if (nota < 1.0 || nota > 7.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("La nota debe ser un número entre 1.0 y 7.0");
                }

                try {
                    porcentaje = Integer.parseInt(porcentajeStr);
                    if (porcentaje < 1 || porcentaje > 100){
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("El porcentaje debe ser un número entre 1 y 100");
                }

                if (errores.isEmpty()) {
                    if (porcentaje + porcentajeActual > 100) {
                        Toast.makeText(MainActivity.this, "El porcentaje es mayor que 100", Toast.LENGTH_SHORT).show();
                    } 
                    else {
                        Nota n = new Nota();
                        n.setValor(nota);
                        n.setPorcentaje(porcentaje);
                        porcentajeActual+=porcentaje;
                        notas.add(n);
                        adapter.notifyDataSetChanged(); //Cada vez que se modifique el recurso asociado al adaptador se debe ejecutar este método.
                        mostrarPromedio();
                    }

                }
                else {
                    mostrarErrores(errores);
                }
            }
        });
        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                notaTxt.setText("");
                porcentajeTxt.setText("");
                promedioLl.setVisibility(View.INVISIBLE);
                porcentajeActual = 0;
                notas.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void mostrarErrores(List<String> errores) {
        String mensaje = "";
        for (String e : errores) {
            mensaje+="-" + e + "\n";
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this); //En el argumento se establece que Activity mostrará el mensaje.
        alertBuilder
            .setTitle("Error de validación") //Define el titulo.
            .setMessage(mensaje) //Define el mensaje del dialogo.
            .setPositiveButton("Aceptar", null) //Agrega el botón aceptar.
            .create() //Crea el Alert.
            .show(); //Se muestra el Alert.
    }

    private void mostrarPromedio() {
        double promedio = 0;
        for (Nota n : notas) {
            promedio+=n.getValor() * n.getPorcentaje() / 100;
        }
        this.promedioTxt.setText(String.format("%.1f", promedio)); //El primer argumento indica que se quiere el resultado con solo un decimal (Se esta formateando el resultado).
        if (promedio < 4.0) {
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorError)); //En el argumento se hace referencia al recurso de color.
        } else {
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorExitoso)); //En el argumento se hace referencia al recurso de color.
        }
        this.promedioLl.setVisibility(View.VISIBLE);
    }
}