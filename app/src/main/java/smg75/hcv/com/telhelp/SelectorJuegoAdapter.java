package smg75.hcv.com.telhelp;

import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.drawable.BitmapDrawable;
        import android.support.annotation.NonNull;
        import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
        import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
        import android.support.v7.widget.RecyclerView;
        import android.view.GestureDetector;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.io.File;
        import java.util.List;

/**
 * Adaptador personalizado para cargar los elementos de la categoría elegida.
 * @author Héctor Crespo Val
 */
public class SelectorJuegoAdapter extends RecyclerView.Adapter<SelectorJuegoAdapter.PlantillaJuegoViewHolder> {
    private List<String> opciones;
    private String ruta;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    //Constructor que recibe un array con los datos para crear el recyclerView y la ruta
    public SelectorJuegoAdapter(@NonNull List<String> opciones, String ruta, @NonNull RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
        this.opciones = opciones;
        this.ruta = ruta;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }
    @Override
    public PlantillaJuegoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       //definimos donde y qué elemento se va a ir incrementando
        View fila = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila, parent, false);
        return new PlantillaJuegoViewHolder(fila);
    }

    @Override
    public void onBindViewHolder(PlantillaJuegoViewHolder holder, int position) {
        String cadena = opciones.get(position);
        //Devolvemos la imegen de portada para el recycler
        String seleccionado = ruta + "/" + cadena +"/portada.jpg";
        //Seleccionamos la imagen que hemos definido en portada
        Bitmap myBitmap = BitmapFactory.decodeFile(seleccionado);
        //Lo convertimos en drawable
        BitmapDrawable myDrawable = new BitmapDrawable(Resources.getSystem(),myBitmap);
        Bitmap originalBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        //Lo redondeamos
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(Resources.getSystem(), originalBitmap);
        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());
        //Se lo asignamos a la fila
        holder.getImageView().setImageDrawable(roundedDrawable);

        //Ponemos el número de tarjetas que tenemos
        String dir = ruta + "/" + cadena +"/";
        int numeroTarjetas = leerNumeroArchivos(dir);
        String mensaje = numeroTarjetas + " elementos en el juego.";
        holder.getSubtitleTextView().setText(mensaje);
        //Ponemos la primera en mayúscula para el menú
                cadena= cadena.substring(0,1).toUpperCase() + cadena.substring(1);
        holder.getTitleTextView().setText(cadena);
    }

    @Override
    public int getItemCount() {
        return opciones.size();
    }

    class PlantillaJuegoViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener,View.OnLongClickListener{
        private ImageView imagenFila;
        private TextView titleTextView;
        private TextView subtitleTextView;

        public PlantillaJuegoViewHolder(View itemView) {
            super(itemView);
            imagenFila = (ImageView) itemView.findViewById(R.id.imagenFila_plantilla);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextViewPlantilla);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitleTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getSubtitleTextView() {
            return subtitleTextView;
        }

        public ImageView getImageView() {
            return imagenFila;
        }

        @Override
        public void onClick(View v) {
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v){
            recyclerViewOnItemClickListener.onLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public int leerNumeroArchivos(String ruta){
        int contador=0;

        //Defino la ruta donde busco los directorios
        File f = new File(ruta);
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();
        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++)
        {
            //Sacamos del array files un fichero
            File file = files[i];
            //Si es archivo...
            if (file.isFile())
               contador++;
        }
        //Restamos la portada, la locución inicial y dividimos entre dos el resultado por tener archivo
        //audio y foto
        contador = (contador-2)/2;
        return contador;
    }

}