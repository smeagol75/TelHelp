package smg75.hcv.com.telhelp.editor;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import smg75.hcv.com.telhelp.R;
import smg75.hcv.com.telhelp.RecyclerViewOnItemClickListener;

/**
 * Activity en la que cargamos los elementos de un juego a añadir o editar
 * @author Héctor Crespo Val
 */

public class PlantillaJuego extends AppCompatActivity {
    private List<String> items;
    private String seleccionado;
    boolean crearJuego;
    String JuegoaEditar =null;
    String directorioSeleccionado;
    EditText nombreJuego;
    RecyclerView recyclerView;
    String nombreDadoDeVuelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hacemos que siga siendo pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_selector_plantilla);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nombreJuego=(EditText) findViewById(R.id.tvNombreElementoAdd);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //Añadimos un elemento extra
                //Obtenemos los elementos del directorio de trabajo
               int numeroDeElementosAdapter = recyclerView.getAdapter().getItemCount();
               //Creamos sus archivos
                String archivoImagen = "nuevoelemento.jpg";
                String archivoAudio =  "nuevoelemento.3gp";
                String archivoImagenDestino =directorioSeleccionado + "/" +numeroDeElementosAdapter + "Extra.jpg";
                String archivoAudioDestino = directorioSeleccionado + "/" + numeroDeElementosAdapter + "Extra.3gp";
                //Destinos
                if(crearJuego){
                    archivoImagenDestino = getCacheDir() + "/template/" + numeroDeElementosAdapter + "Extra.jpg";
                    archivoAudioDestino = getCacheDir() + "/template/" + numeroDeElementosAdapter + "Extra.3gp";
                //Copiamos si es juego nuevo en un cache template, si no en el que estamos
                }

                copiarArchivoElementoNuevo(archivoImagen, archivoImagenDestino);
                copiarArchivoElementoNuevo(archivoAudio, archivoAudioDestino);

                Intent intentElemento = new Intent(getApplicationContext(), smg75.hcv.com.telhelp.editor.EditorElemento.class);
                //intentElemento.putExtra("carpeta",)
                String seleccion = numeroDeElementosAdapter + "Extra";

                intentElemento.putExtra("posicion",numeroDeElementosAdapter+1); //Posicion del array
                intentElemento.putExtra("tipoJuego",seleccionado); //tipo Juego (verbos, vocabulario...etc)
                intentElemento.putExtra("seleccionado",seleccion); //elemento seleccionado, nombre
                intentElemento.putExtra("directorioTrabajo",JuegoaEditar);//Directorio de trabajo
                String nombreDado = nombreJuego.getText().toString();
                intentElemento.putExtra("nombreDelJuego", nombreDado);
                intentElemento.putExtra("crearJuego",crearJuego);
                startActivity(intentElemento);



                Snackbar.make(view, "Elemento Creado, Personalízalo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent entrada = getIntent();
        //Recibimos los datos de entrada del intent
        if (entrada != null)
        {
            try{
                seleccionado  = entrada.getStringExtra("seleccionado");
                crearJuego=entrada.getBooleanExtra("crearJuego",crearJuego);
                directorioSeleccionado= entrada.getStringExtra("directorioSeleccionado");
                nombreDadoDeVuelta= entrada.getStringExtra("nombreDelJuego");

                //Accedemos a la toolbar para cambiar el título y le ponemos el directorio seleccionado
                setSupportActionBar(toolbar);
                //Lo cambiamos en mayúscula
                toolbar.setTitle(seleccionado.toUpperCase());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

       File outDir;


        //Lo primero creamos el directorio temporal comprobando si existe y demás
        if(crearJuego==true){
       outDir = new File(getCacheDir().getPath());
       //comprobamos si estás creando un juego pero no desde el principio para no copiar
            // de nuevo los archivos
       if (nombreDadoDeVuelta==null){
       CopiarAssets("template",outDir.toString());}
       else{
           nombreJuego.setText(nombreDadoDeVuelta);
       }

       // llenamos el array
        String directorioTemporal = outDir.toString()+"/template";
        JuegoaEditar = directorioTemporal;}
        //En caso de edición leemos el directorio del juego
        else{
            outDir = new File(directorioSeleccionado);
            JuegoaEditar =directorioSeleccionado;
            nombreJuego.setText(seleccionado);
            nombreJuego.setEnabled(false);
           //Cambiamos el texto y la acción del botón de grabar ya que si es edición lo que hacemos es
            //volver al principio
            Button botonAdd = (Button) findViewById(R.id.botonAdd);
            botonAdd.setText("Volver al inicio");
            //Ponemos que s e pueda ir atrás como en la ActionBar
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }


        //Leemos los elementos
        String [] archivos = leerArchivos(JuegoaEditar);
        //Definimos el array inicial
        items = new ArrayList<String>();
        for (int i=0;i<archivos.length;i++) {
            String elementoAdd = archivos[i];
            items.add(elementoAdd);
        }

        //obtenemos nuestro Reciclerview donde cargar la lista de juegos que encontremos
         recyclerView = (RecyclerView) findViewById(R.id.layout_opciones_plantilla);
        //Le asignamos el adapatador personalizado donde recoger los datos
        recyclerView.setAdapter(new PlantillaJuegoAdapter(items,JuegoaEditar, new RecyclerViewOnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                String nombreDado2 = nombreJuego.getText().toString();

                if (!nombreDado2.equals("")) {
                Intent intentElemento = new Intent(getApplicationContext(), smg75.hcv.com.telhelp.editor.EditorElemento.class);
                //intentElemento.putExtra("carpeta",)
                String seleccion = items.get(position);

                Toast toast = Toast.makeText(getApplicationContext(),"Has seleccionado la posicion "+seleccion, Toast.LENGTH_SHORT);
                toast.show();
                intentElemento.putExtra("posicion",position); //Posicion del array
                intentElemento.putExtra("tipoJuego",seleccionado); //tipo Juego (verbos, vocabulario...etc)
                intentElemento.putExtra("seleccionado",seleccion); //elemento seleccionado, nombre
                intentElemento.putExtra("directorioTrabajo",JuegoaEditar);//Directorio de trabajo
                String nombreDado = nombreJuego.getText().toString();
                intentElemento.putExtra("nombreDelJuego", nombreDado);
                intentElemento.putExtra("crearJuego",crearJuego);
                startActivity(intentElemento);
            }else
            {
                Toast toast = Toast.makeText(getApplicationContext(),"elige primero el nombre del juego! ", Toast.LENGTH_SHORT);
                toast.show();
            }

            }

            @Override
            public void onLongClick(View view, int position) {

                Snackbar.make(view, "Ninguna acción asociada a una presión larga ;)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        }));
        //le asignamos el layout al adapter de tipo lineal
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * CopiarAssets - Método que copia el contenido de la carpeta en la carpeta data de
     * nuestra aplicación para poder ir personalizandola
     * @param path ruta de los elementos de assests
     * @param pathSalida ruta donde se copiarán los elemntos de los assets
     */
    private void CopiarAssets(String path, String pathSalida) {
        AssetManager assetManager = this.getAssets();
        String assets[];
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copiarArchivo(path, pathSalida);
            } else {
                String fullPath = pathSalida + "/" + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    if (!dir.mkdir()) Toast.makeText(getApplicationContext(), "directorio externo no creado", Toast.LENGTH_LONG).show();;
                for (String asset : assets) {
                    CopiarAssets(path + "/" + asset, pathSalida);
                }
            }
        } catch (IOException ex) {
            Toast.makeText(getApplicationContext(), "IO Exception "+ ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * copiarArchivo - Métodopara copiar el archivo encontrado
     * @param nombreArchivo le pasamos el nombre del archivo
     * @param pathSalida ruta donde copiar el archivo
     */
    private void copiarArchivo(String nombreArchivo, String pathSalida) {
        AssetManager assetManager = this.getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(nombreArchivo);
            String newFileName = pathSalida + "/" + nombreArchivo;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception "+ e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * método que lee los archivos que tenemos en el directorio y un nombre único
     * @param directorio leer los archivos de un directorio en un array
     * @return Devuelve el array para el listview
     */
    private String[] leerArchivos(String directorio){

        //Defino la ruta donde busco los directorios
        File f = new File(directorio);
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();
        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        String[] archivos= new String[files.length/2];

        for (int i = 0; i < files.length; i++)
        {
            //Sacamos del array files un fichero
            File file = files[i];
            //Si es archivo...
            boolean existeNombre=false;

            String nombre=null;
            if (file.isFile())
                nombre = file.getName();
                nombre=nombre.substring(0,nombre.lastIndexOf("."));
               for(int j=0;j<archivos.length;j++){
                   if(archivos[j]!=null && archivos[j].equals(nombre)) existeNombre=true;
                   }
               if(existeNombre==false) {
                   for (int k=0;k<archivos.length;k++){
                       String comprobacion = archivos[k];
                       if(comprobacion==null ){
                           archivos[k]=nombre;
                       break;}
                   }
               }

        }


      return archivos;
    }

    /**
     * Método que graba o vuelve al origen en función de nuestro caso, crear un elemento o editarlo
     * @param view
     */
    public void addJuego(View view){


       if (crearJuego) {
           File dirDestino = new File(directorioSeleccionado + "/" + nombreJuego.getText());
           File dirOrigen = new File(getCacheDir(), "template");

           //compruebo si existe el directorio para no pisar alguna otra opción y muevo sus archvios
           if (!dirDestino.exists()) {
               dirDestino.mkdir();
               String[] ficheros = dirOrigen.list();
               for (int x = 0; x < ficheros.length; x++) {
                   try {
                       File fOrigen = new File(dirOrigen, ficheros[x]);
                       File fDestino = new File(dirDestino, ficheros[x]);
                       fOrigen.renameTo(fDestino);
                   } catch (Exception e) {
                       e.printStackTrace(System.out);
                   }
               }
               Snackbar.make(view, "Nuevo elemento creado en " + seleccionado + " Ve a la sección y juega", Snackbar.LENGTH_LONG)
                       .setAction("Action", null).show();

               Intent inicio = new Intent(getApplicationContext(), smg75.hcv.com.telhelp.ActivityPrincipal.class);
               startActivity(inicio);
           } else {
               Snackbar.make(view, dirDestino.toString() + " el directorio ya existe, elige otro nombre", Snackbar.LENGTH_LONG)
                       .setAction("Action", null).show();
           }
       } else {
           Snackbar.make(view, " Selecciona el Juego para ver los cambios", Snackbar.LENGTH_LONG)
                   .setAction("Action", null).show();

           Intent regreso = new Intent(getApplicationContext(), smg75.hcv.com.telhelp.ActivityPrincipal.class);
           startActivity(regreso);
       }

    }

    public void copiarArchivoElementoNuevo(String origen, String destino){
        AssetManager assetManager = this.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(origen);
            out = new FileOutputStream(destino);


            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception "+ e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}