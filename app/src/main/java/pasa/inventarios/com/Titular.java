package pasa.inventarios.com;

import android.util.Log;

import java.util.List;

/**
 * Created by Abraham on 07/07/2016.
 */
public class Titular {
    private String titulo;
    private String subtitulo;

    public Titular(String tit, String sub){
        titulo = tit;
        subtitulo = sub;
        Log.e("", "SE insertó  " + titulo);
    }

    public String getTitulo(){
        return titulo;
    }
    public String getSubtitulo(){
        return subtitulo;
    }
}
