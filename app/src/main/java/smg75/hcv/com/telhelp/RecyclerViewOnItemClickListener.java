package smg75.hcv.com.telhelp;

import android.view.View;
/**
 * @author Héctor Crespo Val
 */
/**
 * Manejador evento Click del RecyclerView
 */

    public interface  RecyclerViewOnItemClickListener {

        void onClick(View v, int position);
        void onLongClick(View view,int position);
    }





