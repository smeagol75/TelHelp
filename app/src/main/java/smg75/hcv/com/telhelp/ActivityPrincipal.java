package smg75.hcv.com.telhelp;


import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Activity inicial con las opciones del juego
 * @author Héctor Crespo Val
 */
public class ActivityPrincipal extends AppCompatActivity {
   //Cadenas constantes con las opciones que podemos elegir
    private static final String VOCABULARIO ="vocabulario";
    private static final String VERBOS ="verbos";
    private static final String PREPOSICIONES ="preposiciones";
    private static final String ACCIONES ="acciones";



    //Instanciamos el iv
    //Instanciamos un objeto tipo Imagen view para controlar la opcion elegida
    ImageView iv=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hacemos que siga siendo pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_principal);

//Copiar recursivamente los datos de assets a la sd para tenerlos disponibles y luego poder aumentarlos
        File outDir =  new File(getFilesDir().getPath());
        //Compruebo si ya lo he instalado para no hacerlo de nuevo
        File comprobarSiInstalado = new File (outDir.toString()+"/datos");
        if(!comprobarSiInstalado.exists()){
        CopiarAssets("datos",outDir.toString());}

    }

    /*Capturamos el evento Click y lo asociamos a las imageView y en función del click llenaremos
    el Activity de selector de juego
    */
    public void onClick(View v) {
        ImageView ivtemp = (ImageView) findViewById(v.getId());
        Intent intent = new Intent(this, smg75.hcv.com.telhelp.Selector.class);
        String seleccion;

        switch (ivtemp.getId()) {
            case R.id.iv_vocabulario:
                seleccion = "vocabulario";
                intent.putExtra("seleccionado",seleccion);
                startActivity(intent);
                break;
            case R.id.iv_preposiciones:
                seleccion = "preposiciones";
                intent.putExtra("seleccionado",seleccion);
                startActivity(intent);
                break;
            case R.id.iv_verbos:
                seleccion = "verbos";
                intent.putExtra("seleccionado",seleccion);
                startActivity(intent);
                break;
            case R.id.iv_acciones:
                seleccion = "acciones";
                intent.putExtra("seleccionado",seleccion);
                startActivity(intent);
                break;
        }

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
}
