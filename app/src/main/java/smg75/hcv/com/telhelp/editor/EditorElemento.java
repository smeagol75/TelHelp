package smg75.hcv.com.telhelp.editor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import smg75.hcv.com.telhelp.R;
/**
 * Activity para editar los elementos individuales
 * @author Héctor Crespo Val
 */
public class EditorElemento extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 12;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 17;
    private static final int SELECCIONADA_CAMARA = 126;
    Intent intentEntrada;
    int posicionArrayEntrada;
    String JuegoSeleccionadoOrig;
    String nombreElemntoSeleccionado;
    String directorioTrabajo;
    EditText nombre;
    ImageView imagenElegida;
    ImageButton botonGrabar;
    ImageButton botonPlay;
    String archivoAudio;
    MediaRecorder miGrabacion;
    boolean grabando=false;
    boolean imagenGirada=false;
    boolean crearJuego;
    String rutaArchivoAudioOriginal;
    String nombreDelJuego;
    boolean audioCambiado=false;
    boolean imagenCambiada=false;
    boolean permisoOk = false;
    File fichABorrar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hacemos que siga siendo pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_editor_elemento);
        //Recogemos los valores que nos llegan en el intent
        intentEntrada= getIntent();
        if (intentEntrada != null)
        {
            try{

                posicionArrayEntrada= intentEntrada.getIntExtra("posicion",posicionArrayEntrada);//Posicion del array
                JuegoSeleccionadoOrig = intentEntrada.getStringExtra("tipoJuego");//tipo Juego (verbos, vocabulario...etc)
                nombreElemntoSeleccionado = intentEntrada.getStringExtra("seleccionado");//elemento seleccionado, nombre
                directorioTrabajo = intentEntrada.getStringExtra("directorioTrabajo");//Directorio de trabajo
                nombreDelJuego = intentEntrada.getStringExtra("nombreDelJuego");//Nombre del Juego
                crearJuego = intentEntrada.getBooleanExtra("crearJuego", crearJuego);//si estamos creando juego
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //Inicializo los elementos que vamos a usar
        nombre = (EditText) findViewById(R.id.nombreElemento);
        nombre.setText(nombreElemntoSeleccionado);
        imagenElegida = (ImageView) findViewById(R.id.imageViewElemento);
        botonGrabar = (ImageButton) findViewById(R.id.imageButtonGrabarParar);
        botonPlay = (ImageButton) findViewById(R.id.imageButtonElementoPlay);

        //Solicitamos permiso de lectura escritura que vamos a necesitar
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permisoOk = true;

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


            }
        }
        //Comprobamos si es la portada para que no puedan modificar el nombre y se mantenga la funcionalidad.
        if (nombreElemntoSeleccionado.equals("portada")){
            nombre.setEnabled(false);}
        //Cargamos los archivos del elemento
        getSupportActionBar().setTitle("Añadir elemento a la categoria: " + JuegoSeleccionadoOrig.toUpperCase());
        archivoAudio = getCacheDir()+"/temp.3gp";
       //Establecemos los valores iniciales
        rutaArchivoAudioOriginal = directorioTrabajo +"/" +nombreElemntoSeleccionado +".3gp";
        String rutaImagenOriginal = directorioTrabajo +"/" +nombreElemntoSeleccionado +".jpg";
        Bitmap bmOrig = BitmapFactory.decodeFile(rutaImagenOriginal);
        imagenElegida.setImageBitmap(bmOrig);

    }

    /**
     * Método onClick para cargar imagen de la galería
     * @param view View para alimentar el onclick
     */

    public void ClickGaleria(View view) {
        imagenElegida.setImageDrawable(null);
       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }
    @SuppressLint("WrongConstant")
    /**
     * Método onClick para cargar imagen de la cámara
     * @param view View para alimentar el onclick
     */

    public void clickCamara(View view){
        //Pedimos permisos para usar la cámara
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                permisoOk = true;

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);


            }
        }




        int permisoCamara=1234;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permisoCamara  = checkSelfPermission(Manifest.permission.CAMERA);
        }
        if (permisoCamara == PackageManager.PERMISSION_GRANTED){
            imagenElegida.setImageDrawable(null);
            //Capturamos la imagen y metemos sus valores en un cursor con un Content provider por compatir datos
            //entre dos aplicaciones
            Uri mCapturedImageURI;
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver()
                    .insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            Intent intent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent1.putExtra(MediaStore.EXTRA_OUTPUT,
                    mCapturedImageURI);
            startActivityForResult(intent1, SELECCIONADA_CAMARA);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),"Camera Permission Not Granted", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
        @Override
        /**
         * OnActivityResult En función del resultado con las imágenes pues hacemos lo que proceda
         */

        protected void onActivityResult(int requestCode, int resultCode, Intent data){
      //-si recibimos una selección de imagen de galería ya nos llega la imagen con un content provider y cortamos
            //directamente
            if(resultCode == RESULT_OK && requestCode == 100){
                beginCrop(data.getData());
            }
            //Si es de la cámara pues recibimos el cursor, usamos la imagen para la copia y la borramos
            else if(resultCode == RESULT_OK && requestCode == SELECCIONADA_CAMARA){

               // beginCrop(getCaptureUri());
                Uri mCapturedImageURI = null;
                String photoPath = "";

                Cursor cursor = getContentResolver().query
                        (MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Images.Media.DATA,
                                        MediaStore.Images.Media.DATE_ADDED,
                                        MediaStore.Images.ImageColumns.ORIENTATION},
                                MediaStore.Images.Media.DATE_ADDED,
                                null,
                                "date_added ASC");

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        mCapturedImageURI = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                        photoPath = mCapturedImageURI.toString();
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Uri origen=Uri.parse("file://"+photoPath);
                Crop.of(origen, destination).asSquare().start(this);

                fichABorrar = new File(photoPath);

            }
        //Si está el proceso terminado procedemos a hacer el cortado de la nueva imagen recortada
            else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);

            }
 }

    /**
     * beginCrop, libreria externa para recortar las imágenes en cuadrado para adecuarlas a la necesidad
     * @param source Uri con la imagen que vamos a recortar con la librería crop
     */

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.M)
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        int permisolecturaexterna=0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permisolecturaexterna  = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permisolecturaexterna == PackageManager.PERMISSION_GRANTED){
            Crop.of(source, destination).asSquare().start(this);
        }
       else {
            Toast toast = Toast.makeText(getApplicationContext(),"Read External Storage Permission Not Granted", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * Manejador de recorte de la librería crop
     * @param resultCode código Resultado de la operación de recorte
     * @param result Intent con el que hacer el recorte
     */

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imagenElegida.setImageURI(Crop.getOutput(result));
            imagenCambiada=true;
            if(fichABorrar!=null && fichABorrar.exists()) fichABorrar.delete();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Botón Grabar que comienza la grabación y la para si volvemos a pulsar el botón
     * @param view para poder manejar el evento Onclick
     */
    @SuppressLint("WrongConstant")
    public void clickGrabar(View view){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                permisoOk = true;

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);


            }
        }

        int  permisoGrabar=123;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permisoGrabar  = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        }
        if (permisoGrabar == PackageManager.PERMISSION_GRANTED) {
            if (grabando == false) {

                try {

                    arrancarGrabadoraAudio();

                    miGrabacion.prepare();
                    miGrabacion.start();
                    grabando = true;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "La grabación comenzó vuelve a pulsar para detenerla", Toast.LENGTH_LONG).show();

            } else {

                miGrabacion.stop();
                miGrabacion.release();
                miGrabacion = null;
                grabando = false;
                audioCambiado = true;
                Toast.makeText(getApplicationContext(), "Audio grabado con éxito", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No hay permiso de Grabación", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /**
     * Botón para reproducir la grabación
     * @param view Para manejar el evento OnClick
     * @throws IllegalArgumentException Excepcíon a controlar
     * @throws SecurityException Excepcíon a controlar
     * @throws IllegalStateException Excepcíon a controlar
     */

    public void onClickPlay (View view)throws  IllegalArgumentException,SecurityException,
                IllegalStateException {
            MediaPlayer m = new MediaPlayer();

            try {
               if(audioCambiado){
                m.setDataSource(archivoAudio);}
                else{
                   m.setDataSource(rutaArchivoAudioOriginal);
               }
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            try {
                m.prepare();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            m.start();
            Toast.makeText(getApplicationContext(), "Reproduciendo de audio", Toast.LENGTH_LONG).show();
        }

    /**
     * Método que configura los parámetros de grabación
     */
    public void arrancarGrabadoraAudio(){

    miGrabacion =new MediaRecorder();
    miGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
    miGrabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    miGrabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    miGrabacion.setOutputFile(archivoAudio);
}

    /**
     * Botón para girar la imagen
     * @param view View para manejar el evento Click
     */

    public void  girarImagen(View view){
    //imagenElegida.setRotation(imagenElegida.getRotation() + 90);
        //imagenElegida.buildDrawingCache();
       // Bitmap bitmap = imagenElegida.getDrawingCache();
        imagenElegida.setRotation(90);
        imagenGirada = true;
}

    /**
     * Método para grabar el elemento a añadir a un juego dentro de su categoría
     * @param view View para manejar el evento Click
     */
    public void grabarElemento(View view){
        String nombreElemento = nombre.getText().toString();
        File ficheroAudio;
        if(audioCambiado){
            ficheroAudio = new File(archivoAudio);}
        else {
            ficheroAudio = new File(rutaArchivoAudioOriginal);
        }
        //Comprobamos que exista algún cambio en el elemento para empezar a grabar o no hacer nada
        if (imagenCambiada || audioCambiado || !nombreElemento.equals(nombreElemntoSeleccionado)) {
           if(imagenCambiada) {


               //Obtenemos la imagen del ImageView
               imagenElegida.buildDrawingCache();
               Bitmap bitmap = imagenElegida.getDrawingCache();

               //Guardamos el bitmap en el directorio de trabajo
               OutputStream fileOutStream = null;
               Uri uri;
               try {

                   File directorioImagenes = new File(directorioTrabajo, nombreElemento + ".jpg");
                   uri = Uri.fromFile(directorioImagenes);
                   fileOutStream = new FileOutputStream(directorioImagenes);
               } catch (Exception e) {
                   e.printStackTrace();
               }

               try {
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
                   fileOutStream.flush();
                   fileOutStream.close();
               } catch (Exception e) {
                   e.printStackTrace();
               }

           }
            //Guardamos el audio comprobando que se haya cambiado el original
            if(audioCambiado) {
                if (!ficheroAudio.exists()) {
                    System.out.println("Error. No existe un fichero  de audio");
                    return;
                }
            //Renombramos el archivo temporal y lo movemos al directorio correcto con el nombre que le corresponda
                File fichero2 = new File(directorioTrabajo, nombreElemento + ".3gp");
                if (fichero2.exists()) {
                    fichero2.delete();
                    fichero2 = new File(directorioTrabajo, nombreElemento + ".3gp");
                   // return;
                }

                ficheroAudio.renameTo(fichero2);

                //Si hemos cambiado audio pero imagen no y nombre si renombramos la imagen también para
                //no tener problemas
                if(!imagenCambiada&&!nombreElemntoSeleccionado.equals(nombreElemento)){
                File FichOrig =  new File(directorioTrabajo, nombreElemntoSeleccionado + ".jpg");
                File FichDestino = new File(directorioTrabajo, nombreElemento + ".jpg");
                FichOrig.renameTo(FichDestino);}

            }
                //Renombramos el elemento al nombre dado salvo que sea portada que debe permanecer
            // inmutable o que el nombre no se haya cambiado
                if (!nombreElemntoSeleccionado.equals("portada") || !nombreElemntoSeleccionado.equals(nombreElemento) ) {
                   if(!imagenCambiada && !audioCambiado &&!nombreElemntoSeleccionado.equals(nombreElemento) ){
                       File FichOrig =  new File(directorioTrabajo, nombreElemntoSeleccionado + ".jpg");
                       File FichDestino = new File(directorioTrabajo, nombreElemento + ".jpg");
                       FichOrig.renameTo(FichDestino);
                       FichOrig =  new File(directorioTrabajo, nombreElemntoSeleccionado + ".3gp");
                       FichDestino = new File(directorioTrabajo, nombreElemento + ".3gp");
                       FichOrig.renameTo(FichDestino);

                   }
                   // Borramos los archivos antiguos si es necesario
                    File archivoABorrar;
                   if (imagenCambiada &&!nombreElemntoSeleccionado.equals(nombreElemento) ){ archivoABorrar = new File(directorioTrabajo, nombreElemntoSeleccionado + ".jpg");
                    archivoABorrar.delete();}
                    if(audioCambiado && !nombreElemntoSeleccionado.equals(nombreElemento)){
                    archivoABorrar = new File(directorioTrabajo, nombreElemntoSeleccionado + ".3gp");
                    archivoABorrar.delete();}
                    //Renombramos los archivos si es necesario
                    if(!imagenCambiada && !nombreElemntoSeleccionado.equals(nombreElemento)){
                        File FichOrig =  new File(directorioTrabajo, nombreElemntoSeleccionado + ".jpg");
                        File FichDestino = new File(directorioTrabajo, nombreElemento + ".jpg");
                        FichOrig.renameTo(FichDestino);
                    }
                    if(!audioCambiado && !nombreElemntoSeleccionado.equals(nombreElemento)){
                        File FichOrig =  new File(directorioTrabajo, nombreElemntoSeleccionado + ".3gp");
                        File FichDestino = new File(directorioTrabajo, nombreElemento + ".3gp");
                        FichOrig.renameTo(FichDestino);
                    }


                }
          //Volvemos al selector
            final Intent crear = new Intent(getApplicationContext(), smg75.hcv.com.telhelp.editor.PlantillaJuego.class);
            crear.putExtra("seleccionado",JuegoSeleccionadoOrig);
            crear.putExtra("directorioSeleccionado",directorioTrabajo);
            crear.putExtra("crearJuego",crearJuego);
            crear.putExtra("nombreDelJuego", nombreDelJuego);

            startActivity(crear);
        }
        else{
            Toast.makeText(getApplicationContext(), "Nada nuevo que grabar :))", Toast.LENGTH_LONG).show();

        }


    }
}

