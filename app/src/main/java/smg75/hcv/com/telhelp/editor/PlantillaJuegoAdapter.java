package smg75.hcv.com.telhelp.editor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import smg75.hcv.com.telhelp.R;
import smg75.hcv.com.telhelp.RecyclerViewOnItemClickListener;


/**
 * @author Héctor Crespo Val
 * Adapter personalizado para cargar los elementos del juego
 */
public class PlantillaJuegoAdapter extends RecyclerView.Adapter<PlantillaJuegoAdapter.PlantillaDummyJuegoViewHolder> {
    private List<String> lista;
    private String ruta;

    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    //Constructor que recibe un array con los datos para crear el recyclerView y la ruta
    public PlantillaJuegoAdapter(@NonNull List<String> opciones,String ruta, @NonNull RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
        this.lista = opciones;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
        this.ruta = ruta;

    }

    @Override
    public PlantillaDummyJuegoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //definimos donde y qué elemento se va a ir incrementando
        View filaPlantilla = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_plantilla, parent, false);
        return new PlantillaDummyJuegoViewHolder(filaPlantilla);
    }

    @Override
    public void onBindViewHolder(PlantillaDummyJuegoViewHolder holder, int position) {
        String cadena = lista.get(position);
        //Devolvemos la imegen de portada para el recycler
        String seleccionado;
        if(lista.get(position)=="portada"){ seleccionado = ruta +"/portada.jpg";}
        else {
            seleccionado = ruta + "/" + lista.get(position) + ".jpg";
        }

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

       //Ponemos el nombre del elemento
        holder.getSubtitleTextView().setText("Elemento número: "+ position);

        //Ponemos la primera en mayúscula para el menú


        holder.getTitleTextView().setText("Nombre: "+cadena);
        if(lista.get(position).toString()=="portada") holder.getTitleTextView().setText("Portada");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class PlantillaDummyJuegoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private ImageView imagenFila;
        private TextView titleTextView;
        private TextView subtitleTextView;

        public PlantillaDummyJuegoViewHolder(View itemView) {
            super(itemView);
            imagenFila = (ImageView) itemView.findViewById(R.id.imagenFila_plantilla);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextViewPlantilla);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitleTextViewPlantilla);
            itemView.setOnClickListener(this);
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
    }



}