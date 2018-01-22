package smg75.hcv.com.telhelp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Lógica del juego Encuentra en el que hay que encontrar el elemento correcto dentro de las imágenes
 * @author Héctor Crespo Val
 */
public class JuegoEncuentra extends AppCompatActivity {

    //Para reproducir sonidos
    SoundPool sp=null;
    //Diferentes sonidos
    int iAplauso;
    int ifallo;
    int iAcierto;
    int iCorrectaAltavoz;
    int ilocucionPortada;

    //Arrays con los valores para cargar y valores de la solucion
    private List<String> imagenes;
    private List<String> sonidos;
    int valorPosicionArraySolucion=0;
    int ivCorrecto=0;
    //Strings con las diferentes opciones
    private String seleccionado;
    private String tipoJuego;
    private String directorioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hacemos que siga siendo pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_juego_encuentra);

        //Recuperamos el intent con la elección del usuario
        final Intent intent = getIntent();

        if (intent != null)
        {
            try{
                seleccionado  = intent.getStringExtra("seleccionado");
                tipoJuego = intent.getStringExtra("tipoJuego");
                           }catch (Exception e){
                e.printStackTrace();
            }
        }
       //Definimos el directoreio base
        File outDir =  new File(getFilesDir().getPath());
        directorioSeleccionado = outDir.toString()+"/datos/" + tipoJuego+"/"+ seleccionado +"/";

        //Ocultamos la barra para tener más espacio
        getSupportActionBar().hide();
        //quitamos el acceso a editar la información
        TextView textView = (TextView)findViewById(R.id.txt_info);
        textView.setKeyListener(null);

        //recogemos el soundpool
        SoundPool.Builder sp21 =new SoundPool.Builder();;
        sp21.setMaxStreams(5);
        sp = sp21.build();

        //Cargamos los sonidos
        ilocucionPortada=sp.load(directorioSeleccionado +"portada.3gp",0);
        iAplauso=sp.load(this,R.raw.tadaa,0);
        ifallo=sp.load(this,R.raw.no,0);
        iAcierto=sp.load(this,R.raw.sonido_acierto,0);

        //Paramos 300 milisegundos el programa para que le de tiempo a poner la locución de
        // lo que hay que hacer
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sp.play(ilocucionPortada,1,1,1,0,1);
        //Ponemos el nombre del juego Elegido
        textView.setText(seleccionado.toUpperCase());
        //Creamos los recursos con imagenes y sonidos
        crearArraysElementos();
        //Elegimos 6 aleatorias
        empezarJuego();
        //Reproducimos la respuesta a buscar con 3 segundos de retardo para equilibrar
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                sp.play(iCorrectaAltavoz,1,1,
                        1,0,1);

            }
        },3000);
    }

    // Guarda el id de los botones
    int []idBoton={R.id.iv11,R.id.iv12,R.id.iv13,
            R.id.iv21,R.id.iv22,R.id.iv23,};
    //Array para meter id
    int []iDibujos = new int[6];

    /**
     * empezarJuego
     * metodo para arrancar el juego, repartir imágenes e iniciar valores a buscar
     */

    public void empezarJuego(){

        //Ponemos todas las imágenes tapadas
        for(int i=0;i<iDibujos.length;i++) {
            ImageView ivtemp = (ImageView) findViewById(idBoton[i]);
            ivtemp.setImageResource(R.drawable.fondo);
            ivtemp.setClickable(false);

        }
        //pongo a cero el array de imagenes
        for(int j=0;j<iDibujos.length;j++){
            iDibujos[j]=0;
        }
        //Mientras tenga dibujos libres
        while(nDibujosDisponibles()>0){
            //escojo una imagen aleatoria
            int iDibujoAleatorio=(int)(Math.random()*imagenes.size());
           //Controlamos que no se repita
            boolean existe = false;
            for (int i=0;i<iDibujos.length;i++){
                if(iDibujos[i]==0){
                   for (int k=0;k<iDibujos.length-1;k++) {
                       if (iDibujoAleatorio==iDibujos[k])existe=true;
                   }
                  //si es igual a cero y no se ha puesto lo asignamos
                   if(!existe)
                   iDibujos[i] =iDibujoAleatorio;
                    existe=false;
               }
        }
      }
        //Elegimos el correcto dentro del array
        //escojo una imagen aleatoria
        ivCorrecto=(int)(Math.random()*iDibujos.length);
        valorPosicionArraySolucion = iDibujos[ivCorrecto];


        iCorrectaAltavoz=sp.load(sonidos.get(valorPosicionArraySolucion),1);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                for(int i=0;i<iDibujos.length;i++){
                    int boton =idBoton[i];
                    ImageView ivtemp= (ImageView) findViewById(boton);
                    String rutaBmp = imagenes.get(iDibujos[i]);
                    Bitmap bitmap = BitmapFactory.decodeFile(rutaBmp);
                    ivtemp.setImageBitmap(bitmap);
                    //Le volvemos a dejar accesible ya que al haber acertado todas al principio quedan inaccesibles
                    ivtemp.setClickable(true);
                }
            }
        },3000);


    }

    //Controla los dibijos sin asignar
    int nDibujosDisponibles()
    {
        int nDibujosDisponibles=0;
        for(int i=0;i<6;i++)
        {
            if(iDibujos[i]==0)
            {   nDibujosDisponibles++;       }
        }
        return nDibujosDisponibles;
    }

    /**
     * Método que crea el array de elementos con los sonidos e imágenes
     */
    public void crearArraysElementos() {
        // Array TEXTO donde guardaremos los nombres de los directorios
        imagenes = new ArrayList<String>();
        sonidos = new ArrayList<String>();
        //Defino la ruta donde busco los directorios
        File f = new File(directorioSeleccionado);
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();
        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++) {
            //Sacamos del array files un fichero
            File file = files[i];
            String nombreArchivo = file.getName();
            //Si es archivo e imagen o audio.
            if (file.isFile()) {
                if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".JPG") || nombreArchivo.endsWith(".png") || nombreArchivo.endsWith(".PNG")) {
                    if (!nombreArchivo.startsWith("portada"))
                        imagenes.add(directorioSeleccionado + file.getName());
                } else if (nombreArchivo.endsWith(".wav") || nombreArchivo.endsWith(".WAV") || nombreArchivo.endsWith(".mp3") || nombreArchivo.endsWith(".3GP") || nombreArchivo.endsWith(".3gp")) {

                    if (!nombreArchivo.startsWith("portada"))
                        sonidos.add(directorioSeleccionado + file.getName());
                }
            }
        }
        //Ordenamos los arrays para no tener problemas.
        Collections.sort(sonidos);
        Collections.sort(imagenes);
    }

    /**
     * método que controla la lógica del juego cuando pulsa un botón
     * @param v View para controlar el evento Click
     */

    public void clickImagen(View v)
    {
        //Buscamos el botón pulsado
        int iNumeroBotonPulsado=0;

        for(int i=0;i<6;i=i+1)
        {
            if(v.getId()==idBoton[i])
            {
                iNumeroBotonPulsado=i;
                break;
            }
        }
    //Si es corecto pone sonido de victoria y retrasa ejecución para que
        // todos se muestren correctamente
        if (iNumeroBotonPulsado==ivCorrecto){
            ImageButton btemp = (ImageButton)findViewById(R.id.ib_altavoz);
            btemp.setClickable(false);
            sp.play(iAplauso,1,1,1,0,1);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    sp.play(iCorrectaAltavoz,1,1,1,0,1);
                    ImageButton btemp = (ImageButton)findViewById(R.id.ib_altavoz);
                    btemp.setClickable(true);

                }
            },3000);

            empezarJuego();

        }
        else
        {ImageView ivtemp = (ImageView) findViewById(idBoton[iNumeroBotonPulsado]);
            ivtemp.setImageResource(R.drawable.fondo);
            ivtemp.setClickable(false);
            sp.play(ifallo,1,1,1,0,1);
        }
    }
    /**
     * Método que reproduce el sonido del elemento correcto a petición del usuario
     * @param v View para controlar el evento CLick
     */
    public void pulsarAltavoz(View v){

       sp.play(iCorrectaAltavoz,1,1,1,0,1);
    }
}
