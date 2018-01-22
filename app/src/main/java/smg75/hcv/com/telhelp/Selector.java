package smg75.hcv.com.telhelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Selector del juego dentro de la categoria editable y ampliable
 * @author Héctor Crespo Val
 */
public class Selector extends AppCompatActivity {

    private List<String> opciones;
    private String seleccionado;
    private String directorioSeleccionado;
    boolean crearJuego =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //hacemos que siga siendo pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Botón para añadir elementos
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Hay que pasar la opcion que tenemos para crear el directorio ahí.
                final Intent crear = new Intent(getApplicationContext(), smg75.hcv.com.telhelp.editor.PlantillaJuego.class);
                crear.putExtra("directorioSeleccionado",directorioSeleccionado);
                crear.putExtra("seleccionado",seleccionado);
                crearJuego= true;
                crear.putExtra("crearJuego",crearJuego);
                startActivity(crear);
            }
        });

       //Recogemos el Intent con elemento que origina la clase
        final Intent intent = getIntent();
        final Intent juego = new Intent(this, smg75.hcv.com.telhelp.JuegoEncuentra.class);
        final Intent juego2 = new Intent(this, smg75.hcv.com.telhelp.editor.PlantillaJuego.class);
        if (intent != null)
        {
            try{
               seleccionado  = intent.getStringExtra("seleccionado");
                //Accedemos a la toolbar para cambiar el título y le ponemos el directorio seleccionado
                setSupportActionBar(toolbar);
                //Lo cambiamos en mayúscula
                toolbar.setTitle(seleccionado.toUpperCase());
                }catch (Exception e){
                e.printStackTrace();
            }
         }

        leerDirectorios();

        //obtenemos nuestro Reciclerview donde cargar la lista de juegos que encontremos
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.layout_opciones);
        //Le asignamos el adapatador personalizado donde recoger los datos
        recyclerView.setAdapter(new SelectorJuegoAdapter(opciones,directorioSeleccionado, new RecyclerViewOnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                String seleccion = opciones.get(position);
                Toast toast = Toast.makeText(getApplicationContext(),"Has seleccionado "+ seleccion, Toast.LENGTH_SHORT);
                       toast.show();
                juego.putExtra("crearJuego",crearJuego);
                juego.putExtra("tipoJuego",seleccionado);
                juego.putExtra("seleccionado",seleccion);
                startActivity(juego);
            }

            @Override
            public void onLongClick(View view, int position) {
                String seleccion = opciones.get(position);
                Toast toast = Toast.makeText(getApplicationContext(),"Has seleccionado "+ seleccion +" para editarlo", Toast.LENGTH_SHORT);
                toast.show();
                //Ponerlo cuando sea editar crearJuego= false;
                crearJuego=false;
                juego2.putExtra("crearJuego",crearJuego);
                juego2.putExtra("tipoJuego",seleccionado);
                juego2.putExtra("seleccionado",seleccion);
                String directorioSeleccionadoconJueg0 = directorioSeleccionado +"/"+ seleccion;
                juego2.putExtra("directorioSeleccionado",directorioSeleccionadoconJueg0);
                startActivity(juego2);
            }
        }));
        //le asignamos el layout al adapter de tipo lineal
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * leerDirectorios
     * Función que lee los directorios del tipo de juego elegido y los guarda en un array
     */
    public void leerDirectorios(){
        //Obtenemos la ruta de nuestro archivo
        File outDir =  new File(getFilesDir().getPath());
        //Concatenamos para leer la ruta elegida
         directorioSeleccionado = outDir.toString()+"/datos/" + seleccionado +"/";

        // Array TEXTO donde guardaremos los nombres de los directorios
         opciones = new ArrayList<String>();
        //Defino la ruta donde busco los directorios
        File f = new File(directorioSeleccionado);
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();
        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++)
        {
            //Sacamos del array files un fichero
            File file = files[i];
            //Si es directorio...
            if (file.isDirectory())
                opciones.add(file.getName());
           }
    }


}
